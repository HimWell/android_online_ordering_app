package com.example.technologyden.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productName;
    public ImageView productImage;
    public Button btnProdUpdate;

    private ItemClickListener itemClickListener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        productName = (TextView)itemView.findViewById(R.id.productName);
        productImage = (ImageView)itemView.findViewById(R.id.productImage);
        btnProdUpdate = (Button)itemView.findViewById(R.id.btnProdUpdate);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
