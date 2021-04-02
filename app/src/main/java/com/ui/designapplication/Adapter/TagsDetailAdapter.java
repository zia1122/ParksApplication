package com.ui.designapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ui.designapplication.Models.NearbyCards;
import com.ui.designapplication.databinding.TagItemViewBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagsDetailAdapter extends RecyclerView.Adapter<TagsDetailAdapter.TagViewHolder> {

    private final Context context;
    private final List<String> tagsList;

    public TagsDetailAdapter(Context context, List<String> tagsList) {

        this.context = context;
        this.tagsList = tagsList;

    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TagViewHolder(TagItemViewBinding
                .inflate(LayoutInflater.from(context), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {

        String[] tagNames = tagsList.get(position).split(" - ");

        holder.tagItemViewBinding.tagName.setText(tagNames[1]);


    }

    @Override
    public int getItemCount() {

        return tagsList.size();

    }

    static class TagViewHolder extends RecyclerView.ViewHolder {

        private TagItemViewBinding tagItemViewBinding;

        public TagViewHolder(@NonNull TagItemViewBinding tagItemViewBinding) {

            super(tagItemViewBinding.getRoot());

            this.tagItemViewBinding = tagItemViewBinding;

        }
    }
}
