package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<Transaction> transactions;
    private Map<String, String> categoryIcons;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        initCategoryIcons();
    }

    private void initCategoryIcons() {
        categoryIcons = new HashMap<>();
        // Icons cho chi tiêu
        categoryIcons.put("Ăn uống", "🍽️");
        categoryIcons.put("Di chuyển", "🚗");
        categoryIcons.put("Mua sắm", "🛍️");
        categoryIcons.put("Tiền nhà", "🏠");
        categoryIcons.put("Điện nước", "💡");
        categoryIcons.put("Internet", "📶");
        categoryIcons.put("Xăng xe", "⛽");
        categoryIcons.put("Y tế", "🏥");
        categoryIcons.put("Giải trí", "🎮");
        categoryIcons.put("Học tập", "📚");
        categoryIcons.put("Du lịch", "✈️");

        // Icons cho thu nhập
        categoryIcons.put("Lương", "💰");
        categoryIcons.put("Freelance", "💻");
        categoryIcons.put("Tiền thưởng", "🎁");
        categoryIcons.put("Bán hàng", "🛒");
        categoryIcons.put("Đầu tư", "📈");
        categoryIcons.put("Thưởng", "🏆");
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    // Thêm method setTransactionList để khớp với HomeFragment
    public void setTransactionList(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryIcon, tvCategory, tvDescription, tvDate, tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        public void bind(Transaction transaction) {
            // Set icon
            String icon = categoryIcons.get(transaction.getCategory());
            if (icon != null) {
                tvCategoryIcon.setText(icon);
            } else {
                tvCategoryIcon.setText("📋");
            }

            // Set category
            tvCategory.setText(transaction.getCategory());

            // Set description (sửa từ getDescription() thành getNote())
            tvDescription.setText(transaction.getNote());

            // Set date
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = outputFormat.format(inputFormat.parse(transaction.getDate()));
                tvDate.setText(formattedDate);
            } catch (Exception e) {
                tvDate.setText(transaction.getDate());
            }

            // Set amount
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            String formattedAmount = formatter.format(transaction.getAmount());

            if (transaction.getType().equals("income")) {
                tvAmount.setText("+" + formattedAmount + "đ");
                tvAmount.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
            } else {
                tvAmount.setText("-" + formattedAmount + "đ");
                tvAmount.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
            }
        }
    }
}