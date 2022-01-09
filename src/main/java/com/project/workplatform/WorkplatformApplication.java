package com.project.workplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = {"com.project.workplatform.dao"})
public class WorkplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkplatformApplication.class, args);
    }

}
