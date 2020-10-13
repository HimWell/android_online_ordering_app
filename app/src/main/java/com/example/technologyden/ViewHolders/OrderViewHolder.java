package com.example.technologyden.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cart_item_name;
    public TextView cart_item_price;
    public TextView cart_item_count;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        cart_item_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        cart_item_price = (TextView)itemView.findViewById(R.id.cart_item_price);
        cart_item_count = (TextView) itemView.findViewById(R.id.cart_item_count);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
