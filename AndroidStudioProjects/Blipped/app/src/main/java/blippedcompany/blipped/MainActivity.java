package blippedcompany.blipped;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //Google Map Initialize
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private final LatLng mDefaultLocation = new LatLng(14.5955772, 120.9880854);
    private static final int DEFAULT_ZOOM = 17;
    LatLng coordinate;//Declare Coordinate
    LatLng cursor_coordinate;
    SearchView search;
    String query;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    boolean toggle= false;

    //Variables
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    Marker markers = null;
    Double cursor_coordinate_latitude;
    Double cursor_coordinate_longitude;
    String userName;
    String BlipName ;
    String Details;
    EditText friendemail;
    String friendrequestemail;
    EditText mBlipName;
    EditText mDetails;
    Spinner mySpinner;
    CheckBox publiccheckbox;
    CheckBox privatecheckbox;
    CheckBox checkboxArts ;
    CheckBox checkboxBusiness ;
    CheckBox checkboxCommunity;
    CheckBox checkboxFamily ;
    CheckBox checkboxFashion ;
    CheckBox checkboxFood;
    CheckBox checkboxHealth;
    CheckBox checkboxMedia;
    CheckBox checkboxSports;
    CheckBox checkboxTransportation;
    CheckBox checkboxHoliday;
    CheckBox checkboxTravel;
    CheckBox checkboxMusic;
    RadioButton publicradio;
    RadioButton privateradio;
    String[] CustomBlips = {"Arts", "Transportation", "Business",
            "Community", "Family & Education", "Fashion", "Media","Food","Health","Holiday","Music","Sports","Travel"};

    String blipIcon;
    FloatingActionButton  btnFusedLocation;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    //Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userID = mAuth.getCurrentUser();

    //Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //initialize lat and lng values
    DatabaseReference Users = database.getReference("users");
    DatabaseReference Blipsref = database.getReference("blips");
    DatabaseReference BlipsPublic = database.getReference("blips").child("public");
    DatabaseReference BlipsPrivate = database.getReference("blips").child("private");
    DatabaseReference UsersEmailFriends;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar);
        DeclareThings();




                search = (SearchView) findViewById(R.id.searchView);
                         search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                            @Override
                            public boolean onQueryTextSubmit(String query) {

                                return false;
                            }

                               @Override
                               public boolean onQueryTextChange(final String query) {
                                   mMap.clear();
                                   blipsupdateontextchange(query);
                                   return false;
                            }


                 });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });





    }
    public void DeclareThings(){

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//TOOLBAR
        setSupportActionBar(toolbar);

        //Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Connect Geo Location
        GoogleMapAPIConnect();

        //Action Button

        final FloatingActionButton  btnFusedLocation = (FloatingActionButton) findViewById(R.id.fab);
        btnFusedLocation.setImageResource(R.mipmap.ic_gps);
        btnFusedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();

            }
        });

        //Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);//Layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//Layout
        navigationView.setNavigationItemSelectedListener(this);




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_top_right, menu);
        return true;
    }
    @Override//
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addfriend) {
            SendFriendRequest();


        } else if (id == R.id.nav_notifications) {

            Intent ListViewActivity = new Intent(this, ListViewActivity.class);
            startActivity(ListViewActivity);


        } else if (id == R.id.nav_friendlist) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            finish();
            Intent nextscreen = new Intent(this, LoginActivity.class);
            startActivity(nextscreen);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.auber_style));
        getDeviceLocation();


         userName=removecom(userID.getEmail());
         publiccheckbox = (CheckBox)findViewById(R.id.checkboxPublic);
         privatecheckbox = (CheckBox)findViewById(R.id.checkboxPrivate);
         checkboxMusic = (CheckBox)findViewById(R.id.checkboxMusic);
         checkboxArts = (CheckBox)findViewById(R.id.checkboxArts);
         checkboxBusiness = (CheckBox)findViewById(R.id.checkboxBusiness);
         checkboxCommunity= (CheckBox)findViewById(R.id.checkboxCommunity);
         checkboxFamily = (CheckBox)findViewById(R.id.checkboxFamily);
         checkboxFashion = (CheckBox)findViewById(R.id.checkboxFashion);
         checkboxFood = (CheckBox)findViewById(R.id.checkboxFood);
         checkboxHealth = (CheckBox)findViewById(R.id.checkboxHealth);
         checkboxMedia = (CheckBox)findViewById(R.id.checkboxMedia);
         checkboxSports = (CheckBox)findViewById(R.id.checkboxSports);
         checkboxTransportation = (CheckBox)findViewById(R.id.checkboxTransportation);
         checkboxHoliday = (CheckBox)findViewById(R.id.checkboxHoliday);
         checkboxTravel = (CheckBox)findViewById(R.id.checkboxTravel);
         ScrollView filter =(ScrollView) findViewById(R.id.filterscroll);


        checkboxlisteners();
        ShowBlips();

        // WHen map is long clicked
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {


            @Override
            public void onMapLongClick(LatLng point) {

                AddBlip(point);
            }
        });


        //When map is infolong clicked
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                EditBlip(marker);
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {

                DeleteBlip(marker);
            }
        });


    }
    private void GoogleMapAPIConnect() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

    }
    public void getDeviceLocation() {

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
        }



    }



    public void PlaceMarker(LatLng newBlipCoordinates, String newBlipName, String creator, String Details, Marker addmarkers, String blipIcon) {

      addmarkers = mMap.addMarker(new MarkerOptions() // Set Marker
                .position(newBlipCoordinates)
                .title(newBlipName)
                .snippet(creator)
                .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(blipIcon,"mipmap", getPackageName() ))));
    }

    public void AddBlip(LatLng point){
        cursor_coordinate = new LatLng(point.latitude, point.longitude);// Set current click location to marker
        cursor_coordinate_latitude= point.latitude;
        cursor_coordinate_longitude= point.longitude;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mBlipAddView = getLayoutInflater().inflate(R.layout.enter_blip_details_popup,null);

        mBlipName = mBlipAddView.findViewById(R.id.blipnameEt);
        mDetails = mBlipAddView.findViewById(R.id.detailsEt);
        Button mAddBlip = mBlipAddView.findViewById(R.id.addblip_button);
        Button mCancelBlip = mBlipAddView.findViewById(R.id.cancelblip_button);
        publicradio = mBlipAddView.findViewById(R.id.publicRadio);
        privateradio = mBlipAddView.findViewById(R.id.privateRadio);
        mySpinner = mBlipAddView.findViewById(R.id.iconsSpinner);



        RadioGroup radioGroup = (RadioGroup) mBlipAddView.findViewById(R.id.groupRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(publicradio.isChecked()){
                    privateradio.setChecked(false);
                    mySpinner.setAdapter(new MyCustomAdapterPublic(MainActivity.this, R.layout.row, CustomBlips));//Change to Public Spinnes

                }
                else if(privateradio.isChecked()){
                    publicradio.setChecked(false);
                    mySpinner.setAdapter(new MyCustomAdapterPrivate(MainActivity.this, R.layout.row, CustomBlips));//Change to Private Spinner

                }

                // checkedId is the RadioButton selected
            }
        });




        mBuilder.setView(mBlipAddView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAddBlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlipName = mBlipName.getText().toString();


                if(publicradio.isChecked()) {

                    if (validateForm()) {

                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        if (dropboxvalue == "Arts") {
                            blipIcon = "public_art";
                        } else if (dropboxvalue == "Transportation") {
                            blipIcon = "public_autoboatsair";

                        } else if (dropboxvalue == "Business") {
                            blipIcon = "public_business";

                        } else if (dropboxvalue == "Community") {
                            blipIcon = "public_community";

                        } else if (dropboxvalue == "Family & Education") {
                            blipIcon = "public_family";

                        } else if (dropboxvalue == "Fashion") {
                            blipIcon = "public_fashion";

                        } else if (dropboxvalue == "Media") {
                            blipIcon = "public_filmandmedia";

                        } else if (dropboxvalue == "Travel") {
                            blipIcon = "public_travelandoutdoor";

                        } else if (dropboxvalue == "Food") {
                            blipIcon = "public_foodanddrinks";

                        } else if (dropboxvalue == "Health") {
                            blipIcon = "public_health";

                        } else if (dropboxvalue == "Holiday") {
                            blipIcon = "public_holidaysandcelebrations";

                        } else if (dropboxvalue == "Music") {
                            blipIcon = "public_music";

                        } else if (dropboxvalue == "Sports") {
                            blipIcon = "public_sportsandfitness";

                        } else {
                            blipIcon = "ic_launcher_round";
                        }


                        //Place Data

                        Blips blips = new Blips(cursor_coordinate_latitude,
                                cursor_coordinate_longitude,
                                BlipName,
                                userName,
                                Details,
                                blipIcon);
                        Users.child(userName).child("Blips").push().setValue(blips);// Add to user's blips
                        Blipsref.child("public").push().setValue(blips);//Add to public blips

                        dialog.cancel();
                        mMap.clear();
                        ShowBlips();
                    }
                }


                else if(privateradio.isChecked()){
                    if (validateForm()) {


                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        if (dropboxvalue == "Arts") {//
                            blipIcon = "private_art";
                        } else if (dropboxvalue == "Transportation") {//
                            blipIcon = "private_autoboatsair";

                        } else if (dropboxvalue == "Business") {//
                            blipIcon = "private_business";

                        } else if (dropboxvalue == "Community") {//
                            blipIcon = "private_community";

                        } else if (dropboxvalue == "Family & Education") {//
                            blipIcon = "private_family";

                        } else if (dropboxvalue == "Fashion") {//
                            blipIcon = "private_fashion";

                        } else if (dropboxvalue == "Media") {//
                            blipIcon = "private_filmandmedia";

                        } else if (dropboxvalue == "Travel") {//
                            blipIcon = "private_travelandoutdoor";

                        } else if (dropboxvalue == "Food") {//
                            blipIcon = "private_foodanddrinks";

                        } else if (dropboxvalue == "Health") {//
                            blipIcon = "private_health";

                        } else if (dropboxvalue == "Holiday") {
                            blipIcon = "private_holidaysandcelebrations";

                        } else if (dropboxvalue == "Music") {
                            blipIcon = "private_music";

                        } else if (dropboxvalue == "Sports") {
                            blipIcon = "private_sportsandfitness";

                        } else {
                            blipIcon = "ic_launcher_round";
                        }


                        //Place Data

                        Blips blips = new Blips(cursor_coordinate_latitude,
                                cursor_coordinate_longitude,
                                BlipName,
                                userName,
                                Details,
                                blipIcon);
                        Users.child(userName).child("Blips").push().setValue(blips);// Add to user's blips


                        Blipsref.child("private").push().setValue(blips);//Add to private blips

                        dialog.cancel();
                        mMap.clear();
                        ShowBlips();



                    }



                }

            }
        });

        mCancelBlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
    }
    public void DeleteBlip(Marker marker){
        final LatLng coordinatetobedeleted =marker.getPosition();


        BlipsPublic.orderByChild("latitude").equalTo(coordinatetobedeleted.latitude).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {




                        for (DataSnapshot datacollected: dataSnapshot.getChildren()) {

                            String creator= datacollected.child("Creator").getValue(String.class);

                            //We add this because firebase queries sucks
                            if( datacollected.child("longitude").getValue(Double.class) == coordinatetobedeleted.longitude && creator.toLowerCase().contains(userName.toLowerCase()) ){
                                datacollected.getRef().removeValue();
                                Toast.makeText(MainActivity.this,"Blip Deleted", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });


        BlipsPrivate.orderByChild("latitude").equalTo(coordinatetobedeleted.latitude).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {




                        for (DataSnapshot datacollected: dataSnapshot.getChildren()) {
                            //We add this because firebase queries sucks
                            if( datacollected.child("longitude").getValue(Double.class) == coordinatetobedeleted.longitude){
                                datacollected.getRef().removeValue();
                                Toast.makeText(MainActivity.this,"Blip Deleted", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }
    public void EditBlip(final Marker marker){
        DeleteBlip(marker);
        final LatLng coordinatetobeupdated =marker.getPosition();

        cursor_coordinate = new LatLng(coordinatetobeupdated.latitude, coordinatetobeupdated.longitude);// Set current click location to marker
        cursor_coordinate_latitude= coordinatetobeupdated.latitude;
        cursor_coordinate_longitude= coordinatetobeupdated.longitude;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mBlipAddView = getLayoutInflater().inflate(R.layout.edit_blip_details_popup,null);

        mBlipName = mBlipAddView.findViewById(R.id.blipnameEt);
        mDetails = mBlipAddView.findViewById(R.id.detailsEt);
        Button mAddBlip = mBlipAddView.findViewById(R.id.addblip_button);
        Button mCancelBlip = mBlipAddView.findViewById(R.id.cancelblip_button);
        publicradio = mBlipAddView.findViewById(R.id.publicRadio);
        privateradio = mBlipAddView.findViewById(R.id.privateRadio);
        mySpinner = mBlipAddView.findViewById(R.id.iconsSpinner);



        RadioGroup radioGroup = (RadioGroup) mBlipAddView.findViewById(R.id.groupRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(publicradio.isChecked()){
                    privateradio.setChecked(false);
                    mySpinner.setAdapter(new MyCustomAdapterPublic(MainActivity.this, R.layout.row, CustomBlips));//Change to Public Spinnes

                }
                else if(privateradio.isChecked()){
                    publicradio.setChecked(false);
                    mySpinner.setAdapter(new MyCustomAdapterPrivate(MainActivity.this, R.layout.row, CustomBlips));//Change to Private Spinner

                }

                // checkedId is the RadioButton selected
            }
        });




        mBuilder.setView(mBlipAddView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAddBlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlipName = mBlipName.getText().toString();

                if(publicradio.isChecked()) {

                    if (validateForm()) {

                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        if (dropboxvalue == "Arts") {
                            blipIcon = "public_art";
                        } else if (dropboxvalue == "Transportation") {
                            blipIcon = "public_autoboatsair";

                        } else if (dropboxvalue == "Business") {
                            blipIcon = "public_business";

                        } else if (dropboxvalue == "Community") {
                            blipIcon = "public_community";

                        } else if (dropboxvalue == "Family & Education") {
                            blipIcon = "public_family";

                        } else if (dropboxvalue == "Fashion") {
                            blipIcon = "public_fashion";

                        } else if (dropboxvalue == "Media") {
                            blipIcon = "public_filmandmedia";

                        } else if (dropboxvalue == "Travel") {
                            blipIcon = "public_travelandoutdoor";

                        } else if (dropboxvalue == "Food") {
                            blipIcon = "public_foodanddrinks";

                        } else if (dropboxvalue == "Health") {
                            blipIcon = "public_health";

                        } else if (dropboxvalue == "Holiday") {
                            blipIcon = "public_holidaysandcelebrations";

                        } else if (dropboxvalue == "Music") {
                            blipIcon = "public_music";

                        } else if (dropboxvalue == "Sports") {
                            blipIcon = "public_sportsandfitness";

                        } else {
                            blipIcon = "ic_launcher_round";
                        }


                        //Place Data

                        Blips blips = new Blips(cursor_coordinate_latitude,
                                cursor_coordinate_longitude,
                                BlipName,
                                userName,
                                Details,
                                blipIcon);
                        Users.child(userName).child("Blips").push().setValue(blips);// Add to user's blips
                        Blipsref.child("public").push().setValue(blips);//Add to public blips

                        dialog.cancel();
                        mMap.clear();
                        ShowBlips();
                    }
                }


                else if(privateradio.isChecked()){
                    if (validateForm()) {


                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        if (dropboxvalue == "Arts") {//
                            blipIcon = "private_art";
                        } else if (dropboxvalue == "Transportation") {//
                            blipIcon = "private_autoboatsair";

                        } else if (dropboxvalue == "Business") {//
                            blipIcon = "private_business";

                        } else if (dropboxvalue == "Community") {//
                            blipIcon = "private_community";

                        } else if (dropboxvalue == "Family & Education") {//
                            blipIcon = "private_family";

                        } else if (dropboxvalue == "Fashion") {//
                            blipIcon = "private_fashion";

                        } else if (dropboxvalue == "Media") {//
                            blipIcon = "private_filmandmedia";

                        } else if (dropboxvalue == "Travel") {//
                            blipIcon = "private_travelandoutdoor";

                        } else if (dropboxvalue == "Food") {//
                            blipIcon = "private_foodanddrinks";

                        } else if (dropboxvalue == "Health") {//
                            blipIcon = "private_health";

                        } else if (dropboxvalue == "Holiday") {
                            blipIcon = "private_holidaysandcelebrations";

                        } else if (dropboxvalue == "Music") {
                            blipIcon = "private_music";

                        } else if (dropboxvalue == "Sports") {
                            blipIcon = "private_sportsandfitness";

                        } else {
                            blipIcon = "ic_launcher_round";
                        }


                        //Place Data

                        Blips blips = new Blips(cursor_coordinate_latitude,
                                cursor_coordinate_longitude,
                                BlipName,
                                userName,
                                Details,
                                blipIcon);
                        Users.child(userName).child("Blips").push().setValue(blips);// Add to user's blips

                        Blipsref.child("private").push().setValue(blips);//Add to private blips

                        dialog.cancel();
                        mMap.clear();
                        ShowBlips();


                    }



                }

            }
        });

        mCancelBlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });



    }
    private void ShowBlips() {

        Blipsref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                for (DataSnapshot snapm: dataSnapshot.getChildren()) {

                        Double latitude = snapm.child("latitude").getValue(Double.class);
                        Double longitude = snapm.child("longitude").getValue(Double.class);
                        String newBlipName= snapm.child("BlipName").getValue(String.class);
                        String creator= snapm.child("Creator").getValue(String.class);
                        String Details =snapm.child("Details").getValue(String.class);
                        String blipIcon =snapm.child("Icon").getValue(String.class);

                        LatLng newBlipCoordinates = new LatLng(latitude,longitude);


                    if(privatecheckbox.isChecked() &&  blipIcon.toLowerCase().contains("private".toLowerCase()) && creator.toLowerCase().contains(userName.toLowerCase()) ){

                        if(checkboxArts.isChecked() &&  blipIcon.toLowerCase().contains("art".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxBusiness.isChecked() &&  blipIcon.toLowerCase().contains("business".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxCommunity.isChecked() &&  blipIcon.toLowerCase().contains("community".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxFamily.isChecked() &&  blipIcon.toLowerCase().contains("family".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxFashion.isChecked() &&  blipIcon.toLowerCase().contains("fashion".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxFood.isChecked() &&  blipIcon.toLowerCase().contains("food".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxHealth.isChecked() &&  blipIcon.toLowerCase().contains("health".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxHoliday.isChecked() &&  blipIcon.toLowerCase().contains("holiday".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxMedia.isChecked() &&  blipIcon.toLowerCase().contains("media".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxTransportation.isChecked() &&  blipIcon.toLowerCase().contains("auto".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxTravel.isChecked() &&  blipIcon.toLowerCase().contains("travel".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxSports.isChecked() &&  blipIcon.toLowerCase().contains("sports".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxMusic.isChecked() &&  blipIcon.toLowerCase().contains("music".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }


                    }


                    if(publiccheckbox.isChecked() && blipIcon.toLowerCase().contains("public".toLowerCase()) )  {
                        if(checkboxArts.isChecked() &&  blipIcon.toLowerCase().contains("art".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxBusiness.isChecked() &&  blipIcon.toLowerCase().contains("business".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxCommunity.isChecked() &&  blipIcon.toLowerCase().contains("community".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxFamily.isChecked() &&  blipIcon.toLowerCase().contains("family".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxFashion.isChecked() &&  blipIcon.toLowerCase().contains("fashion".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxFood.isChecked() &&  blipIcon.toLowerCase().contains("food".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxHealth.isChecked() &&  blipIcon.toLowerCase().contains("health".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxHoliday.isChecked() &&  blipIcon.toLowerCase().contains("holiday".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxMedia.isChecked() &&  blipIcon.toLowerCase().contains("media".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxTransportation.isChecked() &&  blipIcon.toLowerCase().contains("auto".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxTravel.isChecked() &&  blipIcon.toLowerCase().contains("travel".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxSports.isChecked() &&  blipIcon.toLowerCase().contains("sports".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }
                        if(checkboxMusic.isChecked() &&  blipIcon.toLowerCase().contains("music".toLowerCase())  ){
                            PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);
                        }


                    }



                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if(!search.isIconified()){
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                }
                else {

                    mMap.clear();//Clear Map
                    ShowBlips();//Go back load all blips again
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(!search.isIconified()){
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                }
                else {
                    mMap.clear();//Clear Map
                    ShowBlips();//Go back load all blips again
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class MyCustomAdapterPublic extends ArrayAdapter<String>{

        public MyCustomAdapterPublic(Context context, int textViewResourceId,
                                     String[] objects) {
            super(context, textViewResourceId, objects);
// TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

//return super.getView(position, convertView, parent);

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.weekofday);
            label.setText(CustomBlips[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.icon);


            if (CustomBlips[position]=="Arts" ){
                icon.setImageResource(R.mipmap.public_art);
            }
            else if(CustomBlips[position]=="Transportation"){
                icon.setImageResource(R.mipmap.public_autoboatsair);
            }
            else if(CustomBlips[position]=="Business"){
                icon.setImageResource(R.mipmap.public_business);
            }
            else if(CustomBlips[position]=="Community"){
                icon.setImageResource(R.mipmap.public_community);
            }
            else if(CustomBlips[position]=="Family & Education"){
                icon.setImageResource(R.mipmap.public_family);
            }
            else if(CustomBlips[position]=="Fashion"){
                icon.setImageResource(R.mipmap.public_fashion);
            }
            else if(CustomBlips[position]=="Media"){
                icon.setImageResource(R.mipmap.public_filmandmedia);
            }
            else if(CustomBlips[position]=="Food"){
                icon.setImageResource(R.mipmap.public_foodanddrinks);
            }
            else if(CustomBlips[position]=="Health"){
                icon.setImageResource(R.mipmap.public_health);
            }
            else if(Objects.equals(CustomBlips[position], "Holiday")){
                icon.setImageResource(R.mipmap.public_holidaysandcelebrations);
            }
            else if(CustomBlips[position]=="Music"){
                icon.setImageResource(R.mipmap.public_music);
            }
            else if(CustomBlips[position]=="Sports"){
                icon.setImageResource(R.mipmap.public_sportsandfitness);
            }
            else if(CustomBlips[position]=="Travel"){
                icon.setImageResource(R.mipmap.public_travelandoutdoor);
            }

            else{
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }
    public class MyCustomAdapterPrivate extends ArrayAdapter<String>{

        public MyCustomAdapterPrivate(Context context, int textViewResourceId,
                                      String[] objects) {
            super(context, textViewResourceId, objects);
// TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
//return super.getView(position, convertView, parent);

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.weekofday);
            label.setText(CustomBlips[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.icon);


            if (CustomBlips[position]=="Arts" ){
                icon.setImageResource(R.mipmap.private_art);
            }

            else if(CustomBlips[position]=="Transportation"){
                icon.setImageResource(R.mipmap.private_autoboatsair);
            }
            else if(CustomBlips[position]=="Business"){
                icon.setImageResource(R.mipmap.private_business);
            }
            else if(CustomBlips[position]=="Community"){
                icon.setImageResource(R.mipmap.private_community);
            }
            else if(CustomBlips[position]=="Family & Education"){
                icon.setImageResource(R.mipmap.private_family);
            }
            else if(CustomBlips[position]=="Fashion"){
                icon.setImageResource(R.mipmap.private_fashion);
            }
            else if(CustomBlips[position]=="Media"){
                icon.setImageResource(R.mipmap.private_filmandmedia);
            }
            else if(CustomBlips[position]=="Food"){
                icon.setImageResource(R.mipmap.private_foodanddrinks);
            }
            else if(CustomBlips[position]=="Health"){
                icon.setImageResource(R.mipmap.private_health);
            }
            else if(CustomBlips[position]=="Holiday"){
                icon.setImageResource(R.mipmap.private_holidaysandcelebrations);
            }
            else if(CustomBlips[position]=="Music"){
                icon.setImageResource(R.mipmap.private_music);
            }
            else if(CustomBlips[position]=="Sports"){
                icon.setImageResource(R.mipmap.private_sportsandfitness);
            }
            else if(CustomBlips[position]=="Travel"){
                icon.setImageResource(R.mipmap.private_travelandoutdoor);
            }

            else{
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }





            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if(!search.isIconified()){
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                }
                else {

                    mMap.clear();//Clear Map
                    ShowBlips();//Go back load all blips again
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(!search.isIconified()){
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                }
                else {
                    mMap.clear();//Clear Map
                    ShowBlips();//Go back load all blips again
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class MyCustomAdapterPublic extends ArrayAdapter<String>{

        public MyCustomAdapterPublic(Context context, int textViewResourceId,
                                     String[] objects) {
            super(context, textViewResourceId, objects);
// TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
//return super.getView(position, convertView, parent);

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.weekofday);
            label.setText(CustomBlips[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.icon);


            if (CustomBlips[position]=="Arts" ){
                icon.setImageResource(R.mipmap.public_art);
            }
            else if(CustomBlips[position]=="Transportation"){
                icon.setImageResource(R.mipmap.public_autoboatsair);
            }
            else if(CustomBlips[position]=="Business"){
                icon.setImageResource(R.mipmap.public_business);
            }
            else if(CustomBlips[position]=="Community"){
                icon.setImageResource(R.mipmap.public_community);
            }
            else if(CustomBlips[position]=="Family & Education"){
                icon.setImageResource(R.mipmap.public_family);
            }
            else if(CustomBlips[position]=="Fashion"){
                icon.setImageResource(R.mipmap.public_fashion);
            }
            else if(CustomBlips[position]=="Media"){
                icon.setImageResource(R.mipmap.public_filmandmedia);
            }
            else if(CustomBlips[position]=="Food"){
                icon.setImageResource(R.mipmap.public_foodanddrinks);
            }
            else if(CustomBlips[position]=="Health"){
                icon.setImageResource(R.mipmap.public_health);
            }
            else if(CustomBlips[position]=="Holiday"){
                icon.setImageResource(R.mipmap.public_holidaysandcelebrations);
            }
            else if(CustomBlips[position]=="Music"){
                icon.setImageResource(R.mipmap.public_music);
            }
            else if(CustomBlips[position]=="Sports"){
                icon.setImageResource(R.mipmap.public_sportsandfitness);
            }
            else if(CustomBlips[position]=="Travel"){
                icon.setImageResource(R.mipmap.public_travelandoutdoor);
            }

            else{
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }
    public class MyCustomAdapterPrivate extends ArrayAdapter<String>{


        public MyCustomAdapterPrivate(Context context, int textViewResourceId,
                                      String[] objects) {
            super(context, textViewResourceId, objects);
// TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }


        public View getCustomView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
//return super.getView(position, convertView, parent);


            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.weekofday);
            label.setText(CustomBlips[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.icon);


            if (CustomBlips[position]=="Arts" ){
                icon.setImageResource(R.mipmap.private_art);
            }
            else if(CustomBlips[position]=="Transportation"){
                icon.setImageResource(R.mipmap.private_autoboatsair);
            }
            else if(CustomBlips[position]=="Business"){
                icon.setImageResource(R.mipmap.private_business);
            }
            else if(CustomBlips[position]=="Community"){
                icon.setImageResource(R.mipmap.private_community);
            }
            else if(CustomBlips[position]=="Family & Education"){
                icon.setImageResource(R.mipmap.private_family);
            }
            else if(CustomBlips[position]=="Fashion"){
                icon.setImageResource(R.mipmap.private_fashion);
            }
            else if(CustomBlips[position]=="Media"){
                icon.setImageResource(R.mipmap.private_filmandmedia);
            }
            else if(CustomBlips[position]=="Food"){
                icon.setImageResource(R.mipmap.private_foodanddrinks);
            }
            else if(CustomBlips[position]=="Health"){
                icon.setImageResource(R.mipmap.private_health);
            }
            else if(CustomBlips[position]=="Holiday"){
                icon.setImageResource(R.mipmap.private_holidaysandcelebrations);
            }
            else if(CustomBlips[position]=="Music"){
                icon.setImageResource(R.mipmap.private_music);
            }
            else if(CustomBlips[position]=="Sports"){
                icon.setImageResource(R.mipmap.private_sportsandfitness);
            }
            else if(CustomBlips[position]=="Travel"){
                icon.setImageResource(R.mipmap.private_travelandoutdoor);
            }

            else{
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;



        public MyCustomAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.notif_layout, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
            Button addBtn = (Button)view.findViewById(R.id.add_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });
            addBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }
    private boolean validateForm() {
        boolean valid = true;


        if (TextUtils.isEmpty(BlipName)) {
            mBlipName.setError("Required.");
            valid = false;
        } else {
            mBlipName.setError(null);
        }



        return valid;
    }
    private boolean validateAddFriend(){
        boolean valid = true;


        if (TextUtils.isEmpty(friendrequestemail)  ){
           friendemail.setError("Required.");
            valid = false;
        } else {
           friendemail.setError(null);
        }



        return valid;

    }

    private static String removecom(String str) {
        if(str==null){
            return null;
        }
        else{
            return str.substring(0, str.length() - 4);
        }

    }
    public void blipsupdateontextchange(final String query){
        mMap.clear();

        BlipsPublic.orderByChild("BlipName").addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapm : dataSnapshot.getChildren()) {

                    Double latitude = snapm.child("latitude").getValue(Double.class);
                    Double longitude = snapm.child("longitude").getValue(Double.class);
                    String newBlipName= snapm.child("BlipName").getValue(String.class);
                    String creator= snapm.child("Creator").getValue(String.class);
                    String Details =snapm.child("Details").getValue(String.class);
                    String blipIcon =snapm.child("Icon").getValue(String.class);

                    LatLng newBlipCoordinates = new LatLng(latitude,longitude);

                    if(newBlipName.toUpperCase().startsWith(   query.toUpperCase()  )    )
                    {

                        PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }



        });

        BlipsPrivate.orderByChild("BlipName").addListenerForSingleValueEvent(new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapm : dataSnapshot.getChildren()) {


                    Double latitude = snapm.child("latitude").getValue(Double.class);
                    Double longitude = snapm.child("longitude").getValue(Double.class);
                    String newBlipName= snapm.child("BlipName").getValue(String.class);
                    String creator= snapm.child("Creator").getValue(String.class);
                    String Details =snapm.child("Details").getValue(String.class);
                    String blipIcon =snapm.child("Icon").getValue(String.class);

                    LatLng newBlipCoordinates = new LatLng(latitude,longitude);

                    if(newBlipName.toUpperCase().startsWith(query.toUpperCase()) && creator.toLowerCase().contains(userName.toLowerCase())   )
                    {

                        PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }



        });
    }
    public void checkboxlisteners(){
        publiccheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        privatecheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
        checkboxTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
        checkboxMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
       checkboxSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

        checkboxCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
        checkboxBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
        checkboxFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
        checkboxMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });
        checkboxFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                ShowBlips();
                //handle click
            }
        });

    }

    private void SendFriendRequest() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mFriendAddView = getLayoutInflater().inflate(R.layout.add_friend,null);

        friendemail = mFriendAddView.findViewById(R.id.add_friend_et);
        Button AddFriend = mFriendAddView.findViewById(R.id.add_friend_btn);
        Button CancelFriend = mFriendAddView.findViewById(R.id.cancel_friend_btn);
        mBuilder.setView(mFriendAddView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();


        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendrequestemail=removecom(friendemail.getText().toString());
                if( validateAddFriend()){

                    friendrequestemail=removecom(friendemail.getText().toString());

                    Users.addListenerForSingleValueEvent(new  ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {



                            if (dataSnapshot.hasChild(friendrequestemail)) {

                                if (dataSnapshot.child(friendrequestemail).child("FriendRequests").hasChild(userName)) {

                                    Toast.makeText(MainActivity.this, "Friend Request Already Sent", Toast.LENGTH_SHORT).show();


                                }

                                else {
                                    Users.child(friendrequestemail).child("FriendRequests").push().child(userName).setValue(1);// Add to user's blips
                                    Toast.makeText(MainActivity.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();


                                }


                            }



                            else{
                                Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                friendemail.setText(null);


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        CancelFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();





    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            // Permission was denied. Display an error message.
        }
        mMap.setMyLocationEnabled(true);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();

        }
    }








}





