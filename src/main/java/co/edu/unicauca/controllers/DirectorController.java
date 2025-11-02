package co.edu.unicauca.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/director")
public class DirectorController {

    @PostMapping("/hola")
    public void hola(){
        System.out.printf("HOLAHOLAHOLA");
    }
}
