package com.example.myapplication.UI;

import android.graphics.Color;
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
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView tvCurrentMonth,tvIncome,tvExpense,tvTotal;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private Calendar currentCalendar;
    private DatabaseHelper databaseHelper;

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


        //Database
        databaseHelper = new DatabaseHelper(requireContext());

        // Khởi tạo thư viện AndroidThreeTen
        AndroidThreeTen.init(requireContext());

        // Ánh xạ view
        calendarView = view.findViewById(R.id.calendarView);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        tvTotal = view.findViewById(R.id.tvTotal);

        ImageButton btnNext = view.findViewById(R.id.btnNextMonth);
        ImageButton btnPrev = view.findViewById(R.id.btnPrevMonth);

        // Khởi tạo calendar hiện tại
        currentCalendar = Calendar.getInstance();
        updateMonthTitle();

        // Setup RecyclerView
        allTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);

        // Load dữ liệu
        allTransactions = databaseHelper.getAllTransactions();
        

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
        decorateCalendarWithTransactions();
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


    private void filterTransactionsByMonth() {
        List<Transaction> filteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String targetMonth = sdf.format(currentCalendar.getTime());

        for (Transaction t : allTransactions) {
            if (t.getDate().startsWith(targetMonth)) {
                filteredList.add(t);
            }
        }

        adapter.setTransactionList(filteredList);
        calculateTotal(filteredList);
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
        calculateTotal(filteredList);
    }

    private void calculateTotal(List<Transaction> transactions){
        double income = 0;
        double expense = 0;
        for (Transaction t : transactions){
            if(t.getType().equals("income"))
            {
                income += t.getAmount();
            }else if(t.getType().equals("expense")){
                expense += t.getAmount();
            }
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvIncome.setText( formatter.format(income));
        tvExpense.setText(formatter.format(expense));
        tvTotal.setText(formatter.format(income - expense));
    }
    private void decorateCalendarWithTransactions() {
        Map<CalendarDay, List<Integer>> dotMap = new HashMap<>();

        for (Transaction t : allTransactions) {
            LocalDate localDate = LocalDate.parse(t.getDate());
            CalendarDay day = CalendarDay.from(localDate);

            List<Integer> dots = dotMap.getOrDefault(day, new ArrayList<>());

            if (t.getType().equals("income") && !dots.contains(Color.GREEN)) {
                dots.add(Color.GREEN);
            }
            if (t.getType().equals("expense") && !dots.contains(Color.RED)) {
                dots.add(Color.RED);
            }

            dotMap.put(day, dots);
        }

        calendarView.removeDecorators();
        for (Map.Entry<CalendarDay, List<Integer>> entry : dotMap.entrySet()) {
            calendarView.addDecorator(new TransactionDotDecorator(entry.getKey(), entry.getValue()));
        }    }

}