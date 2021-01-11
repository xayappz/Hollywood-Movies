package com.xay.movies;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetails extends AppCompatActivity {
    TextView name, details, avg, date, lang, website, detailstatus;
    ImageView imageView;
    String URL = "https://api.themoviedb.org/3/movie/";
    String BASEURL = "https://image.tmdb.org/t/p/w500/";
    RatingBar ratingBar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ratingBar = findViewById(R.id.rate);
        name = findViewById(R.id.detailtitle);
        details = findViewById(R.id.detaildetail);
        avg = findViewById(R.id.detailavg);
        date = findViewById(R.id.detailreldate);
        imageView = findViewById(R.id.detailimg);
        lang = findViewById(R.id.detaillang);
        website = findViewById(R.id.detailweb);
        detailstatus = findViewById(R.id.detailstatus);
        linearLayout = findViewById(R.id.layout);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        BitmapDrawable background = new BitmapDrawable(bmp);
        getDetails(getIntent().getStringExtra("id"));

    }

    private void getDetails(String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL + id + getString(R.string.apikey),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            Log.e("DSDDSA", response + "");
                            name.setText(response.getString("original_title"));
                            name.setVisibility(View.GONE);
                            getSupportActionBar().setTitle(name.getText().toString());

                            details.setText(response.getString("overview"));
                            avg.setText("Average Rating : " + response.getString("vote_average"));
                            date.setText("Release Date : " + response.getString("release_date"));
                            lang.setText("Lang Audio : " + response.getString("original_language"));
                            website.setText("Website : " + response.getString("homepage"));
                            detailstatus.setText("Status : " + response.getString("status"));


                            website.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = null;
                                    try {
                                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("homepage")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(browserIntent);
                                }
                            });
                        } catch (Exception e) {
                        }
                        try {
                            Glide.with(getApplicationContext()).load(BASEURL + response.getString("backdrop_path")).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA)).apply(new RequestOptions()
                                    .placeholder(R.drawable.load)
                            ).
                                    into(imageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                View dialogview = inflater.inflate(R.layout.fullview, null);
                                final Dialog dialog = new Dialog(MovieDetails.this);
                                dialog.setContentView(dialogview);
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                ImageView close = dialog.findViewById(R.id.close);
                                ImageView fullimg = dialog.findViewById(R.id.fullimg);
                                try {
                                    Glide.with(getApplicationContext()).load(BASEURL + response.getString("backdrop_path")).
                                            into(fullimg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.cancel();
                                    }
                                });
                                dialog.show();
                            }
                        });

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do something when error occurred
                Toast.makeText(MovieDetails.this, "No Internet", Toast.LENGTH_SHORT).show();
                Log.e("eeee", error.getLocalizedMessage());
            }
        }
        );
ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        Toast.makeText(MovieDetails.this, "You have Rated "+rating, Toast.LENGTH_SHORT).show();
    }
});
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonObjectRequest);
    }

    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }
}

