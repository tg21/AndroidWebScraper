package com.example.codie.shopeverything;



/**
 * Created by Gautam on 08-06-2018.
 */

public class details {
    String name;
    String link;
    String price;
    String image_url;
    String rating;
    String source;

    public details(String name,String link, String price ,String image_url,String rating,String source){
        this.name = name;
        this.link = link;
        this.price = price;
        this.image_url = image_url;
        this.rating=rating;
        this.source=source;
        //this.image=image;

    }

    public String getname() {return name; }
    public String getlink() {return link; }
    public String getprice() {return price; }
    public String getimage_url() {return image_url; }
    public String getrating() {return rating; }
    public String getSource(){return source;}
}


