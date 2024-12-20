package com.codingshuttle.TestingApp.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DataServiceDevImp  implements DataService{
    @Override
    public void getData() {
        System.out.println("dev sata");
    }
}
