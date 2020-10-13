package com.example.technologyden.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.R;

public class ProductCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productCategoryName;
    public ImageView productCategoryImage;
    public Button btnUpdateProdCat;

    private ItemClickListener itemClickListener;

    public ProductCategoryViewHolder(View itemView) {
        super(itemView);

        productCategoryName = (TextView)itemView.findViewById(R.id.productCategoryName);
        productCategoryImage = (ImageView)itemView.findViewById(R.id.productCategoryImage);
        btnUpdateProdCat = (Button)itemView.findViewById(R.id.btnUpdateProdCat);

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
