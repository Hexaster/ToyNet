package com.andrew.toynet;

import Data.Tensor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ToyNetApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;
    private Tensor tensor;

    @Test
    void contextLoads() {

    }

}
