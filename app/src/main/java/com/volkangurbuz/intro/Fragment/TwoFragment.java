package com.volkangurbuz.intro;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment implements View.OnClickListener {


    public TwoFragment() {
        // Required empty public constructor
    }


    EditText eTHobi, eTilgiAlanlari;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_two, container,
                false);


        eTHobi = (EditText) rootView.findViewById(R.id.userHobiler);

        eTilgiAlanlari = (EditText) rootView.findViewById(R.id.userilgiAlanlari);

        eTilgiAlanlari.setOnClickListener(this);

        eTHobi.setOnClickListener(this);

        // Inflate the layout for this fragment

        return rootView;
    }

    public static boolean secimler = false;

    static ArrayList<Integer> hobiler = new ArrayList<>();
    static ArrayList<Integer> ilgiAlanlari = new ArrayList<>();


    public void hobilerGoster() {

        final boolean[] checkedColors = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false

        };


        final List<String> listHobiler = Arrays.asList(getResources().getStringArray(R.array.hobiler));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        builder.setMultiChoiceItems(R.array.hobiler, checkedColors, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                // Update the current focused item's checked status

                if (isChecked) {
                    // if the user checked the item, add it to the selected items
                    hobiler.add(which + 1);
                } else if (hobiler.contains(which + 1)) {
                    // else if the item is already in the array, remove it
                    hobiler.remove(Integer.valueOf(which + 1));
                }

                checkedColors[which] = isChecked;

                // Get the current focused item
                String currentItem = listHobiler.get(which);

                // Notify the current action
                // Toast.makeText(getContext(), currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("Hobilerinizi seçiniz");

        // Set the positive/yes button click listener
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button

                for (int i = 0; i < checkedColors.length; i++) {
                    boolean checked = checkedColors[i];

                    if (checked) {

                        eTHobi.setHint("Secim yapıldı");
                    }
                }

            }
        });

        // Set the negative/no button click listener
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the negative button
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userHobiler:

                hobilerGoster();
                break;

            case R.id.userilgiAlanlari:
                ilgiAlanlariGoster();
                break;

        }
    }


    public void ilgiAlanlariGoster() {

        final boolean[] secilenler = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false

        };

        final List<String> listilgiAlanlar = Arrays.asList(getResources().getStringArray(R.array.ilgiler));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        builder.setMultiChoiceItems(R.array.ilgiler, secilenler, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {


                if (isChecked) {
                    ilgiAlanlari.add(which + 1);
                } else if (ilgiAlanlari.contains(which + 1)) {
                    // else if the item is already in the array, remove it
                    ilgiAlanlari.remove(Integer.valueOf(which + 1));
                }


                // Update the current focused item's checked status
                secilenler[which] = isChecked;

                // Get the current focused item
                String currentItem = listilgiAlanlar.get(which);

                // Notify the current action
                // Toast.makeText(getContext(),   currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("İlgi Alanlari konularını seçiniz: ");

        // Set the positive/yes button click listener


        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button

                for (int i = 0; i < secilenler.length; i++) {
                    boolean checked = secilenler[i];

                    if (checked) {

                        eTilgiAlanlari.setHint("Secim yapıldı");

                        if (eTHobi.getHint().equals("Secim yapıldı"))
                            secimler = true;

                    }
                }


            }
        });

        // Set the negative/no button click listener
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the negative button
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

    }


    /**
     * the method return an array which is points of the user. the points will use to improve user what user need to improve for the topics
     * (science, technology etc.) the points will increase by aid of the users messages which is question type .
     * @return
     */

    public static int[] puanlar() {

        int[] puanlar = new int[3];

        int kspuani = 0, tbspuani = 0, ospuani = 0;

        for (int i = 0; i < hobiler.size(); i++) {

            if ((hobiler.get(i) > 2 && hobiler.get(i) < 5) || hobiler.contains(7)) {
                kspuani++;
            }

            if ((hobiler.get(i) > 0 && hobiler.get(i) < 3) || hobiler.contains(7)) {

                tbspuani++;
            }

            if ((hobiler.get(i) > 4 && hobiler.get(i) < 8)) {

                ospuani++;
            }

        }


        for (int i = 0; i < ilgiAlanlari.size(); i++) {

            if ((ilgiAlanlari.get(i) > 3 && ilgiAlanlari.get(i) < 9)) {
                kspuani++;
            }

            if ((ilgiAlanlari.get(i) > 0 && ilgiAlanlari.get(i) < 4)) {

                tbspuani++;
            }

            if ((ilgiAlanlari.get(i) > 8 && ilgiAlanlari.get(i) < 12)) {

                ospuani++;
            }

        }


        puanlar[0] = kspuani * 10;
        puanlar[1] = tbspuani * 10;
        puanlar[2] = ospuani * 10;

        return puanlar;
    }


}
