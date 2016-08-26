package com.bytecode.project12;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.content.pm.Signature;

public class MainActivity extends AppCompatActivity {

    private CallbackManager cm;
    private LoginButton btnLogin;
    private ViewPager mViewPage;
    private AdView adView;

    private static final ArrayList<Pair<Integer, String>> IMAGE_IDS =
            new ArrayList<Pair<Integer, String>>() {{
                add(new Pair<>(R.drawable.goofy, "http://shareitexampleapp.parseapp.com/goofy/"));
                add(new Pair<>(R.drawable.liking, "http://shareitexampleapp.parseapp.com/liking/"));
                add(new Pair<>(R.drawable.viking, "http://shareitexampleapp.parseapp.com/viking/"));
            }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        cm = CallbackManager.Factory.create();
        getFbKeyHash();
        setContentView(R.layout.activity_main);

        setupViewPage();
        btnLogin = (LoginButton)findViewById(R.id.id_login_facebook);
        adView = (AdView) findViewById(R.id.ad_view);

        btnLogin.registerCallback(cm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Inicio de Sesion!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this,"Inicio Cancelado!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this,"Error al iniciar Sesion!",Toast.LENGTH_LONG).show();
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }

    public void getFbKeyHash(){
        String packageName = "+J+3yf/mrgPgKeg1llIttpSjcws=";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e){
            Log.d("Error package :", e.getMessage());
            System.out.println("Error package :" + e.getMessage());
        }catch (NoSuchAlgorithmException e){

        }

    }
    protected void onActivityResult(int reqCode, int resCode, Intent i){
        cm.onActivityResult(reqCode, resCode, i);
    }

    private void setupViewPage() {
        mViewPage = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPage.setAdapter(adapter);

        final PageSelector pageSelector = (PageSelector)findViewById(R.id.page_selector);
        pageSelector.setImageCount(IMAGE_IDS.size());


        mViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position,
                    float positionOffset,
                    int positionOffsetPixels) {
                pageSelector.setPosition(position);
                String shareContent = IMAGE_IDS.get(position).second;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ShareImageFragment imageFragment = new ShareImageFragment();
            imageFragment.setImage(IMAGE_IDS.get(position).first);
            return imageFragment;
        }

        @Override
        public int getCount() {
            return IMAGE_IDS.size();
        }
    }

    @Override
    protected void onPause() {
        if(adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(adView != null){
            adView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }
}
