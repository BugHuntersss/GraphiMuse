package com.example.covbackend.error;

public class MyException extends Exception{
    ErrorType errorTypeEnum;
    String errorDetail;

    public MyException(ErrorType errorType, String errorDetail){
        this.errorTypeEnum = errorType;
        this.errorDetail = errorDetail;
    }

    public ErrorType getErrorTypeEnum() {
        return errorTypeEnum;
    }

    public void setErrorTypeEnum(ErrorType errorTypeEnum) {
        this.errorTypeEnum = errorTypeEnum;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }


}
