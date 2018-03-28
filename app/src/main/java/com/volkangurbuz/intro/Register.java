package com.volkangurbuz.intro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public Button buttonNext, buttonCancel;
    public EditText eTtarihAc;

    FireBaseOperations fireBaseOperations;


    OneFragment one;
    static User u;
    DatabaseOperations database;


    DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        fireBaseOperations = new FireBaseOperations();
        usersRef = fireBaseOperations.getMyRef();
        one = new OneFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eTtarihAc = (EditText) findViewById(R.id.userYas);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        buttonNext = (Button) findViewById(R.id.btn_nextRegister);
        buttonCancel = (Button) findViewById(R.id.btn_cancel);


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = getItem(+1);
                if (current < 2) {
                    // move to next screen
                    viewPager.setCurrentItem(current);

                }

                u = new User("volkan", 22, "erkek", 65, 85,14);
                startActivity(new Intent(Register.this, MainChat.class));

                if (current == 3) {
                    if (tumKontroller()) {


                        String ad = OneFragment.eTuserAd.getText().toString();
                        int yas = Integer.parseInt(OneFragment.eTuserYas.getText().toString());
                        String cinsiyet = OneFragment.eTUserCinsiyet.getText().toString();



                        int kspuani = TwoFragment.puanlar()[0];
                        int tbpuani = TwoFragment.puanlar()[1];
                        int ospuani = TwoFragment.puanlar()[2];

                        u = new User(ad, yas, cinsiyet, kspuani, tbpuani, ospuani);

                        //firebase save
                        String userId = usersRef.push().getKey();
                        usersRef.child(userId).setValue(u);


                        if (kayitOl(u)) {
                            Toast.makeText(getApplicationContext(), "Basariyla kaydoldu", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Register.this, MainChat.class));
                        } else
                            Toast.makeText(getApplicationContext(), "Hata", Toast.LENGTH_SHORT).show();


                    } else

                        Toast.makeText(getApplicationContext(), "lutfen tüm alanlari doldurunuz", Toast.LENGTH_SHORT).show();

                } else {
                    viewPager.setCurrentItem(current);
                    buttonNext.setText("tamamla");

                }
            }
        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = getItem(0);
                if (current > 0) {
                    // move to back screen
                    viewPager.setCurrentItem(current - 1);
                } else {
                    viewPager.setCurrentItem(current);
                    buttonNext.setText("ileri");
                    buttonCancel.setText("iptal");

                }
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Kişisel Bilgiler");
        adapter.addFragment(new TwoFragment(), "Kişisel Özellikler");
        adapter.addFragment(new ThreeFragment(), "Sözleşme");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    protected void cinsiyetAc(View v) {

        one = new OneFragment();

        one.onClick(v);

    }

    TwoFragment two;

    protected void hobilerAc(View v) {

        two = new TwoFragment();

        two.onClick(v);

    }

    protected void ilgilerAc(View v) {

        two = new TwoFragment();

        two.onClick(v);

    }


    protected boolean tumKontroller() {


        // return ThreeFragment.secilenSozlesme && TwoFragment.secimler && OneFragment.secildimi;

        return ThreeFragment.secilenSozlesme;
    }


    ThreeFragment three;

    protected void ischecked(View view) {

        three = new ThreeFragment();
        three.onClick(view);

    }

    DatabaseOperations databaseOperations;

    protected boolean kayitOl(User u) {
        databaseOperations = new DatabaseOperations(this, null, null, 1);
        return databaseOperations.uyeEkle(u);
    }


}
