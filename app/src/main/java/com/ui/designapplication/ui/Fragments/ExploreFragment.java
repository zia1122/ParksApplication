package com.ui.designapplication.ui.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonParser;
import com.ui.designapplication.Adapter.ParksAdapter;
import com.ui.designapplication.Models.NearbyCards;
import com.ui.designapplication.Models.RequestModel;
import com.ui.designapplication.Models.ServerResponse;
import com.ui.designapplication.Models.myModel;
import com.ui.designapplication.R;
import com.ui.designapplication.databinding.FragmentExploreBinding;
import com.ui.designapplication.network.ApiClient;
import com.ui.designapplication.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ExploreFragment extends Fragment {


    ProgressDialog progressDialog;


    LocationManager locationManager;
    private ApiInterface apiInterface;
    private static final int REQUEST_Location = 1;
    private double currentLatitude;
    private double currentLongitude;
    private ParksAdapter adapter;
    private List<NearbyCards> models;
    FragmentExploreBinding binding;


    private static final int REQUEST_LOCATION = 1;
    LocationManager locManager;

    /*These are the variables for Lat and Long*/
    String currLat, currLong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(getLayoutInflater(), container, false);

        models = new ArrayList<>();

        apiInterface = ApiClient.getClient()
                .create(ApiInterface.class);

        //Add Permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_Location);

        //Get Location
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Check if Gps is ON or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            turnOnGps();
        }
        else
        {
            //Gps is already ON
            getCurrentLocation();
        }




            getServerResponse();

       // DataFetching();

        ShowDialog(getActivity());
        return binding.getRoot();
    }

    private void getCurrentLocation() {
        //Check Permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //Add Permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_Location);
        }
        else
        {
            Location locGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locPassive =locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locGps != null)
            {
                currentLatitude = locGps.getLatitude();
                currentLongitude = locGps.getLongitude();
                currLat = String.valueOf(currentLatitude);
                currLong = String.valueOf(currentLongitude);

            } else if (locNet != null)
            {
                currentLatitude = locNet.getLatitude();
                currentLongitude = locNet.getLongitude();
                currLat = String.valueOf(currentLatitude);
                currLong = String.valueOf(currentLongitude);

            }
            else if (locPassive != null)
            {
                currentLatitude = locPassive.getLatitude();
                currentLongitude = locPassive.getLongitude();
                currLat = String.valueOf(currentLatitude);
                currLong = String.valueOf(currentLongitude);
            }
            else
            {
                Toast.makeText(getActivity(), "Can't get your location", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void turnOnGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enable Gps").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void getServerResponse() {

        RequestModel requestModel = new RequestModel(
                "20", "37.412687780", "-77.64786873015784");

        /*JSONObject jsonObject = new JSONObject();
            jsonObject.put("lat","37.412687780");
            jsonObject.put("long","-77.64786873015784");
            jsonObject.put("radius","20");*/


        Call<ServerResponse> call = apiInterface.getResponse(requestModel);

       call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {

                if (response.isSuccessful() &&
                        response.code() == 200 &&
                        response.body() != null) {

                    ServerResponse serverResponse = response.body();

                    if (serverResponse.getNearbyCards().size() > 0) {

                        models.addAll(serverResponse.getNearbyCards());


                    }
                }
                else {
                    if(response.body().toString().length()==0){
                        Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
                    }
                }
                DismissDialog();
                adapter = new ParksAdapter(models, requireActivity());
                binding.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {

                Log.d(TAG, "Request Failure -> " + t.getMessage());

                Toast.makeText(getActivity(),
                        t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

       }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

    }






    /*private void DataFetching() {
        String url = "https://godiapi.azurewebsites.net/api/cards/getLandingPage";
        StringRequest complainFetchRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                    try {
                        JSONObject jObj = new JSONObject(response);
                        JSONArray jsonArray = jObj.getJSONArray("nearbyCards");
                        if (jsonArray.length() == 0) {
                            Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Data Showing", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = jsonArray.getJSONObject(i);

                                DismissDialog();
                                id = o.getString("id");
                                name = o.getString(("name"));
                                Description = o.getString("description");
                                streetAddress = o.getString("streetAddress");
                                streetAddress2 = o.getString("streetAddress2");
                                state = o.getString("state");
                                city = o.getString("city");
                                zipcode = o.getString("zipCode");
                                decription2 = o.getString("directions");
                                String type = o.getString("type");
                                String phoneNumber = o.getString(("phoneNumber"));
                                String hoursOfOperation = o.getString("hoursOfOperation");
                                String website = o.getString("website");
                                String email = o.getString("email");
                                String latitude = o.getString("lat");
                                String longitude = o.getString("long");
                                String  stayLimit = o.getString("stayLimit");
                                String acres = o.getString("acres");
                                String tags = o.getString("tags");
                                String tagCount = o.getString("tagCount");
                                String  contentImage = o.getString("contentImage");
                                String  file = o.getString("file");
                                String fileString = o.getString("fileString");
                                String lastUpdated = o.getString("lastUpdated");
                                String  origin = o.getString("origin");
                                String approved = o.getString("approved");
                                String status = o.getString("status");
                                String likeCount = o.getString("likeCount");
                                String imageUrl = o.getString("imageUrl");
                                String reviews = o.getString("reviews");
                                String photos = o.getString("photos");
                                String weatherForecast = o.getString("weatherForecast");


                                model.add(new myModel(id, name, Description, streetAddress, streetAddress2, state, city, zipcode, decription2, type, phoneNumber, hoursOfOperation, website, email, latitude, longitude, stayLimit, acres, tags, tagCount, contentImage, file, fileString, lastUpdated, origin, approved, status, likeCount, imageUrl, weatherForecast, reviews, photos));
                            }
                        }

                        DismissDialog();

                        adapter = new ParksAdapter(models, requireActivity());
                        binding.recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        DismissDialog();
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Null" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        DismissDialog();
                    }


                })

        {

            @Override
            protected Map<String, String> getParams() {
            // Posting parameters to login url
            Map<String, String> params = new HashMap<>();
            params.put("lat","37.431572");
            params.put("long", "-78.656891");
            params.put("radius", Integer.toString(40));
            Log.i("sending ", params.toString());
            return params;
        }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(complainFetchRequest);

    }*/




    public void ShowDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void DismissDialog() {
        progressDialog.dismiss();
    }

    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGps) {
            GPSLocationTime = locationGps.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGps;
        } else {
            return locationNet;
        }

    }


}