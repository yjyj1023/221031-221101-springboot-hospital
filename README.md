# <221031-springboot-hospital>
# 1. SpingBoot
## 1.1 프로젝트 목표
- 목표: fulldata_01_01_02_P_의원 - 전국 병의원 정보 검색 API만들기

1) FileLineParser → 저번에 진행했던 HospitalParser 복습하기  
2) DB select, insert를 jdbcTemplate, Create Table → 복습겸 응용
3) Spring JdbcTemplate → 복습
4) Spring Boot과 JdbcTemplate 연동이 왜 되는 것일까? IoC DI는 어떻게 작동하길래 될까? → 복습겸 응용
5) 빌드 - Maven을 이용한다.
6) 다양한 정보를 조회할 수 있는 API만들기

## 1.2 DB생성
- 아래와 같이 MySQL에 Table을 생성한다.  
![](https://velog.velcdn.com/images/lyj1023/post/5cfdea85-b6af-4577-8eb3-b288bfebe8b8/image.png)

```sql
CREATE TABLE `likelion-db`.`hospitals` (
  `id` INT NOT NULL COMMENT '번호',
  `open_service_name` VARCHAR(10) NULL COMMENT '개방서비스명',
  `open_local_government_cod` INT NOT NULL COMMENT '개방자치단체코드',
  `management_number` VARCHAR(40) NULL COMMENT '관리번호',
  `license_date` DATETIME NULL COMMENT '인허가일자',
  `business_status` TINYINT(2) NULL COMMENT '1: 영업/정상\n2: 휴업\n3: 폐업\n4: 취소/말소영업상태구분\n',
  `business_status_code` TINYINT(2) NULL COMMENT '영업상태코드\n2: 휴업\n3: 폐업\n13: 영업중\n',
  `phone` VARCHAR(20) NULL COMMENT '소재지전화',
  `full_address` VARCHAR(200) NULL COMMENT '소재지전체주소',
  `road_name_address` VARCHAR(200) NULL COMMENT '도로명전체주소',
  `hospital_name` VARCHAR(20) NULL COMMENT '사업장명',
  `business_type` VARCHAR(10) NULL COMMENT '업태구분명',
  `healthcare_provider_count` TINYINT(2) NULL COMMENT '의료인수',
  `patient_room_count` INT NULL COMMENT '입원실수',
  `total_number_of_beds` INT NULL COMMENT '병상수',
  `total_area_size` FLOAT NULL COMMENT '총면적',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `management_number_UNIQUE` (`management_number` ASC) VISIBLE);
```

>- MySql 데이터베이스 컬럼 타입  
컬럼 타입을 세분화 하는 이유: db에 저장공간을 줄이고, 검색 속도를 최적화 하기 위함  
tinyint - 작은 int 범위가 작지만 int에 비해 공간을 덜 차지 한다.  
datetime - where 조건에 날짜 from to  
float – 소수점 int에 넣으면 소수점이 잘리기 때문 int에 비해 공간을 더 차지 한다.  

## 1.3 parser 생성
- `parser` 인터페이스 생성

`[Parser.java]`
```java
package com.example.hello.parser;

public interface Parser<T> {
    T parse(String str);
}
```

- 파일을 불러오는 `ReadLineContext`클래스 생성

`[ReadLineContext.java]`
```java
package com.example.hello.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadLineContext<T> {

    private Parser<T> parser;

    public ReadLineContext(Parser<T> parser) {
        this.parser = parser;
    }

    public List<T> readByLine(String filename) throws IOException {
        // 삽
        List<T> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new FileReader(filename)
        );
        String str;
        while ((str = reader.readLine()) != null) {
            result.add(parser.parse(str));
        }
        reader.close();
        return result;
    }
}
```

- Getter,Setter기능을 추가한 Hospital클래스 생성

`[Hospital.java]`
```java
package com.example.hello.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.StringBufferInputStream;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Hospital {
    private int id;
    private String openServiceName;
    private int openLocalGovernmentCode;
    private String managementNumber;
    private LocalDateTime licenseDate;
    private int businessStatus;
    private int businessStatusCode;
    private String phone;
    private String fullAddress;
    private String roadNameAddress;
    private String hospitalName;
    private String businessTypeName;
    private int healthcareProviderCount;
    private int patientRoomCount;
    private int totalNumberOfBeds;
    private float totalAreaSize;
}
```
> - 사용된 어노테이션  
@AllArgsConstructor: 모든 컬럼을 받는 생성자를 추가해준다.  
@Getter: Getter를 만들어준다.  
@Setter: Setter를 만들어준다.  
@NoArgsConstructor: 아무것도 받지 않는 생성자를 추가해준다.  


- 병원 정보가 파싱될 HospitalParser클래스 생성

`[HospitalParser.java]`
```java
package com.example.hello.parser;

import com.example.hello.domain.Hospital;

import java.time.LocalDateTime;
import java.util.Arrays;

public class HospitalParser implements Parser<Hospital>{
//    1, hospital.getId()); // col:0
//    "의원", hospital.getOpenServiceName());//col:1
//    3620000,hospital.getOpenLocalGovernmentCode()); // col: 3
//    "PHMA119993620020041100004",hospital.getManagementNumber()); // col:4
//    LocalDateTime.of(1999, 6, 12, 0, 0, 0), hospital.getLicenseDate()); //19990612 //col:5
//    1, hospital.getBusinessStatus()); //col:7
//    13, hospital.getBusinessStatusCode());//col:9
//    "062-515-2875", hospital.getPhone());//col:15
//    "광주광역시 북구 풍향동 565번지 4호 3층", hospital.getFullAddress()); //col:18
//    "광주광역시 북구 동문대로 24, 3층 (풍향동)", hospital.getRoadNameAddress());//col:19
//    "효치과의원", hospital.getHospitalName());//col:21
//    "치과의원", hospital.getBusinessTypeName());//col:25
//    1, hospital.getHealthcareProviderCount()); //col:29
//    0, hospital.getPatientRoomCount()); //col:30
//    0, hospital.getTotalNumberOfBeds()); //col:31
//    52.29, hospital.getTotalAreaSize()); //col:32


    @Override
    public Hospital parse(String str) {

    
    }
}
```

## 1.4 테스트 코드 생성
- 병원정보의 제일 첫번째 컬럼을 line1으로 가져와 각각의 컬럼을 제대로 출력하는지 확인하는 테스트를 생성한다.

`[HospitalParserTest.java]`
```java
package com.example.hello.parser;

import com.example.hello.domain.Hospital;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HospitalParserTest {
    String line1 = "\"1\",\"의원\",\"01_01_02_P\",\"3620000\",\"PHMA119993620020041100004\",\"19990612\",\"\",\"01\",\"영업/정상\",\"13\",\"영업중\",\"\",\"\",\"\",\"\",\"062-515-2875\",\"\",\"500881\",\"광주광역시 북구 풍향동 565번지 4호 3층\",\"광주광역시 북구 동문대로 24, 3층 (풍향동)\",\"61205\",\"효치과의원\",\"20211115113642\",\"U\",\"2021-11-17 02:40:00.0\",\"치과의원\",\"192630.735112\",\"185314.617632\",\"치과의원\",\"1\",\"0\",\"0\",\"52.29\",\"401\",\"치과\",\"\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"0\",\"\",";
    @Test
    @DisplayName("csv 1줄을 잘만드는지 test")
    void convertToHospital() {
        HospitalParser hp = new HospitalParser();
        Hospital hospital = hp.parse(line1);
        assertEquals(1, hospital.getId());
        assertEquals("의원", hospital.getOpenServiceName());
        assertEquals(3620000,hospital.getOpenLocalGovernmentCode());
        assertEquals("PHMA119993620020041100004",hospital.getManagementNumber());
        assertEquals(LocalDateTime.of(1999, 6, 12, 0, 0, 0), hospital.getLicenseDate()); //19990612
        assertEquals(1, hospital.getBusinessStatus());
        assertEquals(13, hospital.getBusinessStatusCode());
        assertEquals("062-515-2875", hospital.getPhone());
        assertEquals("광주광역시 북구 풍향동 565번지 4호 3층", hospital.getFullAddress());
        assertEquals("광주광역시 북구 동문대로 24, 3층 (풍향동)", hospital.getRoadNameAddress());
        assertEquals("효치과의원", hospital.getHospitalName());
        assertEquals("치과의원", hospital.getBusinessTypeName());
        assertEquals(1, hospital.getHealthcareProviderCount());
        assertEquals(0, hospital.getPatientRoomCount());
        assertEquals(0, hospital.getTotalNumberOfBeds());
        assertEquals(52.29f, hospital.getTotalAreaSize());
    }

}
```

## 1.5 매핑 Map Reduce(병렬처리 후 합치기)
- 1.3에서 작성했던 HospitalParser에 파싱하는 코드를 추가한다.
- LocalDateTime은 substring을 이용해 year, month, day를 구분해서 파싱 후 값을 넣어준다.

`[HospitalParser.java]`
```java
package com.example.hello.parser;

import com.example.hello.domain.Hospital;

import java.time.LocalDateTime;
import java.util.Arrays;

public class HospitalParser implements Parser<Hospital>{

    @Override
    public Hospital parse(String str) {

        String[] row = str.split("\",\"");
        System.out.println(Arrays.toString(row));

        Hospital hospital = new Hospital();
        hospital.setId(Integer.parseInt(row[0].replace("\"","")));
        hospital.setOpenServiceName(row[1]);
        hospital.setOpenLocalGovernmentCode(Integer.parseInt(row[3]));
        hospital.setManagementNumber(row[4]);

        //LicenseDate 파싱하기
        int year = Integer.parseInt(row[5].substring(0,4));
        int month = Integer.parseInt(row[5].substring(4,6));
        int day = Integer.parseInt(row[5].substring(6,8));
        hospital.setLicenseDate(LocalDateTime.of(year,month,day,0,0,0));

        hospital.setBusinessStatus(Integer.parseInt(row[7]));
        hospital.setBusinessStatusCode(Integer.parseInt(row[9]));
        hospital.setPhone(row[15]);
        hospital.setFullAddress(row[18]);
        hospital.setRoadNameAddress(row[19]);
        hospital.setHospitalName(row[21]);
        hospital.setBusinessTypeName(row[25]);
        hospital.setHealthcareProviderCount(Integer.parseInt(row[29]));
        hospital.setPatientRoomCount(Integer.parseInt(row[30]));
        hospital.setTotalNumberOfBeds(Integer.parseInt(row[31]));
        hospital.setTotalAreaSize(Float.parseFloat(row[32]));

        return hospital;
    }
}
```

## 1.6 ParserFactory 생성
- 위에서 작성한 코드를 조립하는 ParserFactory클래스를 생성한다.

`[ParserFactory.java]`
```java
package com.example.hello.parser;

import com.example.hello.domain.Hospital;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserFactory {

    @Bean
    public ReadLineContext<Hospital> hospitalReadLineContext(){
        return new ReadLineContext<Hospital>(new HospitalParser());

    }
}
```

## 1.7 테스트코드 추가
- @SpringBootTest, @Autowired와 파일을 제대로 불러오는지 테스트 메소드를 추가한다.
- @Autowired: ApplicationContext(Spring)에 등록된 빈을 __이름__에 맞게 DI를 해준다.
   - ex) =new ReadlineContext()에서 new를 안하게 해준다(Singletone)
   - 만약 여러명(ex 10000명)이 있다면 만번의 GC가 돌기 때문에 성능이 떨어질 수 있다.
   - Autowired는 주로 Test에서 쓰는 추세이고 서비스 코드는 final과 Constructor를 사용한다. 이렇게 해도 Spring이 DI를 해준다.
