package com.example.semigo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TripsFragment extends Fragment {

    MyAdapter Adapter;
    String UID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trips, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView tripsList = (ListView)getActivity().findViewById(R.id.trips_listview);
        readTrips(tripsList);
    }

    public void readTrips(final ListView tripsList) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tripRef;
        tripRef = database.getReference("CurrentTrips");
        final ArrayList<CurrentTrips> trips = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

        tripRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                trips.clear();
                for(DataSnapshot trip: snapshot.getChildren()) {
                    CurrentTrips trip1 = trip.getValue(CurrentTrips.class);
                    Log.i("trip id",trip1.id+","+UID);
                    if(trip1.user1.equals(UID) || trip1.user2.equals(UID)) {
                        Log.i("user trip", UID);
                        trips.add(trip.getValue(CurrentTrips.class));
                    }
                }
                Collections.reverse(trips);
                Adapter = new MyAdapter(trips);
                tripsList.setAdapter(Adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class MyAdapter extends BaseAdapter {

        // override other abstract methods here
//        User u = new User("Tanish", "tanish@gmail.com", "HI");

        private ArrayList<CurrentTrips> list;

        public MyAdapter( ArrayList<CurrentTrips> list)
        {
            this.list = list;

        }
        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.trip_card, container, false);
            }

            //Adapter populate
//            Trips trip = list.get(position);
            //           TextView trip_status = convertView.findViewById(R.id.trip_status);
            // trip_status.setText(trip.getTag());

            CardView cardView = convertView.findViewById(R.id.tripcard);
            cardView.setCardElevation(10);
            /*cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), InfoActivity.class));
                }
            });*/

            Button book = convertView.findViewById(R.id.start_chat_upcoming);
//            PackageManager pm = getPackageManager();
            CurrentTrips trip = list.get(position);
            if(!UID.equals(trip.bookie)) book.setEnabled(false);
            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("main", "book now");
                    try {
                        String uri = "uber://?action=setPickup&pickup[latitude]=28.5456282&pickup[longitude]=77.2731505&pickup[nickname]=IIITD";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                    catch (Exception e){
                        Toast.makeText(getContext(),"Please install Uber on your phone",Toast.LENGTH_SHORT).show();
                    }
                }
            });



            Button chat = convertView.findViewById(R.id.book_cab);
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("main", "start chat now");
                    Intent chat_activity = new Intent(getActivity(),MessageActivity.class);
                    System.out.println(position+":trip_id");
                    chat_activity.putExtra("trip_id",list.size()-1-position);
                    startActivity(chat_activity);
                }
            });


            TextView text = convertView.findViewById(R.id.trip_status);
            text.setText(list.get(position).status);

            text = convertView.findViewById(R.id.pickup);
            text.setText("Pickup: "+trip.org);

            text = convertView.findViewById(R.id.drop);
            if(UID.equals(trip.user1)) text.setText(trip.dest1.name);
            else text.setText("Drop: "+trip.dest2.name);

            text = convertView.findViewById(R.id.trip_time);
            text.setText("Time: "+trip.st_time);

            text = convertView.findViewById(R.id.trip_date);
            text.setText("Date: "+trip.date);


            return convertView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
}