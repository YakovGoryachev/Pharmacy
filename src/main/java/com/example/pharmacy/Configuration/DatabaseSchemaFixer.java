package com.example.pharmacy.Configuration;

import com.example.pharmacy.Repository.RolesRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Дополняет ddl-auto=update для PostgreSQL: NOT NULL-колонки без DEFAULT
 * не добавляются на таблицы с данными.
 */
@Component
@Profile("!test")
@Order(0)
public class DatabaseSchemaFixer implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final RolesRepository rolesRepository;

    public DatabaseSchemaFixer(JdbcTemplate jdbc, RolesRepository rolesRepository) {
        this.jdbc = jdbc;
        this.rolesRepository = rolesRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        RoleBootstrap.migrateAndEnsure(rolesRepository);
        patchBooleanColumn("nomenclature", "marked", false);
        patchIntegerColumn("users", "failed_login_attempts", 0);
        patchBooleanColumn("cheques", "is_returned", false);
        patchIntegerColumn("categories", "markup_percent", 0);
        fixPostgresSequences();
        ensureBatchNumberUnique();
    }

    private void fixPostgresSequences() {
        if (!isPostgres()) {
            return;
        }
        List<String> tables = List.of(
                "nomenclature", "batch", "stock", "pharmacies", "users", "categories",
                "atc_manual", "cheques", "cheque_positions", "marking_codes",
                "write_off_documents", "stock_transfers", "inventory_sessions", "inventory_lines"
        );
        for (String table : tables) {
            fixSequence(table);
        }
    }

    private void fixSequence(String table) {
        try {
            String sequence = jdbc.queryForObject(
                    "SELECT pg_get_serial_sequence(?, 'id')", String.class, table);
            if (sequence == null) {
                return;
            }
            Long maxId = jdbc.queryForObject(
                    "SELECT COALESCE(MAX(id), 0) FROM " + table, Long.class);
            jdbc.queryForObject("SELECT setval(?, ?)", Long.class, sequence, maxId);
        } catch (Exception ignored) {
            // таблица может ещё не существовать
        }
    }

    private void ensureBatchNumberUnique() {
        if (!isPostgres()) {
            return;
        }
        try {
            jdbc.execute("""
                    CREATE UNIQUE INDEX IF NOT EXISTS batch_batch_number_key ON batch (batch_number)
                    WHERE batch_number IS NOT NULL
                    """);
        } catch (Exception ignored) {
        }
    }

    private void patchBooleanColumn(String table, String column, boolean defaultValue) {
        if (!isPostgres()) {
            return;
        }
        if (columnExists(table, column)) {
            jdbc.update("UPDATE " + table + " SET " + column + " = ? WHERE " + column + " IS NULL", defaultValue);
            return;
        }
        jdbc.execute(
                "ALTER TABLE " + table + " ADD COLUMN IF NOT EXISTS " + column
                        + " boolean DEFAULT " + defaultValue);
        jdbc.update("UPDATE " + table + " SET " + column + " = ? WHERE " + column + " IS NULL", defaultValue);
    }

    private void patchIntegerColumn(String table, String column, int defaultValue) {
        if (!isPostgres()) {
            return;
        }
        if (columnExists(table, column)) {
            jdbc.update("UPDATE " + table + " SET " + column + " = ? WHERE " + column + " IS NULL", defaultValue);
            return;
        }
        jdbc.execute(
                "ALTER TABLE " + table + " ADD COLUMN IF NOT EXISTS " + column
                        + " integer DEFAULT " + defaultValue);
        jdbc.update("UPDATE " + table + " SET " + column + " = ? WHERE " + column + " IS NULL", defaultValue);
    }

    private boolean columnExists(String table, String column) {
        Integer count = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = ? AND column_name = ?
                """,
                Integer.class,
                table,
                column);
        return count != null && count > 0;
    }

    private boolean isPostgres() {
        try (var conn = jdbc.getDataSource().getConnection()) {
            String product = conn.getMetaData().getDatabaseProductName();
            return product != null && product.toLowerCase().contains("postgresql");
        } catch (Exception e) {
            return false;
        }
    }
}
