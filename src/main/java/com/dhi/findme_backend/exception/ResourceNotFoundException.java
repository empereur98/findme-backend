package com.dhi.findme_backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    private final String code;
    
    public ResourceNotFoundException(String message) {
        super(message);
        this.code = "NOT_FOUND";
    }
    
    public ResourceNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.code = "NOT_FOUND";
    }
    
    public String getCode() {
        return code;
    }
}
