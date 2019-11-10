package com.example.semigo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.semigo.ui.home.HomeFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;

public class ScheduleFragment extends Fragment implements DatePickerFragment.OnCompleteListener, TimePickerFragment.OnTimeCompleteListener {

    // FIREBASE

    FirebaseDatabase database;

    // MAPS VARIABLES
    MapView mMapView;
    public GoogleMap googleMap;
    com.example.semigo.LatLng start = new com.example.semigo.LatLng(0.0, 0.0);
    LatLng end = new LatLng(-1, -1);
    String endName = "";
    Boolean startSet = false;
    Boolean endSet = false;
    CameraUpdate cameraUpdate;
    Marker endMarker;
    Polyline polyline;
    LatLngBounds.Builder builder;
    String org_name = "";
    DatabaseReference myRef2;
    EditText edit;
    CameraUpdate cu;

    //SCHEDULE
    String UID, from_time="", to_time="", ride_date="";
    Button button_schedule_now;
    Boolean buttonSet = false;

    public class DistTime {
        public final String distance;
        public final String time;

        public DistTime(String distance, String time) {
            this.distance = distance;
            this.time = time;
        }
    }

    @Override
    public void onComplete(String date) {
        Log.d("main", date);
        Button button_date_picker = getView().findViewById(R.id.date_picker);
        button_date_picker.setText(date);
        ride_date = date;
    }

