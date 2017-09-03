package blippedcompany.blipped;

import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Marcius Jude on 9/1/2017.
 */
public class AsyncGeocoder extends AsyncTask<AsyncGeocoderObject, Void, List<Address>> {

    public String locationstring;

    @Override
    protected List<Address> doInBackground(AsyncGeocoderObject... asyncGeocoderObjects) {
        List<Address> addresses = null;
        AsyncGeocoderObject asyncGeocoderObject = asyncGeocoderObjects[0];
        locationstring = asyncGeocoderObject.locationstring;
        try {
            addresses = asyncGeocoderObject.geocoder.getFromLocation(asyncGeocoderObject.location.latitude,
                    asyncGeocoderObject.location.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        Log.v("onPostExecute", "location: " + addresses);
        String address;
        if (addresses != null)
            address = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
        else address = "Service unavailable.";

        locationstring=address;
    }
}