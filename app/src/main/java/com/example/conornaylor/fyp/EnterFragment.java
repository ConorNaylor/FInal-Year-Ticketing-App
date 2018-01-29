package com.example.conornaylor.fyp;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterFragment extends Fragment {

    private NfcAdapter nfcAdapter;


    public EnterFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(NfcAdapter.getDefaultAdapter(getActivity()) == null){
            basicAlert("Your device is not NFC enabled.");
        }else if(!NfcAdapter.getDefaultAdapter(getActivity()).isEnabled()){
            nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
            Intent NFCSettingsintent = new Intent(Settings.ACTION_NFC_SETTINGS);
            Intent closeAppIntent = new Intent(Intent.ACTION_MAIN);
            closeAppIntent.addCategory(Intent.CATEGORY_HOME);
            settingsAlert("Enable your NFC chip to continue.", NFCSettingsintent,closeAppIntent);
        }else{
            nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    public void onPause(){
        super.onPause();

        disableForegroundDispatchSystem();
    }

    public void enableForegroundDispatchSystem(){

        Fragment fragment = new Fragment ();

    }

    public void disableForegroundDispatchSystem(){

    }

    private void basicAlert(String message) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setTitle("OnePass");
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void settingsAlert(String message, final Intent okIntent, final Intent cancelIntent ){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setTitle("OnePass");
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(okIntent);
            }
        });
        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(cancelIntent);
            }
        });
        dialog.show();
    }
}
