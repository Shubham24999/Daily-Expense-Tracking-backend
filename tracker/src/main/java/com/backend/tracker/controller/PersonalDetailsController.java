package com.backend.tracker.controller;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import com.backend.tracker.entity.ExpenseDetails;
import com.backend.tracker.helper.ExcelExportUtils;
import com.backend.tracker.helper.PdfExportUtil;
import com.backend.tracker.service.ExpenseService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.backend.tracker.entity.Users;
import com.backend.tracker.helper.RequestResponse;
import com.backend.tracker.model.UserSignUpModel;
import com.backend.tracker.repository.UsersRepository;
import com.backend.tracker.service.PersonalDetailsService;

@RestController
@RequestMapping("/api/profile")
public class PersonalDetailsController {

    private static final Logger logger = LogManager.getLogger(PersonalDetailsController.class);

    @Autowired
    private PersonalDetailsService personalDetailsService;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/")
    public ResponseEntity<RequestResponse> getProfileDetails(Principal principal) {

        String email = principal.getName();

        RequestResponse response = personalDetailsService.getProfileDetails(email);

        if (response.getStatus().equalsIgnoreCase("OK")) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<RequestResponse> updateProfile(Principal principal,
            @RequestBody UserSignUpModel userData) {
        String email = principal.getName();
        RequestResponse response = personalDetailsService.updateProfileDetails(email, userData);

        if (response.getStatus().equalsIgnoreCase("OK")) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/expense/get")
    public ResponseEntity<RequestResponse> getExpenseDetails(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        RequestResponse response = expenseService.getExpenseDetails(userId, page, size, fromDate, toDate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> exportExcel(Principal principal) throws IOException {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();
        List<ExpenseDetails> expenses = expenseService.getAllExpenses(userId);
        ByteArrayInputStream in = ExcelExportUtils.dataToExcel(expenses);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=expenses.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }

    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> exportPdf(Principal principal) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ExpenseDetails> expenses = expenseService.getAllExpenses(user.getId());
        ByteArrayInputStream pdfStream = PdfExportUtil.generatePdf(expenses);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=expenses.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

}
