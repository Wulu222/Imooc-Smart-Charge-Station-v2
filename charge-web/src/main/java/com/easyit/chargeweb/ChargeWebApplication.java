package com.easyit.chargeweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.easyit.chargeweb", "com.easyit.SmartChargeStation.iotdb"})
public class ChargeWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargeWebApplication.class, args);
    }

}
