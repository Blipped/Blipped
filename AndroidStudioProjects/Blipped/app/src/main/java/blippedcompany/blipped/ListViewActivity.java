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


public class ListViewActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser userID = mAuth.getCurrentUser();
    String userName=removecom(userID.getEmail());
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersEmailFriends;
    ArrayList<String> friendrequestlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_listview);

        //generate list
        friendrequestlist = new ArrayList<String>();
        ShowFriendRequest(friendrequestlist);


        //instantiate custom adapter
       FriendRequestsAdapter adapter = new FriendRequestsAdapter(friendrequestlist, this);

        //handle listview and assign adapter
        ListView lView = (ListView) findViewById(R.id.notifListview);
        lView.setAdapter(adapter);
    }


    public void ShowFriendRequest(final ArrayList<String> friendrequestlist){

        UsersEmailFriends = database.getReference("users").child(userName).child("FriendRequests");

        UsersEmailFriends.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                for (DataSnapshot snapm: dataSnapshot.getChildren()) {

                    String email = snapm.child("email").getValue(String.class);
                    friendrequestlist.add(email);

                    // TODO
                    //Get map of users in datasnapshot


                }

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

    private static String removecom(String str) {
        if(str==null){
            return null;
        }
        else{
            return str.substring(0, str.length() - 4);
        }

    }
}