- @SpringBootTest: SpringBoot가 스캔을 해서 등록한 Bean을 Test에서 쓸 수 있게 해준다.

`[HospitalParserTest.java]`
```java
@SpringBootTest
class HospitalParserTest {
    String line1 = "\"1\",\"의원\",\"01_01_02_P\",\"3620000\",\"PHMA119993620020041100004\",\"19990612\",\"\",\"01\",\"영업/정상\",\"13\",\"영업중\",\"\",\"\",\"\",\"\",\"062-515-2875\",\"\",\"500881\",\"광주광역시 북구 풍향동 565번지 4호 3층\",\"광주광역시 북구 동문대로 24, 3층 (풍향동)\",\"61205\",\"효치과의원\",\"20211115113642\",\"U\",\"2021-11-17 02:40:00.0\",\"치과의원\",\"192630.735112\",\"185314.617632\",\"치과의원\",\"1\",\"0\",\"0\",\"52.29\",\"401\",\"치과\",\"\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"0\",\"\",";

    @Test
	@DisplayName("10만건 이상 데이터가 파싱 되는지")
	void oneHundreadThousandRows() throws IOException {
   		// 서버환경에서 build할 때 문제가 생길 수 있습니다.
   		// 어디에서든지 실행할 수 있게 짜는 것이 목표.
   		String filename = "./fulldata_01_01_02_P_의원.csv";
   		List<Hospital> hospitalList = hospitalReadLineContext.readByLine(filename);
   		assertTrue(hospitalList.size() > 1000);
   		assertTrue(hospitalList.size() > 10000);
   		for (int i = 0; i < 10; i++) {
       		System.out.println(hospitalList.get(i).getHospitalName());
   		}
   		System.out.printf("파싱된 데이터 개수:", hospitalList.size());
	}
}
```
## 1.8 ReadLineContext 예외 추가
- 파싱에 문제가 있는 경우 넘기는 예외를 처리하는 구문을 추가한다.
`[ReadLineContext.java]`
```java
while ((str = reader.readLine()) != null) {
   try {
       result.add(parser.parse(str));
   } catch (Exception e) {
       System.out.printf("파싱중 문제가 생겨 이 라인은 넘어갑니다. 파일내용:%s\n", str.substring(0, 20));
   }
}
```
# 2. 생각해볼 것들
## 2.1 데이터가 우리 생각대로 안오는 경우
![](https://velog.velcdn.com/images/lyj1023/post/c5ac5945-fd3f-4e44-b56b-f958779a6dee/image.png)
- 위 사진처럼 중간중간 빈칸이 있어서 에러가 나는 경우
  1) 데이터 파싱중 에러나면 그 데이터를 안넣는다.(칼럼을 빼버린다)
  2) null인 경우 0 또는 default value을 정해준다.(기준을 잘 정해야 한다.)

