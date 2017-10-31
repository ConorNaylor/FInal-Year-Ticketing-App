package com.example.conornaylor.fyp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    TextView mEmailView;
    TextView mNameView;
    CheckBox mEditAccount;


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

        mEmailView = view.findViewById(R.id.email);
        mNameView = view.findViewById(R.id.name);

        mEmailView.setEnabled(false);
        mNameView.setEnabled(false);
//        mEditAccount.setOnClickListener(
//
//        );
    }

}
