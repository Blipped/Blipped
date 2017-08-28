package blippedcompany.blipped;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //Google Map Initialize
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private final LatLng mDefaultLocation = new LatLng(14.5955772, 120.9880854);
    private static final int DEFAULT_ZOOM = 17;
    LatLng cursor_coordinate;
    SearchView search ;
    GoogleApiClient mGoogleApiClient;



    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    //Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userID = mAuth.getCurrentUser();
    final String userName = removecom(userID.getEmail());

    //Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //initialize lat and lng values
    DatabaseReference Users = database.getReference("users");
    DatabaseReference Blipsref = database.getReference("blips");
    DatabaseReference BlipsPublic = database.getReference("blips").child("public");
    DatabaseReference BlipsPrivate = database.getReference("blips").child("private");
    DatabaseReference UsersEmailFriends = database.getReference("users").child(userName).child("Friends");
    DatabaseReference UsersEmailPic = database.getReference("users").child(userName).child("profilepic");
    DatabaseReference UsersEmailBlips = database.getReference("users").child(userName).child("Blips");
    DatabaseReference UsersEmailBlipsAttended = database.getReference("users").child(userName).child("BlipsAttended");
    DatabaseReference UsersEmailBlipsPlanned = database.getReference("users").child(userName).child("BlipsPlanned");

    DatabaseReference UsersEmailFriendRequests = database.getReference("users").child(userName).child("FriendRequests");
    DatabaseReference liveGPSEmail = database.getReference("liveGPS").child(userName);
    DatabaseReference liveGPS = database.getReference("liveGPS");
    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://blipped-project.appspot.com");
    //Variables
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    Marker myMarker;

    Double cursor_coordinate_latitude;
    Double cursor_coordinate_longitude;

    String BlipName;
    String Details;
    String BlipStartTime ;
    String BlipStartDate;
    String BlipEndTime ;
    String BlipEndDate ;
    Date BlipStartDateTime ;
    Date BlipEndDateTime ;

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.ENGLISH);
    Boolean isSuperPrivate = false;
    String allowedfriends;

    EditText friendemailEt;
    String friendrequestemail;
    EditText mBlipName;
    EditText mDetails;
    Spinner mySpinner;
    EditText timeedittext;
    CheckBox publiccheckbox;
    CheckBox privatecheckbox;
    CheckBox checkboxArts;
    CheckBox checkboxBusiness;
    CheckBox checkboxCommunity;
    CheckBox checkboxFamily;
    CheckBox checkboxFashion;
    CheckBox checkboxFood;
    CheckBox checkboxHealth;
    CheckBox checkboxMedia;
    CheckBox checkboxSports;
    CheckBox checkboxTransportation;
    CheckBox checkboxHoliday;
    CheckBox checkboxTravel;
    CheckBox checkboxMusic;
    ScrollView filterscroll;
    RadioButton publicradio;
    RadioButton privateradio;
    RadioButton SuperPrivateradio;
    String[] CustomBlips = {"Arts", "Transportation", "Business",
            "Community", "Family & Education", "Fashion", "Media", "Food", "Health", "Holiday", "Music", "Sports", "Travel"};
    String blipIcon;
    String PublicPrivate;
    String Category;
    ArrayList<String> friendrequestlist;
    NavigationView navigationView;
    Menu nv;
    MenuItem item_notifications;
    ListView lView;
    AlertDialog dialogfriendrequest;
    ArrayList<String> friendarraylist;
    ArrayList<String> friendprofilepicarraylist;
    ArrayList<String> profilepicarraylist;
    ArrayList<String> attendinglist;
    ArrayList markerinfolist;
    HashMap<String,Blips> markerlist=new HashMap<>();
    HashMap<String,Marker> markerlist2=new HashMap<>();
    HashMap<String,Blips> mymarkerlist=new HashMap<>();
    HashMap<String,Blips> mymarkerlistattending=new HashMap<>();
    HashMap<String,Blips> mymarkerlistplanning=new HashMap<>();
    HashMap<String,Marker>gpslist=new HashMap<>();

    String friendname;

    private Circle lastUserCircle;
    private long pulseDuration = 10;
    private ValueAnimator lastPulseAnimator;
    LatLng pulseLatlng;
    Marker gpsmarker;
    Switch showGPSToggle;
    LocationListener locationListener;
    LocationManager locationManager;



    Uri filePath=null;
    ImageView imgView;
    ProgressDialog pd;
    HashMap<String,Uri> filePathMap = new HashMap<>();
    UploadTask uploadTask;
    String imageURLLink;
    Blips blips;
    ImageView badge;

    String Description;
    String mCurrentPhotoPath;
    Uri photoURI;
    Marker selected;
    String[] dataarray;
    String profpicdownloadlink;


    ImageView profilepic;
    int markerkey =0;
    int gpsmarkerkey =0;

    BottomNavigationView bottomNavigationView;
    int bottomnavheight;
    Boolean goingStatus=false;


    int showmyplacesmode=0;
    TextView backtonormalview;
    String key = null;
    Button goingbutton;
    Blips blipsattended;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveGPSEmail.onDisconnect().setValue(null);
        setContentView(R.layout.sidebar);
        friendarraylist = new ArrayList<String>();
        friendrequestlist = new ArrayList<String>();
        friendprofilepicarraylist = new ArrayList<String>();
        profilepicarraylist = new ArrayList<String>();
        attendinglist = new ArrayList<String>();

        DeclareThings();
        ShowFriendRequestCount();

        backtonormalview = (TextView) findViewById(R.id.backtonormalmodebutton);
        filterscroll = (ScrollView) findViewById(R.id.filterscroll);
        backtonormalview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showmyplacesmode=0;
                backtonormalview.setVisibility(GONE);
                reload();
            }
        });
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);






    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        if (locationManager != null) {
            try {
                liveGPSEmail.setValue(null);
                locationManager.removeUpdates(locationListener);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        liveGPSEmail.setValue(null);



    }

    public void DeclareThings() {

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//TOOLBAR
        setSupportActionBar(toolbar);

        //Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Connect Geo Location
        GoogleMapAPIConnect();


        //Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);//Layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Navigation View
        navigationView = (NavigationView) findViewById(R.id.nav_view);//Layout
        navigationView.setNavigationItemSelectedListener(this);
        nv = navigationView.getMenu();
        item_notifications = nv.findItem(R.id.nav_notifications);


        //CheckBoxes
        publiccheckbox = (CheckBox) findViewById(R.id.checkboxPublic);
        privatecheckbox = (CheckBox) findViewById(R.id.checkboxPrivate);
        checkboxMusic = (CheckBox) findViewById(R.id.checkboxMusic);
        checkboxArts = (CheckBox) findViewById(R.id.checkboxArts);
        checkboxBusiness = (CheckBox) findViewById(R.id.checkboxBusiness);
        checkboxCommunity = (CheckBox) findViewById(R.id.checkboxCommunity);
        checkboxFamily = (CheckBox) findViewById(R.id.checkboxFamily);
        checkboxFashion = (CheckBox) findViewById(R.id.checkboxFashion);
        checkboxFood = (CheckBox) findViewById(R.id.checkboxFood);
        checkboxHealth = (CheckBox) findViewById(R.id.checkboxHealth);
        checkboxMedia = (CheckBox) findViewById(R.id.checkboxMedia);
        checkboxSports = (CheckBox) findViewById(R.id.checkboxSports);
        checkboxTransportation = (CheckBox) findViewById(R.id.checkboxTransportation);
        checkboxHoliday = (CheckBox) findViewById(R.id.checkboxHoliday);
        checkboxTravel = (CheckBox) findViewById(R.id.checkboxTravel);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws NullPointerException {

        TextView userNameText = (TextView) findViewById(R.id.currentUserTxt);
        userNameText.setText("Welcome " + userID.getEmail());

        getFriendsList();
        ImageButton editprofilepic = (ImageButton) findViewById(R.id.editprofilepicbutton);
        profilepic = (ImageView) findViewById(R.id.profilepic);
        loadprofilepic();

        editprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editprofpic();
            }
        });


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
        locationListen();
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_style));

        showGPSToggle = (Switch) findViewById(R.id.showgpstoggle);

        showGPSToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!showGPSToggle.isChecked()){

                    liveGPSEmail.setValue(null);

                }
                else{



                }

            }
        });
        checkboxlisteners();

        ShowBlipsPublic();
        ShowBlipsPrivate();
        ShowGPSLocation();



        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete: {
                                    try {
                                        if (selected.getSnippet().contains(userName)) {
                                            if (showmyplacesmode==0) {
                                               DeleteBlip(selected);
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Cannot Delete Blip in this Mode", Toast.LENGTH_SHORT).show();
                                            }

                                            bottomNavigationView.setVisibility(GONE);
                                            break;
                                        }
                                    } catch (NullPointerException e) {
                                        Toast.makeText(MainActivity.this, "No marker selected", Toast.LENGTH_SHORT).show();
                                    }
                            }

                            case R.id.action_edit: {
                                    try {
                                        if (selected.getSnippet().contains(userName)) {
                                            if (showmyplacesmode==0) {
                                               EditBlip(selected);
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Cannot Edit Blip in this Mode", Toast.LENGTH_SHORT).show();
                                            }

                                            break;
                                        }
                                    } catch (NullPointerException e) {

                                    }
                            }


                            case R.id.action_info:{
                                ShowBlipInfo(selected);
                            }

                        }
                        return true;
                    }
                });
        // WHen map is long clicked
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {

                if (showmyplacesmode==0) {
                    AddBlip(point);
                }
                else{
                    Toast.makeText(MainActivity.this, "Cannot Add Blip in this Mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (bottomNavigationView.getVisibility() == VISIBLE) {
                    Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_down);
                    mMap.setPadding(0, 0, 0, 0);
                    bottomNavigationView.startAnimation(bottomDown);
                    bottomNavigationView.setVisibility(GONE);
                } else {
                    // Either gone or invisible
                }


            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                onMarkerClickAction(marker);
                bottomnavheight =bottomNavigationView.getHeight();
                mMap.setPadding(0, 0, 0, bottomnavheight );
                Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.bottom_up);

                bottomNavigationView.startAnimation(bottomUp);
                bottomNavigationView.setVisibility(VISIBLE);
                key=null;
                selected = marker;
                getkey(marker);



                return false;
            }
        });


        //When map is infolong clicked
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                ShowBlipInfo(marker);

            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {



            }
        });


    }

    public void onMarkerClickAction(Marker marker)
    { attendinglist.clear();
        if(marker.getSnippet().contains("123marcius")) {

            //If Normal Marker is clicked
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                View v;
                @Override
                public View getInfoWindow(final Marker marker) {

                    v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                    badge = v.findViewById(R.id.badge);
                    //Split Information int array
                    try {
                        dataarray = marker.getSnippet().split("123marcius(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    } catch (NullPointerException e) {

                    }



                    if (dataarray[2] != null) {
                        Picasso.with(getApplicationContext())
                                .load(dataarray[2])
                                .error(R.mipmap.error)
                                .placeholder(R.mipmap.placeholderimage)
                                .into(badge, new MarkerCallback(marker));

                    }


                    TextView title = v.findViewById(R.id.title);
                    title.setText(marker.getTitle());


                    return v;


                }


                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
        }

        else{
            //If Live GPS Marker is clicked
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                View vgps;
                @Override
                public View getInfoWindow(final Marker marker) {
                    vgps = getLayoutInflater().inflate(R.layout.gpscustom_info_window, null);

                    //Split Information int array



                    TextView title = vgps.findViewById(R.id.title);
                    title.setText(marker.getTitle());

                    return vgps;

                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

        }
    }

    public Blips getselectedblipdata(){
        if (dataarray[6].equals("Private")) {
            Blipsref.child("private")
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {////////////////////////////
                            Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                            String newBlipName = dataSnapshot.child("BlipName").getValue(String.class);
                            String creator = dataSnapshot.child("Creator").getValue(String.class);
                            String Details = dataSnapshot.child("Details").getValue(String.class);
                            String blipIcon = dataSnapshot.child("Icon").getValue(String.class);
                            String imgURL = dataSnapshot.child("imageURL").getValue(String.class);
                            String DateCreated = dataSnapshot.child("DateCreated").getValue(String.class);
                            String StartTime= dataSnapshot.child("StartTime").getValue(String.class);
                            String EndTime = dataSnapshot.child("EndTime").getValue(String.class);
                            String allowedfriends= dataSnapshot.child("allowedfriends").getValue(String.class);
                            Boolean isSuperPrivate = dataSnapshot.child("isSuperPrivate").getValue(Boolean.class);
                            String PublicPrivate = dataSnapshot.child("PublicPrivate").getValue(String.class);
                            String Category= dataSnapshot.child("Category").getValue(String.class);

                            blipsattended = new Blips(latitude,
                                    longitude,
                                    newBlipName,
                                    creator,
                                    Details,
                                    blipIcon,
                                    DateCreated,
                                    StartTime,
                                    EndTime,
                                    imgURL,
                                    allowedfriends,
                                    isSuperPrivate,
                                    PublicPrivate,
                                    Category,
                                    attendinglist);



                        }/////////////////////

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }});
        } else if(dataarray[6].equals("Public")){
            Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
            Blipsref.child("public")
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                            String newBlipName = dataSnapshot.child("BlipName").getValue(String.class);
                            String creator = dataSnapshot.child("Creator").getValue(String.class);
                            String Details = dataSnapshot.child("Details").getValue(String.class);
                            String blipIcon = dataSnapshot.child("Icon").getValue(String.class);
                            String imgURL = dataSnapshot.child("imageURL").getValue(String.class);
                            String DateCreated = dataSnapshot.child("DateCreated").getValue(String.class);
                            String StartTime= dataSnapshot.child("StartTime").getValue(String.class);
                            String EndTime = dataSnapshot.child("EndTime").getValue(String.class);
                            String allowedfriends= dataSnapshot.child("allowedfriends").getValue(String.class);
                            Boolean isSuperPrivate = dataSnapshot.child("isSuperPrivate").getValue(Boolean.class);
                            String PublicPrivate = dataSnapshot.child("PublicPrivate").getValue(String.class);
                            String Category= dataSnapshot.child("Category").getValue(String.class);

                           blipsattended = new Blips(latitude,
                                    longitude,
                                    newBlipName,
                                    creator,
                                    Details,
                                    blipIcon,
                                    DateCreated,
                                    StartTime,
                                    EndTime,
                                    imgURL,
                                    allowedfriends,
                                    isSuperPrivate,
                                    PublicPrivate,
                                    Category,
                                    attendinglist);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }});

        }

        return blipsattended;
    }

    public void ShowBlipInfo(final Marker marker)  {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mBlipInfoView = getLayoutInflater().inflate(R.layout.blip_info_page, null);

        ImageButton BlipInfoImage = mBlipInfoView.findViewById(R.id.BlipInfoPhoto);
        TextView BlipInfoMonth = mBlipInfoView.findViewById(R.id.BlipInfoMonth);
        TextView BlipInfoDay = mBlipInfoView.findViewById(R.id.BlipInfoDay);
        TextView BlipInfoTitle = mBlipInfoView.findViewById(R.id.BlipInfoTitle);
        TextView BlipInfoCategoryandHost = mBlipInfoView.findViewById(R.id.BlipInfoCategoryandHost);
        TextView BlipInfoStartTime = mBlipInfoView.findViewById(R.id.BlipInfoStartTIme);
        TextView BlipInfoEndTime = mBlipInfoView.findViewById(R.id.BlipInfoEndTime);
        TextView BlipInfoDetails = mBlipInfoView.findViewById(R.id.BlipInfoDetails);
        TextView BlipInfoCreatedIn = mBlipInfoView.findViewById(R.id.BlipInfoCreatedIn);
        goingbutton = mBlipInfoView.findViewById(R.id.goingbutton);
        Button finishevent = mBlipInfoView.findViewById(R.id.finisheventbutton);
        mBuilder.setView(mBlipInfoView);
       final  AlertDialog dialog = mBuilder.create();
        dataarray = marker.getSnippet().split("123marcius(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        if(showmyplacesmode!=0){
            goingbutton.setVisibility(GONE);

        }
        if(dataarray[0].equals(userName)){
            finishevent.setVisibility(VISIBLE);
        }

        try {
            if (dataarray[6].equals("Private")) {
                Blipsref.child("private")
                        .child(key)
                        .child("AttendingList")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot datacollected : dataSnapshot.getChildren()) {
                                   attendinglist.add( datacollected.getKey());
                                }

                                if(dataSnapshot.hasChild(userName)){
                                    goingStatus=true;
                                    goingbutton.setText("Going");
                                }
                                else{

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }});
            } else if(dataarray[6].equals("Public")){
                Blipsref.child("public")
                        .child(key)
                        .child("AttendingList")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                for (DataSnapshot datacollected : dataSnapshot.getChildren()) {
                                    attendinglist.add( datacollected.getKey());
                                }
                                if(dataSnapshot.hasChild(userName)){
                                    goingStatus=true;
                                    goingbutton.setText("Going");

                                }
                                else{

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }});

            }
        } catch (Exception e) {

            goingbutton.setText("Not Going");
            goingStatus=false;
        }



        BlipInfoTitle.setText(marker.getTitle());
        BlipInfoCategoryandHost.setText(dataarray[6]+"  "+dataarray[7]+"  " +"Hosted by "+dataarray[0]);

        if (dataarray[2] != null) {
            RequestOptions options = new RequestOptions()
                    .error(R.mipmap.background1)
                    .placeholder(R.mipmap.background1);

            Glide.with(getActivity())
                    .load(dataarray[2])
                    .apply(options)
                    .into(BlipInfoImage);

        }

        BlipInfoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage(marker);
            }
        });
        final Blips data = new Blips(marker.getPosition().latitude,
                marker.getPosition().longitude,
                marker.getTitle(),
                dataarray[0],
                dataarray[1],
                dataarray[8],
                dataarray[3],
                dataarray[4],
                dataarray[5],
                dataarray[2],
                null,
                false,
                dataarray[6],
                dataarray[7],
                attendinglist);

        goingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataarray[6].equals("Public")){
                    if(goingStatus==true){
                        BlipsPublic.child(key).child("AttendingList").child(userName).setValue(null);
                        Users.child(userName).child("BlipsPlanned").child(key).setValue(null);
                        goingbutton.setText("Not Going");
                        goingStatus=false;
                    }
                    else{
                        BlipsPublic.child(key).child("AttendingList").child(userName).setValue(1);
                        Users.child(userName).child("BlipsPlanned").child(key).setValue(data);
                        goingbutton.setText("Going");
                        goingStatus=true;
                    }

                }

                if(dataarray[6].equals("Private")){
                    if(goingStatus==true){
                        BlipsPrivate.child(key).child("AttendingList").child(userName).setValue(null);
                        Users.child(userName).child("BlipsPlanned").child(key).setValue(null);
                        goingbutton.setText("Not Going");
                        goingStatus=false;
                    }
                    else{
                        BlipsPrivate.child(key).child("AttendingList").child(userName).setValue(1);
                        Users.child(userName).child("BlipsPlanned").child(key).setValue(data);
                        goingbutton.setText("Going");
                        goingStatus=true;
                    }

                }


            }
        });



        finishevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (String x : attendinglist)
                {   Users.child(x).child("BlipsAttended").child(key).setValue(data);
                    System.out.println(x);
                }
                  dialog.cancel();


            }});


                //TIME SETTING
        BlipInfoDetails.setText(dataarray[1]);
        BlipInfoCreatedIn.setText("Created on "+dataarray[3] + " At "+getAddress(selected.getPosition().latitude,selected.getPosition().longitude));
        String[] startdatetime = new String[0];
        String[] startyearmonthday = new String[0];

        if (dataarray[4]!=null) {
            startdatetime = dataarray[4].split(" ");
            String startdate= startdatetime[0];
            startyearmonthday = startdate.split("-");
            String syear = startyearmonthday[0];
            String smonth = startyearmonthday[1];
            String sday = startyearmonthday[2];
            String samonth = new DateFormatSymbols().getMonths()[Integer.parseInt(smonth) -1];

            String starttime= startdatetime[1];
            String[] starthourmin = starttime.split(":");
            String shour = starthourmin[0];
            String smin = starthourmin[1];


            String startampm= startdatetime[2];
            BlipInfoStartTime.setText("Starts at "+shour+":"+smin+" "+startampm+" "+ samonth +" "+sday+","+syear);
            BlipInfoMonth.setText(samonth);
            BlipInfoDay.setText(sday);
        }

        if(!dataarray[5].contains("null")){
            String[] enddatetime = new String[0];
            String[] endyearmonthday = new String[0];

            enddatetime = dataarray[5].split(" ");
            String enddate= enddatetime[0];
            endyearmonthday = enddate.split("-");
            String eyear = endyearmonthday[0];
            String emonth = endyearmonthday[1];
            String eday = endyearmonthday[2];
            String eamonth = new DateFormatSymbols().getMonths()[Integer.parseInt(emonth) -1];

            String endtime= enddatetime[1];
            String[] endhourmin = endtime.split(":");
            String ehour = endhourmin[0];
            String emin = endhourmin[1];


            String endampm= enddatetime[2];
            BlipInfoEndTime.setText("Ends at "+ehour+":"+emin+" "+endampm+" "+ eamonth +" "+eday+","+eyear);
        }
        else{
            BlipInfoEndTime.setVisibility(View.INVISIBLE);
        }


        dialog.show();



    }

    public class MarkerCallback implements Callback {
        Marker marker=null;

        MarkerCallback(Marker marker) {
            this.marker=marker;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }
    }

    public void showImage(Marker marker){
        View  imagefull = getLayoutInflater().inflate(R.layout.fullscreen_image, null);
        ImageView fullscreenimage = imagefull.findViewById(R.id.fullscreenimage);

        try {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);


            dataarray = marker.getSnippet().split("123marcius(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            RequestOptions options = new RequestOptions()
                    .error(R.mipmap.error)
                    .placeholder(R.mipmap.placeholderimage);

            Glide.with(getActivity())
                    .load(dataarray[2])
                    .apply(options)
                    .into(fullscreenimage);


            mBuilder.setView(imagefull);
            final AlertDialog dialog = mBuilder.create();

            dialog.show();


        } catch (NullPointerException e) {

        }

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
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
        }


    }

    public void PlaceMarker(final Blips blipsadded) {
        if (showmyplacesmode==0) {//If user is not viewing his places, do as normal

                    LatLng newBlipCoordinates = new LatLng(blipsadded.latitude, blipsadded.longitude);
                    // Put inormation into a single string with comma seperators
                    Description = blipsadded.Creator+"123marcius"+
                            blipsadded.Details +"123marcius"+
                            blipsadded.imageURL +"123marcius"+
                            blipsadded.DateCreated+"123marcius"+
                            blipsadded.StartTime+"123marcius"+
                            blipsadded.EndTime+"123marcius"+
                            blipsadded.PublicPrivate +"123marcius"+
                            blipsadded.Category +"123marcius"+
                            blipsadded.Icon +"123marcius"  ;
                    myMarker =   mMap.addMarker(new MarkerOptions() // Set Marker
                            .position(newBlipCoordinates)
                            .title(blipsadded.BlipName)
                            .snippet(Description)
                            .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(blipsadded.Icon, "mipmap", getPackageName()))));

                    markerlist2.put(Integer.toString(markerkey), myMarker);
                    dropPinEffect(myMarker);
        } else {//If user is still viewing dont place only add blip


        }

    }

    public void PlaceMarkernoanimation(final Blips blipsadded) {


            LatLng newBlipCoordinates = new LatLng(blipsadded.latitude, blipsadded.longitude);
            // Put inormation into a single string with comma seperators
            Description = blipsadded.Creator+"123marcius"+//0
                    blipsadded.Details +"123marcius"+//1
                    blipsadded.imageURL +"123marcius"+//2
                    blipsadded.DateCreated+"123marcius"+//3
                    blipsadded.StartTime+"123marcius"+//4
                    blipsadded.EndTime+"123marcius"+//5
                    blipsadded.PublicPrivate +"123marcius"+//6
                    blipsadded.Category +"123marcius" +//7
                    blipsadded.Icon +"123marcius"  ;//8
            myMarker =   mMap.addMarker(new MarkerOptions() // Set Marker

                    .position(newBlipCoordinates)
                    .title(blipsadded.BlipName)
                    .snippet(Description)
                    .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(blipsadded.Icon, "mipmap", getPackageName()))));




    }

    public void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Random random = new Random();
        int key =random.nextInt(1000);

        File photo = new File(Environment.getExternalStorageDirectory(), "picture"+key+".jpg");
        //  File photo = new File(getCacheDir(), "picture.jpg");


        photoURI = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, 0);

    }

    public void AddBlip(LatLng point) {
        cursor_coordinate = new LatLng(point.latitude, point.longitude);// Set current click location to marker
        cursor_coordinate_latitude = point.latitude;
        cursor_coordinate_longitude = point.longitude;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mBlipAddView = getLayoutInflater().inflate(R.layout.add_blip_details_popup, null);

        mBlipName = mBlipAddView.findViewById(R.id.blipnameEt);
        mDetails = mBlipAddView.findViewById(R.id.detailsEt);
        Button mAddBlip = mBlipAddView.findViewById(R.id.addblip_button);
        Button mCancelBlip = mBlipAddView.findViewById(R.id.cancelblip_button);
        publicradio = mBlipAddView.findViewById(R.id.publicRadio);
        privateradio = mBlipAddView.findViewById(R.id.privateRadio);
        SuperPrivateradio = mBlipAddView.findViewById(R.id.SuperPrivateRadio);
        mySpinner = mBlipAddView.findViewById(R.id.iconsSpinner);
        Button chooseImg =  mBlipAddView.findViewById(R.id.chooseImg);
        Button takePhoto =  mBlipAddView.findViewById(R.id.takePhoto);
        imgView = mBlipAddView.findViewById(R.id.imgView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, friendarraylist);
        final MultiAutoCompleteTextView allowedfriendsmultiline = mBlipAddView.findViewById(R.id.allowfriendsedittext);

        allowedfriendsmultiline.setAdapter(adapter);
        allowedfriendsmultiline.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        final TextView timeedittext = mBlipAddView.findViewById(R.id.timeTextView);
        final TextView dateedittext = mBlipAddView.findViewById(R.id.dateTextView);
        final TextView timeedittextEnd = mBlipAddView.findViewById(R.id.timeTextViewend);
        final TextView dateedittextEnd = mBlipAddView.findViewById(R.id.dateTextViewend);
        final TextView addendtimeEnd = mBlipAddView.findViewById(R.id.addendtimebutton);
        final TextView removeendtimeEnd = mBlipAddView.findViewById(R.id.removeendtimebutton);
        final LinearLayout endtimebar = mBlipAddView.findViewById(R.id.endtimebar);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");


         BlipEndTime  = null;
         BlipEndDate  = null;

        Calendar mcurrentDate = Calendar.getInstance();
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentDate.get(Calendar.MONTH);
        int year = mcurrentDate.get(Calendar.YEAR);
        int hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentDate.get(Calendar.MINUTE);
        String AM_PM ;
        if(hour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        if(hour==0 ){hour = 12;}
        else if(hour>12 ){hour = hour -12;}

        String min;
        if (minute < 10)
        {min = "0" + minute ;}
        else
        {min = String.valueOf(minute);}

        timeedittext.setText( hour + ":" + min +" "+ AM_PM);
        dateedittext.setText( month + "/" + day + "/" + year);
        BlipStartTime =( hour + ":" + min +" "+ AM_PM);
        BlipStartDate =year+"-"+month+"-"+day;



        addendtimeEnd.setOnClickListener(new View.OnClickListener() {// ADD END TIME BAR

            @Override
            public void onClick(View v) {
                addendtimeEnd.setVisibility(View.INVISIBLE);
                endtimebar.setVisibility(VISIBLE);

                Calendar mcurrentDate = Calendar.getInstance();
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);
                int hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentDate.get(Calendar.MINUTE);
                String AM_PM ;
                if(hour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                if(hour==0 ){hour = 12;}
                else if(hour>12 ){hour = hour -12;}

                String min;
                if (minute < 10)
                {min = "0" + minute ;}
                else
                {min = String.valueOf(minute);}
                timeedittextEnd.setText( hour+ ":" + min +" "+ AM_PM);
                dateedittextEnd.setText( month + "/" + day + "/" + year);
                BlipEndTime =( hour + ":" + min +" "+ AM_PM);
                BlipEndDate =year+"-"+month+"-"+day;


            }
        });

        removeendtimeEnd.setOnClickListener(new View.OnClickListener() {//REMOVE END TIME BAR

            @Override
            public void onClick(View v) {
                BlipEndDate=null;
                BlipEndTime=null;
                BlipEndDateTime=null;
                addendtimeEnd.setVisibility(VISIBLE);
                endtimebar.setVisibility(GONE);

            }
        });
        timeedittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


                        String AM_PM ;
                        if(selectedHour < 12) {
                           AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        if(selectedHour==0 ){selectedHour = 12;}
                        else if(selectedHour>12 ){selectedHour = selectedHour -12;}

                        String min;
                        if (selectedMinute < 10)
                        {min = "0" + selectedMinute ;}
                        else
                        {min = String.valueOf(selectedMinute);}
                        timeedittext.setText( selectedHour + ":" + min +" "+ AM_PM);

                        BlipStartTime =( selectedHour + ":" + min +" "+ AM_PM);



                    }
                }, hour, minute, false);

                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

       dateedittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentDate = Calendar.getInstance();
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);

               DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        BlipStartDate =year+"-"+month+"-"+day;

                       dateedittext.setText( month + "/" + day + "/" + year);
                    }

                }, year,month,day);

                mDatePicker.setTitle("Select Time");
                mDatePicker.show();

            }
        });

        timeedittextEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        Toast.makeText(MainActivity.this, "Time setted", Toast.LENGTH_SHORT).show();


                        String AM_PM ;
                        if(selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        if(selectedHour==0 ){selectedHour = 12;}
                        else if(selectedHour>12 ){selectedHour = selectedHour -12;}

                        String min;
                        if (selectedMinute < 10)
                        {min = "0" + selectedMinute ;}
                        else
                        {min = String.valueOf(selectedMinute);}


                        timeedittextEnd.setText( selectedHour + ":" + min +" "+ AM_PM);
                        BlipEndTime =( selectedHour + ":" + min +" "+ AM_PM);
                        //Check if both start date and  start time are entered AND also end date/time


                    }}, hour, minute, false);


                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        dateedittextEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentDate = Calendar.getInstance();
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Toast.makeText(MainActivity.this, "Time setted", Toast.LENGTH_SHORT).show();
                        BlipEndDate =year+"-"+month+"-"+day;
                        dateedittextEnd.setText( month + "/" + day + "/" + year);
                    }
                }, year,month,day);

                mDatePicker.setTitle("Select Time");
                mDatePicker.show();

            }
        });


        chooseImg.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
            @Override
            public void onClick(View v) {

                takePicture();
            }
        });


        RadioGroup radioGroup = mBlipAddView.findViewById(R.id.groupRadio);
       final TextView superprivatetext = mBlipAddView.findViewById(R.id.SuperPrivateText);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (publicradio.isChecked()) {
                    isSuperPrivate=false;
                    mySpinner.setAdapter(new MyCustomAdapterPublic(MainActivity.this, R.layout.row, CustomBlips));
                    allowedfriendsmultiline.setVisibility(GONE);
                    superprivatetext.setVisibility(GONE);

                } else if (privateradio.isChecked()) {
                    isSuperPrivate=false;
                    mySpinner.setAdapter(new MyCustomAdapterPrivate(MainActivity.this, R.layout.row, CustomBlips));//Change to Private Spinner
                    superprivatetext.setVisibility(GONE);

                }
                else if (SuperPrivateradio.isChecked()) {
                    mySpinner.setAdapter(new MyCustomAdapterPrivate(MainActivity.this, R.layout.row, CustomBlips));//Change to Private Spinner
                    isSuperPrivate=true;
                    allowedfriendsmultiline.setVisibility(VISIBLE);
                    superprivatetext.setVisibility(VISIBLE);
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

                Boolean TimeRangeIsValid= false;

                if(BlipEndDate !=null && BlipEndTime!=null){

                    try {
                        BlipStartDateTime = format.parse(BlipStartDate+" "+BlipStartTime);//Then parse
                        BlipEndDateTime = format.parse(BlipEndDate+" "+BlipEndTime);//Then parse


                        if(BlipStartDateTime.before(BlipEndDateTime)){
                            TimeRangeIsValid = true;
                        }
                        else{
                            TimeRangeIsValid = false;

                            Toast.makeText(MainActivity.this, "Invalid Range", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        Toast.makeText(MainActivity.this, "Parse Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

                else{
                   TimeRangeIsValid = true;
                }

                if(TimeRangeIsValid) {

                    BlipName = mBlipName.getText().toString();
                    if (publicradio.isChecked()) {

                        if (validateForm()) {

                            Details = mDetails.getText().toString();
                            String dropboxvalue = mySpinner.getSelectedItem().toString();


                                                       if (Objects.equals(dropboxvalue, "Arts")) {
                                blipIcon = "public_art";
                                Category="Arts";
                            } else if (Objects.equals(dropboxvalue, "Transportation")) {
                                blipIcon = "public_autoboatsair";
                                Category="Transportation";
                            } else if (Objects.equals(dropboxvalue, "Business")) {
                                blipIcon = "public_business";
                                Category="Business";
                            } else if (Objects.equals(dropboxvalue, "Community")) {
                                blipIcon = "public_community";
                                Category="Business";
                            } else if (Objects.equals(dropboxvalue, "Family & Education")) {
                                blipIcon = "public_family";
                                Category="Family & Education";
                            } else if (Objects.equals(dropboxvalue, "Fashion")) {
                                blipIcon = "public_fashion";
                                Category="Fashion";
                            } else if (Objects.equals(dropboxvalue, "Media")) {
                                blipIcon = "public_filmandmedia";
                                Category="Media";
                            } else if (Objects.equals(dropboxvalue, "Travel")) {
                                blipIcon = "public_travelandoutdoor";
                                Category="Travel";
                            } else if (Objects.equals(dropboxvalue, "Food")) {
                                blipIcon = "public_foodanddrinks";
                                Category="Food";
                            } else if (Objects.equals(dropboxvalue, "Health")) {
                                blipIcon = "public_health";
                                Category="Health";

                            } else if (Objects.equals(dropboxvalue, "Holiday")) {
                                blipIcon = "public_holidaysandcelebrations";
                                Category= "Holiday";
                            } else if (Objects.equals(dropboxvalue, "Music")) {
                                blipIcon = "public_music";
                                Category="Music";
                            } else if (Objects.equals(dropboxvalue, "Sports")) {
                                blipIcon = "public_sportsandfitness";
                                Category="Sports";
                            } else {
                                blipIcon = "ic_launcher_round";
                            }


                            final DatabaseReference addblippushref = Users.child(userName).child("Blips").push();// Add to user's blips
                            final String blipkey = addblippushref.getKey();


                            if (filePath != null) {

                                //uploading the image
                                for (String name : filePathMap.keySet()) {
                                    pd.show();
                                    Uri value = filePathMap.get(name);


                                    StorageReference childRefKey = storageRef.child("BlipPhotos").child(blipkey);
                                    uploadTask = childRefKey.child(filePath.getLastPathSegment()).putBytes(convertURItocompresseddata(value));

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            pd.dismiss();
                                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                            //get the download URL like this:
                                            @SuppressWarnings("VisibleForTests")
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            //and you can convert it to string like this:
                                            imageURLLink = downloadUrl.toString();


                                            Blips blips = new Blips(cursor_coordinate_latitude,
                                                    cursor_coordinate_longitude,
                                                    BlipName,
                                                    userName,
                                                    Details,
                                                    blipIcon,
                                                    getCurrentDateTime(),
                                                    BlipStartDate + " " + BlipStartTime,
                                                    BlipEndDate + " " + BlipEndTime,
                                                    imageURLLink,
                                                    null,
                                                    false,
                                                    "Public",
                                                    Category,
                                                    null);
                                            addblippushref.setValue(blips);
                                            Blipsref.child("public").child(blipkey).setValue(blips);//Add to private blips

                                            dialog.cancel();

                                            filePathMap.clear();
                                            filePath=null;


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(MainActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                            filePathMap.clear();
                                            filePath=null;
                                        }
                                    });


                                }
                            }
                            else {
                                Blips blips = new Blips(cursor_coordinate_latitude,
                                        cursor_coordinate_longitude,
                                        BlipName,
                                        userName,
                                        Details,
                                        blipIcon,
                                        getCurrentDateTime(),
                                        BlipStartDate + " " + BlipStartTime,
                                        BlipEndDate + " " + BlipEndTime,
                                        null,
                                        null,
                                        false,
                                        "Public",
                                        Category,
                                        null);
                                addblippushref.setValue(blips);
                                Blipsref.child("public").child(blipkey).setValue(blips);//Add to private blips

                                dialog.cancel();

                                filePathMap.clear();
                                filePath=null;
                            }
                        }



                    } else if (privateradio.isChecked() || SuperPrivateradio.isChecked()) {
                        if (validateForm()) {


                            Details = mDetails.getText().toString();
                            String dropboxvalue = mySpinner.getSelectedItem().toString();


                            if (Objects.equals(dropboxvalue, "Arts")) {
                                blipIcon = "private_art";
                                Category="Arts";
                            } else if (Objects.equals(dropboxvalue, "Transportation")) {
                                blipIcon = "private_autoboatsair";
                                Category="Transportation";
                            } else if (Objects.equals(dropboxvalue, "Business")) {
                                blipIcon = "private_business";
                                Category="Business";
                            } else if (Objects.equals(dropboxvalue, "Community")) {
                                blipIcon = "private_community";
                                Category="Business";
                            } else if (Objects.equals(dropboxvalue, "Family & Education")) {
                                blipIcon = "private_family";
                                Category="Family & Education";
                            } else if (Objects.equals(dropboxvalue, "Fashion")) {
                                blipIcon = "private_fashion";
                                Category="Fashion";
                            } else if (Objects.equals(dropboxvalue, "Media")) {
                                blipIcon = "private_filmandmedia";
                                Category="Media";
                            } else if (Objects.equals(dropboxvalue, "Travel")) {
                                blipIcon = "private_travelandoutdoor";
                                Category="Travel";
                            } else if (Objects.equals(dropboxvalue, "Food")) {
                                blipIcon = "private_foodanddrinks";
                                Category="Food";
                            } else if (Objects.equals(dropboxvalue, "Health")) {
                                blipIcon = "private_health";
                                Category="Health";
                            } else if (Objects.equals(dropboxvalue, "Holiday")) {
                                blipIcon = "private_holidaysandcelebrations";
                                Category= "Holiday";
                            } else if (Objects.equals(dropboxvalue, "Music")) {
                                blipIcon = "private_music";
                                Category="Music";
                            } else if (Objects.equals(dropboxvalue, "Sports")) {
                                blipIcon = "private_sportsandfitness";
                                Category="Sports";
                            } else {
                                blipIcon = "ic_launcher_round";
                            }


                            //Place Data
                            final DatabaseReference addblippushref = Users.child(userName).child("Blips").push();// Add to user's blips
                            final String blipkey = addblippushref.getKey();


                            if (filePath != null) {

                                //uploading the image
                                for (String name : filePathMap.keySet()) {
                                    pd.show();
                                    Uri value = filePathMap.get(name);


                                    StorageReference childRefKey = storageRef.child("BlipPhotos").child(blipkey);

                                    uploadTask = childRefKey.child(filePath.getLastPathSegment()).putBytes(convertURItocompresseddata(value));
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            pd.dismiss();
                                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                            //get the download URL like this:
                                            @SuppressWarnings("VisibleForTests")
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            //and you can convert it to string like this:
                                            imageURLLink = downloadUrl.toString();
                                            blips = new Blips(cursor_coordinate_latitude,
                                                    cursor_coordinate_longitude,
                                                    BlipName,
                                                    userName,
                                                    Details,
                                                    blipIcon,
                                                    getCurrentDateTime(),
                                                    BlipStartDate + " " + BlipStartTime,
                                                    BlipEndDate + " " + BlipEndTime,
                                                    imageURLLink,
                                                    allowedfriendsmultiline.getText().toString(),
                                                    isSuperPrivate,
                                                    "Private",
                                                    Category,
                                                    null);

                                            addblippushref.setValue(blips);
                                            Blipsref.child("private").child(blipkey).setValue(blips);//Add to private blips

                                            dialog.cancel();

                                            filePathMap.clear();
                                            filePath=null;


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(MainActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                            filePathMap.clear();
                                            filePath=null;

                                        }
                                    });


                                }
                            }
                            else {
                                blips = new Blips(cursor_coordinate_latitude,
                                        cursor_coordinate_longitude,
                                        BlipName,
                                        userName,
                                        Details,
                                        blipIcon,
                                        getCurrentDateTime(),
                                        BlipStartDate + " " + BlipStartTime,
                                        BlipEndDate + " " + BlipEndTime,
                                        null,
                                        allowedfriendsmultiline.getText().toString(),
                                        isSuperPrivate,
                                        "Private",
                                        Category,
                                        null);

                                addblippushref.setValue(blips);
                                Blipsref.child("private").child(blipkey).setValue(blips);//Add to private blips

                                dialog.cancel();
                                filePathMap.clear();
                                filePath=null;

                            }
                        }
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

    public String getCurrentDateTime(){
        Calendar mcurrentDate = Calendar.getInstance();
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentDate.get(Calendar.MONTH);
        int year = mcurrentDate.get(Calendar.YEAR);
        int hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentDate.get(Calendar.MINUTE);
        String AM_PM ;
        if(hour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        if(hour==0 ){hour = 12;}
        else if(hour>12 ){hour = hour -12;}

        String min;
        if (minute < 10)
        {min = "0" + minute ;}
        else
        {min = String.valueOf(minute);}
        String  dateCreated= ( year + "-" + month + "-" + day)+" "+( hour + ":" + min +" "+ AM_PM);
        return dateCreated;

    }

    public byte[] convertURItocompresseddata(Uri value){

        Scanner scanner = new Scanner();
        Bitmap bitmap = null;
        try {
            bitmap = scanner.decodeBitmapUri(MainActivity.this, value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    public class Scanner {

        public Bitmap decodeBitmapUri(MainActivity ctx, Uri uri) throws FileNotFoundException {
            int targetW = 1000;
            int targetH = 1000;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;


            return BitmapFactory.decodeStream(ctx.getContentResolver()
                    .openInputStream(uri), null, bmOptions);
        }
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoURI);
        this.sendBroadcast(mediaScanIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    launchMediaScanIntent();
                    try {
                        Scanner scanner = new Scanner();
                        Bitmap bitmap = scanner.decodeBitmapUri(MainActivity.this, photoURI);
                        imgView.setImageBitmap(bitmap);

                        filePath = photoURI;
                        filePathMap.put(filePath.getLastPathSegment(),filePath);



                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                                .show();

                    }

                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imgView.setImageURI(selectedImage);

                    filePath = data.getData();
                    filePathMap.put(filePath.getLastPathSegment(),filePath);

                }
                break;
        }



    }

    public void DeleteBlip(final Marker marker) {
        final LatLng coordinatetobedeleted = marker.getPosition();
        selected.remove();


        BlipsPublic.orderByChild("latitude").equalTo(coordinatetobedeleted.latitude).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot datacollected : dataSnapshot.getChildren()) {

                            String creator = datacollected.child("Creator").getValue(String.class);

                            //We add this because firebase queries sucks
                            if (datacollected.child("longitude").getValue(Double.class) == coordinatetobedeleted.longitude && creator.toLowerCase().contains(userName.toLowerCase())) {
                                datacollected.getRef().removeValue();
                                Toast.makeText(MainActivity.this, "Blip Deleted", Toast.LENGTH_SHORT).show();
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


                        for (DataSnapshot datacollected : dataSnapshot.getChildren()) {
                            String creator = datacollected.child("Creator").getValue(String.class);

                            if (datacollected.child("longitude").getValue(Double.class) == coordinatetobedeleted.longitude && creator.toLowerCase().contains(userName.toLowerCase())) {

                                datacollected.getRef().removeValue();

                                Toast.makeText(MainActivity.this, "Blip Deleted", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

    public void EditBlip(final Marker marker) {

        final LatLng coordinatetobeupdated = marker.getPosition();

        cursor_coordinate = new LatLng(coordinatetobeupdated.latitude, coordinatetobeupdated.longitude);// Set current click location to marker
        cursor_coordinate_latitude = coordinatetobeupdated.latitude;
        cursor_coordinate_longitude = coordinatetobeupdated.longitude;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mBlipAddView = getLayoutInflater().inflate(R.layout.add_blip_details_popup, null);

        mBlipName = mBlipAddView.findViewById(R.id.blipnameEt);
        mDetails = mBlipAddView.findViewById(R.id.detailsEt);
        Button mAddBlip = mBlipAddView.findViewById(R.id.addblip_button);
        Button mCancelBlip = mBlipAddView.findViewById(R.id.cancelblip_button);
        publicradio = mBlipAddView.findViewById(R.id.publicRadio);
        privateradio = mBlipAddView.findViewById(R.id.privateRadio);
        mySpinner = mBlipAddView.findViewById(R.id.iconsSpinner);
        TextView x = mBlipAddView.findViewById(R.id.blipdetailstextview);
        x.setText("Edit Blip Details");

        Button chooseImg =  mBlipAddView.findViewById(R.id.chooseImg);
        Button takePhoto =  mBlipAddView.findViewById(R.id.takePhoto);
        imgView = mBlipAddView.findViewById(R.id.imgView);


        final TextView timeedittext = mBlipAddView.findViewById(R.id.timeTextView);
        final TextView dateedittext = mBlipAddView.findViewById(R.id.dateTextView);
        final TextView timeedittextEnd = mBlipAddView.findViewById(R.id.timeTextViewend);
        final TextView dateedittextEnd = mBlipAddView.findViewById(R.id.dateTextViewend);
        final TextView addendtimeEnd = mBlipAddView.findViewById(R.id.addendtimebutton);
        final TextView removeendtimeEnd = mBlipAddView.findViewById(R.id.removeendtimebutton);
        final LinearLayout endtimebar = mBlipAddView.findViewById(R.id.endtimebar);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");


        addendtimeEnd.setOnClickListener(new View.OnClickListener() {// ADD END TIME BAR

            @Override
            public void onClick(View v) {
                addendtimeEnd.setVisibility(GONE);
                endtimebar.setVisibility(VISIBLE);

                Calendar mcurrentDate = Calendar.getInstance();
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);
                int hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentDate.get(Calendar.MINUTE);
                String AM_PM ;
                if(hour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                if(hour==0 ){hour = 12;}
                else if(hour>12 ){hour = hour -12;}

                String min;
                if (minute < 10)
                {min = "0" + minute ;}
                else
                {min = String.valueOf(minute);}
                timeedittextEnd.setText( hour+1 + ":" + min +" "+ AM_PM);
                dateedittextEnd.setText( month + "/" + day + "/" + year);
                BlipEndTime =( hour + ":" + min +" "+ AM_PM);
                BlipEndDate =year+"-"+month+"-"+day;


            }
        });

        removeendtimeEnd.setOnClickListener(new View.OnClickListener() {//REMOVE END TIME BAR

            @Override
            public void onClick(View v) {
                BlipEndDate=null;
                BlipEndTime=null;
                BlipEndDateTime=null;
                addendtimeEnd.setVisibility(VISIBLE);
                endtimebar.setVisibility(GONE);

            }
        });
        timeedittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


                        String AM_PM ;
                        if(selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        if(selectedHour==0 ){selectedHour = 12;}
                        else if(selectedHour>12 ){selectedHour = selectedHour -12;}

                        String min;
                        if (selectedMinute < 10)
                        {min = "0" + selectedMinute ;}
                        else
                        {min = String.valueOf(selectedMinute);}
                        timeedittext.setText( selectedHour + ":" + min +" "+ AM_PM);

                        BlipStartTime =( selectedHour + ":" + min +" "+ AM_PM);



                    }
                }, hour, minute, false);

                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        dateedittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentDate = Calendar.getInstance();
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        BlipStartDate =year+"-"+month+"-"+day;
                        Toast.makeText(MainActivity.this, BlipStartDate, Toast.LENGTH_SHORT).show();
                        dateedittext.setText( month + "/" + day + "/" + year);
                    }

                }, year,month,day);

                mDatePicker.setTitle("Select Time");
                mDatePicker.show();

            }
        });

        timeedittextEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        Toast.makeText(MainActivity.this, "Time setted", Toast.LENGTH_SHORT).show();


                        String AM_PM ;
                        if(selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        if(selectedHour==0 ){selectedHour = 12;}
                        else if(selectedHour>12 ){selectedHour = selectedHour -12;}

                        String min;
                        if (selectedMinute < 10)
                        {min = "0" + selectedMinute ;}
                        else
                        {min = String.valueOf(selectedMinute);}


                        timeedittextEnd.setText( selectedHour + ":" + min +" "+ AM_PM);
                        BlipEndTime =( selectedHour + ":" + min +" "+ AM_PM);
                        //Check if both start date and  start time are entered AND also end date/time





                    }}, hour, minute, false);


                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        dateedittextEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentDate = Calendar.getInstance();
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Toast.makeText(MainActivity.this, "Time setted", Toast.LENGTH_SHORT).show();
                        BlipEndDate =year+"-"+month+"-"+day;
                        dateedittextEnd.setText( month + "/" + day + "/" + year);
                    }
                }, year,month,day);

                mDatePicker.setTitle("Select Time");
                mDatePicker.show();

            }
        });


        chooseImg.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
            @Override
            public void onClick(View v) {

                takePicture();
            }
        });

        RadioGroup radioGroup = mBlipAddView.findViewById(R.id.groupRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (publicradio.isChecked()) {
                    privateradio.setChecked(false);
                    mySpinner.setAdapter(new MyCustomAdapterPublic(MainActivity.this, R.layout.row, CustomBlips));//Change to Public Spinner


                } else if (privateradio.isChecked()) {
                    publicradio.setChecked(false);
                    mySpinner.setAdapter(new MyCustomAdapterPrivate(MainActivity.this, R.layout.row, CustomBlips));//Change to Private Spinner

                }

                // checkedId is the RadioButton selected
            }
        });
