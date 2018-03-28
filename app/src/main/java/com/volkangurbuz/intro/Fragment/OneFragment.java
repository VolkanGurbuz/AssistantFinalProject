package com.volkangurbuz.intro;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment implements View.OnClickListener{

    protected static boolean secildimi = false;


    public OneFragment() {
        // Required empty public constructor
    }

    protected static EditText eTUserCinsiyet, eTuserAd, eTuserYas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_one, container,
                false);


        eTUserCinsiyet = (EditText) rootView.findViewById(R.id.userCinsiyet);

        eTuserAd = (EditText) rootView.findViewById(R.id.userAd);
        eTuserYas = (EditText) rootView.findViewById(R.id.userYas);

        eTUserCinsiyet.setOnClickListener(this);

        return rootView;

    }

    AlertDialog.Builder ad;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userCinsiyet:

                cinsiyetGoster();
                break;

    }
    }

    public void cinsiyetGoster(){

        ad = new AlertDialog.Builder(getContext());

        ad.setTitle("Cinsiyetinizi giriniz: ");

        ad.setSingleChoiceItems(R.array.cinsiyetler, -1,  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                eTUserCinsiyet.setText(getResources().getStringArray(R.array.cinsiyetler)[arg1]);

                if (eTUserCinsiyet.getText().toString().trim().length() > 0 && eTuserAd.getText().toString().trim().length() > 0  &&eTuserYas.getText().toString().trim().length() > 0 )
                secildimi = true;
                dialog.dismiss();

            }
        });

        ad.show();

    }





}
