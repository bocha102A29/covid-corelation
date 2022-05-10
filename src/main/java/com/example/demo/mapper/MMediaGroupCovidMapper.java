package com.example.demo.mapper;

import com.example.demo.dto.CovidStatisticDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class MMediaGroupCovidMapper {

    @SneakyThrows
    public static CovidStatisticDto fillCasesData(String jsonCasesData) {

        CovidStatisticDto statisticDto = CovidStatisticDto.builder()
                .continentMap(new HashMap<>())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonCasesData);

        root.elements().forEachRemaining(countryNode -> {
            JsonNode countryGeneralInfo = countryNode.get("All");
            String continent = countryGeneralInfo.get("continent") != null ? countryGeneralInfo.get("continent").asText().toUpperCase() : null;
            String country = countryGeneralInfo.get("country") != null ? countryGeneralInfo.get("country").asText().toUpperCase() : null;

            if (country != null) {
                Map<String, CovidStatisticDto.InfoDto> countryMap = statisticDto.getContinentMap()
                        .computeIfAbsent(continent, k -> new HashMap<>());
                countryMap.put(country, mapCountrySpecificInfo(CovidStatisticDto.InfoDto.builder().build(), countryGeneralInfo));
            }
        });
        return statisticDto;
    }

    @SneakyThrows
    public static CovidStatisticDto enrichWithVaccinatedData(CovidStatisticDto statisticDto, String jsonVaccinatedData) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonVaccinatedData);
        root.elements().forEachRemaining(countryNode -> {
            JsonNode countryGeneralInfo = countryNode.get("All");
            String continent = countryGeneralInfo.get("continent") != null ? countryGeneralInfo.get("continent").asText().toUpperCase() : null;
            String country = countryGeneralInfo.get("country") != null ? countryGeneralInfo.get("country").asText().toUpperCase() : null;

            if (country != null) {
                Map<String, CovidStatisticDto.InfoDto> countryMap = statisticDto.getContinentMap().get(continent);
                countryMap.put(country, mapCountrySpecificInfo(countryMap.get(country), countryGeneralInfo));
            }
        });
        return statisticDto;
    }

    public static CovidStatisticDto.InfoDto mapCountrySpecificInfo(CovidStatisticDto.InfoDto infoDto, JsonNode countryGeneralInfo) {
        JsonNode population = countryGeneralInfo.get("population");
        JsonNode deaths = countryGeneralInfo.get("deaths");
        JsonNode vaccinated = countryGeneralInfo.get("people_vaccinated");

        infoDto.setDeathPercentage(deaths != null ? calculatePercentage(deaths.asInt(), population.asInt()) : infoDto.getDeathPercentage());
        infoDto.setVaccinatedPercentage(vaccinated != null ? calculatePercentage(vaccinated.asInt(), population.asInt()) : infoDto.getVaccinatedPercentage());
        return infoDto;
    }

    private static double calculatePercentage(int obtained, int total) {
        if (total == 0) {
            return 0;
        }
        return (obtained * 100.0f) / total;
    }
}
