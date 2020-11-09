package io.pp.CovidTracker.services;

import io.pp.CovidTracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CovidDataService {

    private static String Covid_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    private List<LocationStats> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Covid_URL)).build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);


        for (CSVRecord record : records) {
            LocationStats locStat = new LocationStats();
            locStat.setState(record.get("Province/State"));
            locStat.setCountry(record.get("Country/Region"));
            System.out.println(locStat.getCountry());
            int todayCases = Integer.parseInt(record.get(record.size()-1));
            int preCases = Integer.parseInt(record.get(record.size()-2));
            locStat.setLatestCases(todayCases);
            locStat.setDiff(todayCases-preCases);

            newStats.add(locStat);

        }
        this.allStats = newStats;

    }
}
