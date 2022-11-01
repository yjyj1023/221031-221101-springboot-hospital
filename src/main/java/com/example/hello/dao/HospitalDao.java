package com.example.hello.dao;

import com.example.hello.domain.Hospital;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class HospitalDao {

    private final JdbcTemplate jdbcTemplate;

    public HospitalDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //List<Hospital> - 11만건의 데이터를 하나씩(Hospital)꺼내서 넣기
    public void add(Hospital hospital){
        String sql = "INSERT INTO hospitals";
    }
}
