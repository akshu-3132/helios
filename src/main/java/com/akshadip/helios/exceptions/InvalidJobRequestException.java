package com.akshadip.helios.exceptions;

public class InvalidJobRequestException extends RuntimeException {
    public InvalidJobRequestException(String message) {
        super(message);
    }
}
