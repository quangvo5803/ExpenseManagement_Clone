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