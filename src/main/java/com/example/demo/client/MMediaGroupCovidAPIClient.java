package com.example.demo.client;

import com.example.demo.dto.CovidStatisticDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.mapper.MMediaGroupCovidMapper.enrichWithVaccinatedData;
import static com.example.demo.mapper.MMediaGroupCovidMapper.fillCasesData;

@Component
public class MMediaGroupCovidAPIClient {

    Logger logger = LoggerFactory.getLogger(MMediaGroupCovidAPIClient.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String COVID_API_URL_BASE = "https://covid-api.mmediagroup.fr/v1";

    public String getCasesJsonResponse() {
        logger.info("requesting covid-19 cases data...");
        ResponseEntity<String> response = restTemplate.getForEntity(COVID_API_URL_BASE + "/cases", String.class);
        if (!response.getStatusCode().isError()) {
            return response.getBody();
        }
        throw new RuntimeException(response.getStatusCode().getReasonPhrase());
    }


    public String getVaccinatedJsonResponse() {
        logger.info("requesting covid-19 people vaccinated data...");
        ResponseEntity<String> response = restTemplate.getForEntity(COVID_API_URL_BASE + "/vaccines", String.class);
        if (!response.getStatusCode().isError()) {
            return response.getBody();
        }
        throw new RuntimeException(response.getStatusCode().getReasonPhrase());
    }

    @Cacheable("statistics")
    public CovidStatisticDto getStatistic() {
        String casesResultJson = getCasesJsonResponse();
        String vaccinatedResultJson = getVaccinatedJsonResponse();

        return enrichWithVaccinatedData(fillCasesData(casesResultJson), vaccinatedResultJson);
    }
}
