package com.lteam.zooar.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lteam.zooar.Fragment.ChooseAnimalFragment;
import com.lteam.zooar.Model.Animal;

import java.util.ArrayList;

public class AdapterPage extends FragmentStatePagerAdapter {

    ArrayList<Animal> animals;
    public AdapterPage(@NonNull FragmentManager fm, ArrayList<Animal> animals) {
        super(fm);
        this.animals = animals;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ChooseAnimalFragment(position,animals.get(position));
    }

    @Override
    public int getCount() {
        return animals.size();
    }
}
