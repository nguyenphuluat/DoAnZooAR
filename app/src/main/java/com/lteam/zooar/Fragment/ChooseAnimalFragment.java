package com.lteam.zooar.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.lteam.zooar.Activity.ZooMoveSceneformActivity;
import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;

public class ChooseAnimalFragment extends Fragment {

    int post;
    Animal animal;

    public ChooseAnimalFragment(int pos, Animal animal) {
        post = pos;
        this.animal = animal;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_animal, container, false);
        ImageView imageView=view.findViewById(R.id.imgAnimalChoose);
        TextView txtTen=view.findViewById(R.id.txtTen);
        Picasso.get().load(APIUtils.base_url + animal.getUrlimage()).into(imageView);
        txtTen.setText(animal.getName());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), ZooMoveSceneformActivity.class);
                intent1.putExtra("animal", animal);
                startActivity(intent1);
            }
        });
        return view;
    }
}