    @Override
    public void onTimeComplete(String time, int tag) {
        Log.d("main", time);
        if (tag == 0) {
            Button button_date_picker = getView().findViewById(R.id.from_time);
            button_date_picker.setText("From: " + time);
            from_time = time;
        } else if (tag == 1) {
            Button button_date_picker = getView().findViewById(R.id.to_time);
            button_date_picker.setText("To: " + time);
            to_time = time;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View rootView = inflater.inflate(R.layout.schedule_new, container, false);
        Log.d("main", "on create view");
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get start position of user

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();
        database = FirebaseDatabase.getInstance();
        Log.d("UID", UID);

        final DatabaseReference myRef;
        myRef = database.getReference("User");


        //ORG

        myRef.child(UID).child("org").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                org_name = snapshot.getValue().toString();
                Log.i("org", org_name);
                edit.setText(org_name);

               // getActivity()
                myRef2 = database.getReference("Organization");
                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.i("snap2", snapshot.getValue().toString());
                        for (DataSnapshot orgSnap : snapshot.getChildren()) {
                            if (orgSnap.getKey().equals(org_name)) {
                                String laa = orgSnap.child("ll").getValue(String.class);
                                start.latitude = Double.parseDouble(laa.substring(0, laa.indexOf(",")));
                                start.longitude = Double.parseDouble(laa.substring(laa.indexOf(",") + 1));

                                mMapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(GoogleMap mMap) {
                                        Log.d("main", "maps ready");
                                        googleMap = mMap;

                                        builder = new LatLngBounds.Builder();
                                        startSet = true;
                                        MarkerOptions startMarker = new MarkerOptions().position(convertToLL(start)).title("Start");
                                        Log.d("marker", startMarker.getPosition().toString());
                                        googleMap.addMarker(startMarker);
                                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(convertToLL(start), 17);
                                        googleMap.animateCamera(cameraUpdate);
                                        builder.include(startMarker.getPosition());
                                        buildMap(builder);
                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


//        String org =


        return rootView;
    }

    public LatLng convertToLL(com.example.semigo.LatLng ll) {
        return new LatLng(ll.latitude, ll.longitude);
    }

    public Location getLocation(String name, LatLng ll) {
        return new Location(name, new com.example.semigo.LatLng(ll.latitude, ll.longitude));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button_from_time = view.findViewById(R.id.from_time);
        button_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("tag", "0");
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getChildFragmentManager(), "timePicker");
            }
        });

        Button button_to_time = view.findViewById(R.id.to_time);
        button_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("tag", "1");
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getChildFragmentManager(), "timePicker");
            }
        });

        Button button_date_picker = view.findViewById(R.id.date_picker);
        button_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });


        // PLACES START

        AutocompleteSupportFragment autocompleteFragmentFrom = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_from);

        autocompleteFragmentFrom.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragmentFrom.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        edit = autocompleteFragmentFrom.getView().findViewById((R.id.places_autocomplete_search_input));
        edit.setFocusable(false);
        edit.setEnabled(false);
        edit.setHint("Select Pickup Location");
        edit.setTextSize(15);
        edit.setHeight(135);

        String apiKey = "AIzaSyAOWUwIn4qaxL0OzHO64XKF5jy7toSO7TU";

        AutocompleteSupportFragment autocompleteFragmentTo = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_to);

        autocompleteFragmentTo.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragmentTo.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        EditText edit1 = autocompleteFragmentTo.getView().findViewById((R.id.places_autocomplete_search_input));
        edit1.setHint("Select Drop Location");
        edit1.setTextSize(15);
        edit1.setHeight(135);

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }
        autocompleteFragmentTo.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("end place auto", "Place: " + place.getName() + ", " + place.getId());
                end = place.getLatLng();
                endName = place.getName();
                Log.d("end latlng auto", end.toString());
                if (endSet) endMarker.remove();
                MarkerOptions endMarkerOptions = new MarkerOptions().position(end).title("End");
                endMarker = googleMap.addMarker(endMarkerOptions);
                endSet = true;
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(end, 17);
                googleMap.animateCamera(cameraUpdate);
                builder.include(endMarkerOptions.getPosition());
                buildMap(builder);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("end place auto", "An error occurred: " + status);
            }
        });

        // PLACES END

        button_schedule_now = getView().findViewById(R.id.schedule_button);
        button_schedule_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Fucjnigger");


                Log.i("schedule ke pehle", org_name + "," + UID + "," + from_time + "," + to_time + "," + ride_date + "," + start.toString());

                final ArrayList<CurrentTrips> trips = new ArrayList<>();

                final DatabaseReference tripRef;
                tripRef = database.getReference("CurrentTrips");

                tripRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot trip: snapshot.getChildren()) {
                            trips.add(trip.getValue(CurrentTrips.class));
                        }
                        Log.d("trips", trips.toString());
                        Log.i("schedule info", from_time.replace(":","")+ to_time.replace(":","")+ ride_date.substring(ride_date.indexOf(" ")+1)+ getLocation(endName, end).toString()+ trips.toString());
                        tripRef.setValue(schedule(from_time.replace(":",""), to_time.replace(":",""), ride_date.substring(ride_date.indexOf(" ")+1), getLocation(endName, end), trips));
                        TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                        tabhost.getTabAt(1).select();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });


    }

    ArrayList<CurrentTrips> schedule(String strt_time, String end_time, String date, Location destination, ArrayList<CurrentTrips> trips) {
        Boolean foundTrip = false;
        for (int i = 0; i < trips.size(); i++) {
            CurrentTrips trip = trips.get(i);
            if (!trip.user2.equals("") || trip.user1.equals(UID) || trip.status.equals("Ongoing") || !org_name.equals(trip.org) || !timeMatches(strt_time, end_time, trip.st_time, trip.end_time) || !date.equals(trip.date)) {
                continue;
            }
            LatLng startLL = convertToLL(start), destLL = convertToLL(destination.ll), dest1LL = convertToLL(trip.dest1.ll);
            int time1 = getTime(getDistTime(startLL, dest1LL).time);
            int time2 = getTime(getDistTime(startLL, destLL).time);
            int time12 = getTime(getDistTime(startLL, dest1LL).time) + getTime(getDistTime(dest1LL, destLL).time);
            int time21 = getTime(getDistTime(startLL, destLL).time) + getTime(getDistTime(destLL, dest1LL).time);
            Log.i("Times", "" + time1 +" " + time2+ " " + time12+" " + time21);
            if (time12 <= time21 && time12 - Math.max(time2, time1) <= 15) {
                trip.updateTrip(UID, destination, UID, maxTime(strt_time, trip.st_time), minTime(end_time, trip.end_time));
                foundTrip = true;
            } else if (time12 >= time21 && time12 - Math.max(time2, time1) <= 15) {
                trip.updateTrip(UID, destination, trip.user1, maxTime(strt_time, trip.st_time), minTime(end_time, trip.end_time));
                foundTrip = true;
            }
            if (foundTrip) {
                trips.set(i, trip);
                Log.i("existing trip", trips.toString());
                break;
            }
        }

        if (!foundTrip) {
            CurrentTrips trip = new CurrentTrips(UID, destination, strt_time, end_time, org_name, date, "Upcoming");
            trips.add(trip);
        }

        return trips;

    }

    String minTime(String a, String b) {
        if (Integer.parseInt(a) > Integer.parseInt(b)) return b;
        return a;
    }

    String maxTime(String a, String b) {
        if (Integer.parseInt(a) < Integer.parseInt(b)) return b;
        return a;
    }

    boolean timeMatches(String st1, String end1, String st2, String end2) {
        if (end1.compareTo(st2) < 0 || st1.compareTo(end2) > 0) return false;
        return true;
    }


    int getTime(String time) {
        String[] comp = time.split(" ");
        if (comp[1].equals("hour"))
            return (Integer.parseInt(comp[0]) * 60 + Integer.parseInt(comp[2]));
        return (Integer.parseInt(comp[0]));
    }

    DistTime getDistTime(LatLng start, LatLng end) {
        String routeInfo = getRoute(start, end);
        try {
            final JSONObject json = new JSONObject(routeInfo);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            JSONArray newTempARr = routes.getJSONArray("legs");
            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
            JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

            DistTime result = new DistTime(distOb.getString("text"), timeOb.getString("text"));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    String getRoute(LatLng start, LatLng end) {

        String apiKey = "AIzaSyAOWUwIn4qaxL0OzHO64XKF5jy7toSO7TU";

        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving&key=" + apiKey;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    void buildMap(LatLngBounds.Builder builder) {
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);

        if (startSet && endSet) {
            if (polyline != null) polyline.remove();
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(convertToLL(start), end, GMapV2Direction.MODE_DRIVING);
            Log.d("Route", start.toString() + end.toString() + doc.toString());


            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            Log.i("route direc", directionPoint.toString());
            PolylineOptions rectLine = new PolylineOptions().width(8).color(
                    Color.BLACK).clickable(true);

            for (int i = 0; i < directionPoint.size(); i++) {
                rectLine.add(directionPoint.get(i));
            }
            polyline = googleMap.addPolyline(rectLine);
            Log.i("Polyline", rectLine.toString() + " , " + polyline.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
