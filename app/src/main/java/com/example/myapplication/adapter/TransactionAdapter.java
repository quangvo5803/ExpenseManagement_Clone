package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.data.model.Transaction;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvCategory.setText(transaction.getCategory());
        holder.tvNote.setText(transaction.getNote());
        holder.tvDate.setText(transaction.getDate());

        // Hiển thị số tiền và màu sắc
        if (transaction.getType().equals("income")) {
            holder.tvAmount.setText("+ " + transaction.getAmountInVND());
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvAmount.setText("- " + transaction.getAmountInVND());
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
        }

        // Set icon theo category
        holder.imgIcon.setImageResource(getIconResource(transaction.getCategory()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvCategory, tvNote, tvAmount, tvDate;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
    public void setTransactionList(List<Transaction> list) {
        this.transactionList = list;
        notifyDataSetChanged();
    }
    // Hàm chọn icon theo category
    private int getIconResource(String category) {
        switch (category) {
            case "Ăn uống":
                return R.drawable.ic_food;
            case "Di chuyển":
                return R.drawable.ic_transport;
            case "Quần áo":
                return R.drawable.ic_clother;
            case "Chi tiêu hàng ngày":
                return  R.drawable.ic_laundary;
            case "Phí giao lưu":
                return R.drawable.ic_beer;
            case "Y tế":
                return R.drawable.ic_medical;
            case "Tiền nhà":
                return R.drawable.ic_home;
            case "Tiền lương":
                return R.drawable.ic_income;
            case "Tiền phụ cấp":
                return  R.drawable.ic_save;
            case "Tiền thưởng":
                return  R.drawable.ic_gift;
            case "Tiền đầu tư":
                return  R.drawable.ic_invest;
            default:
                return R.drawable.ic_launcher_foreground;
        }
    }
}