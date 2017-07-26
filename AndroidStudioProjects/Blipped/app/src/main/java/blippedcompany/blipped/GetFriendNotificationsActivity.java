package blippedcompany.blipped;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class GetFriendNotificationsActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userID = mAuth.getCurrentUser();
    String userName=removecom(userID.getEmail());
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersEmailFriends;
    ArrayList<String> friendrequestlist;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getfriendnotification_view);

        //generate list
        friendrequestlist = new ArrayList<String>();
        ShowFriendRequest();


    }


    public void ShowFriendRequest(){

        UsersEmailFriends = database.getReference("users").child(userName).child("FriendRequests");
        UsersEmailFriends.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                for (DataSnapshot snapm: dataSnapshot.getChildren()) {

                    email = snapm.getKey().toString();
                    friendrequestlist.add(email);


                }
               load(friendrequestlist);

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

    public void load(ArrayList<String> friendrequestlist){
        //instantiate custom adapter
        FriendRequestsAdapter adapter = new FriendRequestsAdapter(friendrequestlist, this);

        //handle listview and assign adapter
        ListView lView = (ListView) findViewById(R.id.notifListview);
        lView.setAdapter(adapter);
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
