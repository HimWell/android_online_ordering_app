package com.example.technologyden.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.R;

public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView store_image;
    public TextView store_name;
    public TextView store_location;
    public TextView store_number;
    public TextView store_delivery;
    public Button btnUpdateStore;

    private ItemClickListener itemClickListener;

    public StoreViewHolder(View itemView) {
        super(itemView);

        store_image = (ImageView)itemView.findViewById(R.id.store_image);
        store_name = (TextView) itemView.findViewById(R.id.store_name);
        store_location = (TextView) itemView.findViewById(R.id.store_location);
        store_number = (TextView) itemView.findViewById(R.id.store_number);
        store_delivery = (TextView) itemView.findViewById(R.id.store_delivery);
        btnUpdateStore = (Button)itemView.findViewById(R.id.btnUpdateStore);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);
    }
}
