package com.toy.model;

import java.io.Serializable;

public class Toy implements Serializable {
    private String imageName;
    private String name;
    private String description;
    private double price;

    public Toy() {
    }

    public Toy(String imageName, String name, String description, double price) {
        this.imageName = imageName;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return imageName + "," + name + "," + description + "," + price;
    }
} 