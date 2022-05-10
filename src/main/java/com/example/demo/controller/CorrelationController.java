package com.example.demo.controller;

import com.example.demo.service.CorrelationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;

@RestController
@AllArgsConstructor
public class CorrelationController {

    private CorrelationService correlationService;

    @GetMapping("/correlation")
    Double getCorrelation(
            @RequestParam(name = "continent", required = false)
            @Pattern(regexp = "/\\b(?:Africa|Antarctica|Asia|Oceania|Europe|North America|South America)\\b/gi")
            String continent) {
        return continent == null ?
                correlationService.getCorrelation() :
                correlationService.getCorrelationByContinent(continent.toUpperCase());
    }
}
