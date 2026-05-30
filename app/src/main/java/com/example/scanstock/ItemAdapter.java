package com.example.scanstock;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanstock.database.AppDatabase;
import com.example.scanstock.database.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Context context;
    List<Item> itemList;
    AppDatabase db;
    int lastPosition = -1;

    public ItemAdapter(Context context, List<Item> itemList, AppDatabase db) {
        this.context = context;
        this.itemList = itemList;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvName.setText(item.name);
        holder.tvBarcode.setText(item.barcode != null ? item.barcode : "No barcode");
        holder.tvQuantity.setText(String.valueOf(item.quantity));
        holder.tvCategory.setText(item.category != null ? item.category : "General");

        if (item.quantity <= 2) {
            holder.tvLowBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvLowBadge.setVisibility(View.GONE);
        }

        // Slide in animation for each item
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }

        // Long press to delete
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Delete " + item.name + " from inventory?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.itemDao().delete(item);
                        itemList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, itemList.size());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBarcode, tvQuantity, tvLowBadge, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvBarcode = itemView.findViewById(R.id.tvItemBarcode);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvLowBadge = itemView.findViewById(R.id.tvLowBadge);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
        }
    }
}