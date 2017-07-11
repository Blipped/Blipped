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

    public String key;


    public Blips() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Blips(Double latitude,Double longitude, String BlipName, String Creator, String Details,String Icon) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.BlipName = BlipName;
        this.Creator = Creator;
        this.Details = Details;
        this.Icon = Icon;
    }


    public String getKey( String key) {
        return key;
    }
}
