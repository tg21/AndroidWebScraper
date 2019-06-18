package com.example.codie.shopeverything;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Gautam on 07-06-2018.
 */

public class ResultActivity extends AppCompatActivity {
    ListView resultView;
    ArrayList<details> itemArray=null;
    String type,query;
     public static final String CUSTOM_TAB_PACKAGE_NAME ="com.android.chrome";
     CustomTabsClient mClient;
     CustomTabsSession mCustomTabsSession;
     CustomTabsServiceConnection mCustomTabsServiceConnection;
     static CustomTabsIntent customTabsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        query=getIntent().getExtras().getString("query");
        type=getIntent().getExtras().getString("type");
        System.out.println(query+type);
        resultView=findViewById(R.id.list_result);
        //itemArray=new ArrayList<>();

        BackgroundWorker backgroundWorker=new BackgroundWorker(this);
        try {
            itemArray=backgroundWorker.execute(type,query).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ArrayAdapter<details> adapter = new myArrayAdapter(this, 0,itemArray);
        resultView.setAdapter(adapter);
        /*AdapterView.OnItemClickListener adapterViewListner=new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                details detail=itemArray.get(i);
                System.out.println(detail.getname());
                System.out.println("hola");
            }
        };
        resultView.setOnItemClickListener(adapterViewListner);*/
        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;
                mClient.warmup(0L);
                mCustomTabsSession = mClient.newSession(null);
            } @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
            }
        };
        CustomTabsClient.bindCustomTabsService(getApplicationContext(),CUSTOM_TAB_PACKAGE_NAME,mCustomTabsServiceConnection);
        customTabsIntent=new CustomTabsIntent.Builder(mCustomTabsSession).setShowTitle(true).build();

    }




    /*****************************************************************************************************/
    class myArrayAdapter extends ArrayAdapter<details> {

        private Context context;
        private List<details> result_array;

        //constructor, call on creation
        public myArrayAdapter(Context context, int resource, ArrayList<details> objects) {
            super(context, resource, objects);

            this.context = context;
            this.result_array = objects;
        }

        //called when rendering the list
        public View getView(final int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            final details detail = result_array.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.result_item, null);

            final TextView name = (TextView) view.findViewById(R.id.item_name);
            name.setClickable(true);
            name.setMovementMethod(LinkMovementMethod.getInstance());
            TextView price = (TextView) view.findViewById(R.id.item_price);
            TextView rating = (TextView) view.findViewById(R.id.item_rating);
            ImageView image = (ImageView) view.findViewById(R.id.item_image);
            ImageView image2=view.findViewById(R.id.imageView2);


            price.setText(String.valueOf(detail.getprice()));
            /*String link=String.valueOf(detail.getlink());
            String hyperText = ;*/
            //name.setText(Html.fromHtml("<a href ="+String.valueOf(detail.getlink())+">"+String.valueOf(detail.getname())+"</a>"));//String.valueOf(detail.getname()));
            name.setText(String.valueOf(detail.getname()));
            rating.setText(String.valueOf(detail.getrating()));
            String img=detail.getimage_url();
            if(!img.isEmpty()) {
                Bitmap itemImage = null;
                ImageSeter imageSeter = new ImageSeter();
                try {
                    itemImage = imageSeter.execute(img).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                image.setImageBitmap(itemImage);
            }
            switch (String.valueOf(detail.getSource())){
                case "amazon":
                    image2.setImageDrawable(getResources().getDrawable(R.drawable.amazon_icon));
                    break;


                case "flipkart":
                    image2.setImageResource(R.drawable.flipkart_icon);
            }
            ConstraintLayout clickLayout=view.findViewById(R.id.clickLayout);
            clickLayout.setTag(position);
            clickLayout.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   int clickPos=(Integer)view.getTag();
                                                   details clickDetail=getItem(clickPos);
                                                   System.out.println(clickDetail.getname()+"by inside");
                                                   String url = clickDetail.getlink().toString();
                                                   /*CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                                   CustomTabsIntent customTabsIntent = builder.build();
                                                   customTabsIntent.launchUrl(getApplicationContext(), Uri.parse(url));*/
                                                   customTabsIntent.launchUrl(getApplicationContext(),Uri.parse(url));
                                               }
                                           });


                    //get the image associated with this property
            /*int imageID = context.getResources().getIdentifier(property.getImage(), "drawable", context.getPackageName());
            image.setImageResource(imageID);*/

            return view;
        }

    }
    public class ImageSeter extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... strings) {
            String image_url= strings[0];
            URL newurl = null;
            try {
                newurl = new URL(image_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap mIcon_val = null;
            try {
                mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mIcon_val;
        }
    }

}

