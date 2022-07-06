package com.rishiksahu.stockalertsmain.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rishiksahu.stockalertsmain.MainActivity;
import com.rishiksahu.stockalertsmain.R;

import static android.content.Context.MODE_PRIVATE;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAnAccount;
    private TextView forgotPassword;
    private FrameLayout parentFrameLayout;

    private EditText email;
    private EditText password;
    private ProgressBar progressBar;
    private Button logInBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean onResetPasswordFragment = false;
    public static boolean onSignUpFragment = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        dontHaveAnAccount = view.findViewById(R.id.sign_in_sign_up_button);
        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);

        email = view.findViewById(R.id.sign_in_email);
        forgotPassword = view.findViewById(R.id.sign_in_forgot_password);
        password = view.findViewById(R.id.sign_in_password);


        logInBtn = view.findViewById(R.id.sign_in_login_button);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progressBarLogIn);
        
        firestore = FirebaseFirestore.getInstance();


        if (SingupFragment.showVerificationDialogDefault){
            SingupFragment.showVerificationDialogDefault = false;
            showVerificationInstructionDialog("Verification Email Sent Successfully!");
        }

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpFragment = true;
                setFragment(new SingupFragment());
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });

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
        password.addTextChangedListener(new TextWatcher() {
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

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        if (!TextUtils.isEmpty(email.getText())){
            if (!TextUtils.isEmpty(password.getText())){
                logInBtn.setEnabled(true);
            }else {
                logInBtn.setEnabled(false);
            }
        }else {
            logInBtn.setEnabled(false);
        }
    }

    private void checkEmailAndPassword(){
        if (email.getText().toString().matches(emailPattern)){
            if (password.length() >= 6){

                progressBar.setVisibility(View.VISIBLE);
                logInBtn.setEnabled(false);

                loginUserAndSendToMainActivity();


            }else {
                password.setError("Please Enter a Valid Password");
            }
        }else {
            email.setError("Please Enter a Valid Email Address!");
        }
    }

    private void loginUserAndSendToMainActivity(){

            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                
                                //Email has been verified
                                //Set isEmailVerified = true
                                //Check for other login instances
                                
                                firestore.collection("USERS").document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        Boolean isEmailVerified = documentSnapshot.getBoolean("isEmailVerified");
                                        Boolean isLoggedIn = documentSnapshot.getBoolean("isLoggedIn");
                                        final String fullName = documentSnapshot.getString("full_name");
                                        final String phone = documentSnapshot.getString("email");
                                        final String email = documentSnapshot.getString("phone");

                                        if (!isEmailVerified) {
                                            firestore.collection("USERS").document(firebaseAuth.getUid()).update("isEmailVerified", true);
                                        }

                                        if (isLoggedIn){
                                            //Already Logged In To Another Device

                                            final AlertDialog warningDialog = new AlertDialog.Builder(getContext())
                                                    .setTitle("Already Logged In!")
                                                    .setMessage("You have already logged in on another device please logout from old device and then try again. If you still face problems then please contact us on: +91-6366689135")
                                                    .setPositiveButton("Okay!", null)
                                                    .show();
                                            warningDialog.setCancelable(true);
                                            Button positiveBtn = warningDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                            positiveBtn.setTextColor(Color.parseColor("#0F94FF"));
                                            positiveBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    warningDialog.dismiss();
                                                }
                                            });

                                            firebaseAuth.signOut();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            logInBtn.setEnabled(true);
                                        }else {
                                            //Not Logged in Any Device

                                            firestore.collection("USERS").document(firebaseAuth.getUid())
                                                    .update("isLoggedIn", true)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                storeUserMetaInSharedPrefs(email, fullName, phone);
                                                                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                                                                startActivity(mainIntent);
                                                                getActivity().finish();

                                                            }else {
                                                                firebaseAuth.signOut();
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                logInBtn.setEnabled(true);
                                                                Toast.makeText(getContext(), "" + task.getException() + ". Please Try Again Later!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });



                            }else {
                                firebaseAuth.signOut();
                                progressBar.setVisibility(View.INVISIBLE);
                                logInBtn.setEnabled(true);
                                showVerificationInstructionDialog( "Please Verify Your Email!");
                            }

                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            logInBtn.setEnabled(true);
                            String errormsg = task.getException().getMessage();
                            Toast.makeText(getActivity(), errormsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });




    }

    private void storeUserMetaInSharedPrefs(String email, String name, String phone) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.pratikwaghmare.stockalertsmain.user", MODE_PRIVATE).edit();
        editor.putString("user_email", email);
        editor.putString("user_phone", phone);
        editor.putString("user_full_name", name);
        editor.apply();
    }

    private void showVerificationInstructionDialog(String title){
        final AlertDialog warningDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(getString(R.string.emailNotVerifiedAlertText))
                .setPositiveButton("Okay!", null)
                .show();
        warningDialog.setCancelable(true);
        Button positiveBtn = warningDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setTextColor(Color.parseColor("#0F94FF"));
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
            }
        });
    }

}
