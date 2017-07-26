package blippedcompany.blipped;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcius Jude on 7/17/2017.
 */

public class FriendRequestsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userID = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //initialize lat and lng values
    DatabaseReference Users = database.getReference("users");
    String userName=removecom(userID.getEmail());


    public FriendRequestsAdapter(ArrayList<String> list, Context context) {
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.getfriendnotificationbuttons, null);


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

                String emailtobedeleted =  list.get(position).toString();
                list.remove(position); //or some other task
                DeleteFriendNotif(emailtobedeleted);


            }
        });
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String emailtobeadded =  list.get(position).toString();
                list.remove(position); //or some other task
                DeleteFriendNotif(emailtobeadded);
                //TODO Add Friend
                Users.child(userName).child("Friends").push().child(emailtobeadded).setValue(1);// Add to user's blips


            }
        });

        return view;
    }

    public void DeleteFriendNotif(String emailtobedeleted){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userID = mAuth.getCurrentUser();
        String  userName=removecom(userID.getEmail());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference UsersEmailFriends = database.getReference("users").child(userName).child("FriendRequests");

        UsersEmailFriends.orderByChild("email").equalTo(emailtobedeleted).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot datacollected: dataSnapshot.getChildren()) {
                            Toast.makeText(context, "Declined", Toast.LENGTH_SHORT).show();
                            datacollected.getRef().removeValue();


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });




    }
    private static String removecom(String str) {
        if(str==null){
            return null;
        }
        else{
            return str.substring(0, str.length() - 4);
        }

    }






}

