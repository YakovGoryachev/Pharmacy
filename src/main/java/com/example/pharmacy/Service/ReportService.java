package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.DashboardChartsDto;
import com.example.pharmacy.DTO.PharmacyAnalyticsDto;
import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.ChequePositionRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Repository.RequestReportRepository;
import com.example.pharmacy.Repository.StockRepository;
import com.example.pharmacy.Repository.WriteOffDocumentRepository;
import com.example.pharmacy.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final ChequeService chequeService;
    private final StockRepository stockRepository;
    private final NomenclatureRepository nomenclatureRepository;
    private final ChequePositionRepository chequePositionRepository;
    private final RequestReportRepository requestReportRepository;
    private final WriteOffDocumentRepository writeOffDocumentRepository;
    private final ObjectMapper objectMapper;

    public ReportService(ChequeService chequeService,
                         StockRepository stockRepository,
                         NomenclatureRepository nomenclatureRepository,
                         ChequePositionRepository chequePositionRepository,
                         RequestReportRepository requestReportRepository,
                         WriteOffDocumentRepository writeOffDocumentRepository,
                         ObjectMapper objectMapper) {
        this.chequeService = chequeService;
        this.stockRepository = stockRepository;
        this.nomenclatureRepository = nomenclatureRepository;
        this.chequePositionRepository = chequePositionRepository;
        this.requestReportRepository = requestReportRepository;
        this.writeOffDocumentRepository = writeOffDocumentRepository;
        this.objectMapper = objectMapper;
    }

    public PharmacyAnalyticsDto buildPharmacyAnalytics(Long pharmacyId, Instant from, Instant to) {
        Map<String, Object> metrics = dashboard(pharmacyId, from, to);
        DashboardChartsDto charts = dashboardCharts(pharmacyId, from, to);

        List<Object[]> byCategory = chequePositionRepository.revenueByCategory(pharmacyId, from, to);
        List<String> categoryLabels = new ArrayList<>();
        List<Integer> categoryRevenues = new ArrayList<>();
        for (Object[] row : byCategory) {
            categoryLabels.add(String.valueOf(row[0]));
            categoryRevenues.add(row[1] != null ? ((Number) row[1]).intValue() : 0);
        }

        String topCategoryName = categoryLabels.isEmpty() ? "—" : categoryLabels.getFirst();
        Integer topCategoryRevenue = categoryRevenues.isEmpty() ? 0 : categoryRevenues.getFirst();

        List<Object[]> topProducts = chequePositionRepository.topSelling(pharmacyId, from, to);
        String bestProductName = topProducts.isEmpty() ? "—" : String.valueOf(topProducts.getFirst()[0]);
        Integer bestProductRevenue = topProducts.isEmpty() ? 0
                : (topProducts.getFirst()[1] != null ? ((Number) topProducts.getFirst()[1]).intValue() : 0);

        List<Object[]> bottomProducts = chequePositionRepository.bottomSellingByQty(pharmacyId, from, to);
        String worstProductName = bottomProducts.isEmpty() ? "—" : String.valueOf(bottomProducts.getFirst()[0]);
        Integer worstProductQty = bottomProducts.isEmpty() ? 0
                : (bottomProducts.getFirst()[1] != null ? ((Number) bottomProducts.getFirst()[1]).intValue() : 0);

        Integer writeOffQty = writeOffDocumentRepository.sumQuantityInPeriod(pharmacyId, from, to);
        if (writeOffQty == null) {
            writeOffQty = 0;
        }

        return new PharmacyAnalyticsDto(
                metrics,
                charts,
                topCategoryName,
                topCategoryRevenue,
                categoryLabels,
                categoryRevenues,
                bestProductName,
                bestProductRevenue,
                worstProductName,
                worstProductQty,
                writeOffQty
        );
    }

    public Map<String, Object> dashboard(Long pharmacyId, Instant from, Instant to) {
        List<Cheque> sales = chequeService.salesForPeriod(pharmacyId, from, to);
        int revenue = sales.stream().mapToInt(c -> c.getTotalAmount() != null ? c.getTotalAmount() : 0).sum();
        double avg = sales.isEmpty() ? 0 : (double) revenue / sales.size();
        List<Stock> stocks = pharmacyId != null
                ? stockRepository.findByPharmacyId(pharmacyId)
                : stockRepository.findAll();
        long lowStock = nomenclatureRepository.findAll().stream()
                .filter(n -> {
                    int onHand = stocks.stream()
                            .filter(s -> s.getBatch().getNomenclature().getId().equals(n.getId()))
                            .mapToInt(Stock::getAvailable)
                            .sum();
                    return n.getMinStockLevel() != null && onHand < n.getMinStockLevel();
                })
                .count();
        Map<String, Object> m = new HashMap<>();
        m.put("revenue", revenue);
        m.put("chequeCount", sales.size());
        m.put("avgCheque", Math.round(avg));
        m.put("lowStockCount", lowStock);
        m.put("stockLines", stocks.size());
        return m;
    }

    public DashboardChartsDto dashboardCharts(Long pharmacyId, Instant from, Instant to) {
        ZoneId zone = ZoneId.systemDefault();
        List<Cheque> sales = chequeService.salesForPeriod(pharmacyId, from, to);
        LocalDate start = from.atZone(zone).toLocalDate();
        LocalDate end = to.atZone(zone).toLocalDate();
        long dayCount = ChronoUnit.DAYS.between(start, end) + 1;
        if (dayCount < 1) {
            dayCount = 1;
        }
        if (dayCount > 90) {
            dayCount = 90;
            start = end.minusDays(89);
        }

        List<String> dayLabels = new ArrayList<>();
        List<Integer> dayRevenue = new ArrayList<>();
        for (int i = 0; i < dayCount; i++) {
            LocalDate day = start.plusDays(i);
            dayLabels.add(day.format(DateTimeFormatter.ofPattern("dd.MM")));
            int sum = sales.stream()
                    .filter(c -> c.getCreatedAt().atZone(zone).toLocalDate().equals(day))
                    .mapToInt(c -> c.getTotalAmount() != null ? c.getTotalAmount() : 0)
                    .sum();
            dayRevenue.add(sum);
        }

        Map<String, Integer> paymentTotals = new LinkedHashMap<>();
        for (Cheque c : sales) {
            if (c.getPaymentMethod() == null) {
                continue;
            }
            String label = c.getPaymentMethod().name();
            int amount = c.getTotalAmount() != null ? c.getTotalAmount() : 0;
            paymentTotals.merge(label, amount, Integer::sum);
        }

        List<String> topLabels = new ArrayList<>();
        List<Integer> topAmounts = new ArrayList<>();
        for (Object[] row : chequePositionRepository.topSelling(pharmacyId, from, to)) {
            if (topLabels.size() >= 5) {
                break;
            }
            topLabels.add(String.valueOf(row[0]));
            topAmounts.add(row[1] != null ? ((Number) row[1]).intValue() : 0);
        }

        return new DashboardChartsDto(
                dayLabels,
                dayRevenue,
                new ArrayList<>(paymentTotals.keySet()),
                new ArrayList<>(paymentTotals.values()),
                topLabels,
                topAmounts
        );
    }

    public RequestReport generateStockPdf(Long pharmacyId, User user) throws Exception {
        if (pharmacyId == null) {
            throw new BusinessException("Не выбрана аптека для отчёта по остаткам");
        }
        Path dir = Path.of("uploads", "reports");
        Files.createDirectories(dir);
        String fileName = "stock-" + pharmacyId + "-" + System.currentTimeMillis() + ".pdf";
        Path file = dir.resolve(fileName);

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(file.toFile()));
        doc.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        doc.add(new Paragraph("Отчёт по остаткам", font));
        doc.add(new Paragraph("Дата: " + LocalDate.now(), font));

        PdfPTable table = new PdfPTable(5);
        table.addCell("Препарат");
        table.addCell("Партия");
        table.addCell("Годен до");
        table.addCell("Кол-во");
        table.addCell("Резерв");

        for (Stock s : stockRepository.findWithBatchAndNomenclature(pharmacyId, null)) {
            table.addCell(s.getBatch().getNomenclature().getBrandName());
            table.addCell(s.getBatch().getBatchNumber());
            table.addCell(String.valueOf(s.getBatch().getExpiryDate()));
            table.addCell(String.valueOf(s.getQuantity()));
            table.addCell(String.valueOf(s.getReserved()));
        }
        doc.add(table);
        doc.close();

        RequestReport rr = new RequestReport();
        rr.setReportType("STOCK");
        rr.setFilePath(file.toString());
        rr.setUser(user);
        rr.setFilters(objectMapper.writeValueAsString(Map.of("pharmacyId", pharmacyId)));
        return requestReportRepository.save(rr);
    }

    public RequestReport generateSalesPdf(Long pharmacyId, User user, Instant from, Instant to) throws Exception {
        if (pharmacyId == null) {
            throw new BusinessException("Не выбрана аптека для отчёта по продажам");
        }
        Path dir = Path.of("uploads", "reports");
        Files.createDirectories(dir);
        String fileName = "sales-" + pharmacyId + "-" + System.currentTimeMillis() + ".pdf";
        Path file = dir.resolve(fileName);

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(file.toFile()));
        doc.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        doc.add(new Paragraph("Отчёт по продажам", font));
        doc.add(new Paragraph("Период: " + format(from) + " — " + format(to), font));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Чек");
        table.addCell("Дата");
        table.addCell("Сумма");
        table.addCell("Оплата");

        for (Cheque c : chequeService.salesForPeriod(pharmacyId, from, to)) {
            table.addCell(c.getNumberCheque());
            table.addCell(c.getCreatedAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE));
            table.addCell(String.valueOf(c.getTotalAmount()));
            table.addCell(String.valueOf(c.getPaymentMethod()));
        }
        doc.add(table);
        doc.close();

        RequestReport rr = new RequestReport();
        rr.setReportType("SALES");
        rr.setFilePath(file.toString());
        rr.setUser(user);
        rr.setFilters(objectMapper.writeValueAsString(Map.of("pharmacyId", pharmacyId, "from", from.toString(), "to", to.toString())));
        return requestReportRepository.save(rr);
    }

    public List<RequestReport> history(Long userId) {
        return requestReportRepository.findByUserIdOrderByGeneratedAtDesc(userId);
    }

    private String format(Instant i) {
        return i.atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }
}
