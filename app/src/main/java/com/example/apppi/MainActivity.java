package com.example.apppi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView jokeView;
    Button newJoke;
    ImageView memePick;
    Random myRand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jokeView = (TextView)findViewById(R.id.jokeView);
        newJoke = (Button)findViewById(R.id.newJoke);
        memePick = (ImageView)findViewById(R.id.jokeImg);
        myRand = new Random();

        newJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJoke();
            }
        });
    }
    //https://api.imgflip.com/get_memes
    //https://api.chucknorris.io/jokes/random
    public void getJoke()
    {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.imgflip.com/get_memes", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int randNum = myRand.nextInt(100);
                    JSONObject STdata = response.optJSONObject("data");
                    JSONArray STmeme = STdata.optJSONArray("memes");
                    JSONObject STurl = STmeme.optJSONObject(randNum);
                    String memeUrl = STurl.getString("url");
                    int lengthM = STmeme.length();
                    Log.d("myapp", " The response is " + STmeme.length());
                    new DownloadImageFromInternet((ImageView)findViewById(R.id.jokeImg)).execute(memeUrl);
                    jokeView.setText(STurl.getString("name"));

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "Something went wrong");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}