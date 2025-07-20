package com.example.myapplication.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

public class StatisticFragmentPerYear extends Fragment {

    private TextView tvCurrentYear, tvIncomeAmountYear, tvExpenseAmountYear, tvTotalAmountYear;
    private TextView tvExpenseTabYear, tvIncomeTabYear;
    private TextView tvMonthlyTab, tvYearlyTab; // Added for tab switching
    private ImageButton btnPrevYear, btnNextYear;
    private RecyclerView recyclerViewYear;
    private TransactionAdapter adapter;
    private DatabaseHelper dbHelper;
    private Calendar currentCalendar;
    private boolean isExpenseTabSelected = true;
    private List<Transaction> currentYearTransactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic_per_year, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Map views
        tvMonthlyTab = view.findViewById(R.id.tvMonthlyTab);
        tvYearlyTab = view.findViewById(R.id.tvYearlyTab);
        tvCurrentYear = view.findViewById(R.id.tvCurrentYear);
        tvIncomeAmountYear = view.findViewById(R.id.tvIncomeAmountYear);
        tvExpenseAmountYear = view.findViewById(R.id.tvExpenseAmountYear);
        tvTotalAmountYear = view.findViewById(R.id.tvTotalAmountYear);
        tvExpenseTabYear = view.findViewById(R.id.tvExpenseTabYear);
        tvIncomeTabYear = view.findViewById(R.id.tvIncomeTabYear);
        btnPrevYear = view.findViewById(R.id.btnPrevYear);
        btnNextYear = view.findViewById(R.id.btnNextYear);
        recyclerViewYear = view.findViewById(R.id.recyclerViewYear);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Initialize current calendar
        currentCalendar = Calendar.getInstance();
        currentYearTransactions = new ArrayList<>();

        // Setup RecyclerView
        recyclerViewYear.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        recyclerViewYear.setAdapter(adapter);

        // Initial UI update
        updateYearTitle();
        loadYearlyStatistics();
        setupTabSelection();

        // Handle year navigation
        btnPrevYear.setOnClickListener(v -> {
            currentCalendar.add(Calendar.YEAR, -1);
            updateYearTitle();
            loadYearlyStatistics();
        });

        btnNextYear.setOnClickListener(v -> {
            currentCalendar.add(Calendar.YEAR, 1);
            updateYearTitle();
            loadYearlyStatistics();
        });

        // Handle tab switching (Expense/Income)
        tvExpenseTabYear.setOnClickListener(v -> {
            if (!isExpenseTabSelected) {
                isExpenseTabSelected = true;
                setupTabSelection();
                updateRecyclerView();
            }
        });

        tvIncomeTabYear.setOnClickListener(v -> {
            if (isExpenseTabSelected) {
                isExpenseTabSelected = false;
                setupTabSelection();
                updateRecyclerView();
            }
        });

        // Handle Monthly/Yearly tab switching
        tvMonthlyTab.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StatisticFragmentPerMonth())
                    .commit();
        });



    }

    private void updateYearTitle() {
        int year = currentCalendar.get(Calendar.YEAR);
        tvCurrentYear.setText(String.valueOf(year));
    }

    private void loadYearlyStatistics() {
        List<Transaction> allTransactions = dbHelper.getAllTransactions();
        String targetYear = String.valueOf(currentCalendar.get(Calendar.YEAR));

        double totalIncome = 0;
        double totalExpense = 0;
        currentYearTransactions.clear();

        for (Transaction t : allTransactions) {
            // Assuming date is stored as "yyyy-MM-dd"
            if (t.getDate().startsWith(targetYear)) {
                currentYearTransactions.add(t);
                if (t.getType().equals("income")) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();
                }
            }
        }

        // Update UI
        tvIncomeAmountYear.setText(String.format("+%sđ", formatCurrency(totalIncome)));
        tvExpenseAmountYear.setText(String.format("-%sđ", formatCurrency(totalExpense)));

        double total = totalIncome - totalExpense;
        String totalText = (total >= 0 ? "+" : "") + formatCurrency(total) + "đ";
        tvTotalAmountYear.setText(totalText);

        // Update total text color
        if (total >= 0) {
            tvTotalAmountYear.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        } else {
            tvTotalAmountYear.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
        }

        updateRecyclerView();
    }

    private void setupTabSelection() {
        if (isExpenseTabSelected) {
            tvExpenseTabYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_selected));
            tvIncomeTabYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_unselected));
            tvExpenseTabYear.setBackgroundResource(R.drawable.bottom_border_selected);
            tvIncomeTabYear.setBackgroundResource(R.drawable.bottom_border_unselected);
        } else {
            tvExpenseTabYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_unselected));
            tvIncomeTabYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_selected));
            tvExpenseTabYear.setBackgroundResource(R.drawable.bottom_border_unselected);
            tvIncomeTabYear.setBackgroundResource(R.drawable.bottom_border_selected);
        }
    }


    private void updateRecyclerView() {
        List<Transaction> filteredTransactions = new ArrayList<>();

        for (Transaction t : currentYearTransactions) {
            if (isExpenseTabSelected && t.getType().equals("expense")) {
                filteredTransactions.add(t);
            } else if (!isExpenseTabSelected && t.getType().equals("income")) {
                filteredTransactions.add(t);
            }
        }

        // Sort by date (newest first)
        filteredTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        // Update adapter
        adapter.setTransactionList(filteredTransactions);
    }

    private String formatCurrency(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getNumberInstance(Locale.getDefault());
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(Math.abs(amount));
    }
}
