package com.example.codie.shopeverything;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class BackgroundWorker extends AsyncTask<String,Void,ArrayList<details>>{

    Context context;

    String url,title,result;
    ArrayList<details> item_details,result_array;

    void getAmazon(String queary){
        String url="https://www.amazon.in/s/?field-keywords="+queary.trim().replaceAll("\\s{1,}", "+");
        //url="http://192.168.1.7/Amazon.in: xbox: Video Games.html";
        Document doc=null;
        try {
            doc=Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //title =doc.title();
        Elements container=doc.getElementsByClass("s-item-container");
        for(Element i:container){
            try {
                String name = i.getElementsByClass("a-size-medium s-inline  s-access-title  a-text-normal").attr("data-attribute");
                String link = i.getElementsByClass("a-link-normal a-text-normal").attr("abs:href") + "&tag=gee098-21";
//                String price=i.select("span.a-link-normal a-text-normal").text();
                String price = i.getElementsByClass("a-size-base a-color-price s-price a-text-bold").first().text().replaceAll(",+","");
                String rating = i.getElementsByClass("a-popover-trigger a-declarative").first().text();
                String image_url = i.getElementsByClass("s-access-image cfMarker").attr("src");
                System.out.println("price == " + price + "\nname == " + name + "\nlink == " + link + "\nImage ==" + image_url + "\nRating == " + rating + "\n");

                if (!name.isEmpty()) {
                    item_details.add(new details(name, link, price, image_url, rating, "amazon"));
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }

    void getFlipkart(String queary){
        String url="http://www.flipkart.com/search?q="+queary.trim().replaceAll("\\s{1,}", "+");
        //url="http://192.168.1.7/flipkart.html";

        String name,link,price,rating,image_url;
        Document doc = null;
        try {
            doc=Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(doc.title());
        Elements containter = doc.getElementsByClass("_3liAhj _1R0K0g");
        for(Element i:containter){
            try {
                name = i.getElementsByClass("_2cLu-l").attr("title");
                link = i.getElementsByClass("_2cLu-l").attr("abs:href");
                price = i.select("div._1vC4OE").text();
                //image_url=i.getElementsByClass("_1Nyybr  _30XEf0").attr("abs:src");
                image_url = i.getElementsByAttribute("src").first().attr("src");
                //

                rating = i.getElementsByClass("hGSR34 _2beYZw").text();
                System.out.println("price == " + price + "\nname == " + name + "\nlink == " + link + "\nImage ==" + image_url + "\nRating == " + rating + "\n");
                if (!name.isEmpty()) {
                    item_details.add(new details(name, link, price, image_url, rating, "flipkart"));
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    void getMyntra(String queary){

    }

    BackgroundWorker(Context ctx){
        context=ctx;
    }

    @Override
    protected ArrayList<details> doInBackground(String... strings) {
        item_details=new ArrayList<>();

        String type=strings[0];
        if(type.equals("others")){
            getAmazon(strings[1]);

        }else if(type.equals("clothes")){
            getFlipkart(strings[1]);
        }else {
            getAmazon(strings[1]);
            getFlipkart(strings[1]);
        }
        result_array=item_details;
        return result_array;
    }


    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(ArrayList<details> result) {
        //Toast.makeText(context,result, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

