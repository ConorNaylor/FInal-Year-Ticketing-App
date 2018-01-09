package com.example.conornaylor.fyp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private TextView mEmailView;
    private TextView mNameView;
    private CheckBox mEditAccount;
    private ArrayList<Event> myEvents;
    private static final String MyPreferences = "preferences";
    private SharedPreferences preferences;
    private String userIdString = "id";
    private String usernameString = "username";
    private String userEmailString = "email";
    private String userID;
    private String username;
    private String userEmail;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Account");

        preferences = getActivity().getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userID = preferences.getString(userIdString, null);
        userEmail = preferences.getString(userEmailString, null);
        username = preferences.getString(usernameString, null);

        mEmailView = view.findViewById(R.id.email);
        mNameView = view.findViewById(R.id.name);

        mEmailView.setText(userEmail);
        mNameView.setText(username);

        mEmailView.setSelected(false);
        mNameView.setSelected(false);

        mEmailView.setEnabled(false);
        mNameView.setEnabled(false);

        myEvents = new ArrayList();
        for(Event e: Event.getEvents()) {
            if(userID.equals(e.getUserId())) {
                myEvents.add(e);
            }
        }

        ListView accountEventsList = view.findViewById(R.id.yourEvents);
        ListAdapter myAccountAdapter = new EventAdaptor(getActivity(), myEvents);
        accountEventsList.setAdapter(myAccountAdapter);
//        );
    }

}
