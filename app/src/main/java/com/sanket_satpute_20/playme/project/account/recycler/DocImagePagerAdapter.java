package com.sanket_satpute_20.playme.project.account.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;

import java.util.ArrayList;

public class DocImagePagerAdapter extends RecyclerView.Adapter<DocImagePagerAdapter.DocImagePagerHolder> {

    Context context;
    ArrayList<Integer> pictures;

    public DocImagePagerAdapter(Context context, ArrayList<Integer> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public DocImagePagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_documentation_child_image_layout, parent, false);
        return new DocImagePagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocImagePagerHolder holder, int position) {
        holder.image.setImageResource(pictures.get(position));
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class DocImagePagerHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public DocImagePagerHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
