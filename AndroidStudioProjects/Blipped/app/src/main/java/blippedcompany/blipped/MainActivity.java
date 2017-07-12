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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //Google Map Initialize
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;
    private final LatLng mDefaultLocation = new LatLng(14.5955772, 120.9880854);
    private static final int DEFAULT_ZOOM = 17;
    LatLng coordinate;//Declare Coordinate
    LatLng cursor_coordinate;

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
    EditText mBlipName;
    EditText mDetails;
    Spinner mySpinner;
    String[] CustomBlips = {"Arts", "Transportation", "Business",
            "Community", "Family & Education", "Fashion", "Media","Anime"};
    String blipIcon;



    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.



    //Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userID = mAuth.getCurrentUser();

    //Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //initialize lat and lng values
    DatabaseReference Users = database.getReference("users");
    DatabaseReference Blipsref = database.getReference("blips");
    DatabaseReference BlipsPublic = database.getReference("blips").child("public");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sidebar);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//TOOLBAR
        setSupportActionBar(toolbar);

        //Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Connect Geo Location
        GoogleMapAPIConnect();

        //Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.mipmap.ic_gps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
                Snackbar.make(view, "Replace with your owns action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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



        //Search Box
        SearchView search =(SearchView) findViewById(R.id.searchView);




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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_top_right, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "Sign out", Toast.LENGTH_SHORT).show();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

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



    /**
     * Google Maps Functions
     *
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.auber_style));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(14.5955772, 120.9880854), 17);// Zoom w/ 17
        mMap.animateCamera(yourLocation); /// Zoom

        userName=removecom(userID.getEmail());

        ShowBlips();

        // WHen map is long clicked
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {


            @Override
            public void onMapLongClick(LatLng point) {
                cursor_coordinate = new LatLng(point.latitude, point.longitude);// Set current click location to marker
                cursor_coordinate_latitude= point.latitude;
                cursor_coordinate_longitude= point.longitude;

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.enter_blip_details_popup,null);

                mBlipName = mView.findViewById(R.id.blipnameEt);
                mDetails = mView.findViewById(R.id.detailsEt);
                Button mAddBlip = mView.findViewById(R.id.addblip_button);
                Button mCancelBlip = mView.findViewById(R.id.cancelblip_button);
                //DROP DOWN
                mySpinner = mView.findViewById(R.id.iconsSpinner);
                mySpinner.setAdapter(new MyCustomAdapter(MainActivity.this, R.layout.row, CustomBlips));

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mAddBlip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlipName = mBlipName.getText().toString();
                        if(BlipName==""){
                            BlipName="My Blip";
                        }
                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();
                        Toast.makeText(MainActivity.this, dropboxvalue, Toast.LENGTH_SHORT).show();

                        if (dropboxvalue=="Arts"){
                            blipIcon="publicarts";
                        }
                        else if(dropboxvalue=="Transportation"){
                            blipIcon="publicautoboatsair";
                        }
                        else if(dropboxvalue=="Business"){
                            blipIcon="publicbusiness";
                        }
                        else if(dropboxvalue=="Community"){
                            blipIcon="publiccommunity";
                        }
                        else if(dropboxvalue=="Family & Education"){
                            blipIcon="publicfamilyneducation";
                        }
                        else if(dropboxvalue=="Fashion"){
                            blipIcon="publicfashion";
                        }
                        else if(dropboxvalue=="Media"){
                            blipIcon="publicfilmnmedia";
                        }
                        else if(dropboxvalue=="Anime"){
                            blipIcon="anime";
                        }

                        else{
                            blipIcon="ic_launcher_round";
                        }


                        //Place Data

                        Blips blips = new Blips(cursor_coordinate_latitude,
                                                cursor_coordinate_longitude,
                                                BlipName,
                                                userName,
                                                Details,
                                                blipIcon);
                        Users.child(userName).child("Blips").push().setValue(blips);
                        Blipsref.child("public").push().setValue(blips);

                        dialog.cancel();
                        mMap.clear();
                        ShowBlips();

                    }
                });

                mCancelBlip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();

                    }
                });

                mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                    @Override
                    public void onInfoWindowLongClick(Marker marker) {

                        DeleteBlip(marker);

                    }
                });





            }
        });


    }

    public void PlaceMarker(LatLng newBlipCoordinates, String newBlipName, String creator, String Details, Marker addmarkers, String blipIcon) {

      addmarkers = mMap.addMarker(new MarkerOptions() // Set Marker
                .position(newBlipCoordinates)
                .title(newBlipName)
                .snippet(creator)
                .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(blipIcon,"mipmap", getPackageName() ))));
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

    private void getDeviceLocation() {
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
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
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
        }
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

    private static String removecom(String str) {
        return str.substring(0, str.length() - 4);
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

                    PlaceMarker(newBlipCoordinates,newBlipName,creator,Details,markers,blipIcon);

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                mMap.clear();//Clear Map
                ShowBlips();//Go back load all blips again
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                              mMap.clear();//Clear Map
                              ShowBlips();//Go back load all blips again
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   private void DeleteBlip(Marker marker){

       final LatLng coordinatetobedeleted =marker.getPosition();
       String x=Double.toString(coordinatetobedeleted.latitude);

       Toast.makeText(MainActivity.this,x, Toast.LENGTH_SHORT).show();

       BlipsPublic.orderByChild("latitude").equalTo(coordinatetobedeleted.latitude).addListenerForSingleValueEvent(
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

    public class MyCustomAdapter extends ArrayAdapter<String>{

        public MyCustomAdapter(Context context, int textViewResourceId,
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

            if (CustomBlips[position]=="Arts"){
                icon.setImageResource(R.mipmap.publicarts);
            }
            else if(CustomBlips[position]=="Transportation"){
                icon.setImageResource(R.mipmap.publicautoboatsair);
            }
            else if(CustomBlips[position]=="Business"){
                icon.setImageResource(R.mipmap.publicbusiness);
            }
            else if(CustomBlips[position]=="Community"){
                icon.setImageResource(R.mipmap.publiccommunity);
            }
            else if(CustomBlips[position]=="Family & Education"){
                icon.setImageResource(R.mipmap.publicfamilyneducation);
            }
            else if(CustomBlips[position]=="Fashion"){
                icon.setImageResource(R.mipmap.publicfashion);
            }
            else if(CustomBlips[position]=="Media"){
                icon.setImageResource(R.mipmap.publicfilmnmedia);
            }
            else if(CustomBlips[position]=="Anime"){
                icon.setImageResource(R.mipmap.anime);
            }

            else{
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }



}





