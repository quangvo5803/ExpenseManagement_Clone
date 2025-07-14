package com.example.myapplication.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddExpenseFragment extends Fragment {
    private RecyclerView rvCategory;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    private String selectedCategory = "";
    private TextView etDate;
    private EditText etNote, etAmount;
    private Button btnSave;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false); // bạn đã có layout này

        etDate = view.findViewById(R.id.etDate);
        etNote = view.findViewById(R.id.etNote);
        etAmount = view.findViewById(R.id.etAmount);
        btnSave = view.findViewById(R.id.btnSave);
        rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));

        ImageView ivPrevDate = view.findViewById(R.id.ivPrevDate);
        ImageView ivNextDate = view.findViewById(R.id.ivNextDate);

        // Set ngày hôm nay
        etDate.setText(displayFormat.format(calendar.getTime()));

        // Chọn ngày với DatePicker
        etDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        etDate.setText(displayFormat.format(calendar.getTime()));
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
            etDate.setText(displayFormat.format(calendar.getTime()));
        });

        ivNextDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            etDate.setText(displayFormat.format(calendar.getTime()));
        });

        // Danh sách danh mục
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Ăn uống", R.drawable.ic_food));
        categoryList.add(new Category("Chi tiêu hàng ngày", R.drawable.ic_laundary));
        categoryList.add(new Category("Quần áo", R.drawable.ic_clother));
        categoryList.add(new Category("Quà", R.drawable.ic_gift));
        categoryList.add(new Category("Phí giao lưu", R.drawable.ic_beer));
        categoryList.add(new Category("Di chuyển", R.drawable.ic_transport));
        categoryList.add(new Category("Mart", R.drawable.ic_home));
        categoryList.add(new Category("Y tế", R.drawable.ic_medical));


        categoryAdapter = new CategoryAdapter(getContext(), categoryList, category -> {
            selectedCategory = category.getName();

        });

        rvCategory.setAdapter(categoryAdapter);

        // Chọn ngày với DatePickerDialog
        etDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        etDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Sự kiện nút Lưu
        btnSave.setOnClickListener(v -> {
            String note = etNote.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (amountStr.isEmpty() || date.isEmpty() || selectedCategory == null || selectedCategory.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String dbFormattedDate = convertDateToDbFormat(date);  // Chuyển "dd/MM/yyyy" → "yyyy-MM-dd"

            Transaction transaction = new Transaction(
                    0,
                    "income",
                    selectedCategory,
                    amount,
                    dbFormattedDate,
                    note
            );

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.addTransaction(transaction);

            Toast.makeText(getContext(), "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();

            // Reset form
            etNote.setText("");
            etAmount.setText("");
            etDate.setText("");
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


