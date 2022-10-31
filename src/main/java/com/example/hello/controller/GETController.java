package com.example.hello.controller;

import com.example.hello.domain.dto.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/get-api")
@Slf4j
public class GETController {

    @GetMapping(value = "/hello")
    public String hello() {
        log.info("hello로 요청이 들어왔습니다.");
        return "Hello World";
    }

    @GetMapping(value = "/name")
    public String getName() {
        log.info("getName로 요청이 들어왔습니다.");
        return "YeonJae";
    }

    //http://localhost:8080/api/v1/get-api/variable1/1
    @GetMapping(value = "/variable1/{variable}")
    public String getVariable1(@PathVariable String variable) {
        log.info("getVariable1로 요청이 들어왔습니다. variable: {}",variable);
        return variable;
    }

    //http://localhost:8080/api/v1/get-api/request1?name=YeonJae&email=70003738@gmail.com&organization=멋사
    @GetMapping(value = "/request1")
    public String getRequestParam1(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String organization) {
        return name + " " + email + " " + organization;
    }

    //http://localhost:8080/api/v1/get-api/request1?name=YeonJae&email=70003738@gmail.com&organization=멋사
    @GetMapping(value = "/request2")
    public String getVariable2(@RequestParam Map<String, String> param) {
        param.entrySet().forEach((map) -> {
            System.out.printf("key:%s value:%s\n", map.getKey(), map.getValue());
        });
        return "request2가 호출 완료 되었습니다";
    }

    @GetMapping(value = "/request3")
    public String getVariable3(MemberDto memberDto) {
        return memberDto.toString();
    }
}
