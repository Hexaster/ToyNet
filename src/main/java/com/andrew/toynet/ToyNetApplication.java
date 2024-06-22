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
        ConfigurableApplicationContext context = SpringApplication.run(ToyNetApplication.class, args);
        DoubleArrayList a = new DoubleArrayList();
        a.add(1);
        a.add(2);
        a.add(3);
        System.out.println(a.getDouble(a.size()-1));
    }

}
