package com.xay.movies.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xay.movies.MovieDetails;
import com.xay.movies.R;
import com.xay.movies.models.ListModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    String BASEURL = "https://image.tmdb.org/t/p/w500/";
    private int lastPosition = -1;

    private Context mContext;
    private ArrayList<ListModel> imagesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView name;
        public TextView date;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.img);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);

        }
    }


    public CustomAdapter(Context mContext, ArrayList<ListModel> imagesList) {
        this.mContext = mContext;
        this.imagesList = imagesList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tabitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ListModel images = imagesList.get(position);

        Log.e("adapterimage", images.getImg() + ".");
        Glide.with(mContext).load(BASEURL + images.getImg()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA)).apply(new RequestOptions()
                .placeholder(R.drawable.load)
        ).
                into(holder.thumbnail);
        holder.date.setText(imagesList.get(position).getDate());
        holder.name.setText(imagesList.get(position).getName());
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetOn()) {         //Convert to byte array


                    URL url = null;

                    try {
                        url = new URL(BASEURL + images.getImg());
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                        Log.e("BITMAPS", image + "");

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent in1 = new Intent(mContext, MovieDetails.class);
                        in1.putExtra("id", imagesList.get(position).getId());
                        in1.putExtra("image", byteArray);

                        mContext.startActivity(in1);

                        Animatoo.animateCard(mContext); //fire the slide left animation


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mContext, "No internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.a : R.anim.b);
        holder.thumbnail.startAnimation(animation);
        lastPosition = position;
    }


    @Override
    public int getItemCount() {

        return imagesList.size();
    }

    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
