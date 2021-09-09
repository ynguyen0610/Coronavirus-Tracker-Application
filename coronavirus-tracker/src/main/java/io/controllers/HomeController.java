package io.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import io.services.CoronaVirusDataService;

@Controller // or @RestController
// Render HTML UI
public class HomeController {
    
    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("locationStats", coronaVirusDataService.getAllStats());
        return "home"; 
        /* This will link to the home.html file
        This works because in the pom.xml file, we use the thymeleaf dependency
        */
    }
}
