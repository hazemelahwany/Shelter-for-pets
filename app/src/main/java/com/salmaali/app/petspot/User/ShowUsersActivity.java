package com.salmaali.app.petspot.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.salmaali.app.petspot.Fragments.ShowUsersFragment;
import com.salmaali.app.petspot.R;

public class ShowUsersActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        if (savedInstanceState == null) {
            ShowUsersFragment showUsersFragment = new ShowUsersFragment();
            getFragmentManager().beginTransaction().add(R.id.show_users_container, showUsersFragment).commit();
        }

    }
}
