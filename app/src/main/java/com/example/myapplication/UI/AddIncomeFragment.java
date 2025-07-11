package com.example.myapplication.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.data.database.DatabaseHelper;
import com.example.myapplication.data.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddIncomeFragment extends Fragment {

    private TextView tvDate, tvNote;
    private EditText etAmount;
    private Button btnSave;
    private LinearLayout itemSalary, itemSubvention, itemBonus, itemInvest;
    private int selectedIndex = -1;

    private final String[] categoryNames = {"Tiền lương", "Tiền phụ cấp", "Tiền thưởng", "Tiền đầu tư"};
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
        itemSalary = view.findViewById(R.id.itemSalary);
        itemSubvention = view.findViewById(R.id.itemSubvention);
        itemBonus = view.findViewById(R.id.itemBonus);
        itemInvest = view.findViewById(R.id.itemInvest);

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

        // Ghi chú
        tvNote.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Nhập ghi chú");
            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(tvNote.getText().toString().equals("Chưa nhập vào") ? "" : tvNote.getText().toString());
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String note = input.getText().toString();
                tvNote.setText(note.isEmpty() ? "Chưa nhập vào" : note);
                tvNote.setTextColor(note.isEmpty() ? Color.GRAY : Color.WHITE);
            });
            builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Chọn danh mục
        LinearLayout[] categories = {itemSalary, itemSubvention, itemBonus, itemInvest};
        for (int i = 0; i < categories.length; i++) {
            int index = i;
            categories[i].setOnClickListener(v -> {
                for (LinearLayout cat : categories) {
                    cat.setBackgroundResource(R.drawable.bg_category_item);
                }
                categories[index].setBackgroundResource(R.drawable.bg_category_selected);
                selectedIndex = index;
            });
        }

        // Sự kiện lưu
        btnSave.setOnClickListener(v -> {
            String note = tvNote.getText().toString().equals("Chưa nhập vào") ? "" : tvNote.getText().toString();
            String amountStr = etAmount.getText().toString().trim();

            if (amountStr.isEmpty() || selectedIndex == -1) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String category = categoryNames[selectedIndex];
            String dbDate = dbFormat.format(calendar.getTime());

            Transaction transaction = new Transaction(
                    0,
                    "income",
                    category,
                    amount,
                    dbDate,
                    note
            );

            // Lưu DB
            DatabaseHelper db = new DatabaseHelper(requireContext());
            db.addTransaction(transaction);

            // Lưu dấu hiệu để HomeFragment refresh
            SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            prefs.edit().putLong("last_save_time", System.currentTimeMillis()).apply();

            Toast.makeText(requireContext(), "Đã lưu dữ liệu", Toast.LENGTH_SHORT).show();

            // Reset form
            etAmount.setText("");
            tvNote.setText("Chưa nhập vào");
            tvNote.setTextColor(Color.GRAY);
            selectedIndex = -1;

            for (LinearLayout cat : categories) {
                cat.setBackgroundResource(R.drawable.bg_category_item);
            }
        });

        return view;
    }
}
