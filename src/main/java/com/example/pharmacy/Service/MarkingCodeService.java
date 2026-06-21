package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.MarkingCodeRepository;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MarkingCodeService {

    private static final Pattern GTIN_PATTERN = Pattern.compile("(01)(\\d{14})");
    private static final Pattern SERIAL_PATTERN = Pattern.compile("(21)([\\w\\d]{1,20})");
    private static final Pattern EXPIRY_PATTERN = Pattern.compile("(17)(\\d{6})");

    private final MarkingCodeRepository markingCodeRepository;

    public MarkingCodeService(MarkingCodeRepository markingCodeRepository) {
        this.markingCodeRepository = markingCodeRepository;
    }

    public ParsedMarking parseCode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException("Код маркировки пуст");
        }
        String code = raw.trim();
        ParsedMarking parsed = new ParsedMarking();
        parsed.rawCode = code;

        Matcher gtin = GTIN_PATTERN.matcher(code);
        if (gtin.find()) {
            parsed.gtin = gtin.group(2);
        }
        Matcher serial = SERIAL_PATTERN.matcher(code);
        if (serial.find()) {
            parsed.serial = serial.group(2);
        }
        Matcher exp = EXPIRY_PATTERN.matcher(code);
        if (exp.find()) {
            String yymmdd = exp.group(2);
            parsed.expiryDate = LocalDate.parse("20" + yymmdd.substring(0, 2) + "-"
                    + yymmdd.substring(2, 4) + "-" + yymmdd.substring(4, 6),
                    DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (parsed.gtin == null || parsed.serial == null) {
            if (code.length() >= 20) {
                parsed.gtin = code.substring(0, 14);
                parsed.serial = code.substring(14, Math.min(code.length(), 25));
            } else {
                throw new BusinessException("Некорректный формат Data Matrix");
            }
        }
        return parsed;
    }

    public void validateReceiptCodes(List<String> rawCodes, boolean markingRequired, int qtyReceived) {
        List<String> codes = normalizeCodes(rawCodes);
        if (markingRequired) {
            if (qtyReceived <= 0) {
                throw new BusinessException("Укажите количество упаковок в партии");
            }
            if (codes.size() != qtyReceived) {
                throw new BusinessException("Отсканируйте все коды: " + codes.size() + " из " + qtyReceived);
            }
        } else if (!codes.isEmpty()) {
            throw new BusinessException("Коды маркировки указываются только для маркированных товаров");
        }
    }

    @Transactional
    public void registerAllOnReceipt(Batch batch, List<String> rawCodes, boolean markingRequired, int qtyReceived) {
        validateReceiptCodes(rawCodes, markingRequired, qtyReceived);
        for (String code : normalizeCodes(rawCodes)) {
            registerOnReceipt(code, batch);
        }
    }

    @Transactional
    public MarkingCode registerOnReceipt(String raw, Batch batch) {
        ParsedMarking p = parseCode(raw);
        if (markingCodeRepository.existsByCode(p.rawCode)) {
            throw new BusinessException("Код маркировки уже зарегистрирован");
        }
        MarkingCode mc = new MarkingCode();
        mc.setCode(p.rawCode);
        mc.setGtin(p.gtin);
        mc.setSerialNumber(p.serial);
        mc.setExpiryDate(p.expiryDate != null ? p.expiryDate : batch.getExpiryDate());
        mc.setStatus(MarkingCodeStatus.IN_STOCK);
        mc.setMdlpStatus("REGISTERED");
        mc.setBatch(batch);
        return markingCodeRepository.save(mc);
    }

    @Transactional
    public MarkingCode bindToSale(String raw, ChequePosition position) {
        ParsedMarking p = parseCode(raw);
        MarkingCode mc = markingCodeRepository.findByCode(p.rawCode)
                .orElseThrow(() -> new BusinessException("Код не найден в системе"));
        if (!MarkingCodeStatus.IN_STOCK.equals(mc.getStatus())
                && !MarkingCodeStatus.RESERVED.equals(mc.getStatus())) {
            throw new BusinessException("Код не доступен для продажи: " + mc.getStatus());
        }
        if (position.getBatch() != null) {
            if (mc.getBatch() == null || !mc.getBatch().getId().equals(position.getBatch().getId())) {
                throw new BusinessException("Код маркировки принадлежит другой партии");
            }
        }
        mc.setStatus(MarkingCodeStatus.DISPOSED);
        mc.setWithdrawnAt(Instant.now());
        mc.setMdlpStatus("PENDING_SEND");
        mc.setChequePosition(position);
        mc.setDisposalDocumentId(position.getCheque() != null ? position.getCheque().getId() : null);
        return markingCodeRepository.save(mc);
    }

    @Transactional
    public void returnToStock(ChequePosition position) {
        if (position.getMarkingCode() != null) {
            MarkingCode mc = position.getMarkingCode();
            mc.setStatus(MarkingCodeStatus.RETURNED);
            mc.setMdlpStatus("RETURNED");
            mc.setChequePosition(null);
            mc.setWithdrawnAt(null);
            mc.setDisposalDocumentId(null);
            markingCodeRepository.save(mc);
        }
    }

    @Transactional
    public int sendPendingToMdlp() {
        List<MarkingCode> pending = markingCodeRepository.findByMdlpStatus("PENDING_SEND");
        for (MarkingCode mc : pending) {
            mc.setMdlpStatus("SENT");
        }
        markingCodeRepository.saveAll(pending);
        return pending.size();
    }

    public List<MarkingCode> findAllByStatus(String mdlpStatus) {
        return markingCodeRepository.findByMdlpStatus(mdlpStatus);
    }

    public List<MarkingCode> listJournal() {
        return markingCodeRepository.findAll();
    }

    private List<String> normalizeCodes(List<String> rawCodes) {
        if (rawCodes == null || rawCodes.isEmpty()) {
            return List.of();
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String raw : rawCodes) {
            if (raw != null && !raw.isBlank()) {
                unique.add(raw.trim());
            }
        }
        return new ArrayList<>(unique);
    }

    public static class ParsedMarking {
        public String rawCode;
        public String gtin;
        public String serial;
        public LocalDate expiryDate;
    }
}
