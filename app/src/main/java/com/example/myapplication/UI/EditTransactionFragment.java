package com.example.myapplication.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditTransactionFragment extends Fragment {
    private Transaction transaction;
    private EditText etAmount, etNote;
    private TextView tvDate;
    private RecyclerView rvCategory;
    private Button btnSave, btnDelete;

    private String selectedCategory = null;
    private DatabaseHelper db;

    public static EditTransactionFragment newInstance(Transaction transaction) {
        EditTransactionFragment fragment = new EditTransactionFragment();
        Bundle args = new Bundle();
        args.putSerializable("transaction", transaction);
        args.putString("type", transaction.getType());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        String type = transaction != null ? transaction.getType() : "expense";

        if (type.equals("income")) {
            view = inflater.inflate(R.layout.fragment_edit_transaction_income, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_edit_transaction_expense, container, false);
        }

        initViews(view);
        populateFields();
        setupCategoryRecycler();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        populateFields();
        setupCategoryRecycler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transaction = (Transaction) getArguments().getSerializable("transaction");
        }
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        etNote = view.findViewById(R.id.tvNote);
        tvDate = view.findViewById(R.id.tvDate);
        rvCategory = view.findViewById(R.id.rvCategory);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);

        btnSave.setText(transaction.getType().equals("income") ?
                "Chỉnh sửa khoản thu" : "Chỉnh sửa khoản chi");

        tvDate = view.findViewById(R.id.tvDate);

        tvDate.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> updateTransaction());
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());

        db = new DatabaseHelper(requireContext());
    }

    private void populateFields() {
        etAmount.setText(String.valueOf(transaction.getAmount()));
        if (etNote != null) {
            etNote.setText(transaction.getNote());
        }
        selectedCategory = transaction.getCategory();
    }

    private void updateTransaction() {
        String amountText = etAmount.getText().toString().trim();
        if (amountText.isEmpty()) return;

        double amount = Double.parseDouble(amountText);
        transaction.setAmount(amount);

        if (etNote != null) {
            transaction.setNote(etNote.getText().toString());
        }

        transaction.setDate(tvDate.getText().toString());
        transaction.setCategory(selectedCategory);

        db.updateTransaction(transaction);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void setupCategoryRecycler() {
        List<Category> categories = transaction.getType().equals("income") ?
                CategoryProvider.getIncomeCategories() :
                CategoryProvider.getExpenseCategories();

        CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories, category -> {
            selectedCategory = category.getName(); // Lưu tên danh mục được chọn
        });

        // Gán danh mục đã chọn ban đầu nếu có
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equalsIgnoreCase(selectedCategory)) {
                adapter.selectedPosition = i; // Gán vị trí đã chọn
                break;
            }
        }

        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvCategory.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        String[] dateParts = transaction.getDate().split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, y, m, d) -> {
            String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            tvDate.setText(dateStr);
        }, year, month, day);

        dialog.show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá giao dịch này?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    db.deleteTransaction(transaction.getId());
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    public class CategoryProvider {
        public static List<Category> getIncomeCategories() {
            List<Category> list = new ArrayList<>();
            list.add(new Category("Tiền lương", R.drawable.ic_income));
            list.add(new Category("Tiền đầu tư", R.drawable.ic_invest));
            list.add(new Category("Tiền phụ cấp", R.drawable.ic_save));
            list.add(new Category("Tiền thưởng", R.drawable.ic_gift));

            return list;
        }

        public static List<Category> getExpenseCategories() {
            List<Category> list = new ArrayList<>();

            list.add(new Category("Ăn uống", R.drawable.ic_food));
            list.add(new Category("Chi tiêu hàng ngày", R.drawable.ic_laundary));
            list.add(new Category("Quần áo", R.drawable.ic_clother));
            list.add(new Category("Quà", R.drawable.ic_gift));
            list.add(new Category("Phí giao lưu", R.drawable.ic_beer));
            list.add(new Category("Di chuyển", R.drawable.ic_transport));
            list.add(new Category("Mart", R.drawable.ic_home));
            list.add(new Category("Y tế", R.drawable.ic_medical));
            return list;
        }
    }

}
