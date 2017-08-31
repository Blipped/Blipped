package blippedcompany.blipped;

import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

public class AsyncGeocoderObject {

    public LatLng location; // location to get address from
    Geocoder geocoder; // the geocoder
    String locationstring; // textview to update text

    public AsyncGeocoderObject(Geocoder geocoder, LatLng location, String locationstring) {
        this.geocoder = geocoder;
        this.location = location;
        this.locationstring = locationstring;
    }
}