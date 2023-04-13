package com.itmsd.medical.controllers;

import com.itmsd.medical.repositories.PlanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("plan")
public class PlanController {
    private PlanRepository planRepository;


    @Autowired
    public PlanController(PlanRepository planRepository){
        this.planRepository=planRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPlans(){
        return new ResponseEntity<>(planRepository.findAll(), HttpStatus.OK);
    }

}
