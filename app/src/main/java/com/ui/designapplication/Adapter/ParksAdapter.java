package com.ui.designapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ui.designapplication.DetailActivity;
import com.ui.designapplication.Models.NearbyCards;
import com.ui.designapplication.databinding.ItemDesignparksBinding;

import java.io.Serializable;
import java.util.List;

public class ParksAdapter extends RecyclerView.Adapter<ParksAdapter.ParkViewHolder> {

    List<NearbyCards> list;
    Context context;

    public ParksAdapter(List<NearbyCards> models, Context context) {
        this.list = models;
        this.context = context;
    }

    @NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ParkViewHolder(ItemDesignparksBinding
                .inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder holder, int position) {

        NearbyCards model = list.get(position);

        holder.itemDesignparksBinding.parkName.setText(model.getName());

        String address = model.getStreetAddress() +
                model.getCity() + model.getState() + model.getZipCode();

        holder.itemDesignparksBinding.Parkaddress.setText(address);

        Picasso.get().load(model.getImageUrl())
                .into(holder.itemDesignparksBinding.parkImage);


        // Okay
        holder.itemDesignparksBinding.tagsRecyclerView
                .setAdapter(new TagsAdapter(context, model.getTags()));

        holder.itemDesignparksBinding.getRoot().setOnClickListener(v -> {

            Intent intent = new Intent(context, DetailActivity.class);


           intent.putExtra("name", model.getName());
            intent.putExtra("id", model.getId());
            intent.putExtra("state", model.getState());
            intent.putExtra("city", model.getCity());
            intent.putExtra("imageUrl", model.getImageUrl());
            intent.putExtra("zipcode", model.getZipCode());
            intent.putExtra("latitude", model.getLatitude());
            intent.putExtra("longitude", model.getLongitude());
            intent.putExtra("Description", model.getDescription());
            intent.putExtra("streetAddress", model.getStreetAddress());
            intent.putExtra("tags",model.getTags().toString());

            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ParkViewHolder extends RecyclerView.ViewHolder {

        private final ItemDesignparksBinding itemDesignparksBinding;

        public ParkViewHolder(@NonNull ItemDesignparksBinding itemDesignparksBinding) {

            super(itemDesignparksBinding.getRoot());

            this.itemDesignparksBinding = itemDesignparksBinding;

        }
    }
}
