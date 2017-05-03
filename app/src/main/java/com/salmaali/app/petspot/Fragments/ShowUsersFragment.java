package com.salmaali.app.petspot.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salmaali.app.petspot.Adapters.UserAdapter;
import com.salmaali.app.petspot.Authentication.LogInActivity;
import com.salmaali.app.petspot.R;
import com.salmaali.app.petspot.User.ShowUsersActivity;
import com.salmaali.app.petspot.User.UserProfileActivity;

import java.util.ArrayList;

public class ShowUsersFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_users, container, false);

        final String userID = getActivity().getIntent().getStringExtra("userID");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersDatabase = firebaseDatabase.getReference().child("Users");

        final ArrayList<String> usersList = new ArrayList<>();
        final UserAdapter adapter = new UserAdapter(getActivity(), R.layout.users_list_item, usersList);
        final ListView usersListView = (ListView) rootView.findViewById(R.id.show_users_list_view);

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
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra("userID", s);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
