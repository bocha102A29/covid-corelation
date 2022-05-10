package com.example.demo.service;

import com.example.demo.dto.CovidStatisticDto;
import com.example.demo.client.MMediaGroupCovidAPIClient;
import lombok.SneakyThrows;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CorrelationServiceImpl implements CorrelationService {

    Logger logger = LoggerFactory.getLogger(MMediaGroupCovidAPIClient.class);
    @Autowired
    private MMediaGroupCovidAPIClient client;

    @SneakyThrows
    @Override
    public double getCorrelation() {
        CovidStatisticDto statisticDto = client.getStatistic();
        return getPearsonCorrelation(getCasesVaccinatedListsPairByWorld(statisticDto.getContinentMap()));
    }

    @SneakyThrows
    @Override
    public double getCorrelationByContinent(String continent) {
        CovidStatisticDto statisticDto = client.getStatistic();
        return getPearsonCorrelation(getCasesVaccinatedListsPairByContinent(statisticDto.getContinentMap().get(continent)));
    }

    private double getPearsonCorrelation(Pair<List<Double>, List<Double>> pair) {
        PearsonsCorrelation pc = new PearsonsCorrelation();
        return pc.correlation(listToPrimitiveArray(pair.getFirst()), listToPrimitiveArray(pair.getSecond()));
    }

    public Pair<List<Double>, List<Double>> getCasesVaccinatedListsPairByWorld(Map<String, Map<String, CovidStatisticDto.InfoDto>> continentMap) {
        List<Double> cases = new ArrayList<>();
        List<Double> vaccinated = new ArrayList<>();

        continentMap.forEach((continentName, countryMap) -> {
            Pair<List<Double>, List<Double>> pair = getCasesVaccinatedListsPairByContinent(countryMap);
            cases.addAll(pair.getFirst());
            vaccinated.addAll(pair.getSecond());
        });

        return new Pair<>(cases, vaccinated);
    }

    public Pair<List<Double>, List<Double>> getCasesVaccinatedListsPairByContinent(Map<String, CovidStatisticDto.InfoDto> countryMap) {
        List<Double> cases = new ArrayList<>();
        List<Double> vaccinated = new ArrayList<>();

        countryMap.forEach((countryName, info) -> {
            if (info.getDeathPercentage() != 0 & info.getVaccinatedPercentage() != 0) {
                cases.add(info.getDeathPercentage());
                vaccinated.add(info.getVaccinatedPercentage());
            } else {
                logger.info(String.format("No data for country: %s deaths: %s vaccinated: %s", countryName, info.getDeathPercentage(), info.getVaccinatedPercentage()));
            }

        });

        return new Pair<>(cases, vaccinated);
    }

    private double[] listToPrimitiveArray(List<Double> list) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;

    }
}
