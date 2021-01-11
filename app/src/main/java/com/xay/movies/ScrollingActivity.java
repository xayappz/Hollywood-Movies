package com.xay.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.xay.movies.adapters.CustomAdapter;
import com.xay.movies.models.ListModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {
    RecyclerView movies_list;
    ArrayList<ListModel> data = new ArrayList<>();
    SkeletonScreen skeletonScreen;
    String URL = "https://api.themoviedb.org/3/movie/popular?api_key=a72d7b9263b79336b41e3230903011fc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        movies_list = findViewById(R.id.movies_list);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        toolBarLayout.setTitle("Popular Movies List");
        if (isInternetOn()) {
            fetchMovies();
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }


    }

    private void fetchMovies() {
        skeletonScreen = Skeleton.bind(movies_list)
                .load(R.layout.tabitem)
                .show();
        skeletonScreen.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("DATA", response + "");
                        try {
                            // Get the JSON array
                            JSONArray array = response.getJSONArray("results");
                            // Loop through the array elements
                            for (int i = 0; i < array.length(); i++) {
                                ListModel listModel = new ListModel();
                                JSONObject movies = array.getJSONObject(i);
                                Log.e("DATATA", movies.getString("id"));
                                ;

                                listModel.setId(movies.getString("id"));
                                listModel.setAvgrate(movies.getString("vote_average"));
                                listModel.setDate(movies.getString("release_date"));
                                listModel.setDetails(movies.getString("overview"));
                                listModel.setName(movies.getString("title"));
                                listModel.setImg(movies.getString("poster_path"));
                                listModel.setLang(movies.getString("original_language"));

                                data.add(listModel);
                            }
                            setInList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do something when error occurred
                Toast.makeText(ScrollingActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonObjectRequest);
    }

    private void setInList() {
        skeletonScreen.hide();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        movies_list.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        CustomAdapter customAdapter = new CustomAdapter(this, data);
        movies_list.setAdapter(customAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

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