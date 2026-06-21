package com.example.pharmacy.Configuration;

import com.example.pharmacy.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + fallbackUrl(request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    public String handleBinding(Exception ex, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", "Проверьте корректность заполнения полей формы");
        return "redirect:" + fallbackUrl(request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request, RedirectAttributes ra) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/batches/create")) {
            ra.addFlashAttribute("errorMessage", "Партия с таким номером уже есть в системе");
            return "redirect:/batches/create";
        }
        ra.addFlashAttribute("errorMessage", "Операция нарушает ограничения базы данных");
        return "redirect:" + fallbackUrl(request);
    }

    private static String fallbackUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/admin/pharmacies")) {
            return "/admin/pharmacies";
        }
        if (uri != null && uri.startsWith("/batches/create")) {
            return "/batches/create";
        }
        if (uri != null && uri.startsWith("/batches")) {
            return "/batches";
        }
        if (uri != null && uri.startsWith("/cashier")) {
            return "/cashier";
        }
        if (uri != null && uri.startsWith("/nomenclature/categories")) {
            return "/nomenclature/categories";
        }
        if (uri != null && uri.startsWith("/nomenclature")) {
            return "/nomenclature";
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return referer;
        }
        return "/dashboard";
    }
}
