package com.example.pharmacy.Configuration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Дополняет ddl-auto=update для PostgreSQL: NOT NULL-колонки без DEFAULT
 * не добавляются на таблицы с данными.
 */
@Component
@Profile("!test")
@Order(0)
public class DatabaseSchemaFixer implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public DatabaseSchemaFixer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        patchBooleanColumn("nomenclature", "marked", false);
        patchIntegerColumn("users", "failed_login_attempts", 0);
        patchBooleanColumn("cheques", "is_returned", false);
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
