package com.lteam.zooar.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ar.sceneform.rendering.AnimationData;
import com.lteam.zooar.Image.CircleImage;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public abstract class AdapterAnimal extends RecyclerView.Adapter<AdapterAnimal.RecycleviewHolder> {
    String TAG="AdapterAnimal";
    List<Animal> animals;
    int lastPosition=-1;
    Context context;

    public AdapterAnimal(List<Animal> animals,Context context){
        this.animals=animals;
        this.context=context;
    }

    @NonNull
    @Override
    public RecycleviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_layout, parent, false);
        return new AdapterAnimal.RecycleviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleviewHolder holder, int position) {
        Animal animal=animals.get(position);
        Picasso.get()
                .load(APIUtils.base_url+animal.getUrlimage())
                .resize(50,50)
                .into(holder.circleImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderModel(animal);
            }
        });
        setAnimation(holder.itemView,position);
    }
    @Override
    public int getItemCount() {
        return animals.size();
    }

    public abstract void renderModel(Animal animal);

    public class RecycleviewHolder extends RecyclerView.ViewHolder {
        ImageView circleImage;
        public RecycleviewHolder(@NonNull View itemView) {
            super(itemView);
            circleImage=itemView.findViewById(R.id.imgItem);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
