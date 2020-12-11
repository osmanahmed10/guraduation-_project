package com.example.graduation_project;

public class exampleWorkRequest {
    private int problemImage;
    private String problemDiscription;

    public exampleWorkRequest(int Image, String Discription){
        problemImage = Image;
        problemDiscription = Discription;
    }

    public int getProblemImage() {
        return problemImage;
    }

    public String getProblemDiscription() {
        return problemDiscription;
    }
}
