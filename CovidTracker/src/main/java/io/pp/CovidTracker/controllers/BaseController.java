package io.pp.CovidTracker.controllers;


import io.pp.CovidTracker.models.LocationStats;
import io.pp.CovidTracker.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BaseController {
    @Autowired
    CovidDataService covidService;
    @GetMapping("/")
    public String base(Model model) {
        List<LocationStats> allStats = covidService.getAllStats();
        int totalCases = allStats.stream().mapToInt(stat -> stat.getLatestCases()).sum();
        int totalGrowth = allStats.stream().mapToInt(stat -> stat.getDiff()).sum();
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalCases);
        model.addAttribute("TotalGrowth",totalGrowth);


        return "homepage";
    }
}