## 2.2 MYSQL에서 date가 아닌 datetime을 사용한 이유
- 지금하는 프로젝트에서는 datetime보다 date가 더편하지만 실제로 사용할 땐 datetime을 더 많이 사용하기 때문에 datetime을 사용했다.

## 2.3 @Autowired의 이해
- 아래 두개는 같은 코드이다.
```java
@Autowired
ApplicationContext context;

@BeforeEach
void setUp() {
   this.hospitalReadLineContext = context.getBean("hospitalReadLineContext", ReadLineContext.class);
}
```
=
```java
@Autowired
ReadLineContext<Hospital> hospitalReadLineContext;
```

## 2.4 한글 인코딩 이슈 해결
- csv파일을 열어서 파일 > 다른이름저장 > 인코딩을 UTF-8로 변경 후 저장한다.
![](https://velog.velcdn.com/images/lyj1023/post/f3f038f3-2cda-4c5d-82a9-0caf6ef852a3/image.png)


# <221101-springboot-hospital>
# 1. SpringBoot
## 1.1 목표
- __지난시간에 했던것__
   1) 테이블 설계(논리적 설계)
   2) 물리적 설계 및 적용(Create Table)
   ```sql
   CREATE TABLE `likelion-db`.`nation_wide_hospitals` (
  `id` INT NOT NULL,
  `open_service_name` VARCHAR(10) NULL COMMENT '개방서비스명',
  `open_local_government_code` INT NOT NULL COMMENT '개방자치단체코드',
  `management_number` VARCHAR(40) NULL COMMENT '관리번호',
  `license_date` DATETIME NULL COMMENT '인허가일자',
  `business_status` TINYINT(2) NULL COMMENT '영업상태구분(boolean)\n1: 영업/정상\n2: 휴업\n3: 폐업\n4: 취소/말소',
  `business_status_code` TINYINT(2) NULL COMMENT '영업상태코드\n2: 휴업\n3: 폐업\n13: 영업중',
  `phone` VARCHAR(20) NULL COMMENT '소재지전화',
  `full_address` VARCHAR(200) NULL COMMENT '소재지전체주소',
  `road_name_address` VARCHAR(200) NULL COMMENT '도로명전체주소',
  `hospital_name` VARCHAR(20) NULL COMMENT '사업장명',
  `business_type_name` VARCHAR(10) NULL COMMENT '업태구분명',
  `healthcare_provider_count` TINYINT(2) NULL,
  `patient_room_count` TINYINT(2) NULL COMMENT '입원실수',
  `total_number_of_beds` TINYINT(2) NULL COMMENT '병상수',
  `total_area_size` FLOAT NULL COMMENT '총면적',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `management_number_UNIQUE` (`management_number` ASC) VISIBLE);
  ```
   3) 한줄 파싱하는 로직의 테스트 코드 개발
   4) 한줄 파싱하는 로직개발
   5) 파일에서 여러줄 불러와서 `List<Hospital>` 형태로 만들기 11만여건
 