///////////////////////////////////Retrieve Data to TextBoxes
        BlipsPublic.orderByChild("latitude").equalTo(coordinatetobeupdated.latitude).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot datacollected : dataSnapshot.getChildren()) {

                            String creator = datacollected.child("Creator").getValue(String.class);

                            //We add this because firebase queries sucks
                            if (datacollected.child("longitude").getValue(Double.class) == coordinatetobeupdated.longitude && creator.toLowerCase().contains(userName.toLowerCase())) {
                               String refKey =   datacollected.getRef().toString();


                                String BlipName =  datacollected.child("BlipName").getValue(String.class);
                                String Details = datacollected.child("Details").getValue(String.class);


                                publicradio.setChecked(true);
                                mBlipName.setText(BlipName);
                                mDetails.setText(Details);



                            }


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });


        BlipsPrivate.orderByChild("latitude").equalTo(coordinatetobeupdated.latitude).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot datacollected : dataSnapshot.getChildren()) {
                            String creator = datacollected.child("Creator").getValue(String.class);

                            if (datacollected.child("longitude").getValue(Double.class) == coordinatetobeupdated.longitude && creator.toLowerCase().contains(userName.toLowerCase())) {

                                String BlipName =  datacollected.child("BlipName").getValue(String.class);
                                String Details = datacollected.child("Details").getValue(String.class);

                                privateradio.setChecked(true);
                                mBlipName.setText(BlipName);
                                mDetails.setText(Details);

                            }


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });


