package com.rishiksahu.stockalertsmain.fragments;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rishiksahu.stockalertsmain.MainActivity;
import com.rishiksahu.stockalertsmain.R;
import com.rishiksahu.stockalertsmain.activities.TnCActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SingupFragment extends Fragment {

    public static final int TRIAL_PERIOD = 7;

    public SingupFragment() {
        // Required empty public constructor
    }

    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private EditText email;
    private EditText phone;
    private EditText fullName;
    private EditText password;
    private EditText confirmPass;
    private TextView tncTv;
    private Button signUpBtn;

    private ProgressBar progressBar;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean showVerificationDialogDefault = false;
    public static boolean fromSignUpFragment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_singup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);

        alreadyHaveAnAccount = view.findViewById(R.id.sign_up_sign_in_button);

        email = view.findViewById(R.id.sign_up_email);
        fullName = view.findViewById(R.id.sign_up_full_name);
        phone = view.findViewById(R.id.sign_up_phone);
        password = view.findViewById(R.id.sign_up_password);
        confirmPass = view.findViewById(R.id.sign_up_confirm_password);

        tncTv = view.findViewById(R.id.tncTv);

        signUpBtn = view.findViewById(R.id.sign_up_register_button);

        progressBar = view.findViewById(R.id.progressBarSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        tncTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: send to terms and conditions activity with privacy policy.

                Intent tncIntent = new Intent(getContext(), TnCActivity.class);
                fromSignUpFragment = true;
                getActivity().startActivity(tncIntent);
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
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
        fullName.addTextChangedListener(new TextWatcher() {
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
        phone.addTextChangedListener(new TextWatcher() {
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
        confirmPass.addTextChangedListener(new TextWatcher() {
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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(fullName.getText())){
            if(!TextUtils.isEmpty(email.getText())){
                if (!TextUtils.isEmpty(phone.getText())) {
                    if (!TextUtils.isEmpty(password.getText()) && password.length() >=6){
                        if (!TextUtils.isEmpty(confirmPass.getText())){
                            signUpBtn.setEnabled(true);
                        }else {
                            confirmPass.setError("Please Confirm Your Password!");
                            signUpBtn.setEnabled(false);
                        }

                    }else{
                        password.setError("Please Enter a Password!");
                        signUpBtn.setEnabled(false);
                    }
                }else{
                    phone.setError("Please Enter Your Phone Number");
                    signUpBtn.setEnabled(false);
                }
            }else {
                email.setError("Please Enter Your Email Address!");
                signUpBtn.setEnabled(false);
            }
        } else {
            email.setError("Please Enter Your Name!");
            signUpBtn.setEnabled(false);

        }
    }

    private void checkEmailAndPassword(){
        if (email.getText().toString().matches(emailPattern)){
            if (confirmPass.getText().toString().equals(password.getText().toString())){

                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(false);

                createUserOnFirebase();

            }else {
                confirmPass.setError("Confirm Password does not matches with Password!");
            }

        }else {
            email.setError("Please Enter a Valid Email Address!");
        }
    }

    private void createUserOnFirebase(){
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        uploadUserMetaToDB();

                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        signUpBtn.setEnabled(true);
                                        Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }else {
                            //User Creation Failed
                            progressBar.setVisibility(View.INVISIBLE);
                            signUpBtn.setEnabled(true);
                            Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void uploadUserMetaToDB(){

        //subscribing user for the trial period

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, TRIAL_PERIOD);
        Timestamp validTime = new Timestamp(c.getTime());

        Timestamp currentTime = new Timestamp(new Date());

        List<String> fakeEmptyList = new ArrayList<>();

        Map<String,Object> userdata = new HashMap<>();
        userdata.put("full_name",fullName.getText().toString());
        userdata.put("email",email.getText().toString());
        userdata.put("phone",phone.getText().toString());
        userdata.put("initial_password",password.getText().toString());
        userdata.put("created_at", currentTime);
        userdata.put("active_plan_name", "Trial Plan");
        userdata.put("notific_with_chart_subscription_validity", validTime);
        userdata.put("chart_only_subscription_validity", validTime);
        userdata.put("interests", (List<String>)fakeEmptyList);
        userdata.put("notInUse_string_array_1", (List<String>)fakeEmptyList);
        userdata.put("isLoggedIn", false);
        userdata.put("isEmailVerified", false);
        userdata.put("subscribed_positional", false);
        userdata.put("subscribed_intraday", false);
        userdata.put("notInUse_string_1","");
        userdata.put("notInUse_string_2","");
        userdata.put("notInUse_string_3","");
        userdata.put("notInUse_string_4","");
        userdata.put("notInUse_string_5","");
        userdata.put("notInUse_string_6","");
        userdata.put("notInUse_boolean_1",false);
        userdata.put("notInUse_boolean_2",false);
        userdata.put("notInUse_boolean_3",false);
        userdata.put("notInUse_boolean_4",false);

        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                .set(userdata)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

//                            createUserDataCollection();
                            loginUserAndOpenMainActivity();


                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            signUpBtn.setEnabled(true);
                            Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void createUserDataCollection(){
//
//        CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");
//
//        // Maps
//        Map<String,Object> ratingsMap = new HashMap<>();
//        ratingsMap.put("list_size",(long) 0);
//
//        Map<String,Object> cartMap = new HashMap<>();
//        cartMap.put("list_size",(long) 0);
//
//        Map<String,Object> coursesMap = new HashMap<>();
//        coursesMap.put("list_size",(long) 0);
//
//        Map<String,Object> testsMap = new HashMap<>();
//        testsMap.put("list_size",(long) 0);
//
//        Map<String,Object> notesMap = new HashMap<>();
//        notesMap.put("list_size",(long) 0);
//
//        // End Maps
//
//        final List<String> documentNames = new ArrayList<>();
//        documentNames.add("MY_RATINGS");
//        documentNames.add("MY_CART");
//        documentNames.add("MY_COURSES");
//        documentNames.add("MY_TESTS");
//        documentNames.add("MY_NOTES");
//
//        final List<Map<String, Object>> documentFields = new ArrayList<>();
//        documentFields.add(ratingsMap);
//        documentFields.add(cartMap);
//        documentFields.add(coursesMap);
//        documentFields.add(testsMap);
//        documentFields.add(notesMap);
//
//        for (int x = 0; x < documentNames.size() ; x++){
//            final int finalX = x;
//            userDataReference.document(documentNames.get(x))
//                    .set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()){
//
//
//                        if (finalX == documentNames.size() -1) {
//
//                           loginUserAndOpenMainActivity();
//
//                        }
//
//                    } else {
//                        progressBar.setVisibility(View.INVISIBLE);
//                        signUpBtn.setEnabled(true);
//                        String error = task.getException().getMessage();
//                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }

    private void loginUserAndOpenMainActivity(){
        if (firebaseAuth.getCurrentUser().isEmailVerified()){
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
            getActivity().finish();
        }else {
            progressBar.setVisibility(View.INVISIBLE);
            //send user to login activity and show the dialog
            firebaseAuth.signOut();
            setFragment(new LoginFragment());
            showVerificationDialogDefault = true;
        }
    }

}