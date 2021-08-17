package io.javabrains.coronavirustracker.controllers;
//used to render the data in html ui(webpages)

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    //we can auto wire the Service to our Controller to use it
    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    //this means it responds to a template named "home"
    @GetMapping("/")    //mapping it to home template
    public String home(Model model)
    {
        //summing the total cases in the world to display at the end as an attribute
        List<LocationStats> allStats=coronaVirusDataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();

        //we can pass an attribute and use it in the html file
        model.addAttribute("locationStats", coronaVirusDataService.getAllStats());
        model.addAttribute("totalReportedCases", totalReportedCases);

        return "home";  //home.html is rendered
    }

}
