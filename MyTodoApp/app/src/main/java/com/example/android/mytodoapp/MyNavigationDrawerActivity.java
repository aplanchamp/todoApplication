package com.example.android.mytodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.mytodoapp.user.TabUserActivity;
import com.parse.ParseUser;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by aude on 24/07/2015.
 */

// this class uses : compile 'it.neokree:MaterialNavigationDrawer:1.3.3'

public class MyNavigationDrawerActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {


        // sections are items in the navigation drawer. Section1, 2 and 3 starts a fragment and section4 starts a function. The first section must always launch a fragment.
        MaterialSection section1 = newSection("My Current todos", this.getResources().getDrawable(R.drawable.ic_action_assignment), new CurrentTodoFragment());
        section1.setSectionColor(this.getResources().getColor(R.color.colorPrimary), this.getResources().getColor(R.color.colorPrimaryDark));
        MaterialSection section2 = newSection("My Completed todos",this.getResources().getDrawable(R.drawable.ic_action_assignment_turned_in), new CompletedTodoFragment());
        section2.setSectionColor(this.getResources().getColor(R.color.colorPrimary), this.getResources().getColor(R.color.colorPrimaryDark));
        MaterialSection section3 = newSection("About",this.getResources().getDrawable(R.drawable.ic_action_info_outline), new AboutFragment());
        section3.setSectionColor(this.getResources().getColor(R.color.colorPrimary), this.getResources().getColor(R.color.colorPrimaryDark));

        // MaterialAccount implements the account part in a navigation drawer regarding to material design guidelines
        MaterialAccount account = new MaterialAccount(this.getResources(), ParseUser.getCurrentUser().getUsername(), ParseUser.getCurrentUser().getEmail(), null, R.drawable.background);


        MaterialSection section4 = this.newSection("Logout", this.getResources().getDrawable(R.drawable.ic_action_exit_to_app),new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                ParseUser.logOut();
                startActivity(new Intent(getApplication(), TabUserActivity.class));
                finish();
            }
        });
        section4.setSectionColor(this.getResources().getColor(R.color.colorPrimary), this.getResources().getColor(R.color.colorPrimaryDark));

        addSection(section1);
        addSection(section2);
        addSection(section3);
        addSection(section4);

        this.addAccount(account);

    }
}