- __앞으로 해볼것들__  

   6) SQL Query문을 실행하는 Dao와 test코드 작성 --> 오늘은 여기까지  
   7) `List<Hospital>` 을 DB에 Insert  
   8) Insert한 데이터를 Select  
   9) Select한 데이터를 web(API)으로 보여주는 것  

## 1.2 DB에 Insert하기
### 1.2.1 HospitalDao 생성
- sql쿼리문이 들어갈 HospitalDao를 생성해 jdbcTemplate을 사용할 준비를 한다.
`[HospitalDao.java]`
```java
@Component
public class HospitalDao {

   private final JdbcTemplate jdbcTemplate;

   public HospitalDao(JdbcTemplate jdbcTemplate) {
       this.jdbcTemplate = jdbcTemplate;
   }
}
```

### 1.2.2 Insert 쿼리 확인하기
- jdbcTemplate을 사용하기 전에 먼저 table에 데이터가 어떻게 들어가는지 MySQL Workbench에 컬럼하나만 넣어서 확인해 보자
```sql
INSERT INTO `likelion-db`.`nation_wide_hospitals` (`id`, `open_service_name`, `open_local_government_code`, `management_number`, `license_date`, `business_status`, `business_status_code`, `phone`, `full_address`, `road_name_address`, `hospital_name`, `business_type_name`, `healthcare_provider_count`, `patient_room_count`, `total_number_of_beds`, `total_area_size`) VALUES ('1', '의원', '3620000', 'PHMA119993620020041100004', '19990612', '1', '13', '062-515-2875', '광주광역시 북구 풍향동 565번지 4호 3층', '광주광역시 북구 동문대로 24, 3층 (풍향동)', '효치과의원', '치과의원', '1', '0', '0', '52.29');
```

