package com.foodprice.model;

public class Product {
    private String name;
    private double price;
    private String store;
    private String image;
    private String category;
    private String link;
    private String unit;
    private String dateScraped;

    public Product(String name, double price, String store, String image,
                   String category, String link, String unit, String dateScraped) {
        this.name        = name;
        this.price       = price;
        this.store       = store;
        this.image       = image;
        this.category    = category;
        this.link        = link;
        this.unit        = unit;
        this.dateScraped = dateScraped;
    }

    // Getters
    public String getName()        { return name; }
    public double getPrice()       { return price; }
    public String getStore()       { return store; }
    public String getImage()       { return image; }
    public String getCategory()    { return category; }
    public String getLink()        { return link; }
    public String getUnit()        { return unit; }
    public String getDateScraped() { return dateScraped; }

    // Setters
    public void setName(String name)             { this.name = name; }
    public void setPrice(double price)           { this.price = price; }
    public void setStore(String store)           { this.store = store; }
    public void setImage(String image)           { this.image = image; }
    public void setCategory(String category)     { this.category = category; }
    public void setLink(String link)             { this.link = link; }
    public void setUnit(String unit)             { this.unit = unit; }
    public void setDateScraped(String date)      { this.dateScraped = date; }

    @Override
    public String toString() {
        return name + " | $" + price + " | " + store + " | " + category;
    }
}