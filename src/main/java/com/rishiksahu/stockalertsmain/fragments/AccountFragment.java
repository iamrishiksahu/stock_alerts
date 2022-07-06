package com.rishiksahu.stockalertsmain.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rishiksahu.stockalertsmain.R;
import com.rishiksahu.stockalertsmain.classes.SimpleRVAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class AccountFragment extends Fragment {

    private TextView greetingText, subsValPeriodText, plantText;
    private RecyclerView interestRv;
    private Button manageInterestBtn;
    private String[] availableInterestList;
    private boolean[] checkedItems;
    private List<String> interestList, tempList;

    private ProgressBar loadingSign;
    private RelativeLayout activeSubsLayout;
    private LinearLayout notSubscribedLayout;

    private ArrayList<Integer> mUserItems = new ArrayList<>();
    public static SimpleRVAdapter rvAdapter;
    public static Timestamp subsValidity;


    private boolean isSubscribed = false;
    private String activePlanTitle = "";
    private Timestamp comboValidity, chartsValidity;
    private static List<String> interestListFirst = new ArrayList<>();


    private FirebaseFirestore firebaseFirestore;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        plantText = view.findViewById(R.id.planTv);
        greetingText = view.findViewById(R.id.greetingTv);
        subsValPeriodText = view.findViewById(R.id.valididtyDateTv);
        interestRv = view.findViewById(R.id.account_interest_rv);
        manageInterestBtn = view.findViewById(R.id.manage_interest_btn);
        loadingSign = view.findViewById(R.id.loadingBar);
        activeSubsLayout = view.findViewById(R.id.activeSubsLayout);
        notSubscribedLayout = view.findViewById(R.id.not_subscirbed_layout);

        loadingSign.setVisibility(View.VISIBLE);

        availableInterestList = getResources().getStringArray(R.array.interest_lists);
        checkedItems = new boolean[availableInterestList.length];
        interestList = new ArrayList<>();
        tempList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getActivity().getSharedPreferences("com.wealthcreatorpro.stockalerts.user", MODE_PRIVATE);
        final String name = prefs.getString("user_full_name", "");
        final String email = prefs.getString("user_email", "");

        greetingText.setText("Hello, " + name + "!");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        interestRv.setLayoutManager(layoutManager);

        rvAdapter = new SimpleRVAdapter(interestList);
        interestRv.setAdapter(rvAdapter);


        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    comboValidity = (Timestamp) task.getResult().get("notific_with_chart_subscription_validity");
                    chartsValidity = (Timestamp) task.getResult().get("chart_only_subscription_validity");
                    activePlanTitle = (String) task.getResult().get("active_plan_name");
                    interestListFirst = (List<String>) task.getResult().get("interests");
                    interestList = interestListFirst;

                    Timestamp currentTime = new Timestamp(new Date());


                    Long today = currentTime.getSeconds();
                    Long combo = comboValidity.getSeconds();
                    Long charts = chartsValidity.getSeconds();

                    Long secsConvertibleCombo = combo*1000;
                    Long secsConvertibleChart = charts*1000;

                    Date ComboDate = new Date(secsConvertibleCombo);
                    Date ChartDate = new Date(secsConvertibleChart);

                    SimpleDateFormat DateFor = new SimpleDateFormat("hh:mm aa dd/MM/yyyy");
                    String stringDateCombo= DateFor.format(ComboDate);
                    String stringDateChart= DateFor.format(ChartDate);

                    if (combo > today){
                        //subscribed for notifications + charts
                        isSubscribed = true;
                    }else if (charts > today){
                        //subscribed for charts

                        isSubscribed = true;
                    }else {
                        //subscription expired

                        isSubscribed = false;
                    }

                    //if not subscribed then INVISIBLE ActiveSubsLayout and VISIBLE UnSubscribedLayout
                    //Never set ActiveSubsLayout to GONE because some layouts are dependent on its id
                    if (isSubscribed){

                        activeSubsLayout.setVisibility(View.VISIBLE);
                        notSubscribedLayout.setVisibility(View.INVISIBLE);

                        if (combo > charts){
                            subsValPeriodText.setText(stringDateCombo);
                        }else {
                            subsValPeriodText.setText(stringDateChart);
                        }

                        plantText.setText("<<" + activePlanTitle);

                     }else {
                        notSubscribedLayout.setVisibility(View.VISIBLE);
                        activeSubsLayout.setVisibility(View.INVISIBLE);
                    }


                    rvAdapter = new SimpleRVAdapter(interestList);
                    interestRv.setAdapter(rvAdapter);
                    loadingSign.setVisibility(View.GONE);


                }else {
                    Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });





        manageInterestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show the category selection dialog

                AlertDialog.Builder selectDialogBuilder = new AlertDialog.Builder(getContext());
                selectDialogBuilder.setTitle("Select Your Interests -");
                selectDialogBuilder.setCancelable(true);
                selectDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        loadingSign.setVisibility(View.VISIBLE);

                        interestList.clear();
                        tempList.clear();
                        for (int i = 0; i < mUserItems.size(); i++){
                            interestList.add(availableInterestList[mUserItems.get(i)]);
                            tempList.add(availableInterestList[mUserItems.get(i)]);

                        }

                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                .update("interests", (List<String>) tempList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    loadingSign.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Interests Update Successfully!", Toast.LENGTH_SHORT).show();
                                    rvAdapter.notifyDataSetChanged();
                                }else {
                                    loadingSign.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Something Went Wrong! : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                selectDialogBuilder.setMultiChoiceItems(availableInterestList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        try {
                            if (isChecked){
                                if (!mUserItems.contains(position)){
                                    mUserItems.add(position);
                                }
                            }else if (mUserItems.contains(position)){
                                mUserItems.remove(position);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });
                selectDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        for (int i = 0; i<checkedItems.length; i++){
                            checkedItems[i] = false;
                            mUserItems.clear();
                        }
                    }
                });

                AlertDialog selectionDialog = selectDialogBuilder.create();
                selectionDialog.show();
            }
        });
    }
}