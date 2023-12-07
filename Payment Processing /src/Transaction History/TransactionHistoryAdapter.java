package com.example.jomride;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>{
    ArrayList<HistoryObject> historyObjectList;
    Context context;
    public TransactionHistoryAdapter(ArrayList<HistoryObject>data,HistoryActivity activity) {
        this.historyObjectList=data;
        context=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transaction_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (historyObjectList != null && position < historyObjectList.size()) {
            HistoryObject historyObject = historyObjectList.get(position);
            HistoryData historyData = historyObject.getData();

            holder.dateTime.setText(historyData.getDateTime());
            holder.amount.setText(historyData.getAmount());
            holder.type.setText(historyData.getType());
            holder.description.setText(historyData.getDescription());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Edit the popup class contructor to meet your requirement
                    PopUpClass tpopup = new PopUpClass(historyObject);
                    //Edit rating display pop up method
                    tpopup.showTransactionHistoryPopupWindow(v);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if(historyObjectList.size()<30)
            return historyObjectList.size();
        else
            return 30;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView dateTime;
        TextView description;
        TextView type;
        TextView amount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTime=itemView.findViewById(R.id.TV_card_DateTime);
            description=itemView.findViewById(R.id.TV_card_Description);
            type=itemView.findViewById(R.id.TV_card_type);
            amount=itemView.findViewById(R.id.TV_card_amount);
        }
    }

    public void addItem(HistoryObject obj){
        historyObjectList.add(obj);
        notifyDataSetChanged();
    }
}
