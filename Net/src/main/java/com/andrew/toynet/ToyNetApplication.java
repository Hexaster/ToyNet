package com.andrew.toynet;

import Data.Tensor;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
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
        //ConfigurableApplicationContext context = SpringApplication.run(ToyNetApplication.class, args);
        long startTime = System.nanoTime();
        List<Integer> a = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        System.out.println(a.get(0));
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println(duration);
    }

}
