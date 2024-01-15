package com.example.jomride;

// CashbackAdapter.java

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CashbackAdapter extends RecyclerView.Adapter<CashbackAdapter.ViewHolder> {

    private Context context;
    private List<Cashbackinfo> cashbackList;

    public CashbackAdapter(Context context, List<Cashbackinfo> cashbackList) {
        this.context = context;
        this.cashbackList = cashbackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cashback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cashbackinfo cashback = cashbackList.get(position);
        holder.tvAmount.setText(String.valueOf(cashback.getAmount()));
        holder.tvDateTime.setText(cashback.getDateTime());
    }

    @Override
    public int getItemCount() {
        return cashbackList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAmount;
        TextView tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }
}
