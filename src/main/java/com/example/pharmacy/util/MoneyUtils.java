package com.example.pharmacy.util;

import java.util.Locale;

/**
 * Денежные суммы в системе хранятся в копейках (целое число).
 * В формах и отчётах для пользователя — рубли.
 */
public final class MoneyUtils {

    public static final int KOPECKS_PER_RUBLE = 100;

    private MoneyUtils() {
    }

    public static Integer toKopecks(Double rubles) {
        if (rubles == null) {
            return null;
        }
        return (int) Math.round(rubles * KOPECKS_PER_RUBLE);
    }

    public static Double toRubles(Integer kopecks) {
        if (kopecks == null) {
            return null;
        }
        return kopecks / (double) KOPECKS_PER_RUBLE;
    }

    public static String formatRubles(Integer kopecks) {
        if (kopecks == null) {
            return "—";
        }
        return String.format(Locale.forLanguageTag("ru"), "%.2f ₽", kopecks / (double) KOPECKS_PER_RUBLE);
    }

    /** Приоритет: рубли из формы, иначе уже переданные копейки. */
    public static Integer resolveKopecks(Double rubles, Integer kopecks) {
        if (rubles != null) {
            return toKopecks(rubles);
        }
        return kopecks;
    }
}
