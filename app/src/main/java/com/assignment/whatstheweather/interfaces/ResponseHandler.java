package com.assignment.whatstheweather.interfaces;

public interface ResponseHandler {
    void successHandler(Object inputObject);
    void failureHandler(Object inputObject);
}