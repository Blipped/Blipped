package blippedcompany.blipped;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Marcius Jude on 7/10/2017.
 */
@IgnoreExtraProperties
 public class Blips {
    public LatLng coordinate;
    public Double latitude;
    public Double longitude;
    public String BlipName;
    public String Creator;
    public String Details;
    public String Icon;
    public String DateCreated;
    public String StartTime;
    public String EndTime;
    public String imageURL;
    public String allowedfriends;
    public Boolean isSuperPrivate;
    public String PublicPrivate;
    public String Category;


    public String key;
    //TODO get time and date

    public Blips() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Blips(Double latitude,
                 Double longitude,
                 String BlipName,
                 String Creator,
                 String Details,
                 String Icon,
                 String DateCreated,
                 String StartTime,
                 String EndTime,
                 String imageURL,
                 String allowedfriends,
                 Boolean isSuperPrivate,
                 String PublicPrivate,
                 String Category) {



        this.latitude = latitude;
        this.longitude = longitude;
        this.BlipName = BlipName;
        this.Creator = Creator;
        this.Details = Details;
        this.Icon = Icon;
        this.DateCreated= DateCreated;
        this.StartTime= StartTime;
        this.EndTime= EndTime;
        this.imageURL= imageURL;
        this.allowedfriends= allowedfriends;
        this.isSuperPrivate=isSuperPrivate;
        this.PublicPrivate=PublicPrivate;
        this.Category=Category;
    }


    public String getKey( String key) {
        return key;
    }
}
