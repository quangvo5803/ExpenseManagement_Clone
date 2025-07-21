package com.example.myapplication.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class UpdateAndDeleteFragment extends Fragment {

    private static final String ARG_TRANSACTION_ID = "transaction_id";
    private int transactionId;

    private EditText etNote, etAmount;
    private TextView etDate;
    private RecyclerView rvCategory;
    private Button btnSave, btnDelete;

    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private String selectedCategory;

    private DatabaseHelper db;

    public static UpdateAndDeleteFragment newInstance(int id) {
        UpdateAndDeleteFragment fragment = new UpdateAndDeleteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TRANSACTION_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(requireContext());
        if (getArguments() != null) {
            transactionId = getArguments().getInt(ARG_TRANSACTION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_and_delete, container, false);

        etNote = view.findViewById(R.id.etNote);
        etAmount = view.findViewById(R.id.etAmount);
        etDate = view.findViewById(R.id.etDate);
        rvCategory = view.findViewById(R.id.rvCategory);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);

        Transaction transaction = db.getTransactionById(transactionId);
        if (transaction == null) {
            Toast.makeText(getContext(), "Không tìm thấy giao dịch", Toast.LENGTH_SHORT).show();
            return view;
        }

        etNote.setText(transaction.getNote());
        etAmount.setText(String.format("%.0f", transaction.getAmount()));
        etDate.setText(transaction.getDate());

        selectedCategory = transaction.getCategory();
        setupCategoryList(transaction.getType());

        btnSave.setOnClickListener(v -> {
            String note = etNote.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (amountStr.isEmpty() || selectedCategory == null) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            transaction.setNote(note);
            transaction.setAmount(amount);
            transaction.setCategory(selectedCategory);

            db.updateTransaction(transaction);
            Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();

        });

        btnDelete.setOnClickListener(v -> {
            db.deleteTransaction(transactionId);
            Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void setupCategoryList(String type) {
        categoryList = new ArrayList<>();
        if (type.equals("expense")) {
            categoryList.add(new Category("Ăn uống", R.drawable.ic_food));
            categoryList.add(new Category("Chi tiêu hàng ngày", R.drawable.ic_laundary));
            categoryList.add(new Category("Quần áo", R.drawable.ic_clother));
            categoryList.add(new Category("Quà", R.drawable.ic_gift));
            categoryList.add(new Category("Phí giao lưu", R.drawable.ic_beer));
            categoryList.add(new Category("Di chuyển", R.drawable.ic_transport));
            categoryList.add(new Category("Mart", R.drawable.ic_home));
            categoryList.add(new Category("Y tế", R.drawable.ic_medical));
        } else if(type.equals("income")) {
            categoryList.add(new Category("Tiền lương", R.drawable.ic_income));
            categoryList.add(new Category("Tiền đầu tư", R.drawable.ic_invest));
            categoryList.add(new Category("Tiền phụ cấp", R.drawable.ic_save));
            categoryList.add(new Category("Tiền thưởng", R.drawable.ic_gift));
        }

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, category -> selectedCategory = category.getName());
        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvCategory.setAdapter(categoryAdapter);

        // ✅ Đặt lại danh mục được chọn ban đầu (nếu có)
        if (selectedCategory != null) {
            categoryAdapter.setSelectedCategory(selectedCategory);
        }
    }
}
