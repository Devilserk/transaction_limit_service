package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.limit.CreateLimitDto;
import org.example.dto.limit.ResponseLimitDto;
import org.example.service.LimitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/limits")
public class LimitController {
    private final LimitService limitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseLimitDto createLimit(@RequestBody @Valid CreateLimitDto createLimitDto) {
        return limitService.createLimit(createLimitDto);
    }

    @GetMapping("/{accountNumber}/all")
    public List<ResponseLimitDto> getLimitsByAccount(@PathVariable Long accountNumber) {
        return limitService.getLimitsByAccount(accountNumber);
    }
}
