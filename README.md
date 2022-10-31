# 221026-221027-springboot
# 1. 221026
## 1.1 시작하기
1) [Spring Initializr](https://start.spring.io/)에 들어간다.
   - 아래 그림처럼 설정한 후 하단의 Generate를 클릭해 프로젝트를 생성한다.
 ![](https://velog.velcdn.com/images/lyj1023/post/e6575d57-85ed-4e3e-9b0a-a7240de732bc/image.png)
 
2) IntelliJ에서 프로젝트를 열면된다.

## 1.2 Jar파일 생성하기

## 1.3 controller 생성하기

## 1.4 Tablend API Tester 추가하기

## 1.5 @RequestMapping

## 1.6 @GetMapping

## 1.7 variable 추가하기

# 2. 221027
## 2-1. API 문서란?
- API쓰는법을 써놓은(기술 해놓은) 문서이다.
- 아래 그림은 깃허브의 API 문서이다.
- 링크: [GitHub API 문서](https://docs.github.com/en/rest)
![](https://velog.velcdn.com/images/lyj1023/post/335fa1de-1476-440f-bca7-ad4856ba5b20/image.png)

## 2-2. Path parameter
- 주소를 통해 값을 넘길 때 사용한다.
- 주로 id등 꼭 넣어주어야 하는 parameter는 Path parameter로 만드는 경우가 많다.
![](https://velog.velcdn.com/images/lyj1023/post/9d41875c-df53-4ec1-8b38-ea7738c48995/image.png)

- 구현
```java
//http://localhost:8080/api/v1/get-api/variable1/1
@GetMapping(value = "/variable1/{variable}")
public String getVariable1(@PathVariable String variable){
    return variable;
}
```
![](https://velog.velcdn.com/images/lyj1023/post/c42c8130-a501-4dec-bbc5-6c23a17f0380/image.png)



## 2-3. Query parameter
- 주소에 ?표 뒤로 전달하는 parameter이다.
- 있어도 되고 없어도 되는 parameter를 주로 Query parameters로 만드는 경우가 많다.
- 첫번째만 ?뒤로 연결하고 두번째 parameter부터는 &으로 연결한다.
![](https://velog.velcdn.com/images/lyj1023/post/6bb0ec88-0d76-48d5-b011-6cc040270542/image.png)

### 2-3-1 기본 Query parameter 구현과 결과
```java
//http://localhost:8080/api/v1/get-api/request1?name=YeonJae&email=70003738@gmail.com&organization=멋사
@GetMapping(value = "/request1")
public String getRequestParam1(
        //RequestParam으로 3개의 값 받기
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String organization){
    return name+" "+email+" "+organization;
}
```
![](https://velog.velcdn.com/images/lyj1023/post/a5b2aad3-30dc-4a33-a6ab-9b60e7417ae7/image.png)

### 2-3-2 Map을 활용한 Query parameter 구현과 결과
```java
@GetMapping(value = "/request2")
public String getVariable2(@RequestParam Map<String, String> param) {
    param.entrySet().forEach((map) -> {
        System.out.printf("key:%s value:%s\n", map.getKey(), map.getValue());
    });
    return "request2가 호출 완료 되었습니다";
}
```
![](https://velog.velcdn.com/images/lyj1023/post/33ec771e-512e-4a01-b219-0e73eebfa3a5/image.png)![](https://velog.velcdn.com/images/lyj1023/post/e44d3fed-d716-4b7c-bbc9-bd3b68542292/image.png)

### 2-3-3 DTO(Data Transfer Object)활용하기
- DTO란? 다른 레이어 간의 데이터 교환에 활용된다. 간단하게 설명하자면 각 클래스 및 인터페이스를 호출하면서 전달하는 매개변수로 사용되는 데이터 객체이다.
- 따라서 DTO는 데이터를 교환하는 용도로만 사용하는 객체이기 때문에 별도의 로직이 포함되지 않는다.
- 비슷한 것으로 VO(Value Object)가 있는데 VO는 데이터 그 자체로 의미가 있는 객체를 의마한다. 가장 특징적인 부분은 읽기전용으로 설계하여 값을 변경할 수 없게 만들어 데이터의 신뢰성을 유지한다.

`[MemberDto.java]`
```java
package com.example.hello.domain.dto;

public class MemberDto {

    private String name;
    private String email;
    private String organization;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "MemberDto{" + "name='" + name + '\''+", email='" + email+ '\'' +
                ", organization='" + organization + '\''+'}';
    }
}
```
- getter/setter 메서드를 구현했지만 setter는 현재 잘 사용하지 않는 추세이다.

`[HelloController.java]`
```java
@GetMapping(value = "/request3")
public String getVariable3(MemberDto memberDto) {
    return memberDto.toString();
}
```
![](https://velog.velcdn.com/images/lyj1023/post/ad524218-b4fc-4461-b62c-f1e17e629795/image.png)

## 2-4. POST API 만들기
- http method에는 get, post, delete, put등 여러가지 종류가 있다.
- 이전까지 사용했던 GET API에서는 URL의 경로나 파라미터에 변수를 넣어 요청을 보냈지만 POST API에서는 저장하고자 하는 리소스나 값을 HTTP 바디에 담아서 서버에 전달한다.

>- GET과 Post 차이
>   - 가장 큰 차이점은 HTTP 요청을 할 때 파라미터를 path나 쿼리파라미터를 쓰지 않고 Request >Body에 보낸다.
>   - 주로 회원가입 등 Http전송 중 노출되면 안되는 정보를 보낼 때 Post를 사용한다.
>   - GET은 조회할때 사용한다.
>   - POST는 주로 Insert할때 하용한다.

### 2-4-1 @RequestMapping으로 구현하기
`[POSTController.java]`
```java
package com.example.hello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post-api")
public class POSTController {

    @RequestMapping(value = "/domain", method = RequestMethod.POST)
    public String postExample() {
        return "Hello Post API";
    }
}
```
![](https://velog.velcdn.com/images/lyj1023/post/8a261219-dbf7-4b14-b338-77bf50722884/image.png)
- POST 요청에서는 위 그림처럼 HTTP Body에 값을 넣어 전송한다. Body 영역에 작성되는 값은 일정한 형태를 취하는데 일반적으로 JSON형식으로 전송된다.
- 위의 코드에선 들어온 요청을 처리하는 부분이 없어서 `Hello Post API`만 출력되고 있다.

>- JSON?
>   - JavaScript Object Notation 의 약자로 Java에 Class가 있다면 JavaScript에는 JSON이 있다.
> ![](https://velog.velcdn.com/images/lyj1023/post/39ec6692-7548-4213-9132-74a5d812cda1/image.png)
>   - 위 그림을 보면 java의 map처럼 쌍을 이루어 선언하여 사용된다.

### 2-4-2 @RequestBody를 활용한 POST메서드 구현
- POST에서는 @RequestBody를 주로 사용한다. json(등)형식으로 들어온 데이터를 사용할 수 있게 해준다.

>- @RequestBody Map<String, Object> postData?
>   - 아래 Json코드와 같이 어떤 타입으로 데이터가 들어올지 Sever입장에서는 예측할 수 없기 때문에 Value를 Object로 처리한다. 
>   - Object는 모든 자바 Class의 최상위 Class로 무엇이든 올 수 있다.
>```java
>{
>  "name":"YeonJae",
>  "email":"70003738@naver.com",
>  "age":27
>}
>```

#### 2-4-2-1 Map을 이용해서 POST 구현
```java
@PostMapping("/member")
public String postMember(@RequestBody Map<String, Object> postData){
    StringBuilder sb = new StringBuilder();

    postData.entrySet().forEach(map -> {
        sb.append(map.getKey()+" : " + map.getValue()+"\n");
    });

    return sb.toString();
}
```
![](https://velog.velcdn.com/images/lyj1023/post/2f013084-ba79-492d-8df6-bbbb570295c4/image.png)

#### 2-4-2-2 MemberDto를 이용해서 POST 구현
```java
@PostMapping("/member2")
public String postMember2(@RequestBody MemberDto memberDto){
    return memberDto.toString();
}
```
![](https://velog.velcdn.com/images/lyj1023/post/51e24714-bd9a-4313-ac20-fa9614a968e5/image.png)


## 2-5. PUT API 만들기
- POST와 비슷하지만 주로 Update하는데 사용한다.
- POST와 마찬가지로 @RequestBody를 이용한다.

### 2-5-1 @ResponseEntity를 활용한 PUT 메서드 구현
- @ResponseEntity는 get, post, put, delete, patch 다른 메소드에도 다 쓸 수 있다.
- Http Response Header와 Body를 구성하기 쉽게 해준다.

```java
@PUTMapping("/member3")
public ResponseEntity<MemberDto> putMember(@RequestBody MemberDto memberDto) {
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(memberDto);
}
```
![](https://velog.velcdn.com/images/lyj1023/post/4402f6a8-d452-4d5d-a7f0-3a3ca45ff2e9/image.png)
- POST방식과는 다르게 response형식이 application/json형이다.(POST는 text/plain형식이였음)

## 2-6. Swagger 추가하기
- Swagger? 일종의 API 문서 자동 생성기이다.
- 사용 방법은 다음과 같다.

1) 의존성 추가
- 아래 내용을 porm.xml에 추가한다.
```java
// swagger dependency
implementation "io.springfox:springfox-boot-starter:3.0.0"
implementation "io.springfox:springfox-swagger-ui:3.0.0"
```
![](https://velog.velcdn.com/images/lyj1023/post/5487c500-30be-443b-9696-30e39bd489ff/image.png)

2) Configuration 클래스 추가
- 아래 내용을 SwaggerConfig.java에 추가한다.(java파일 위치 조심할것)
```java
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
```
![](https://velog.velcdn.com/images/lyj1023/post/411188cd-f7a4-47b4-8f9d-80066f7cbdc4/image.png)

>- 에러 나는 경우  
>`application.properties`를 `application.yml`로 이름을 변경하고 `application.yml`에 아래와 같이 추가
>```java
>spring:
> mvc:
>   pathmatch:
>     matching-strategy: ant_path_matcher
>```

- [Swagger](http://localhost:8080/swagger-ui/)를 들어가 보면 현재 생성된 Controller들을 확인할 수 있다.
- 출처: https://velog.io/@wotj7687/Spring-Boot-Swagger-3.0.0-%EC%A0%81%EC%9A%A9
