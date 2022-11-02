package com.example.hello.controller;


import com.example.hello.dao.HospitalDao;
import com.example.hello.domain.Hospital;
import com.example.hello.domain.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private HospitalDao hospitalDao;

    public HospitalController(HospitalDao hospitalDao) {
        this.hospitalDao = hospitalDao;
    }

    @GetMapping("/id/{id}")
    public Hospital getHospital(@RequestParam int id){
        return hospitalDao.findById(id);
    }
}
