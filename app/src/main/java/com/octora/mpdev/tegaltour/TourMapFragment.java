package com.octora.mpdev.tegaltour;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class TourMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String[] nama_lap, deskripsi_lap, alamat_lap, kontak_lap;
    private String namaLap;
    int numData;
    private Integer[] id_lap;
    LatLng latLng[], destination, currentLoc;
    Boolean markerD[];
    private Double[] latitude_lap, longitude_lap;
    private Marker myMarker;
    private Button btn_go;
    private Marker[] mk;
    LocationManager locationManager;
    String provider;
    Location location;
    private MapView mapFragment;
    private View mMainView;

    public TourMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_tour_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getLokasi();
        mapFragment = (MapView) mMainView.findViewById(R.id.map);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMainView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
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
                        nama_lap = new String[numData];
                        alamat_lap = new String[numData];
                        deskripsi_lap = new String[numData];
                        kontak_lap = new String[numData];
                        latitude_lap = new Double[numData];
                        longitude_lap = new Double[numData];
                        id_lap = new Integer[numData];

                        for (int i = 0; i < numData; i++) {
                            try {
                                JSONObject data = response.getJSONObject(i);
                                id_lap[i] = data.getInt("id_lap");
                                latLng[i] = new LatLng(data.getDouble("latitude_lap"),
                                        data.getDouble("longitude_lap"));
                                nama_lap[i] = data.getString("nama_lap");
                                alamat_lap[i] = data.getString("alamat_lap");
                                deskripsi_lap[i] = data.getString("deskripsi_lap");
                                kontak_lap[i] = data.getString("kontak_lap");
                                latitude_lap[i] = data.getDouble("latitude_lap");
                                longitude_lap[i] = data.getDouble("longitude_lap");

                                markerD[i] = false;
                                myMarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng[i])
                                        .title(nama_lap[i])
                                        .snippet(kontak_lap[i])
                                        .position(latLng[i])
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
                            } catch (JSONException je) {
                            }
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[i], 15.5f));
                            mapFragment.getMapAsync(TourMapFragment.this);

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
}
