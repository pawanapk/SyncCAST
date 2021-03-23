package com.adsrole.sync;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.CategoryHolder> {

    List<Category> catlist;
    Context context;
    public UpcomingEventsAdapter(Context context, List<Category> catlist) {
        this.catlist=catlist;
        this.context=context;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomingevents_item, parent, false);
        CategoryHolder categoryHolder = new CategoryHolder(v);
        return categoryHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        //holder.category_name.setText(catlist.get(position).getCatname());
        //holder.cardContainer.setBackgroundResource(R.drawable.gradient_bg);

        if (catlist.get(position).getCatimage().isEmpty()) {
            holder.category_image.setImageResource(R.drawable.category_icon);
        } else{
            Picasso.get().load(catlist.get(position).getCatimage()).placeholder(R.drawable.category_icon)
                    .into(holder.category_image);
        }

        holder.category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent category = new Intent(context, UpcomingEventsActivity.class);
                //category.putExtra("catname", catlist.get(position).getCatname());
                category.putExtra("catimage", catlist.get(position).getCatimage());
                category.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                category.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catlist.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView category_image;
        CardView cardContainer;
        //TextView category_name;
        public CategoryHolder(@NonNull View v1) {
            super(v1);
            category_image = v1.findViewById(R.id.category_image);
            cardContainer = v1.findViewById(R.id.cardContainer);
            //category_name = v1.findViewById(R.id.category_name);
        }
    }
}
