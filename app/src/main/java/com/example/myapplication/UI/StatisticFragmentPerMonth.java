package com.example.myapplication.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.TransactionAdapter;
import com.example.myapplication.data.database.DatabaseHelper;
import com.example.myapplication.data.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticFragmentPerMonth extends Fragment {

    private TextView tvCurrentMonth, tvIncomeAmount, tvExpenseAmount, tvTotalAmount;
    private TextView tvExpenseTab, tvIncomeTab;
    private ImageButton btnPrev, btnNext;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DatabaseHelper dbHelper;
    private Calendar currentCalendar;
    private boolean isExpenseTabSelected = true;
    private List<Transaction> currentTransactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic_per_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        tvIncomeAmount = view.findViewById(R.id.tvIncomeAmount);
        tvExpenseAmount = view.findViewById(R.id.tvExpenseAmount);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvExpenseTab = view.findViewById(R.id.tvExpenseTab);
        tvIncomeTab = view.findViewById(R.id.tvIncomeTab);
        btnPrev = view.findViewById(R.id.btnPrevMonth);
        btnNext = view.findViewById(R.id.btnNextMonth);
        recyclerView = view.findViewById(R.id.recyclerView);


        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Khởi tạo calendar hiện tại
        currentCalendar = Calendar.getInstance();
        currentTransactions = new ArrayList<>();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Cập nhật giao diện ban đầu
        updateMonthTitle();
        loadStatistics();
        setupTabSelection();

        // Xử lý chuyển tháng
        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateMonthTitle();
            loadStatistics();
        });

        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateMonthTitle();
            loadStatistics();
        });

        // Xử lý chuyển tab
        tvExpenseTab.setOnClickListener(v -> {
            if (!isExpenseTabSelected) {
                isExpenseTabSelected = true;
                setupTabSelection();
                updateRecyclerView();
            }
        });

        tvIncomeTab.setOnClickListener(v -> {
            if (isExpenseTabSelected) {
                isExpenseTabSelected = false;
                setupTabSelection();
                updateRecyclerView();
            }
        });
        TextView tvYearlyTab = view.findViewById(R.id.tvYearlyTab);
        tvYearlyTab.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new StatisticFragmentPerYear())
                    .commit();
        });

    }

    private void updateMonthTitle() {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;

        // Tính ngày đầu và cuối tháng
        Calendar startOfMonth = (Calendar) currentCalendar.clone();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endOfMonth = (Calendar) currentCalendar.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

        String startDay = dayFormat.format(startOfMonth.getTime());
        String endDay = dayFormat.format(endOfMonth.getTime());
        String monthStr = monthFormat.format(currentCalendar.getTime());

        String title = String.format("%s/%d (%s/%s-%s/%s)",
                monthStr, year, startDay, monthStr, endDay, monthStr);

        tvCurrentMonth.setText(title);
    }

    private void loadStatistics() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String targetMonth = sdf.format(currentCalendar.getTime());

        double totalIncome = 0;
        double totalExpense = 0;
        currentTransactions.clear();

        for (Transaction t : transactions) {
            if (t.getDate().startsWith(targetMonth)) {
                currentTransactions.add(t);
                if (t.getType().equals("income")) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();
                }
            }
        }

        // Cập nhật giao diện
        tvIncomeAmount.setText(String.format("+%sđ", formatCurrency(totalIncome)));
        tvExpenseAmount.setText(String.format("-%sđ", formatCurrency(totalExpense)));

        double total = totalIncome - totalExpense;
        String totalText = (total >= 0 ? "+" : "") + formatCurrency(total) + "đ";
        tvTotalAmount.setText(totalText);

        // Cập nhật màu sắc cho tổng
        if (total >= 0) {
            tvTotalAmount.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            tvTotalAmount.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }

        updateRecyclerView();
    }

    private void setupTabSelection() {
        if (isExpenseTabSelected) {
            tvExpenseTab.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            tvIncomeTab.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvExpenseTab.setBackgroundResource(R.drawable.bottom_border_selected);
            tvIncomeTab.setBackgroundResource(R.drawable.bottom_border_unselected);
        } else {
            tvExpenseTab.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvIncomeTab.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            tvExpenseTab.setBackgroundResource(R.drawable.bottom_border_unselected);
            tvIncomeTab.setBackgroundResource(R.drawable.bottom_border_selected);
        }
    }

    private void updateRecyclerView() {
        List<Transaction> filteredTransactions = new ArrayList<>();

        for (Transaction t : currentTransactions) {
            if (isExpenseTabSelected && t.getType().equals("expense")) {
                filteredTransactions.add(t);
            } else if (!isExpenseTabSelected && t.getType().equals("income")) {
                filteredTransactions.add(t);
            }
        }

        // Sắp xếp theo ngày (mới nhất trước)
        filteredTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        // Cập nhật adapter
        adapter.updateTransactions(filteredTransactions);
    }

    private String formatCurrency(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getNumberInstance(Locale.getDefault());
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(Math.abs(amount));
    }
}