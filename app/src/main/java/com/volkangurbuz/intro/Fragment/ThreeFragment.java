package com.volkangurbuz.intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThreeFragment extends Fragment implements View.OnClickListener {

    CheckBox checkSozlesme;


    public ThreeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_three, container,
                false);

        checkSozlesme = (CheckBox) rootView.findViewById(R.id.checkSozlesme);

        checkSozlesme.setOnClickListener(this);

        return rootView;
    }


    public static boolean secilenSozlesme;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.checkSozlesme:

                secilenSozlesme = checkSozlesme.isChecked();

                break;
        }

    }


}