### 1.2.3 구조 알아보기
- Infra Architecture
![](https://velog.velcdn.com/images/lyj1023/post/3b8bb0b4-66d7-4e8d-bd32-d4fa741f60dd/image.png)
  - 클라이언트(User 혹은 또 다른 서버)에서 API를 호출하면 API는 DB에서 sql명령문을 통해 요청하고 ResultSet을 반환받는다.
  - 클라이언트에서 바로 DB로 접근할수도 있는데 SQL Workbench를 이용해 접근할 수 있다.
  - 클라이언트에서 바로 DB에 접근하지 않고 API를 거치는 이유는 API에는 비즈니스 로직을 추가할 수 있기 때문이다. 


- API의 Application Architecture
![](https://velog.velcdn.com/images/lyj1023/post/f837f547-a2c3-435b-8b99-318eefa5e6ed/image.png)
  - HospitalController가 Presentation, HospitalDao가 Service, JdbcTemplate이 Data이다.
  
### 1.3.4 add메소드 추가하기
- HospitalDao에 add메소드를 추가한다. 
- sql 쿼리를 작성하고 VALUES의 물음표에 대응 하는 값을 `jdbcTemplate.update`를 통해 값을 넣어준다.
`[HospitalDao.java]`
```java
public void add(Hospital hospital){
    String sql = "INSERT INTO `likelion-db`.`nation_wide_hospitals` (`id`, `open_service_name`, `open_local_government_code`, `management_number`, `license_date`, `business_status`, `business_status_code`, `phone`, `full_address`, `road_name_address`, `hospital_name`, `business_type_name`, `healthcare_provider_count`, `patient_room_count`, `total_number_of_beds`, `total_area_size`) " +
                "VALUES (?,?,?," +
                " ?, ?, ?," +
                " ?, ?, ?," +
                " ?, ?, ?," +
                " ?, ?, ?, ?);"; 

    this.jdbcTemplate.update(sql, hospital.getId(), hospital.getOpenServiceName(),
                hospital.getOpenLocalGovernmentCode(), hospital.getManagementNumber(),
                hospital.getLicenseDate(), hospital.getBusinessStatus(),
                hospital.getBusinessStatusCode(), hospital.getPhone(),
                hospital.getFullAddress(), hospital.getRoadNameAddress(),
                hospital.getHospitalName(), hospital.getBusinessTypeName(),
                hospital.getHealthcareProviderCount(), hospital.getPatientRoomCount(),
                hospital.getTotalNumberOfBeds(), hospital.getTotalAreaSize());
}
```
### 1.3.5 test코드 추가
- @Autowired에 HospitalDao를 추가할 수 있는 이유: @component가 달린 클래스를 Bean으로 등록한다.

`[HospitalParserTest.java]`
```java
@Autowired
HospitalDao hospitalDao;

@Test
@DisplayName("HospitalDao의 insert가 잘되는지 테스트")
void add() {
    HospitalParser hp = new HospitalParser();
    Hospital hospital = hp.parse(line1);
    hospitalDao.add(hospital);
}
```
- class위에 @SpringBootTest를 붙이지않으면 NullPointerError가 날수 있다.
   - 그 이유는 @SpringBootTest가 모든 빈들을 스캔하고 애플리케이션 컨텍스트를 생성하여 테스트를 실행해주기 때문이다.


### 1.3.6 DB의 table 수정하기
- 기존에 작성했던 table이 이름 길이가 30 이상인 것이 존재, 병상수,입원실 수, 의료인수가 127 이상인 것이 존재하기 때문에 Alter를 이용해 수정한다.

>table 수정할때 조심해야하는 이유
>- 만약 float → int로 변환한다면 아래와 같이 데이터가 잘린다.
>```
>10.1 → 10
>0.2 → 0
>```  
>- 이상태에서 다시 float로 변환하면 아래와 같이 데이터가 사라지게 된다.
>```
>10 → 10.0
>0 → 0.0
>```  
>- 따라서 수정하기전에 꼭 백업을 해둬야한다.  
> ![](https://velog.velcdn.com/images/lyj1023/post/be8cf68e-0700-4c31-9070-621930fe4ac5/image.png)


![](https://velog.velcdn.com/images/lyj1023/post/c66da8b7-4012-4379-8671-8d788dc126e5/image.png)
```sql
ALTER TABLE `likelion-db`.`nation_wide_hospitals` 
CHANGE COLUMN `hospital_name` `hospital_name` VARCHAR(50) NULL DEFAULT NULL COMMENT '사업장명' ,
CHANGE COLUMN `healthcare_provider_count` `healthcare_provider_count` INT NULL DEFAULT NULL ,
CHANGE COLUMN `patient_room_count` `patient_room_count` INT NULL DEFAULT NULL COMMENT '입원실수' ,
CHANGE COLUMN `total_number_of_beds` `total_number_of_beds` INT NULL DEFAULT NULL COMMENT '병상수' ,
ADD UNIQUE INDEX `total_number_of_beds_UNIQUE` (`total_number_of_beds` ASC) VISIBLE;
;
```

### 1.3.7 getCount, deleteAll메소드 추가
- HospitalDao에 개수를 count해주는 getCount와 모든 컬럼을 지워주는 deleteAll메소드를 추가한다.

`[HospitalDao.java]`
```java
public int getCount() {
   String sql = "select count(id) from nation_wide_hospitals;";
   return this.jdbcTemplate.queryForObject(sql, Integer.class);
}
public void deleteAll(){
    this.jdbcTemplate.update("delete from nation_wide_hospitals");
}
```

### 1.3.8 getCount, deleteAll를 테스트하는 코드 추가
- HospitalParserTest에 getCount와 deleteAll을 테스트하는 코드를 추가한다.

`[HospitalParserTest.java]`
```java
@Test
@DisplayName("HospitalDao의 insert, delete, count가 잘되는지 테스트")
void add() {
    HospitalParser hp = new HospitalParser();
    Hospital hospital = hp.parse(line1);

    hospitalDao.deleteAll();
    assertEquals(0,hospitalDao.getCount());

    hospitalDao.add(hospital);
    assertEquals(1,hospitalDao.getCount());
}
```

### 1.3.9 RowMapper와 findById 메소드 추가
- RowMapper와 select를 할수있는 findById 메소드를 추가한다.

`[HospitalDao.java]`
```java
RowMapper<Hospital> rowMapper = (rs, rowNum) -> {
    Hospital hospital = new Hospital();
    hospital.setId(rs.getInt("id"));
    hospital.setOpenServiceName(rs.getString("open_service_name"));
    hospital.setOpenLocalGovernmentCode(rs.getInt("open_local_government_code"));
    hospital.setManagementNumber(rs.getString("management_number"));
    hospital.setLicenseDate(rs.getTimestamp("license_date").toLocalDateTime());
    hospital.setBusinessStatus(rs.getInt("business_status"));
    hospital.setBusinessStatusCode(rs.getInt("business_status_code"));
    hospital.setPhone(rs.getString("phone"));
    hospital.setFullAddress(rs.getString("full_address"));
    hospital.setRoadNameAddress(rs.getString("road_name_address"));
    hospital.setHospitalName(rs.getString("hospital_name"));
    hospital.setBusinessTypeName(rs.getString("business_type_name"));
    hospital.setHealthcareProviderCount(rs.getInt("healthcare_provider_count"));
    hospital.setPatientRoomCount(rs.getInt("patient_room_count"));
    hospital.setTotalNumberOfBeds(rs.getInt("total_number_of_beds"));
    hospital.setTotalAreaSize(rs.getFloat("total_area_size"));

    return hospital;
};

public Hospital findById(int id) {
    return this.jdbcTemplate.queryForObject("select * from nation_wide_hospitals where id = ?", rowMapper, id);
}
```

### 1.3.10 findById 테스트하는 코드 추가
- select구문이 잘되는지 테스트하는 코드를 추가한다.

`[HospitalParserTest.java]`
```java
@Test
@DisplayName("HospitalDao의 insert, delete, count, select가 잘되는지 테스트")
void add() {
    HospitalParser hp = new HospitalParser();
    Hospital hospital = hp.parse(line1);

    hospitalDao.deleteAll();
    assertEquals(0,hospitalDao.getCount());

    hospitalDao.add(hospital);
    assertEquals(1,hospitalDao.getCount());

    Hospital selectedHospital = hospitalDao.findById(hospital.getId());
    assertEquals(selectedHospital.getId(), hospital.getId());
    assertEquals(selectedHospital.getOpenServiceName(), hospital.getOpenServiceName());
    assertEquals(selectedHospital.getOpenLocalGovernmentCode(), hospital.getOpenLocalGovernmentCode());
    assertEquals(selectedHospital.getManagementNumber(), hospital.getManagementNumber());
    assertEquals(selectedHospital.getLicenseDate(), hospital.getLicenseDate());
    assertEquals(selectedHospital.getBusinessStatus(), hospital.getBusinessStatus());
    assertEquals(selectedHospital.getBusinessStatusCode(), hospital.getBusinessStatusCode());
    assertEquals(selectedHospital.getPhone(), hospital.getPhone());
    assertEquals(selectedHospital.getFullAddress(), hospital.getFullAddress());
    assertEquals(selectedHospital.getRoadNameAddress(), hospital.getRoadNameAddress());
    assertEquals(selectedHospital.getHospitalName(), hospital.getHospitalName());
    assertEquals(selectedHospital.getBusinessTypeName(), hospital.getBusinessTypeName());
    assertEquals(selectedHospital.getHealthcareProviderCount(), hospital.getHealthcareProviderCount());
    assertEquals(selectedHospital.getPatientRoomCount(), hospital.getPatientRoomCount());
    assertEquals(selectedHospital.getTotalNumberOfBeds(), hospital.getTotalNumberOfBeds());
    assertEquals(selectedHospital.getTotalAreaSize(), hospital.getTotalAreaSize());
}
```
