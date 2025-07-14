package com.example.myapplication.UI;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.data.database.DatabaseHelper;
import com.example.myapplication.data.model.Category;
import com.example.myapplication.data.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddIncomeFragment extends Fragment {

    private TextView tvDate, tvNote;
    private EditText etAmount;
    private Button btnSave;
    private RecyclerView rvCategory;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    private String selectedCategory = "";
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_income, container, false);

        // Ánh xạ view
        tvDate = view.findViewById(R.id.tvDate);
        tvNote = view.findViewById(R.id.tvNote);
        etAmount = view.findViewById(R.id.etAmount);
        btnSave = view.findViewById(R.id.btnSave);
        rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));

        ImageView ivPrevDate = view.findViewById(R.id.ivPrevDate);
        ImageView ivNextDate = view.findViewById(R.id.ivNextDate);

        // Set ngày hôm nay
        tvDate.setText(displayFormat.format(calendar.getTime()));

        // Chọn ngày với DatePicker
        tvDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        tvDate.setText(displayFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        // Chuyển ngày
        ivPrevDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            tvDate.setText(displayFormat.format(calendar.getTime()));
        });

        ivNextDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            tvDate.setText(displayFormat.format(calendar.getTime()));
        });


        // Danh sách danh mục
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Tiền lương", R.drawable.ic_income));
        categoryList.add(new Category("Tiền đầu tư", R.drawable.ic_invest));
        categoryList.add(new Category("Tiền phụ cấp", R.drawable.ic_save));
        categoryList.add(new Category("Tiền thưởng", R.drawable.ic_gift));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, category -> {
            selectedCategory = category.getName();
        });

        rvCategory.setAdapter(categoryAdapter);

        // Chọn ngày với DatePickerDialog
        tvDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        tvDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Sự kiện lưu
        btnSave.setOnClickListener(v -> {
            String note = tvNote.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String date = tvDate.getText().toString().trim();

            if (amountStr.isEmpty() || date.isEmpty() || selectedCategory == null || selectedCategory.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String dbFormattedDate = convertDateToDbFormat(date);

            Transaction transaction = new Transaction(
                    0,
                    "income",
                    selectedCategory,
                    amount,
                    dbFormattedDate,
                    note
            );

            // Lưu DB
            DatabaseHelper db = new DatabaseHelper(getContext());
            db.addTransaction(transaction);

            Toast.makeText(getContext(), "Đã lưu giao liệu", Toast.LENGTH_SHORT).show();

            // Reset form
            tvNote.setText("");
            etAmount.setText("");
            tvDate.setText("");
            selectedCategory = "";
            categoryAdapter.clearSelection();

        });

        return view;
    }
    private String convertDateToDbFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dbFormat.format(inputFormat.parse(inputDate));
        } catch (Exception e) {
            e.printStackTrace();
            return inputDate;
        }
    }
}
