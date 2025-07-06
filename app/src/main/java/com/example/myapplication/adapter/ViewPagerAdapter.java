package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.UI.AddTransactionFragment;
import com.example.myapplication.UI.ChiFragment;
import com.example.myapplication.UI.ThuFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new ChiFragment();
        else return new ThuFragment(); // rỗng
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

