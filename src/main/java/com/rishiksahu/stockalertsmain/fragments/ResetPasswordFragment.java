package com.rishiksahu.stockalertsmain.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rishiksahu.stockalertsmain.R;


public class ResetPasswordFragment extends Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText email;
    private TextView goBack;
    private Button resetPasswordButton;
    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;

    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private TextView spamFolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        email = view.findViewById(R.id.forgotPass_email);
        resetPasswordButton = view.findViewById(R.id.forgotPass_resetPassBtn);
        goBack = view.findViewById(R.id.forgotPass_goBackBtn);
        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);
        firebaseAuth = FirebaseAuth.getInstance();

        linearLayout = view.findViewById(R.id.successfulRecoverySentLinearLayout);
        spamFolder = view.findViewById(R.id.txt_checkSpamFolder);
        progressBar = view.findViewById(R.id.forgotPass_progressBar);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetPasswordButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                if(!email.getText().toString().equals("")) {
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        linearLayout.setVisibility(View.VISIBLE);
                                        spamFolder.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);

                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                    }
                                    resetPasswordButton.setEnabled(true);
                                }
                            });
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Please Enter Your Registered Email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkInputs(){
        if (!TextUtils.isEmpty(email.getText())){
            resetPasswordButton.setEnabled(true);
        }else {
            resetPasswordButton.setEnabled(false);
        }
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}