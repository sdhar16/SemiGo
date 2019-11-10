package com.example.semigo.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.semigo.R;
import com.example.semigo.TripsFragment;
import com.example.semigo.User;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

   /* private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        *//*galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*//*
        return root;
    }*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView tripsList = (ListView)getActivity().findViewById(R.id.past_trips_listview);


        GalleryFragment.MyAdapter Adapter = new GalleryFragment.MyAdapter(new ArrayList<String>());

        tripsList.setAdapter(Adapter);

    }

    private class MyAdapter extends BaseAdapter {

        // override other abstract methods here


        public MyAdapter( ArrayList<String> list)
        {
//            list.add(u.toString());
//            list.add(u.toString());

        }
        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.past_trip_card, container, false);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
}