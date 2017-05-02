package com.salmaali.app.petspot.User;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowUsersActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu_item) {
            LogInActivity.mFirebaseAuth.signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        final String userID = getIntent().getStringExtra("userID");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersDatabase = firebaseDatabase.getReference().child("Users");

        final ArrayList<String> usersList = new ArrayList<>();
        final UserAdapter adapter = new UserAdapter(ShowUsersActivity.this, R.layout.users_list_item, usersList);
        final ListView usersListView = (ListView) findViewById(R.id.show_users_list_view);

        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (!user.getKey().equals(userID)) {
                        String s = user.child("firstName").getValue(String.class) + " "
                                + user.child("lastName").getValue(String.class) +
                                "%%" + user.child("photoUrl").getValue(String.class)
                                + "%%" + user.getKey();
                        usersList.add(s);
                    }
                }
                usersListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = adapter.getItem(i).split("%%")[2];
                Intent intent = new Intent(ShowUsersActivity.this, UserProfileActivity.class);
                intent.putExtra("userID", s);
                startActivity(intent);
            }
        });
    }
}
