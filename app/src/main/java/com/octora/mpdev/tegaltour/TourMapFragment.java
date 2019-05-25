package com.octora.mpdev.tegaltour;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class TourMapFragment extends Fragment implements
        OnMapReadyCallback , View.OnClickListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private String[] nama_tempat, deskripsi, alamat, no_telp, kategori;
    private String title;
    int numData;
    private Integer[] id_tempat_wisata, id_kategori;
    LatLng latLng[], destination, currentLoc;
    Boolean markerD[];
    private Double[] latitude, longitude, rating;
    private Marker myMarker;
    private Button btn_go;
    private Marker[] mk;
    LocationManager locationManager;
    String provider;
    Location location;
    private MapView mapFragment;
    private View mMainView;
    ArrayList<HashMap<String, Object>> listDataMap = new ArrayList<>();
    private LinearLayout layoutButton;
    private Button btnDirection, btnViewDetail;

    public TourMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_tour_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        layoutButton = (LinearLayout) mMainView.findViewById(R.id.layout_button);
        btnDirection = (Button) mMainView.findViewById(R.id.btn_direction);
        btnViewDetail = (Button) mMainView.findViewById(R.id.btn_view_detail);
        layoutButton.setVisibility(View.INVISIBLE);
        //btnDirection.setVisibility(View.INVISIBLE);
        //btnViewDetail.setVisibility(View.INVISIBLE);
        btnDirection.setOnClickListener(this);
        btnViewDetail.setOnClickListener(this);
        getLokasi();
        mapFragment = (MapView) mMainView.findViewById(R.id.map);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
            //mapFragment.getMapAsync(TourMapFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMainView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        if (ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }


        for(int i = 0 ; i < listDataMap.size() ; i++){
            Log.d("addmarker", listDataMap.get(i).get(Config.KEY_ID_TW).toString());
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position((LatLng) listDataMap.get(i).get(Config.KEY_LatLng))
                    .title(listDataMap.get(i).get(Config.KEY_nama_tempat).toString())
                    .snippet(listDataMap.get(i).get(Config.KEY_rating).toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
            myMarker.setTag(listDataMap.get(i).get(Config.KEY_ID_TW).toString());
        }
        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom((LatLng) listDataMap.get(listDataMap.size()-1).get(Config.KEY_LatLng), 12));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                layoutButton.setVisibility(View.INVISIBLE);
            }
        });
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                // Retrieve the data from the marker.
//                Integer clickCount = (Integer) marker.getTag();
//
//                // Check if a click count was set, then display the click count.
//                if (clickCount != null) {
//                    //clickCount = clickCount + 1;
//                    //marker.setTag(clickCount);
//                    Toast.makeText(getActivity(),
//                            marker.getTitle() +
//                                    " has been clicked " + clickCount + " times.",
//                            Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//        });
    }

    private void getLokasi() {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, Config.URL_GET_TEMPAT_WISATA, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        numData = response.length();
                        Log.d("DEBUG_", "Parse JSON");
                        latLng = new LatLng[numData];
                        markerD = new Boolean[numData];
                        id_tempat_wisata = new Integer[numData];
                        nama_tempat = new String[numData];
                        kategori = new String[numData];
                        id_kategori = new Integer[numData];
                        alamat = new String[numData];
                        deskripsi = new String[numData];
                        no_telp = new String[numData];
                        rating = new Double[numData];
                        latitude = new Double[numData];
                        longitude = new Double[numData];

                        for (int i = 0; i < numData; i++) {
                            try {
                                JSONObject data = response.getJSONObject(i);
                                id_tempat_wisata[i] = data.getInt(Config.KEY_ID_TW);
                                latLng[i] = new LatLng(data.getDouble(Config.KEY_latitude),
                                        data.getDouble(Config.KEY_longitude));
                                nama_tempat[i] = data.getString(Config.KEY_nama_tempat);
                                kategori[i] = data.getString(Config.KEY_kategori);
                                id_kategori[i] = data.getInt(Config.KEY_id_kategori);
                                alamat[i] = data.getString(Config.KEY_alamat);
                                deskripsi[i] = data.getString(Config.KEY_deskripsi);
                                no_telp[i] = data.getString(Config.KEY_no_telp);
                                rating[i] = data.getDouble(Config.KEY_rating);
                                latitude[i] = data.getDouble(Config.KEY_latitude);
                                longitude[i] = data.getDouble(Config.KEY_longitude);

                                HashMap<String,Object> dt = new HashMap<>();
                                dt.put(Config.KEY_ID_TW,id_tempat_wisata[i]);
                                dt.put(Config.KEY_LatLng,latLng[i]);
                                dt.put(Config.KEY_nama_tempat,nama_tempat[i]);
                                dt.put(Config.KEY_kategori,kategori[i]);
                                dt.put(Config.KEY_id_kategori,id_kategori[i]);
                                dt.put(Config.KEY_alamat,alamat[i]);
                                dt.put(Config.KEY_deskripsi,deskripsi[i]);
                                dt.put(Config.KEY_no_telp,no_telp[i]);
                                dt.put(Config.KEY_rating,rating[i]);
                                dt.put(Config.KEY_latitude,latitude[i]);
                                dt.put(Config.KEY_longitude,longitude[i]);
                                listDataMap.add(dt);

//                                markerD[i] = false;
//                                myMarker = mMap.addMarker(new MarkerOptions()
//                                        .position(latLng[i])
//                                        .title(nama_tempat[i])
//                                        .snippet(rating[i].toString())
//                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
//                                myMarker.setTag(id_tempat_wisata[i]);

                            } catch (JSONException je) {
                            }
                            mapFragment.getMapAsync(TourMapFragment.this);
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[i], 12));
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Error!");
                        builder.setMessage("No Internet Connection");
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getLokasi();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
        Volley.newRequestQueue(getContext()).add(request);
    }

    @Override
    public void onClick(View view) {
        if (view == btnDirection)
        {
            destination = myMarker.getPosition();
            title = myMarker.getTitle();

//        currentLoc  = new LatLng(location.getLatitude(),location.getLongitude());

            currentLoc = new LatLng(-6.9828315,110.4085595);

            Intent i = null;
            i = new Intent(getActivity(), SimpleDirectionActivity.class);

            Bundle args = new Bundle();
            args.putParcelable(Config.KEY_destination, destination);
            args.putString("title", title);
            args.putParcelable("origin", currentLoc);
            i.putExtra("bundle", args);

            startActivity(i);
        }
    }

    public LatLng getMiddle(ArrayList<HashMap<String, Object>> list){
        double lat=0,lng=0;

        for (int i = 0; i < list.size(); i++){
            lat += (Double)list.get(i).get(Config.KEY_latitude);
            lng += (Double)list.get(i).get(Config.KEY_longitude) ;
        }

        lat = lat/list.size();
        lng = lng/list.size();

        return new LatLng(lat,lng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Integer clickCount = (Integer) marker.getTag();
        Log.d(marker.getTitle(),"-------------------------------");
        layoutButton.setVisibility(View.VISIBLE);
        // Check if a click count was set, then display the click count.
//        if (clickCount != null) {
//            //clickCount = clickCount + 1;
//            //marker.setTag(clickCount);
//            Toast.makeText(getActivity(),
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
//        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapFragment.removeAllViews();
        listDataMap.removeAll(listDataMap);
    }
}
