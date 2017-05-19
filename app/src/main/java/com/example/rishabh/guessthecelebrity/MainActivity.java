package com.example.rishabh.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int celebChosen=0;
    ImageView imageView;
    int correctpos=0;
    String[] btnCeleb = new String[4];
    Button button,button2,button3,button4;

    public void isCorrect(View view) {

        if(view.getTag().toString().equals(Integer.toString(correctpos))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Wrong! Correct Ans. Is "+celebNames.get(celebChosen),Toast.LENGTH_LONG).show();
        }
        generate();
    }

    public class ImageTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            Bitmap celebImage;
            URL url;
            try {
                url=new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection connection;
            try {
                url=new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data!=-1){
                    char current = (char)data;
                    result = result+current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        button=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button3=(Button)findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        try {
            String result = task.execute("http://www.posh24.com/celebrities").get();
            String splitResult[] = result.split("<div class=\"col-xs-12 col-sm-6 col-md-4\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while(m.find()){
                celebURLs.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"/>");
            m=p.matcher(splitResult[0]);
            while(m.find()){
                celebNames.add(m.group(1));
            }

            generate();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void generate(){

        Random random = new Random();
        celebChosen=random.nextInt(celebURLs.size());

        ImageTask imageTask = new ImageTask();
        Bitmap celebImage;
        try {
            celebImage=imageTask.execute(celebURLs.get(celebChosen)).get();
            imageView.setImageBitmap(celebImage);

            correctpos = random.nextInt(4);
            for(int i=0;i<4;i++){
                if(i==correctpos){
                    btnCeleb[i] = celebNames.get(celebChosen);
                }
                else{
                    int x=random.nextInt(celebURLs.size());
                    while (x==celebChosen){
                        x=random.nextInt(celebURLs.size());
                    }
                    btnCeleb[i] = celebNames.get(x);
                }
            }

            button.setText(btnCeleb[0]);
            button2.setText(btnCeleb[1]);
            button3.setText(btnCeleb[2]);
            button4.setText(btnCeleb[3]);




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
