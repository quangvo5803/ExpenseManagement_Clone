package com.example.myapplication.UI;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AddTransactionFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public AddTransactionFragment() {
        // Bắt buộc phải có constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Gắn layout fragment_nhap.xml
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Ánh xạ view
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set adapter cho ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        // Gắn TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            TextView tabText = new TextView(getContext());
            tabText.setText(position == 0 ? "Sổ Chi" : "Sổ Thu");
            tabText.setTextColor(Color.WHITE);
            tabText.setPadding(20, 8, 20, 8);
            tabText.setTextSize(15);
            tab.setCustomView(tabText);
        }).attach();


        return view;
    }
}

