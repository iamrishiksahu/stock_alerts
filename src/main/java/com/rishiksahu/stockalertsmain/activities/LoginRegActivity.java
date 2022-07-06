package com.rishiksahu.stockalertsmain.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.rishiksahu.stockalertsmain.R;
import com.rishiksahu.stockalertsmain.fragments.LoginFragment;

import static com.rishiksahu.stockalertsmain.fragments.LoginFragment.onResetPasswordFragment;
import static com.rishiksahu.stockalertsmain.fragments.LoginFragment.onSignUpFragment;

public class LoginRegActivity extends AppCompatActivity {


    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reg);

        frameLayout = findViewById(R.id.register_frameLayout);
        setDefaultFragment(new LoginFragment());


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){

            if (onResetPasswordFragment){
                onResetPasswordFragment = false;
                setFragment(new LoginFragment());
                return false;
            }
            if (onSignUpFragment){
                onSignUpFragment = false;
                setFragment(new LoginFragment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}