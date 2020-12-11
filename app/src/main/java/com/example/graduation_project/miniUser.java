package com.example.graduation_project;

public class miniUser {
    private String id, area, profession;
    private int role;

    public miniUser() {
    }

    public miniUser(String id, String area, String profession, int role) {
        this.id = id;
        this.area = area;
        this.profession = profession;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "miniUser{" +
                "id='" + id + '\'' +
                ", area='" + area + '\'' +
                ", profession='" + profession + '\'' +
                ", role=" + role +
                '}';
    }
}
