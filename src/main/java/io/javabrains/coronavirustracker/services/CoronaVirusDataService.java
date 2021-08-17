package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.LocationStats;
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

@Service    //this marks this as a service to spring
public class CoronaVirusDataService {
    //this code will fetch the data from the url when the application loads

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats=new ArrayList<>();

    //we have to create a getter for allStats to use it in the Controller as it is private
    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct  //this tells spring to run this after application starts
    @Scheduled(cron="* * 1 * * *")  //it runs the function at 0th hour every day
    public void fetchVirusData() throws IOException, InterruptedException {

        List<LocationStats> newStats=new ArrayList<>();// we fill the latest data from url in this
        //and then copy it in allStates hence allStats can be accessed even if we are calling latest data

        HttpClient client=HttpClient.newHttpClient();

        //creating request to the server
        HttpRequest request=HttpRequest.newBuilder().
                uri(URI.create(VIRUS_DATA_URL))
                .build();

        //getting response
        HttpResponse<String> httpResponse=client
                .send(request, HttpResponse.BodyHandlers.ofString());

        //reader to read the string returned by the request
        StringReader csvBodyReader= new StringReader(httpResponse.body());

        //iterating over the response
        String prev="";
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();   //creating an individual instance of location
            locationStat.setState(record.get("Province/State"));
            locationStat.setState(record.get("Country/Region"));
            int latestCases=Integer.parseInt(record.get(record.size()-1));
            int prevDayCases=Integer.parseInt(record.get(record.size()-2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases-prevDayCases);
            if(prev.equals(locationStat.getState())){
                newStats.remove(newStats.size()-1);
            }
            newStats.add(locationStat);
            prev=locationStat.getState();
        }

        //setting allStat to newStat
        this.allStats=newStats;
    }
}
