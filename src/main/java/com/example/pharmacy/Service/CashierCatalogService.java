package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.CashierCatalogPageDto;
import com.example.pharmacy.DTO.CashierProductRowDto;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Pojo.Stock;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Repository.StockRepository;
import com.example.pharmacy.util.FormOfReleaseLabels;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CashierCatalogService {

    private final StockRepository stockRepository;
    private final NomenclatureRepository nomenclatureRepository;

    public CashierCatalogService(StockRepository stockRepository,
                                 NomenclatureRepository nomenclatureRepository) {
        this.stockRepository = stockRepository;
        this.nomenclatureRepository = nomenclatureRepository;
    }

    public CashierCatalogPageDto listProducts(String query,
                                              Long pharmacyId,
                                              Integer priceMinKop,
                                              Integer priceMaxKop,
                                              String sort,
                                              String dir,
                                              Long userPharmacyId,
                                              int page,
                                              int size) {
        LocalDate today = LocalDate.now();
        Map<String, CashierProductRowDto> rows = new LinkedHashMap<>();

        for (Stock stock : stockRepository.findAvailableForCatalog(pharmacyId, today)) {
            if (stock.getAvailable() <= 0) {
                continue;
            }
            Nomenclature n = stock.getBatch().getNomenclature();
            String key = n.getId() + ":" + stock.getPharmacy().getId();
            rows.compute(key, (k, existing) -> {
                if (existing == null) {
                    return toRow(n, stock, userPharmacyId);
                }
                existing.setAvailableQty(existing.getAvailableQty() + stock.getAvailable());
                existing.setCanAddToCart(userPharmacyId != null
                        && userPharmacyId.equals(existing.getPharmacyId())
                        && existing.getAvailableQty() > 0);
                return existing;
            });
        }

        boolean hasQuery = query != null && query.trim().length() >= 2;
        if (hasQuery) {
            String q = query.trim().toLowerCase(Locale.ROOT);
            for (Nomenclature n : nomenclatureRepository.searchByQuery(query.trim(), PageRequest.of(0, 200))) {
                if (!matchesPrice(resolveSalePrice(pharmacyId, userPharmacyId, n.getId(), today), priceMinKop, priceMaxKop)) {
                    continue;
                }
                if (pharmacyId != null) {
                    mergeStocksForPharmacy(rows, n, pharmacyId, userPharmacyId);
                } else {
                    for (Stock s : stockRepository.findAvailableByNomenclatureFefo(n.getId())) {
                        if (s.getAvailable() > 0) {
                            String key = n.getId() + ":" + s.getPharmacy().getId();
                            rows.putIfAbsent(key, toRow(n, s, userPharmacyId));
                        }
                    }
                }
            }
            rows.values().removeIf(r -> !matchesQuery(r, q));
        }

        List<CashierProductRowDto> all = rows.values().stream()
                .filter(r -> r.getAvailableQty() != null && r.getAvailableQty() > 0)
                .filter(r -> matchesPrice(r.getPrice(), priceMinKop, priceMaxKop))
                .sorted(buildComparator(sort, dir))
                .toList();

        return paginate(all, page, size);
    }

    private void mergeStocksForPharmacy(Map<String, CashierProductRowDto> rows,
                                        Nomenclature n,
                                        Long pharmacyId,
                                        Long userPharmacyId) {
        for (Stock s : stockRepository.findAvailableByNomenclatureFefo(n.getId())) {
            if (!s.getPharmacy().getId().equals(pharmacyId) || s.getAvailable() <= 0) {
                continue;
            }
            String key = n.getId() + ":" + pharmacyId;
            rows.putIfAbsent(key, toRow(n, s, userPharmacyId));
        }
    }

    private Integer resolveSalePrice(Long pharmacyId, Long userPharmacyId, Long nomenclatureId, LocalDate today) {
        Long scope = pharmacyId != null ? pharmacyId : userPharmacyId;
        if (scope == null) {
            return null;
        }
        return stockRepository.findAvailableByNomenclatureFefo(nomenclatureId).stream()
                .filter(s -> s.getPharmacy().getId().equals(scope))
                .filter(s -> s.getAvailable() > 0)
                .filter(s -> s.getBatch().getExpiryDate() == null || !s.getBatch().getExpiryDate().isBefore(today))
                .filter(s -> s.getBatch().getWrittenOff() == null || !s.getBatch().getWrittenOff())
                .map(s -> s.getBatch().getPrice())
                .findFirst()
                .orElse(null);
    }

    private static CashierCatalogPageDto paginate(List<CashierProductRowDto> all, int page, int size) {
        int safeSize = Math.max(1, size);
        int total = all.size();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / safeSize);
        int safePage = totalPages == 0 ? 0 : Math.max(0, Math.min(page, totalPages - 1));
        int from = safePage * safeSize;
        int to = Math.min(from + safeSize, total);
        List<CashierProductRowDto> slice = from < to ? new ArrayList<>(all.subList(from, to)) : List.of();
        return new CashierCatalogPageDto(slice, safePage, totalPages, total, safeSize);
    }

    private static boolean matchesQuery(CashierProductRowDto r, String q) {
        return contains(r.getBrandName(), q) || contains(r.getMnn(), q);
    }

    private static boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    private static boolean matchesPrice(Integer price, Integer min, Integer max) {
        if (min == null && max == null) {
            return true;
        }
        if (price == null) {
            return min == null;
        }
        if (min != null && price < min) {
            return false;
        }
        if (max != null && price > max) {
            return false;
        }
        return true;
    }

    private CashierProductRowDto toRow(Nomenclature n, Stock stock, Long userPharmacyId) {
        return buildRow(n, stock.getPharmacy().getId(), stock.getPharmacy().getName(),
                stock.getAvailable(), userPharmacyId, stock.getBatch().getPrice());
    }

    private CashierProductRowDto buildRow(Nomenclature n, Long pharmacyId, String pharmacyName,
                                          int available, Long userPharmacyId, Integer price) {
        CashierProductRowDto row = new CashierProductRowDto();
        row.setNomenclatureId(n.getId());
        row.setMnn(n.getMnn() != null ? n.getMnn() : "—");
        row.setBrandName(n.getBrandName());
        row.setDosageText(formatDosage(n));
        row.setPrice(price);
        row.setAvailableQty(available);
        row.setPharmacyId(pharmacyId);
        row.setPharmacyName(pharmacyName);
        row.setReceipt(n.getReceipt());
        row.setNarcotic(n.getNarcotic());
        row.setPsychotropic(n.getPsychotropic());
        row.setReceiptRequired(n.requiresPrescription());
        row.setMarked(n.getMarked());
        row.setCanAddToCart(userPharmacyId != null && userPharmacyId.equals(pharmacyId) && available > 0);
        return row;
    }

    private static String formatDosage(Nomenclature n) {
        StringBuilder sb = new StringBuilder();
        if (n.getFormOfRelease() != null && !n.getFormOfRelease().isBlank()) {
            sb.append(FormOfReleaseLabels.shortLabel(n.getFormOfRelease()));
        }
        if (n.getDosage() != null) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(n.getDosage());
            if (n.getDosageUnit() != null) {
                sb.append(" ").append(n.getDosageUnit());
            }
        }
        if (n.getQuantityInPack() != null) {
            if (!sb.isEmpty()) {
                sb.append(" · ");
            }
            sb.append(n.getQuantityInPack()).append(" шт/уп.");
        }
        return sb.isEmpty() ? "—" : sb.toString();
    }

    private Comparator<CashierProductRowDto> buildComparator(String sort, String dir) {
        Comparator<CashierProductRowDto> cmp = switch (sort != null ? sort : "brandName") {
            case "mnn" -> Comparator.comparing(r -> nullSafe(r.getMnn()), String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(r -> r.getPrice() != null ? r.getPrice() : Integer.MAX_VALUE);
            case "available" -> Comparator.comparing(r -> r.getAvailableQty() != null ? r.getAvailableQty() : 0);
            case "pharmacy" -> Comparator.comparing(r -> nullSafe(r.getPharmacyName()), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(r -> nullSafe(r.getBrandName()), String.CASE_INSENSITIVE_ORDER);
        };
        if ("desc".equalsIgnoreCase(dir)) {
            cmp = cmp.reversed();
        }
        return cmp;
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}
