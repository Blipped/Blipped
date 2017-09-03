package blippedcompany.blipped;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Marcius Jude on 9/3/2017.
 */
public class MyCustomAdapterPublic extends ArrayAdapter<String> {

    String[] CustomBlips = {"Arts",
            "Transportation",
            "Business",
            "Community",
            "Family & Education",
            "Fashion",
            "Media",
            "Food",
            "Health",
            "Holiday",
            "Music",
            "Sports",
            "Travel"};

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

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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