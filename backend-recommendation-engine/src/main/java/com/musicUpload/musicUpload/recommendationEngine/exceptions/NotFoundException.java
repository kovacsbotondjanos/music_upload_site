package com.musicUpload.musicUpload.recommendationEngine.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException() {
        super();
    }
}