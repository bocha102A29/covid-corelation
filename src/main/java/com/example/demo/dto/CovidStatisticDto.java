package com.example.demo.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
public class CovidStatisticDto {

    // continentMap is a map of continents where the key is continent name
    // Internal map is a map of countries where the key is country name
    Map<String, Map<String, InfoDto>> continentMap;

    @Data
    @Builder
    public static class InfoDto {
        double deathPercentage;
        double vaccinatedPercentage;
    }
}
