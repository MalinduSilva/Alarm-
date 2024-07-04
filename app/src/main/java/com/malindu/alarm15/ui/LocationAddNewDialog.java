package com.malindu.alarm15.ui;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.malindu.alarm15.BuildConfig;
import com.malindu.alarm15.R;
import com.malindu.alarm15.adapters.LatLngAdapter;
import com.malindu.alarm15.adapters.PlacePredictionAdapter;
import com.malindu.alarm15.models.LocationAlarm;
import com.malindu.alarm15.models.geo.GeocodingResult;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationAddNewDialog extends DialogFragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "LocationAddNewDialog";
    private SharedPreferences sharedPreferences;
    private boolean locationPermissionsGranted = false;

    // Map
    private GoogleMap mMap;

    // Current location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    // Autocomplete
    private final Handler handler = new Handler();
    private final PlacePredictionAdapter placePredictionAdapter = new PlacePredictionAdapter();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LatLng.class, new LatLngAdapter()).create();
    private RequestQueue queue;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private static final LatLngBounds LAT_LNG_BOUNDS_DEFAULT = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180)); // Default lat-lang to cover whole world
    private ViewAnimator viewAnimator;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
//    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
//    private GoogleApiClient googleApiClient;

    // Widgets
    private RadioGroup radioGroupRange;
    private LinearLayout layoutRange;
    private AutoCompleteTextView searchTxt;
    private EditText txtRange;
    private ImageView btnLocateMe;
    private Button btnDiscard, btnSave;
    private LocationAlarm locationAlarm = new LocationAlarm();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_add_location_alarm, container, false);
        radioGroupRange = view.findViewById(R.id.radioGroup);
        layoutRange = view.findViewById(R.id.layout_range);
        searchTxt = view.findViewById(R.id.editTextSearch);
        txtRange = view.findViewById(R.id.txtRange);
        btnLocateMe = view.findViewById(R.id.btnLocateMe);
        btnDiscard = view.findViewById(R.id.btn_discard);
        btnSave = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        placesClient = Places.createClient(requireContext());
        queue = Volley.newRequestQueue(requireContext());
        sessionToken = AutocompleteSessionToken.newInstance();

        sharedPreferences = view.getContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Constants.ALARM_PREFERENCES_KEY_FIRST_LAUNCH_LOCATION, true)) {
            firstLaunchTourLocation();
        }
        if (PermissionUtils.hasPermissions(getContext(), Constants.REQUIRED_PERMISSIONS_LOCATION)) {
            Log.d(TAG, "firstLaunchTourLocation: Permissions granted already");
            locationPermissionsGranted = true;
        } else {
            PermissionUtils.requestPermissions(getActivity(), Constants.REQUIRED_PERMISSIONS_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        initRecyclerView(view);

        radioGroupRange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonProximity) {
                    layoutRange.setVisibility(View.VISIBLE);
                    locationAlarm.setProximity(true);
                    locationAlarm.setExact(false);
                } else if (checkedId == R.id.radioButtonExact) {
                    layoutRange.setVisibility(View.INVISIBLE);
                    locationAlarm.setProximity(false);
                    locationAlarm.setExact(true);
                }
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationAlarm.setRange(Integer.parseInt(txtRange.getText().toString()));
                locationAlarm.set();
            }
        });

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //a
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString() + ", " + start + ", " + before + ", " + count);
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPlacePredictions(s.toString());
                    }
                }, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //a
            }
        });
