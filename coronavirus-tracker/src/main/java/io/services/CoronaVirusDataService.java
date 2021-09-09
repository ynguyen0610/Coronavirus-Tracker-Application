package io.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.models.LocationStats;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;
import java.net.URI;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.StringReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;

@Service
public class CoronaVirusDataService extends Object {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    
    private List<LocationStats> allStats = new ArrayList<>();
    
    public List<LocationStats> getAllStats() {
        return allStats;
    }
    
    @PostConstruct 
    // Teling Spring that when it constructs an instance of this class, after it's done, it should execute the method
    @Scheduled(cron = "* * 1 * * *") // Execute this method once everyday
    // Tell the application to constantly update the data

    public void fetchVirusData() throws IOException, InterruptedException { // Handle exceptions when the client send method fails
        List<LocationStats> newStats = new ArrayList<>(); 
        /* Create new instance while keeping the old allStats because of concurrency reasons 
        We don't want people to receive error messages while Spring is constructing this class
        After we are done constructing, we will populate the allStats with newStats
        */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder() // Create a requesto to building a body pattern
                .uri(URI.create(VIRUS_DATA_URL)) // Convert String to URI // or URI to String?
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); // Take the body and return it as a String

        // This method is just sitting here, need to make it a Spring server and tell Spring that this method exists and it needs to be executed then the application starts
        
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            System.out.println(locationStat);
            newStats.add(locationStat);
        } 
        this.allStats = newStats; 
        
        /* By this stage, what is happening is: 
        - Data is converted to be String
        - A StringReader will read through the String
        - Parse the CSV through an open-source library
        - Loop through the data
        */
   }
}