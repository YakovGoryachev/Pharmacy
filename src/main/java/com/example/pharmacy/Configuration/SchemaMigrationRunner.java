package com.example.pharmacy.Configuration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Дополняет существующую БД колонками, которые Hibernate ddl-auto=update
 * не всегда добавляет к уже заполненным таблицам.
 */
@Component
@Profile("!test")
public class SchemaMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public SchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                ALTER TABLE nomenclature
                ADD COLUMN IF NOT EXISTS product_type VARCHAR(32) NOT NULL DEFAULT 'MEDICINE'
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS app_migration (
                    name VARCHAR(64) PRIMARY KEY
                )
                """);
        runOnce("fix_batch_price_kopecks_v1", () -> fixPricesStoredAsRubles("batch"));
        runOnce("fix_nomenclature_price_kopecks_v1", () -> fixPricesStoredAsRubles("nomenclature"));
    }

    private void runOnce(String migrationName, Runnable migration) {
        Integer exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_migration WHERE name = ?",
                Integer.class,
                migrationName);
        if (exists != null && exists > 0) {
            return;
        }
        migration.run();
        jdbcTemplate.update("INSERT INTO app_migration (name) VALUES (?)", migrationName);
    }

    /**
     * Раньше в поле price иногда сохраняли рубли (85, 800), а отображение делит на 100.
     * Seed-данные в копейках (8900, 35000) не затрагиваем.
     */
    private void fixPricesStoredAsRubles(String table) {
        jdbcTemplate.update("""
                UPDATE %s SET price = price * 100
                WHERE price IS NOT NULL AND price > 0 AND (
                    price < 1000
                    OR (price >= 100 AND price < 5000 AND MOD(price, 100) = 0)
                )
                """.formatted(table));
    }
}