//        //initialize the AutocompleteSupportFragment and associate it with a layout element
//        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);// requireActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        if (autocompleteSupportFragment != null) {
//            //specify what kind of place the user will expect to type in
//            //autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);
//            //location biasing by country and geographic coordinates
//            autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(LAT_LNG_BOUNDS));
//            //autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(new LatLng(-33.880490, 151.184363), new LatLng(-33.858754, 151.229596)));
//            //autocompleteSupportFragment.setCountries("IN");
//            //specify the types of place data to return
//            autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//            autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onError(@NonNull Status status) {
//                    Log.d(TAG, "Error occurred when selecting place: " + status);
//                }
//
//                @Override
//                public void onPlaceSelected(@NonNull Place place) {
//                    Log.d(TAG, "Place: " + place.getId() + " *** " + place.getName());
//                }
//            });
//        }

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        //Log.d(TAG, "onCreateView: " + BuildConfig.MAPS_API_KEY);
        if (BuildConfig.MAPS_API_KEY.isEmpty()) {
            Log.e(TAG, "onCreate: No API key", new Exception("No API Key"));
            getActivity().finish();
            return;
        }
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(requireContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(requireContext());
        if (Places.isInitialized()) {
            Log.d(TAG, "onCreate: Places initialized");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Uncomment if map's roads are black
//        // Clear the Google Maps cache
//        mMap.clear();
//        // Ensure no custom map styles are applied
//        mMap.setMapStyle(null);
//
//        // Add logging to monitor zoom levels
//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                float zoom = mMap.getCameraPosition().zoom;
//                Log.d(TAG, "Current zoom level: " + zoom);
//            }
//        });

        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (PermissionUtils.hasPermissions(getContext(), Constants.REQUIRED_PERMISSIONS_LOCATION)) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            init();
        } else {
            getDialog().dismiss();
            Toast.makeText(getContext(), "Location permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void firstLaunchTourLocation() {
        // TODO implement guide

        if (PermissionUtils.hasPermissions(getContext(), Constants.REQUIRED_PERMISSIONS_LOCATION)) {
            //Toast.makeText(getContext(), "Permissions ok", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "firstLaunchTourLocation: Permissions granted already");
        } else {
            PermissionUtils.requestPermissions(getActivity(), Constants.REQUIRED_PERMISSIONS_LOCATION);
        }
        sharedPreferences.edit().putBoolean(Constants.ALARM_PREFERENCES_KEY_FIRST_LAUNCH_LOCATION, false).apply();
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            if (PermissionUtils.hasPermissions(getContext(), Constants.REQUIRED_PERMISSIONS_LOCATION)) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Found location");
                            currentLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(latLng, Constants.DEFAULT_MAP_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: last location is null");
                            Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
    }
    
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to - lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }

    private void init() {
        Log.d(TAG, "init: initializing");
//        googleApiClient = new GoogleApiClient.Builder(requireContext())
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(requireActivity(), this)
//                .build();
//        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(requireContext(), googleApiClient, LAT_LNG_BOUNDS, null);
//        editTextSearch.setAdapter(placesAutoCompleteAdapter);
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //editTextSearch.setText(editTextSearch.getText().subSequence(0, editTextSearch.getText().length()));
                    //editTextSearch.setSelection(editTextSearch.getText().length());
                    searchTxt.setCursorVisible(false);
                    geoLocate();
                    hideSoftKeyboard(); // Ensure the keyboard is hidden after the action
                    return true; // Prevent the new line insertion
                }
                return false;
            }
        });
        searchTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTxt.setCursorVisible(true);
            }
        });
        btnLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        //hideSoftKeyboard();
    }
    
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = searchTxt.getText().toString();
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException : " + e.getMessage());
        }
        if (!list.isEmpty()) {
//            for (Address address : list) {
//                Log.d(TAG, "geoLocate: " + address.toString());
//            }
            Address address = list.get(0);
//            Toast.makeText(getContext(), address.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "geoLocate: Found address : " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), Constants.DEFAULT_MAP_ZOOM, address.getAddressLine(0));
        }
    }

    private void hideSoftKeyboard() {
        Log.d(TAG, "hideSoftKeyboard: ");
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);
        }
        //requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initRecyclerView(View view) {
        //final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(placePredictionAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), linearLayoutManager.getOrientation()));
        placePredictionAdapter.setPlaceClickListener(new PlacePredictionAdapter.OnPlaceClickListener() {
            @Override
            public void onPlaceClicked(AutocompletePrediction place) {
                geocodePlaceAndDisplay(place);
            }
        });
    }

    private void getPlacePredictions(String query) {
        LocationBias bias;
        if (currentLocation != null) {
            LatLng southwest = new LatLng(currentLocation.getLatitude()-1, currentLocation.getLongitude()-1);
            LatLng northeast = new LatLng(currentLocation.getLatitude()+1, currentLocation.getLongitude()+1);
            bias = RectangularBounds.newInstance(southwest, northeast);
        } else {
            bias = RectangularBounds.newInstance(LAT_LNG_BOUNDS_DEFAULT);
        }

        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(sessionToken)
                .setLocationBias(bias)
                .setQuery(query)
                .setCountries(Arrays.asList("LK"))
                //.setTypesFilter(null)
                .build();

        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                List<AutocompletePrediction> predictions = findAutocompletePredictionsResponse.getAutocompletePredictions();
                placePredictionAdapter.setPredictions(predictions);
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setIndeterminate(false);
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode() + " - " + apiException.getMessage());
                }
            }
        });
    }

    private void geocodePlaceAndDisplay(AutocompletePrediction placePrediction) {
        final String apiKey = BuildConfig.MAPS_API_KEY;
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?place_id=%s&key=%s";
        final String requestURL = String.format(url, placePrediction.getPlaceId(), apiKey);

        // Use the HTTP request URL for Geocoding API to get geographic coordinates for the place
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestURL, null,
                response -> {
                    try {
                        // Inspect the value of "results" and make sure it's not empty
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() == 0) {
                            Log.w(TAG, "No results from geocoding request.");
                            return;
                        }

                        // Use Gson to convert the response JSON object to a POJO
                        GeocodingResult result = gson.fromJson(
                                results.getString(0), GeocodingResult.class);
                        displayDialog(placePrediction, result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e(TAG, "Request failed"));

        // Add the request to the Request queue.
        queue.add(request);
        recyclerView.setVisibility(View.GONE);
    }

    private void displayDialog(AutocompletePrediction place, GeocodingResult result) {
        Log.d("RESULTS", place.getPrimaryText(null).toString() + " : " + result.geometry.location);
        Log.d("RESULTS", place.toString());
        moveCamera(new LatLng(result.geometry.location.latitude, result.geometry.location.longitude), Constants.DEFAULT_MAP_ZOOM, place.getPrimaryText(null).toString());
        locationAlarm.setLatLng(new LatLng(result.geometry.location.latitude, result.geometry.location.longitude));
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (PermissionUtils.handlePermissionsResult(requestCode, permissions, grantResults)) {
//            // Permissions are granted
//            Log.d(TAG, "onRequestPermissionsResult: permissions granted");
//        } else {
//            // Permissions are denied
//            Log.d(TAG, "onRequestPermissionsResult: permissions denied");
//            Toast.makeText(getContext(), "Permissions denied", Toast.LENGTH_SHORT).show();
//        }
//    }
}
