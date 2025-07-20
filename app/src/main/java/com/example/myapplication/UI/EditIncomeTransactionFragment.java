package com.example.myapplication.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.data.database.DatabaseHelper;
import com.example.myapplication.data.model.Category;
import com.example.myapplication.data.model.Transaction;

import java.util.List;
import java.util.Locale;

public class EditIncomeTransactionFragment extends Fragment {

    private EditText etAmount, etNote;
    private TextView etDate;
    private RecyclerView rvCategory;
    private Button btnSave,btnDelete;
    private String selectedCategory;
    private Transaction transaction;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_transaction_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHelper(requireContext());

        transaction = (Transaction) getArguments().getSerializable("transaction");

        etAmount = view.findViewById(R.id.etAmount);
        etNote = view.findViewById(R.id.etNote);
        etDate = view.findViewById(R.id.etDate);
        rvCategory = view.findViewById(R.id.rvCategory);
        btnSave = view.findViewById(R.id.btnSave);

        // Load data lên view
        etAmount.setText(String.valueOf(transaction.getAmount()));
        etNote.setText(transaction.getNote());
        etDate.setText(transaction.getDate());
        selectedCategory = transaction.getCategory();

        // Ngày
        etDate.setOnClickListener(v -> showDatePicker());

        // Danh mục
        setupCategoryRecycler();

        // Lưu
        btnSave.setOnClickListener(v -> {
            double amount = Double.parseDouble(etAmount.getText().toString().trim());
            transaction.setAmount(amount);
            transaction.setNote(etNote.getText().toString().trim());
            transaction.setDate(etDate.getText().toString());
            transaction.setCategory(selectedCategory);

            db.updateTransaction(transaction);
            Toast.makeText(requireContext(), "Đã cập nhật giao dịch", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnDelete = view.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc chắn muốn xoá giao dịch này không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        db.deleteTransaction(transaction.getId()); // <-- gọi hàm xoá
                        requireActivity().getSupportFragmentManager().popBackStack(); // quay lại
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

    }

    private void showDatePicker() {
        String[] dateParts = transaction.getDate().split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);

        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            etDate.setText(dateStr);
        }, year, month, day).show();
    }

    private void setupCategoryRecycler() {
        List<Category> categories = EditTransactionFragment.CategoryProvider.getIncomeCategories();

        CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories, category -> {
            selectedCategory = category.getName();
        });

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equalsIgnoreCase(selectedCategory)) {
                adapter.selectedPosition = i;
                break;
            }
        }

        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvCategory.setAdapter(adapter);
    }
}
