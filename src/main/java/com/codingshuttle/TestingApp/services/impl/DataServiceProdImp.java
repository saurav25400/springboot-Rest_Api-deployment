package com.codingshuttle.TestingApp.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class DataServiceProdImp implements DataService{
    @Override
    public void getData() {
        System.out.println("prod data");
    }
}
