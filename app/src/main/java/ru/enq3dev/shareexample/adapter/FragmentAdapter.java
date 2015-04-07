package ru.enq3dev.shareexample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import ru.enq3dev.shareexample.fragment.DummyPagerFragment;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private SparseArray<String> entries;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        entries = new SparseArray<>();
        notifyDataSetChanged();
    }

    public String getEntry(int position) {
        return entries.get(position);
    }

    public void addEntry(String entry) {
        entries.put(entries.size(), entry);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return DummyPagerFragment.newInstance(position, entries.get(position));
    }

    @Override
    public int getCount() {
        return entries.size();
    }

}
