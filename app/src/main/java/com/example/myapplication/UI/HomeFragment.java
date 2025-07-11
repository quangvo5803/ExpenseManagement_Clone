package com.example.myapplication.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.TransactionAdapter;
import com.example.myapplication.data.database.DatabaseHelper;
import com.example.myapplication.data.model.Transaction;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment  {

    private MaterialCalendarView calendarView;
    private TextView tvCurrentMonth;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private Calendar currentCalendar;
    private TextView tvIncome, tvExpense, tvTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo thư viện AndroidThreeTen
        AndroidThreeTen.init(requireContext());

        // Ánh xạ view
        calendarView = view.findViewById(R.id.calendarView);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        ImageButton btnNext = view.findViewById(R.id.btnNextMonth);
        ImageButton btnPrev = view.findViewById(R.id.btnPrevMonth);

        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        tvTotal = view.findViewById(R.id.tvTotal);

        // Khởi tạo calendar hiện tại
        currentCalendar = Calendar.getInstance();
        updateMonthTitle();

        // Setup RecyclerView
        allTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);

        // Load dữ liệu mẫu
        loadDummyTransactions();

        // Chuyển tháng
        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarView();
        });

        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarView();
        });

        // Chọn ngày trên CalendarView
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            filterTransactionsByDate(date);
        });

        // Load transaction theo tháng hiện tại
        filterTransactionsByMonth();
    }


    private void updateCalendarView() {
        LocalDate date = LocalDate.of(
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH) + 1,
                1
        );
        calendarView.setCurrentDate(CalendarDay.from(date));
        calendarView.clearSelection();
        updateMonthTitle();
        filterTransactionsByMonth();
    }

    private void updateMonthTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy");
        tvCurrentMonth.setText(sdf.format(currentCalendar.getTime()));
    }

    private void loadDummyTransactions() {
        /*allTransactions.add(new Transaction(1, "expense", "Ăn uống", 50000, "2025-07-01", "Ăn sáng"));
        allTransactions.add(new Transaction(2, "income", "Lương", 15000000, "2025-07-05", "Lương tháng 7"));
        allTransactions.add(new Transaction(3, "expense", "Di chuyển", 30000, "2025-07-10", "Xe bus"));
        allTransactions.add(new Transaction(4, "expense", "Y tế", 200000, "2025-07-01", "Mua thuốc"));
        allTransactions.add(new Transaction(5, "income", "Tiền thưởng", 2000000, "2025-07-15", "Thưởng dự án"));*/
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        allTransactions = dbHelper.getAllTransactions();

        // Debug log
        /*Log.d("HomeFragment", "Loaded " + allTransactions.size() + " transactions from database");
        for (Transaction t : allTransactions) {
            Log.d("HomeFragment", "Transaction: " + t.getType() + " - " + t.getCategory() + " - " + t.getAmount() + " - " + t.getDate());
        }*/
    }

    private void filterTransactionsByMonth() {
        List<Transaction> filteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String targetMonth = sdf.format(currentCalendar.getTime());

        for (Transaction t : allTransactions) {
            if (t.getDate().startsWith(targetMonth)) {
                filteredList.add(t);
            }
        }

        // Debug log
        /*android.util.Log.d("HomeFragment", "Filtered " + filteredList.size() + " transactions for month: " + targetMonth);*/

        adapter.setTransactionList(filteredList);
        updateSummary(filteredList);
    }

    private void filterTransactionsByDate(CalendarDay date) {
        List<Transaction> filteredList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String targetDate = date.getDate().format(formatter);

        for (Transaction t : allTransactions) {
            if (t.getDate().equals(targetDate)) {
                filteredList.add(t);
            }
        }

        adapter.setTransactionList(filteredList);
        updateSummary(filteredList);
    }
    private void updateSummary(List<Transaction> list) {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : list) {
            if (t.getType().equals("income")) {
                totalIncome += t.getAmount();
            } else if (t.getType().equals("expense")) {
                totalExpense += t.getAmount();
            }
        }

        double total = totalIncome - totalExpense;

        tvIncome.setText("Thu nhập: " + formatMoney(totalIncome) + "đ");
        tvExpense.setText("Chi tiêu: " + formatMoney(totalExpense) + "đ");
        tvTotal.setText("Tổng: " + formatMoney(total) + "đ");
    }

    private String formatMoney(double amount) {
        return String.format("%,.0f", amount);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        long lastSaveTime = prefs.getLong("last_save_time", 0);

        if (lastSaveTime > 0) {
            Log.d("HomeFragment", "Reload triggered: last_save_time=" + lastSaveTime);
            loadDummyTransactions();         // Load lại dữ liệu mới nhất từ DB
            filterTransactionsByMonth();    // Lọc lại theo tháng đang hiển thị
            prefs.edit().putLong("last_save_time", 0).apply(); // reset
        }
    }

}