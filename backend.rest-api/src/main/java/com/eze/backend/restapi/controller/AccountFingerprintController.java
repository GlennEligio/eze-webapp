package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.AccountFingerprint;
import com.eze.backend.restapi.service.AccountFingerprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Implement websocket alongside biometric validation (DFPF api) to properly validate fingerprint
@RestController
@RequestMapping("/api/v1")
public class AccountFingerprintController {

    @Autowired
    private AccountFingerprintService service;

    @GetMapping("/accountFingerprints")
    public ResponseEntity<List<AccountFingerprint>> getAccountFingerprints() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/accountFingerprints/{accountCode}")
    public ResponseEntity<AccountFingerprint> getAccountFingerprint(@PathVariable("accountCode") String accountCode) {
        return ResponseEntity.ok(service.get(accountCode));
    }

    @PostMapping("/accountFingerprints")
    public ResponseEntity<AccountFingerprint> createAccountFingerprint(@RequestBody AccountFingerprint af) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(af));
    }

    @PutMapping("/accountFingerprints/{accountCode}")
    public ResponseEntity<AccountFingerprint> updateAccountFingerprint(@RequestBody AccountFingerprint af,
                                                                       @PathVariable("accountCode") String accountCode) {
        return ResponseEntity.ok(service.update(af, accountCode));
    }

    @DeleteMapping("/accountFingerprints/{accountCode}")
    public ResponseEntity<Object> deleteAccountFingerprint(@PathVariable("accountCode") String accountCode) {
        service.delete(accountCode);
        return ResponseEntity.ok().build();
    }
}