////////////////////////////////////////////////////////////////////

        mBuilder.setView(mBlipAddView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAddBlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteBlip(marker);
                Blips blipsedit;
                BlipName = mBlipName.getText().toString();

                if (publicradio.isChecked()) {

                    if (validateForm()) {

                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        if (Objects.equals(dropboxvalue, "Arts")) {
                            blipIcon = "public_art";
                            Category="Arts";
                        } else if (Objects.equals(dropboxvalue, "Transportation")) {
                            blipIcon = "public_autoboatsair";
                            Category="Transportation";
                        } else if (Objects.equals(dropboxvalue, "Business")) {
                            blipIcon = "public_business";
                            Category="Business";
                        } else if (Objects.equals(dropboxvalue, "Community")) {
                            blipIcon = "public_community";
                            Category="Business";
                        } else if (Objects.equals(dropboxvalue, "Family & Education")) {
                            blipIcon = "public_family";
                            Category="Family & Education";
                        } else if (Objects.equals(dropboxvalue, "Fashion")) {
                            blipIcon = "public_fashion";
                            Category="Fashion";
                        } else if (Objects.equals(dropboxvalue, "Media")) {
                            blipIcon = "public_filmandmedia";
                            Category="Media";
                        } else if (Objects.equals(dropboxvalue, "Travel")) {
                            blipIcon = "public_travelandoutdoor";
                            Category="Travel";
                        } else if (Objects.equals(dropboxvalue, "Food")) {
                            blipIcon = "public_foodanddrinks";
                            Category="Food";
                        } else if (Objects.equals(dropboxvalue, "Health")) {
                            blipIcon = "public_health";
                            Category="Health";

                        } else if (Objects.equals(dropboxvalue, "Holiday")) {
                            blipIcon = "public_holidaysandcelebrations";
                            Category= "Holiday";
                        } else if (Objects.equals(dropboxvalue, "Music")) {
                            blipIcon = "public_music";
                            Category="Music";
                        } else if (Objects.equals(dropboxvalue, "Sports")) {
                            blipIcon = "public_sportsandfitness";
                            Category="Sports";
                        } else {
                            blipIcon = "ic_launcher_round";
                        }


                        //Place Data

                        final DatabaseReference addblippushref = Users.child(userName).child("Blips").push();// Add to user's blips
                        final String blipkey = addblippushref.getKey();


                        if (filePath != null) {

                            //uploading the image
                            for (String name : filePathMap.keySet()) {
                                pd.show();
                                Uri value = filePathMap.get(name);


                                StorageReference childRefKey = storageRef.child("BlipPhotos").child(blipkey);
                                uploadTask = childRefKey.child(filePath.getLastPathSegment()).putBytes(convertURItocompresseddata(value));

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                        //get the download URL like this:
                                        @SuppressWarnings("VisibleForTests")
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        //and you can convert it to string like this:
                                        imageURLLink = downloadUrl.toString();


                                        Blips blips = new Blips(cursor_coordinate_latitude,
                                                cursor_coordinate_longitude,
                                                BlipName,
                                                userName,
                                                Details,
                                                blipIcon,
                                                getCurrentDateTime(),
                                                BlipStartDate + " " + BlipStartTime,
                                                BlipEndDate + " " + BlipEndTime,
                                                imageURLLink,
                                                null,
                                                false,
                                                "Public",
                                                Category,
                                                null);
                                        addblippushref.setValue(blips);
                                        Blipsref.child("public").child(blipkey).setValue(blips);//Add to private blips

                                        dialog.cancel();
                                        filePathMap.clear();
                                        filePath=null;
                                        imageURLLink=null;



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                        filePathMap.clear();
                                        filePath=null;
                                        imageURLLink=null;


                                    }
                                });


                            }
                        }
                        else {
                            Blips blips = new Blips(cursor_coordinate_latitude,
                                    cursor_coordinate_longitude,
                                    BlipName,
                                    userName,
                                    Details,
                                    blipIcon,
                                    getCurrentDateTime(),
                                    BlipStartDate + " " + BlipStartTime,
                                    BlipEndDate + " " + BlipEndTime,
                                    null,
                                    null,
                                    false,
                                    "Public",
                                    Category,
                                    null);
                            addblippushref.setValue(blips);
                            Blipsref.child("public").child(blipkey).setValue(blips);//Add to private blips

                            dialog.cancel();
                            filePathMap.clear();
                            filePath=null;
                            imageURLLink=null;
                        }
                    }
                } else if (privateradio.isChecked()) {
                    if (validateForm()) {


                        Details = mDetails.getText().toString();
                        String dropboxvalue = mySpinner.getSelectedItem().toString();


                        if (Objects.equals(dropboxvalue, "Arts")) {
                            blipIcon = "private_art";
                            Category="Arts";
                        } else if (Objects.equals(dropboxvalue, "Transportation")) {
                            blipIcon = "private_autoboatsair";
                            Category="Transportation";
                        } else if (Objects.equals(dropboxvalue, "Business")) {
                            blipIcon = "private_business";
                            Category="Business";
                        } else if (Objects.equals(dropboxvalue, "Community")) {
                            blipIcon = "private_community";
                            Category="Business";
                        } else if (Objects.equals(dropboxvalue, "Family & Education")) {
                            blipIcon = "private_family";
                            Category="Family & Education";
                        } else if (Objects.equals(dropboxvalue, "Fashion")) {
                            blipIcon = "private_fashion";
                            Category="Fashion";
                        } else if (Objects.equals(dropboxvalue, "Media")) {
                            blipIcon = "private_filmandmedia";
                            Category="Media";
                        } else if (Objects.equals(dropboxvalue, "Travel")) {
                            blipIcon = "private_travelandoutdoor";
                            Category="Travel";
                        } else if (Objects.equals(dropboxvalue, "Food")) {
                            blipIcon = "private_foodanddrinks";
                            Category="Food";
                        } else if (Objects.equals(dropboxvalue, "Health")) {
                            blipIcon = "private_health";
                            Category="Health";
                        } else if (Objects.equals(dropboxvalue, "Holiday")) {
                            blipIcon = "private_holidaysandcelebrations";
                            Category= "Holiday";
                        } else if (Objects.equals(dropboxvalue, "Music")) {
                            blipIcon = "private_music";
                            Category="Music";
                        } else if (Objects.equals(dropboxvalue, "Sports")) {
                            blipIcon = "private_sportsandfitness";
                            Category="Sports";
                        } else {
                            blipIcon = "ic_launcher_round";
                        }


                        //Place Data
                        final DatabaseReference addblippushref = Users.child(userName).child("Blips").push();// Add to user's blips
                        final String blipkey = addblippushref.getKey();

                        if (filePath != null) {

                            //uploading the image
                            for (String name : filePathMap.keySet()) {
                                pd.show();
                                Uri value = filePathMap.get(name);


                                StorageReference childRefKey = storageRef.child("BlipPhotos").child(blipkey);

                                uploadTask = childRefKey.child(filePath.getLastPathSegment()).putBytes(convertURItocompresseddata(value));
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                        //get the download URL like this:
                                        @SuppressWarnings("VisibleForTests")
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        //and you can convert it to string like this:
                                        imageURLLink = downloadUrl.toString();
                                        blips = new Blips(cursor_coordinate_latitude,
                                                cursor_coordinate_longitude,
                                                BlipName,
                                                userName,
                                                Details,
                                                blipIcon,
                                                getCurrentDateTime(),
                                                BlipStartDate + " " + BlipStartTime,
                                                BlipEndDate + " " + BlipEndTime,
                                                imageURLLink,
                                                null,
                                                isSuperPrivate,
                                                "Private",
                                                Category,
                                                null);

                                        addblippushref.setValue(blips);
                                        Blipsref.child("private").child(blipkey).setValue(blips);//Add to private blips

                                        dialog.cancel();
                                        filePathMap.clear();
                                        filePath=null;
                                        imageURLLink=null;


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }

                        else{

                            blips = new Blips(cursor_coordinate_latitude,
                                                            cursor_coordinate_longitude,
                                                            BlipName,
                                                            userName,
                                                            Details,
                                                            blipIcon,
                                                            getCurrentDateTime(),
                                                            BlipStartDate + " " + BlipStartTime,
                                                            BlipEndDate + " " + BlipEndTime,
                                                            null,
                                                            null,
                                                            isSuperPrivate,
                                                            "Private",
                                                            Category,
                                                             null);
                            addblippushref.setValue(blips);
                            Blipsref.child("private").child(blipkey).setValue(blips);//Add to private blips

                            dialog.cancel();
                            filePathMap.clear();
                            filePath=null;
                            imageURLLink=null;
                        }


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

    private void ShowMyBlips(DatabaseReference myref, final HashMap<String,Blips> markerlistselected) {

      myref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {


                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                String newBlipName = dataSnapshot.child("BlipName").getValue(String.class);
                String creator = dataSnapshot.child("Creator").getValue(String.class);
                String Details = dataSnapshot.child("Details").getValue(String.class);
                String blipIcon = dataSnapshot.child("Icon").getValue(String.class);
                String imgURL = dataSnapshot.child("imageURL").getValue(String.class);
                String DateCreated = dataSnapshot.child("DateCreated").getValue(String.class);
                String StartTime= dataSnapshot.child("StartTime").getValue(String.class);
                String EndTime = dataSnapshot.child("EndTime").getValue(String.class);
                String allowedfriends= dataSnapshot.child("allowedfriends").getValue(String.class);
                Boolean isSuperPrivate = dataSnapshot.child("isSuperPrivate").getValue(Boolean.class);
                String PublicPrivate = dataSnapshot.child("PublicPrivate").getValue(String.class);
                String Category= dataSnapshot.child("Category").getValue(String.class);

                Blips blipsadded = new Blips(latitude,
                        longitude,
                        newBlipName,
                        creator,
                        Details,
                        blipIcon,
                        DateCreated,
                        StartTime,
                        EndTime,
                        imgURL,
                        allowedfriends,
                        isSuperPrivate,
                        PublicPrivate,
                        Category,
                         null);




                if (publiccheckbox.isChecked() && blipIcon.toLowerCase().contains("public".toLowerCase())) {
                    categoryFilterPublicnoanimation(blipsadded);
                }
                try {
                    if(blipsadded.isSuperPrivate){//If it is super private

                        if (privatecheckbox.isChecked() &&
                                (blipsadded.Creator.toLowerCase().contains(userName.toLowerCase()) ||//If this blips is yours
                                        ( friendarraylist.contains(blipsadded.Creator) && blipsadded.allowedfriends.contains(userName) ) )      ) //Ot if you are part of allowed friends and is your firend
                        {

                            categoryFilterPrivatenoanimation(blipsadded);

                        }

                    }

                    else  if ((blipsadded.Creator.toLowerCase().contains(userName.toLowerCase()) || friendarraylist.contains(blipsadded.Creator))) {

                        categoryFilterPrivatenoanimation(blipsadded);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                markerkey++;
                markerlistselected.put(Integer.toString(markerkey), blipsadded);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


                if (!search.isIconified()) {
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                } else {



                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                if (!search.isIconified()) {
                    Toast.makeText(MainActivity.this, "Searchbox still focused", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng coordinateremove = new LatLng(latitude,longitude);
                    removemarkerfromhashmap(coordinateremove);
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

    private void ShowBlipsPublic() {

       BlipsPublic.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {


                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String newBlipName = dataSnapshot.child("BlipName").getValue(String.class);
                    String creator = dataSnapshot.child("Creator").getValue(String.class);
                    String Details = dataSnapshot.child("Details").getValue(String.class);
                    String blipIcon = dataSnapshot.child("Icon").getValue(String.class);
                    String imgURL = dataSnapshot.child("imageURL").getValue(String.class);
                    String DateCreated = dataSnapshot.child("DateCreated").getValue(String.class);
                    String StartTime= dataSnapshot.child("StartTime").getValue(String.class);
                    String EndTime = dataSnapshot.child("EndTime").getValue(String.class);
                    String allowedfriends= dataSnapshot.child("allowedfriends").getValue(String.class);
                    Boolean isSuperPrivate = dataSnapshot.child("isSuperPrivate").getValue(Boolean.class);
                String PublicPrivate = dataSnapshot.child("PublicPrivate").getValue(String.class);
                String Category= dataSnapshot.child("Category").getValue(String.class);

                Blips blipsadded = new Blips(latitude,
                        longitude,
                        newBlipName,
                        creator,
                        Details,
                        blipIcon,
                        DateCreated,
                        StartTime,
                        EndTime,
                        imgURL,
                        allowedfriends,
                        isSuperPrivate,
                        PublicPrivate,
                        Category,
                        null);



                    if (publiccheckbox.isChecked() && blipIcon.toLowerCase().contains("public".toLowerCase())) {
                        categoryFilterPublic(blipsadded);
                    }

                     markerkey++;
                     markerlist.put(Integer.toString(markerkey), blipsadded);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


                if (!search.isIconified()) {
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                } else {



                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                if (!search.isIconified()) {
                    Toast.makeText(MainActivity.this, "Searchbox still focused", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng coordinateremove = new LatLng(latitude,longitude);
                    removemarkerfromhashmap(coordinateremove);
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

    private void ShowBlipsPrivate() {
        BlipsPrivate.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String newBlipName = dataSnapshot.child("BlipName").getValue(String.class);
                    String creator = dataSnapshot.child("Creator").getValue(String.class);
                    String Details = dataSnapshot.child("Details").getValue(String.class);
                    String blipIcon = dataSnapshot.child("Icon").getValue(String.class);
                    String imgURL = dataSnapshot.child("imageURL").getValue(String.class);
                    String DateCreated = dataSnapshot.child("DateCreated").getValue(String.class);
                    String StartTime= dataSnapshot.child("StartTime").getValue(String.class);
                    String EndTime = dataSnapshot.child("EndTime").getValue(String.class);
                    String allowedfriends= dataSnapshot.child("allowedfriends").getValue(String.class);
                    Boolean isSuperPrivate = dataSnapshot.child("isSuperPrivate").getValue(Boolean.class);
                    String PublicPrivate = dataSnapshot.child("PublicPrivate").getValue(String.class);
                    String Category= dataSnapshot.child("Category").getValue(String.class);

                Blips blipsadded = new Blips(latitude,
                        longitude,
                        newBlipName,
                        creator,
                        Details,
                        blipIcon,
                        DateCreated,
                        StartTime,
                        EndTime,
                        imgURL,
                        allowedfriends,
                        isSuperPrivate,
                        PublicPrivate,
                        Category,
                        null);

                try {
                    if(blipsadded.isSuperPrivate){//If it is super private

                        if (privatecheckbox.isChecked() &&
                                (blipsadded.Creator.toLowerCase().contains(userName.toLowerCase()) ||//If this blips is yours
                                        ( friendarraylist.contains(blipsadded.Creator) && blipsadded.allowedfriends.contains(userName) ) )      ) //Ot if you are part of allowed friends and is your firend
                        {

                            categoryFilterPrivate(blipsadded);

                        }

                    }

                    else  if ((blipsadded.Creator.toLowerCase().contains(userName.toLowerCase()) || friendarraylist.contains(blipsadded.Creator))) {

                        categoryFilterPrivate(blipsadded);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                markerkey++;
                markerlist.put(Integer.toString(markerkey), blipsadded);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if (!search.isIconified()) {
                    Toast.makeText(MainActivity.this, "Searchbox still  focused", Toast.LENGTH_SHORT).show();
                } else {



                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                if (!search.isIconified()) {
                    Toast.makeText(MainActivity.this, "Searchbox still focused", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng coordinateremove = new LatLng(latitude,longitude);
                    removemarkerfromhashmap(coordinateremove);
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

    private void reload(){
        mMap.clear();
        // Iterate through hashmap
        if (showmyplacesmode==0) {

            for (Map.Entry<String, Blips> entry : markerlist.entrySet())
            {
                Blips value = markerlist.get(entry.getKey());

                try {
                    if(value.isSuperPrivate){//If it is super private

                        if (privatecheckbox.isChecked() &&
                                (value.Creator.toLowerCase().contains(userName.toLowerCase()) ||//If this blips is yours
                                        ( friendarraylist.contains(value.Creator) && value.allowedfriends.contains(userName) ) )      ) //Ot if you are part of allowed friends and is your firend
                        {

                            categoryFilterPrivatenoanimation(value);

                        }

                    }

                    else if (privatecheckbox.isChecked() && value.Icon.toLowerCase().contains("private".toLowerCase()) &&  ( friendarraylist.contains(value.Creator)
                            || value.Creator.toLowerCase().contains(userName.toLowerCase()) )   ) {

                        categoryFilterPrivatenoanimation(value);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (publiccheckbox.isChecked() && value.Icon.toLowerCase().contains("public".toLowerCase())) {
                    categoryFilterPublicnoanimation(value);
                }

            }
            ShowGPSLocation();
        }
        ///zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz
        else if (showmyplacesmode==1){

            for (Map.Entry<String, Blips> entry : mymarkerlist.entrySet())
            {
                Blips value = mymarkerlist.get(entry.getKey());


                if (privatecheckbox.isChecked() && value.PublicPrivate.toLowerCase().contains("private".toLowerCase())) {

                    categoryFilterPrivatenoanimation(value);
                }


                if (publiccheckbox.isChecked() && value.PublicPrivate.toLowerCase().contains("public".toLowerCase())) {
                    categoryFilterPublicnoanimation(value);
                }


            }


        }
        //zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz
        else if (showmyplacesmode==2){
            for (Map.Entry<String, Blips> entry : mymarkerlistattending.entrySet())
            {
                Blips value = mymarkerlistattending.get(entry.getKey());


                if (privatecheckbox.isChecked() && value.PublicPrivate.toLowerCase().contains("Private".toLowerCase())) {
                    categoryFilterPrivatenoanimation(value);
                }


                if (publiccheckbox.isChecked() && value.PublicPrivate.toLowerCase().contains("public".toLowerCase())) {
                    categoryFilterPublicnoanimation(value);
                }

            }


        }
        else if (showmyplacesmode==3){
            for (Map.Entry<String, Blips> entry : mymarkerlistplanning.entrySet())
            {
                Blips value = mymarkerlistplanning.get(entry.getKey());


                if (privatecheckbox.isChecked() && value.PublicPrivate.toLowerCase().contains("Private".toLowerCase())) {
                    categoryFilterPrivatenoanimation(value);
                }


                if (publiccheckbox.isChecked() && value.PublicPrivate.toLowerCase().contains("public".toLowerCase())) {
                    categoryFilterPublicnoanimation(value);
                }

            }


        }

    }

    private void removemarkerfromhashmap(LatLng delete){

        try {
            // Iterate through hashmap
            for (Map.Entry<String, Blips> entry : markerlist.entrySet())
            {
                Blips value = markerlist.get(entry.getKey());
                LatLng coordinatedeleted = new LatLng( value.latitude,value.longitude);

                if(Objects.equals(delete, coordinatedeleted)){
                    markerlist.remove(entry.getKey());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // Iterate through hashmap
            for (Map.Entry<String, Marker> entry : markerlist2.entrySet())
            {
                Marker value = markerlist2.get(entry.getKey());

                LatLng coordinatedeleted = new LatLng( value.getPosition().latitude,value.getPosition().longitude);

                if(Objects.equals(delete, coordinatedeleted)){
                    markerlist2.remove(entry.getKey());
                    value.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void categoryFilterPrivate(Blips blipsadded) {

        if (checkboxArts.isChecked() && blipsadded.Icon.toLowerCase().contains("art".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxBusiness.isChecked() && blipsadded.Icon.toLowerCase().contains("business".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxCommunity.isChecked() && blipsadded.Icon.toLowerCase().contains("community".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxFamily.isChecked() && blipsadded.Icon.toLowerCase().contains("family".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxFashion.isChecked() && blipsadded.Icon.toLowerCase().contains("fashion".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxFood.isChecked() && blipsadded.Icon.toLowerCase().contains("food".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxHealth.isChecked() && blipsadded.Icon.toLowerCase().contains("health".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxHoliday.isChecked() && blipsadded.Icon.toLowerCase().contains("holiday".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxMedia.isChecked() && blipsadded.Icon.toLowerCase().contains("media".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxTransportation.isChecked() && blipsadded.Icon.toLowerCase().contains("auto".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxTravel.isChecked() && blipsadded.Icon.toLowerCase().contains("travel".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxSports.isChecked() && blipsadded.Icon.toLowerCase().contains("sports".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxMusic.isChecked() && blipsadded.Icon.toLowerCase().contains("music".toLowerCase())) {
            PlaceMarker(blipsadded);
        }

    }

    public void categoryFilterPublic(Blips blipsadded) {
        if (checkboxArts.isChecked() && blipsadded.Icon.toLowerCase().contains("art".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxBusiness.isChecked() && blipsadded.Icon.toLowerCase().contains("business".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxCommunity.isChecked() && blipsadded.Icon.toLowerCase().contains("community".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxFamily.isChecked() && blipsadded.Icon.toLowerCase().contains("family".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxFashion.isChecked() && blipsadded.Icon.toLowerCase().contains("fashion".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxFood.isChecked() && blipsadded.Icon.toLowerCase().contains("food".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxHealth.isChecked() && blipsadded.Icon.toLowerCase().contains("health".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxHoliday.isChecked() && blipsadded.Icon.toLowerCase().contains("holiday".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxMedia.isChecked() && blipsadded.Icon.toLowerCase().contains("media".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxTransportation.isChecked() && blipsadded.Icon.toLowerCase().contains("auto".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxTravel.isChecked() && blipsadded.Icon.toLowerCase().contains("travel".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxSports.isChecked() && blipsadded.Icon.toLowerCase().contains("sports".toLowerCase())) {
            PlaceMarker(blipsadded);
        }
        if (checkboxMusic.isChecked() && blipsadded.Icon.toLowerCase().contains("music".toLowerCase())) {
            PlaceMarker(blipsadded);
        }


    }

    public void categoryFilterPrivatenoanimation(Blips blipsadded) {

        if (checkboxArts.isChecked() && blipsadded.Icon.toLowerCase().contains("art".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxBusiness.isChecked() && blipsadded.Icon.toLowerCase().contains("business".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxCommunity.isChecked() && blipsadded.Icon.toLowerCase().contains("community".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxFamily.isChecked() && blipsadded.Icon.toLowerCase().contains("family".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxFashion.isChecked() && blipsadded.Icon.toLowerCase().contains("fashion".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxFood.isChecked() && blipsadded.Icon.toLowerCase().contains("food".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxHealth.isChecked() && blipsadded.Icon.toLowerCase().contains("health".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxHoliday.isChecked() && blipsadded.Icon.toLowerCase().contains("holiday".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxMedia.isChecked() && blipsadded.Icon.toLowerCase().contains("media".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxTransportation.isChecked() && blipsadded.Icon.toLowerCase().contains("auto".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxTravel.isChecked() && blipsadded.Icon.toLowerCase().contains("travel".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxSports.isChecked() && blipsadded.Icon.toLowerCase().contains("sports".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxMusic.isChecked() && blipsadded.Icon.toLowerCase().contains("music".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }

    }

    public void categoryFilterPublicnoanimation(Blips blipsadded) {
        if (checkboxArts.isChecked() && blipsadded.Icon.toLowerCase().contains("art".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxBusiness.isChecked() && blipsadded.Icon.toLowerCase().contains("business".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxCommunity.isChecked() && blipsadded.Icon.toLowerCase().contains("community".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxFamily.isChecked() && blipsadded.Icon.toLowerCase().contains("family".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxFashion.isChecked() && blipsadded.Icon.toLowerCase().contains("fashion".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxFood.isChecked() && blipsadded.Icon.toLowerCase().contains("food".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxHealth.isChecked() && blipsadded.Icon.toLowerCase().contains("health".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxHoliday.isChecked() && blipsadded.Icon.toLowerCase().contains("holiday".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxMedia.isChecked() && blipsadded.Icon.toLowerCase().contains("media".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxTransportation.isChecked() && blipsadded.Icon.toLowerCase().contains("auto".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxTravel.isChecked() && blipsadded.Icon.toLowerCase().contains("travel".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxSports.isChecked() && blipsadded.Icon.toLowerCase().contains("sports".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }
        if (checkboxMusic.isChecked() && blipsadded.Icon.toLowerCase().contains("music".toLowerCase())) {
            PlaceMarkernoanimation(blipsadded);
        }


    }

    private void getFriendsList() {

        UsersEmailFriends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                 //Split String
               friendname = dataSnapshot.getKey().toString();

                if(!friendarraylist.contains( friendname)){

                    friendarraylist.add( friendname);

                    DatabaseReference UsersxEmailxprofilepic = database.getReference("users").child(friendname).child("profilepic");

                    UsersxEmailxprofilepic.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get Download link for profile pic
                            String profilepiclink =   dataSnapshot.child("MyProfilePic").getValue(String.class);
                            friendprofilepicarraylist.add(profilepiclink);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getFriendsList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

    private boolean validateAddFriend() {
        boolean valid = true;


        if (TextUtils.isEmpty(friendrequestemail) || friendrequestemail.length() < 7) {
            friendemailEt.setError("Required");
            valid = false;
        } else {
            friendemailEt.setError(null);
        }


        return valid;

    }

    private static String removecom(String str) {
        if (str == null) {
            return null;
        } else {
            return str.substring(0, str.length() - 4);
        }

    }

    private static String ReplacePeriodiWithComma(String str) {


        if (str == null) {
            return null;
        } else {
            return    str.replace(".", ",");
        }

    }

    public void blipsupdateontextchange(final String query) {
        mMap.clear();
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        // Iterate through hashmap
        for (Map.Entry<String, Blips> entry : markerlist.entrySet())
        {
            Blips value = markerlist.get(entry.getKey());
            if (value.BlipName.toUpperCase().startsWith(query.toUpperCase() )  ) {


                try {
                    if(value.isSuperPrivate){//If it is super private

                        if (privatecheckbox.isChecked() &&
                                (value.Creator.toLowerCase().contains(userName.toLowerCase()) ||//If this blips is yours
                                        ( friendarraylist.contains(value.Creator) && value.allowedfriends.contains(userName) ) )      ) //Ot if you are part of allowed friends and is your firend
                        {

                            categoryFilterPrivatenoanimation(value);

                        }

                    }

                    else if (privatecheckbox.isChecked() && value.Icon.toLowerCase().contains("private".toLowerCase()) &&  ( friendarraylist.contains(value.Creator)
                            || value.Creator.toLowerCase().contains(userName.toLowerCase()) )   ) {

                        categoryFilterPrivatenoanimation(value);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (publiccheckbox.isChecked() && value.Icon.toLowerCase().contains("public".toLowerCase())) {
                    categoryFilterPublicnoanimation(value);
                }

            }


        }
        ShowGPSLocation();


    }

    public void checkboxlisteners() {
        publiccheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();



                //handle click
            }
        });

        privatecheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });

        checkboxArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });
        checkboxTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });

        checkboxSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });

        checkboxTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });
        checkboxMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });

        checkboxHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });

        checkboxHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });
        checkboxSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });

        checkboxFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();

                //handle click
            }
        });

        checkboxCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });
        checkboxBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();


                //handle click
            }
        });
        checkboxFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();

                //handle click
            }
        });
        checkboxMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();

                //handle click
            }
        });
        checkboxFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();

                //handle click
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDeviceLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        locationManager.removeUpdates(locationListener);
        locationListener = null;

        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            Toast.makeText(this, "Error: Permission not Granted", Toast.LENGTH_SHORT).show();
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
            liveGPSEmail.setValue(null);
            locationManager.removeUpdates(locationListener);
            locationListener = null;

            mAuth.signOut();
            MainActivity.this.finish();
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_top_right, menu);
        search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchmenuItem = menu.findItem(R.id.action_search);




        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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


        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                filterscroll.setVisibility(GONE);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                filterscroll.setVisibility(VISIBLE);
                return true;  // Return true to expand action view
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
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
        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_search) {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //TODO GET NOTIF Count then then display to nav view text


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addfriend) {
            SendFriendRequest();


        } else if (id == R.id.nav_notifications) {

            ShowFriendRequest();


        } else if (id == R.id.nav_friendlist) {
            //TODO Friend List
            ShowFriendList();

        } else if (id == R.id.nav_manage) {
            backtonormalview.setVisibility(VISIBLE);
            showmyplacesmode=1;
            backtonormalview.setText("Currently showing your blips \n Click here to back");
            mMap.clear();
            ShowMyBlips(UsersEmailBlips,mymarkerlist);

        } else if (id == R.id.nav_completed) {
            backtonormalview.setVisibility(VISIBLE);
            showmyplacesmode=2;
            backtonormalview.setText("Currently showing your attended blips \n Click here to back");
            mMap.clear();
            ShowMyBlips(UsersEmailBlipsAttended,mymarkerlistattending);


        } else if (id == R.id.nav_planned) {
            backtonormalview.setVisibility(VISIBLE);
            showmyplacesmode=3;
            backtonormalview.setText("Currently showing your planned blips \n Click here to back");
            mMap.clear();
            ShowMyBlips(UsersEmailBlipsPlanned,mymarkerlistplanning);


        } else if (id == R.id.nav_signout) {
            liveGPSEmail.child("longitude").setValue(null);
            liveGPSEmail.child("latitude").setValue(null);
            liveGPSEmail.child("Creator").setValue(null);
            liveGPSEmail.setValue(null);
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

    public Context getActivity() {

        return MainActivity.this;
    }

    private class MyCustomAdapterPublic extends ArrayAdapter<String> {

        MyCustomAdapterPublic(Context context, int textViewResourceId,
                              String[] objects) {
            super(context, textViewResourceId, objects);

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    @NonNull ViewGroup parent) {

            return getCustomView(position, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            return getCustomView(position, parent);
        }

        View getCustomView(int position, ViewGroup parent) {

//return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = row.findViewById(R.id.weekofday);
            label.setText(CustomBlips[position]);

            ImageView icon = row.findViewById(R.id.icon);


            if (Objects.equals(CustomBlips[position], "Arts")) {
                icon.setImageResource(R.mipmap.public_art);
            } else if (Objects.equals(CustomBlips[position], "Transportation")) {
                icon.setImageResource(R.mipmap.public_autoboatsair);
            } else if (Objects.equals(CustomBlips[position], "Business")) {
                icon.setImageResource(R.mipmap.public_business);
            } else if (Objects.equals(CustomBlips[position], "Community")) {
                icon.setImageResource(R.mipmap.public_community);
            } else if (Objects.equals(CustomBlips[position], "Family & Education")) {
                icon.setImageResource(R.mipmap.public_family);
            } else if (Objects.equals(CustomBlips[position], "Fashion")) {
                icon.setImageResource(R.mipmap.public_fashion);
            } else if (Objects.equals(CustomBlips[position], "Media")) {
                icon.setImageResource(R.mipmap.public_filmandmedia);
            } else if (Objects.equals(CustomBlips[position], "Food")) {
                icon.setImageResource(R.mipmap.public_foodanddrinks);
            } else if (Objects.equals(CustomBlips[position], "Health")) {
                icon.setImageResource(R.mipmap.public_health);
            } else if (Objects.equals(CustomBlips[position], "Holiday")) {
                icon.setImageResource(R.mipmap.public_holidaysandcelebrations);
            } else if (Objects.equals(CustomBlips[position], "Music")) {
                icon.setImageResource(R.mipmap.public_music);
            } else if (Objects.equals(CustomBlips[position], "Sports")) {
                icon.setImageResource(R.mipmap.public_sportsandfitness);
            } else if (Objects.equals(CustomBlips[position], "Travel")) {
                icon.setImageResource(R.mipmap.public_travelandoutdoor);
            } else {
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }

    private class MyCustomAdapterPrivate extends ArrayAdapter<String> {

        MyCustomAdapterPrivate(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    @NonNull ViewGroup parent) {

            return getCustomView(position, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            return getCustomView(position, parent);
        }

        View getCustomView(int position, ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = row.findViewById(R.id.weekofday);
            label.setText(CustomBlips[position]);

            ImageView icon = row.findViewById(R.id.icon);


            if (Objects.equals(CustomBlips[position], "Arts")) {
                icon.setImageResource(R.mipmap.private_art);
            } else if (Objects.equals(CustomBlips[position], "Transportation")) {
                icon.setImageResource(R.mipmap.private_autoboatsair);
            } else if (Objects.equals(CustomBlips[position], "Business")) {
                icon.setImageResource(R.mipmap.private_business);
            } else if (Objects.equals(CustomBlips[position], "Community")) {
                icon.setImageResource(R.mipmap.private_community);
            } else if (Objects.equals(CustomBlips[position], "Family & Education")) {
                icon.setImageResource(R.mipmap.private_family);
            } else if (Objects.equals(CustomBlips[position], "Fashion")) {
                icon.setImageResource(R.mipmap.private_fashion);
            } else if (Objects.equals(CustomBlips[position], "Media")) {
                icon.setImageResource(R.mipmap.private_filmandmedia);
            } else if (Objects.equals(CustomBlips[position], "Food")) {
                icon.setImageResource(R.mipmap.private_foodanddrinks);
            } else if (Objects.equals(CustomBlips[position], "Health")) {
                icon.setImageResource(R.mipmap.private_health);
            } else if (Objects.equals(CustomBlips[position], "Holiday")) {
                icon.setImageResource(R.mipmap.private_holidaysandcelebrations);
            } else if (Objects.equals(CustomBlips[position], "Music")) {
                icon.setImageResource(R.mipmap.private_music);
            } else if (Objects.equals(CustomBlips[position], "Sports")) {
                icon.setImageResource(R.mipmap.private_sportsandfitness);
            } else if (Objects.equals(CustomBlips[position], "Travel")) {
                icon.setImageResource(R.mipmap.private_travelandoutdoor);
            } else {
                icon.setImageResource(R.mipmap.ic_launcher_round);
            }

            return row;
        }
    }

    private class FriendRequestsAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private ArrayList<String> profilepicarraylist = new ArrayList<String>();
        private Context context;


        public FriendRequestsAdapter(ArrayList<String> list, ArrayList<String> profilepicarraylist, Context context) {
            this.list = list;
            this.profilepicarraylist = profilepicarraylist;
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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null && !friendrequestlist.isEmpty()) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.getfriendnotificationbuttons, null);


            }

            //Handle TextView and display string from your list
            final TextView listItemText = view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            ImageButton friendrequestviewprofile = view.findViewById(R.id.friendrequestviewprofile);
            try {

                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .error(R.mipmap.error)
                        .placeholder(R.mipmap.placeholderimage);
                Context context = getApplicationContext();

                Glide.with(context)
                        .load(profilepicarraylist.get(position))
                        .apply(options)
                        .into(friendrequestviewprofile);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Handle buttons and add onClickListeners
            Button deleteBtn = view.findViewById(R.id.delete_btn);
            Button addBtn = view.findViewById(R.id.add_btn);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//TODO ADD BUTTON
                    String emailtobeadded = list.get(position);
                    list.remove(position); //or some other task
                    DeleteFriendNotif(emailtobeadded);
                    Users.child(userName).child("Friends").child(emailtobeadded).setValue(1);// Add to user's blips
                    Users.child(emailtobeadded).child("Friends").child(userName).setValue(1);

                    notifyDataSetChanged();
                    item_notifications.setTitle("Friend Requests    " + list.size());

                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//TODO DELETE BUTTON
                    //do something

                    String emailtobedeleted = list.get(position);
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                    DeleteFriendNotif(emailtobedeleted);
                    item_notifications.setTitle("Friend Requests    " + list.size());



                }
            });


            return view;
        }


    }

    private void SendFriendRequest() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mFriendAddView = getLayoutInflater().inflate(R.layout.add_friend, null);

        friendemailEt = mFriendAddView.findViewById(R.id.add_friend_et);
        Button AddFriend = mFriendAddView.findViewById(R.id.add_friend_btn);
        Button CancelFriend = mFriendAddView.findViewById(R.id.cancel_friend_btn);
        mBuilder.setView(mFriendAddView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //Check if
        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendrequestemail = removecom(friendemailEt.getText().toString());
                if (validateAddFriend()) {

                    Users.addListenerForSingleValueEvent(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.hasChild(friendrequestemail)) {

                                if (dataSnapshot.child(friendrequestemail).child("FriendRequests").hasChild(userName)) {

                                    Toast.makeText(MainActivity.this, "Friend Request Already Sent", Toast.LENGTH_SHORT).show();


                                } else {
                                    Users.child(friendrequestemail).child("FriendRequests").child(userName).child(userName).setValue(1);// Add to user's blips
                                    Toast.makeText(MainActivity.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();


                                }


                            } else {
                                Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                friendemailEt.setText(null);


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
        });


    }

    public void ShowFriendRequestCount() {

        UsersEmailFriendRequests.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String y = dataSnapshot.getKey().toString();
                friendrequestlist.add(y);
                DatabaseReference UsersxEmailxprofilepic = database.getReference("users").child(y).child("profilepic");

                UsersxEmailxprofilepic.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get Download link for profile pic
                        String   profilepiclink =   dataSnapshot.child("MyProfilePic").getValue(String.class);
                        profilepicarraylist.add(profilepiclink);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                item_notifications.setTitle("Friend Requests    " + friendrequestlist.size());


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void ShowFriendRequest() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        View mShowFriendAddView = getLayoutInflater().inflate(R.layout.friendrequestlistview, null);
        lView = mShowFriendAddView.findViewById(R.id.notifListview);

        mBuilder.setView(mShowFriendAddView);
        AlertDialog dialogfriendrequest = mBuilder.create();

        dialogfriendrequest.show();
        FriendRequestsAdapter adapter = new FriendRequestsAdapter(friendrequestlist,profilepicarraylist, MainActivity.this);
        lView.setAdapter(adapter);

    }

    public void DeleteFriendNotif(String emailtobedeleted) {


        UsersEmailFriendRequests.orderByChild(emailtobedeleted).equalTo(1).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot datacollected : dataSnapshot.getChildren()) {

                            datacollected.getRef().removeValue();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });


    }

    private void addPulsatingEffect(final LatLng pulseLatlng) {
        if (lastPulseAnimator != null) {
            lastPulseAnimator.cancel();
            Log.d("onLocationUpdated: ", "cancelled");
        }
        if (lastUserCircle != null)
            lastUserCircle.setCenter(pulseLatlng);

        lastPulseAnimator = valueAnimate(pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (lastUserCircle != null)
                    lastUserCircle.setRadius((Float) animation.getAnimatedValue());
                else {
                    lastUserCircle = mMap.addCircle(new CircleOptions()
                            .center(pulseLatlng)
                            .radius((Float) animation.getAnimatedValue())
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.TRANSPARENT));
                }
            }
        });

    }

    protected ValueAnimator valueAnimate(long duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        Log.d("valueAnimate: ", "called");
        ValueAnimator va = ValueAnimator.ofFloat(0, 1000);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);

        va.start();
        return va;
    }

    public void locationListen() {

       locationListener = new android.location.LocationListener() {

            @Override
            public void onLocationChanged(Location location) {


               if(showGPSToggle.isChecked()){


                     Blips gpsblips = new Blips(location.getLatitude(),
                           location.getLongitude(),
                           userName,
                           userName,
                             null,
                             null,
                             null,
                             null,
                             null,
                             profpicdownloadlink,
                             null,
                             null,
                             null,
                             null,
                             null
                           );

                     liveGPS.child(userName).setValue(gpsblips);


               }
               else{

                   liveGPSEmail.setValue(null);
               }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

       locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }

    public void ShowGPSLocation(){
        liveGPS.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                    String creator = dataSnapshot.child("Creator").getValue(String.class);
                    final String imagedownloadlink = dataSnapshot.child("imageURL").getValue(String.class);
                    if(friendarraylist.contains(creator)) {

                           Bitmap mybitmap = null;
                        final LatLng x = new LatLng(  dataSnapshot.child("latitude").getValue(Double.class),  dataSnapshot.child("longitude").getValue(Double.class));


                            RequestOptions options = new RequestOptions()
                                    .circleCrop()
                                    .error(R.mipmap.error)
                                    .placeholder(R.mipmap.placeholderimage);
                             Context context = getApplicationContext();

                            Glide.with(context)
                                    .asBitmap()
                                    .load(imagedownloadlink)
                                    .apply(options)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                            gpsmarker = mMap.addMarker(new MarkerOptions()
                                                    .position(x)// Set Marker
                                                    .title(dataSnapshot.child("Creator").getValue(String.class))
                                                    .snippet(imagedownloadlink)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(resource, 50, 50, false))));
                                        }


                                    });





                        gpsmarkerkey++;
                        gpslist.put(Integer.toString(gpsmarkerkey),gpsmarker);
                    }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                try {
                    LatLng x = new LatLng(  dataSnapshot.child("latitude").getValue(Double.class),  dataSnapshot.child("longitude").getValue(Double.class));
                    updategpsmarkerfromhashmap(x,dataSnapshot.child("Creator").getValue(String.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                try {
                    LatLng x = new LatLng(  dataSnapshot.child("latitude").getValue(Double.class),  dataSnapshot.child("longitude").getValue(Double.class));
                   removegpsmarkerfromhashmap(x,dataSnapshot.child("Creator").getValue(String.class));
                } catch (NullPointerException e) {
                    e.printStackTrace();
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

    private void removegpsmarkerfromhashmap(LatLng delete,String creatorx){


        try {
            // Iterate through hashmap
            for (Map.Entry<String, Marker> entry : gpslist.entrySet())
            {
                Marker value = gpslist.get(entry.getKey());
                //Check if updated child is your friend
                //If friend then
                if(friendarraylist.contains(creatorx)) {
                    value.remove();//update the marker
                    gpslist.remove(entry.getKey()); //remove old data of friend in list using key
                    reload();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }





    }
    private void updategpsmarkerfromhashmap(LatLng updatedcoordinate, String creatorx){

        try {
            // Iterate through hashmap
            for (Map.Entry<String, Marker> entry : gpslist.entrySet())
            {
                Marker value = gpslist.get(entry.getKey());
                LatLng coordinateupdated = new LatLng( value.getPosition().latitude,value.getPosition().longitude);

                //Check if updated child is your friend

                    //If friend then
                    if(friendarraylist.contains(creatorx)) {
                       //update the marker
                        value.setPosition(updatedcoordinate);//update the marker
                        gpslist.put(entry.getKey(),value);//save new data of friend


                    }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void ShowFriendList() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        View mShowFriendAddView = getLayoutInflater().inflate(R.layout.friendlistlistview, null);
        lView = mShowFriendAddView.findViewById(R.id.notifListview);

        mBuilder.setView(mShowFriendAddView);
        AlertDialog dialogfriendlist = mBuilder.create();

        dialogfriendlist.show();
        FriendListAdapter adapter = new FriendListAdapter(friendarraylist, friendprofilepicarraylist, MainActivity.this);
        lView.setAdapter(adapter);

    }

    private class FriendListAdapter extends BaseAdapter implements ListAdapter {

        private ArrayList<String> names = new ArrayList<String>();
        private ArrayList<String> pictures = new ArrayList<String>();
        private Context context;


        public FriendListAdapter(ArrayList<String> friendnamesarraylist, ArrayList<String> friendprofilepicarraylist, Context context) {
            this.names = friendnamesarraylist;
            this.pictures = friendprofilepicarraylist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int pos) {
            return names.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view = convertView;

            if (view == null ) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.getfriendlistbuttons, null);


            }

            //Handle TextView and display string from your list
            Button deleteBtn = null;
            try {
                final TextView namestext = view.findViewById(R.id.list_item_string1);
                namestext.setText(names.get(position)+".com");

                ImageButton friendlistviewprofile = view.findViewById(R.id.friendlistviewprofile);

                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .error(R.mipmap.error)
                        .placeholder(R.mipmap.placeholderimage);
                Context context = getApplicationContext();

                Glide.with(context)
                        .load(pictures.get(position))
                        .apply(options)
                        .into(friendlistviewprofile);


                //Handle buttons and add onClickListeners
                deleteBtn = view.findViewById(R.id.delete_btn);
            } catch (Exception e) {
                e.printStackTrace();
            }


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//TODO DELETE BUTTON
                    //do something

                    String friendtobedeleted = names.get(position);
                    names.remove(position); //or some other task
                    notifyDataSetChanged();
                    DeleteFriend(friendtobedeleted);
                    Users.child(userName).child("Friends").child(friendtobedeleted).setValue(null);// Add to user's blips
                    Users.child(friendtobedeleted).child("Friends").child(userName).setValue(null);



                }
            });


            return view;
        }


    }

    public void DeleteFriend(String friendtobedeleted) {


        UsersEmailFriends.orderByChild(friendtobedeleted).equalTo(1).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot datacollected : dataSnapshot.getChildren()) {

                            datacollected.getRef().removeValue();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });


    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                } else {

                }
            }
        });
    }

    private void editprofpic(){
       View  editprofilepicview= getLayoutInflater().inflate(R.layout.editprofilepicview, null);
       Button chooseImg =  editprofilepicview.findViewById(R.id.chooseImg);
       Button takePhoto =  editprofilepicview.findViewById(R.id.takePhoto);
       imgView = editprofilepicview.findViewById(R.id.imgView);
       Button uploadprofpic = editprofilepicview.findViewById(R.id.uploadprofilepic);
       pd = new ProgressDialog(this);
       pd.setMessage("Uploading....");
       try {

           AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
           mBuilder.setView(editprofilepicview);
           final AlertDialog dialog = mBuilder.create();

           dialog.show();



           chooseImg.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
               @Override
               public void onClick(View v) {
                   Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

               }
           });

           takePhoto.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
               @Override
               public void onClick(View v) {
                   takePicture();
               }
           });


          uploadprofpic.setOnClickListener(new View.OnClickListener() {//Choose Image Button Click
               @Override
               public void onClick(View v) {

                   if(filePath != null) {

                       //uploading the image
                       for (String name : filePathMap.keySet()){
                           pd.show();
                           Uri value = filePathMap.get(name);

                           StorageReference childusername = storageRef.child("BlipPhotos").child(userName);
                           uploadTask = childusername.child(filePath.getLastPathSegment()).putBytes(convertURItocompresseddata(value));

                           uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   pd.dismiss();
                                   Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                   //get the download URL like this:
                                   @SuppressWarnings("VisibleForTests")
                                   Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                   //and you can convert it to string like this:
                                   imageURLLink = downloadUrl.toString();
                                   UsersEmailPic.child("MyProfilePic").setValue(imageURLLink);

                                   dialog.cancel();
                                   filePathMap.clear();


                                   RequestOptions options = new RequestOptions()
                                           .error(R.mipmap.error)
                                           .placeholder(R.mipmap.placeholderimage);

                                   Glide.with(getActivity())
                                           .load(imageURLLink)
                                           .apply(options)
                                           .into(profilepic);


                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   pd.dismiss();
                                   Toast.makeText(MainActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                               }
                           });


                       }
                   }
               }
           });





       } catch (NullPointerException e) {

       }

   }

   private void loadprofilepic(){
       UsersEmailPic.addValueEventListener(new ValueEventListener(){
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               profpicdownloadlink= dataSnapshot.child("MyProfilePic").getValue(String.class);


               RequestOptions options = new RequestOptions()
                       .circleCrop()
                       .error(R.mipmap.error)
                       .placeholder(R.mipmap.placeholderimage);
               Context context = getApplicationContext();

               Glide.with(context)
                       .load(profpicdownloadlink)
                       .apply(options)
                       .into(profilepic);

           }
           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }

    public String getAddress(double lat, double lng) {
        String add = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
           add = obj.getAddressLine(0);
          //  add = add + "\n" + obj.getCountryName();
          //  add = add + "\n" + obj.getCountryCode();
          //  add = add + "\n" + obj.getAdminArea();
          //  add = add + "\n" + obj.getPostalCode();
          //  add = add + "\n" + obj.getSubAdminArea();
          //  add = add + "\n" + obj.getLocality();
          //  add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }

    public String getkey(Marker marker){

    Double longitude= marker.getPosition().longitude;

        try {
            Blipsref.child("public").orderByChild("longitude")
                    .equalTo(longitude)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                key = childSnapshot.getKey();
                                Toast.makeText(MainActivity.this, "public", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }});

            Blipsref.child("private").orderByChild("longitude")
                    .equalTo(longitude)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                key = childSnapshot.getKey();

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }});
        } catch (Exception e) {

        }






    return key;
    }

}

