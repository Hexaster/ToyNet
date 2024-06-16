package com.andrew.toynet;

import Data.Tensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Data.arrayHelper.array;

@SpringBootApplication
@ComponentScan(basePackages = {"Data"})
public class ToyNetApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ToyNetApplication.class, args);
        List<?> highDimensionalArray = array(
                array(array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)),
                array(array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)),
                array(array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9))
        );

        Tensor tensor = context.getBean(Tensor.class, highDimensionalArray);
        System.out.println(tensor.toString());
    }

}
