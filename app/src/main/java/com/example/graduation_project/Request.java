package com.example.graduation_project;

public class Request {
    private String requestId, imageUrl, userId, workerId, workerName, problemExplanation, area, field;

    public Request() {
    }

    public Request(String requestId, String imageUrl, String userId, String workerId, String workerName, String problemExplanation, String area, String field) {
        this.requestId = requestId;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.workerId = workerId;
        this.workerName = workerName;
        this.problemExplanation = problemExplanation;
        this.area = area;
        this.field = field;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getProblemExplanation() {
        return problemExplanation;
    }

    public void setProblemExplanation(String problemExplanation) {
        this.problemExplanation = problemExplanation;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", workerId='" + workerId + '\'' +
                ", workerName='" + workerName + '\'' +
                ", problemExplanation='" + problemExplanation + '\'' +
                ", area='" + area + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}
