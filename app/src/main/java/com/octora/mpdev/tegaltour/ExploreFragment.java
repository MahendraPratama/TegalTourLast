package com.octora.mpdev.tegaltour;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private String JSON_STRING;
    final ArrayList<HashMap<String, Object>> list = new ArrayList<>();
    GridView gridView;
    private View mMainView;
    private boolean isFirtsLoad;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_explore, container, false);

        if (list == null){
            getJSON();
        }
        else
        {
            show();
        }

        return mMainView;


    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Fetching Data", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                try {
                    JSONArray result = new JSONArray(JSON_STRING);
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jo = result.getJSONObject(i);
                        String id_tw = jo.getString(Config.KEY_ID_TW);
                        String nm_tw = jo.getString(Config.KEY_nama_tempat);
                        String almt_tw = jo.getString(Config.KEY_alamat);
                        String rating_tw = jo.getString(Config.KEY_rating);
                        String foto_tw = jo.getString(Config.KEY_linkfoto);
                        //LatLng destination = new LatLng(jo.getDouble("latitude_lap"),jo.getDouble("longitude_lap"));

                        HashMap<String, Object> StadiumList = new HashMap<>();
                        StadiumList.put(Config.KEY_nama_tempat, nm_tw);
                        StadiumList.put(Config.KEY_alamat, almt_tw);
                        StadiumList.put(Config.KEY_rating, rating_tw);
                        StadiumList.put(Config.KEY_linkfoto , foto_tw);
                        StadiumList.put(Config.KEY_ID_TW , id_tw);
                        //StadiumList.put("destination",destination);
                        list.add(StadiumList);

                    }
                    isFirtsLoad = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                show();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_EXPLORE_FRAGMENT);Log.d("==",":"+s);
                return s;

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void show() {

        gridView = (GridView) mMainView.findViewById(R.id.grid_view);
        if(isFirtsLoad){
            gridView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return list.size();
                }

                @Override
                public Object getItem(int position) {
                    return list.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                            getActivity().LAYOUT_INFLATER_SERVICE);
                    View gridview;
                    gridview = inflater.inflate(R.layout.item_tempat_wisata,null);
                    TextView nm_std = (TextView)gridview.findViewById(R.id.namaStd);
                    nm_std.setText((CharSequence) list.get(position).get(Config.KEY_nama_tempat));
                    TextView almt_std = (TextView)gridview.findViewById(R.id.alamatStd);
                    almt_std.setText((CharSequence) list.get(position).get(Config.KEY_alamat));
                    RatingBar rateTeam = (RatingBar)gridview.findViewById(R.id.rating_stadium);
                    rateTeam.setRating(Float.parseFloat((String) list.get(position).get(Config.KEY_rating)));
                    ImageView image_stadium = (ImageView) gridview.findViewById(R.id.image_stadium);
                    Glide.with(getActivity())
                            .load(list.get(position).get(Config.KEY_linkfoto))
                            .into(image_stadium);
                    return gridview;
                }
            });
            isFirtsLoad = false;
        }


        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //destination = (LatLng) list.get(position).get("destination");
                //currentLoc = new LatLng(-6.9828315,110.4085595);
                Intent i = null;
//                i = new Intent(getActivity(), SimpleDirectionActivity.class);
//
//                Bundle args = new Bundle();
//                args.putParcelable("destination", destination);
//                args.putParcelable("origin", currentLoc);
//                i.putExtra("bundle", args);
//
//                Log.e("Message", String.valueOf(destination));
//                startActivity(i);

            }
        });
    }



}
