package com.budget.personalbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class DailyAnalyticsActivity<AnyChartView> extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount, analyticsFoodAmount, analyticsHouseExpensesAmount, analyticsEntertainmentAmount;
    private TextView analyticsEducationAmount, analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount, analyticsPersonalExpensesAmount, analyticsOtherAmount, monthSpentAmount;

    private RelativeLayout linearLayoutFood, linearLayoutTransport, linearLayoutFoodHouse, linearLayoutEntertainment, linearLayoutEducation;
    private RelativeLayout linearLayoutCharity, linearLayoutApparel, linearLayoutHealth, linearLayoutPersonalExp, linearLayoutOther, linearLayoutAnalysis;

    //private AnyChartView anyChartView;
    private TextView progress_ratio_transport, progress_ratio_food, progress_ratio_house, progress_ratio_ent, progress_ratio_edu, progress_ratio_cha, progress_ratio_app, progress_ratio_hea, progress_ratio_per, progress_ratio_oth, monthRatioSpending;
    private ImageView status_Image_transport, status_Image_food, status_Image_house, status_Image_ent, status_Image_edu, status_Image_cha, status_Image_app, status_Image_hea, status_Image_per, status_Image_oth, monthRatioSpending_Image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        totalBudgetAmountTextView = findViewById(R.id.totalAmount);

        //general analytic
        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);

        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount = findViewById(R.id.analyticsHouseExpenseAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalExpensesAmount = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //Relative layouts views
        linearLayoutTransport = findViewById(R.id.relativeLayoutTransport);
        linearLayoutFood = findViewById(R.id.relativeLayoutFood);
        linearLayoutFoodHouse = findViewById(R.id.relativeLayoutHouse);
        linearLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        linearLayoutEducation = findViewById(R.id.relativeLayoutEducation);
        linearLayoutCharity = findViewById(R.id.relativeLayoutCharity);
        linearLayoutApparel = findViewById(R.id.relativeLayoutApparel);
        linearLayoutHealth = findViewById(R.id.relativeLayoutHealth);
        linearLayoutPersonalExp = findViewById(R.id.relativeLayoutPersonal);
        linearLayoutOther = findViewById(R.id.relativeLayoutOther);

        //textviews
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_entertainment);
        progress_ratio_edu = findViewById(R.id.progress_ratio_education);
        progress_ratio_cha = findViewById(R.id.progress_ratio_charity);
        progress_ratio_app = findViewById(R.id.progress_ratio_apparel);
        progress_ratio_hea = findViewById(R.id.progress_ratio_health);
        progress_ratio_per = findViewById(R.id.progress_ratio_personal);
        progress_ratio_oth = findViewById(R.id.progress_ratio_other);
        //imageviews
        status_Image_transport = findViewById(R.id.status_image_transport);
        status_Image_food = findViewById(R.id.status_image_food);
        status_Image_house = findViewById(R.id.status_image_house);
        status_Image_ent = findViewById(R.id.status_image_entertainment);
        status_Image_edu = findViewById(R.id.status_image_education);
        status_Image_cha = findViewById(R.id.status_image_charity);
        status_Image_app = findViewById(R.id.status_image_apparel);
        status_Image_hea = findViewById(R.id.status_image_health);
        status_Image_per = findViewById(R.id.status_image_personal);
        status_Image_oth = findViewById(R.id.status_image_other);

        //anyChartView
        //anyChartView = findViewById(R.id.anyChartView);


        getTotalWeekTransportExpense();
        getTotalWeekFoodExpense();
        getTotalWeekHouseExpenses();
        getTotalWeekEntertainmentExpenses();
        getTotalWeekEducationExpenses();
        getTotalWeekCharityExpenses();
        getTotalWeekApparelExpenses();
        getTotalWeekHealthExpenses();
        getTotalWeekPersonalExpenses();
        getTotalWeekOtherExpenses();
        getTotalDaySpending();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        setStatusAndImageResource();
                    }
                },
                2000
        );

    }

    private void getTotalWeekTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Transport" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsTransportAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayTrans").setValue(totalAmount);
                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekFoodExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNday = "Food" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsFoodAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayFood").setValue(totalAmount);
                } else {
                    linearLayoutFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekHouseExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "House" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsHouseExpensesAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayHouse").setValue(totalAmount);
                } else {
                    linearLayoutFoodHouse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekEntertainmentExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsEntertainmentAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayEnt").setValue(totalAmount);
                } else {
                    linearLayoutEntertainment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekEducationExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsEducationAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayEdu").setValue(totalAmount);
                } else {
                    linearLayoutEducation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekCharityExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsCharityAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayCha").setValue(totalAmount);
                } else {
                    linearLayoutCharity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekApparelExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsApparelAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayApp").setValue(totalAmount);
                } else {
                    linearLayoutApparel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekHealthExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsHealthAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayHea").setValue(totalAmount);
                } else {
                    linearLayoutHealth.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekPersonalExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Personal" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsPersonalExpensesAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayPer").setValue(totalAmount);
                } else {
                    linearLayoutPersonalExp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekOtherExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        String itemNDay = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsOtherAmount.setText("Spent : Rs." + totalAmount);
                    personalRef.child("dayOther").setValue(totalAmount);
                } else {
                    linearLayoutOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalDaySpending() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    totalBudgetAmountTextView.setText("Total day's spending : Rs." + totalAmount);
                    monthSpentAmount.setText("Total Spent: Rs." + totalAmount);
                } else {
                    totalBudgetAmountTextView.setText("You have not spent today");
                    monthSpentAmount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatusAndImageResource() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    float chaTotal;
                    if (snapshot.hasChild("dayCha")) {
                        chaTotal = Integer.parseInt(snapshot.child("dayCha").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("dayHea")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }
                    float othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("today")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }

                    //GETTING RATIOS
                    float traRatio;
                    if (snapshot.hasChild("dayTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("dayFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("dayHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("dayEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("dayEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }

                    float eduRatio;
                    if (snapshot.hasChild("dayEduRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("dayEduRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }

                    float chaRatio;
                    if (snapshot.hasChild("dayCharRatio")) {
                        chaRatio = Integer.parseInt(snapshot.child("dayCharRatio").getValue().toString());
                    } else {
                        chaRatio = 0;
                    }

                    float appRatio;
                    if (snapshot.hasChild("dayAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("dayAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }

                    float heaRatio;
                    if (snapshot.hasChild("dayHealthRatio")) {
                        heaRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                    } else {
                        heaRatio = 0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("dayPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("dayPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("dayOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("dailyBudget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }


                    float monthPercent = (float) ((monthTotalSpentAmount / monthTotalSpentAmountRatio) * 100.0);
                    monthPercent = returnZeroForNaN(monthPercent);
                    if (monthPercent < 50) {
                        monthRatioSpending.setText(monthPercent + " %" + " used of " + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    } else if (monthPercent >= 50 && monthPercent < 100) {
                        monthRatioSpending.setText(monthPercent + " %" + " used of " + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    } else {
                        monthRatioSpending.setText(monthPercent + " %" + " used of " + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);

                    }

                    float transportPercent = (float) ((traTotal / traRatio) * 100.0);
                    transportPercent = returnZeroForNaN(transportPercent);
                    if (transportPercent < 50) {
                        progress_ratio_transport.setText(transportPercent + " %" + " used of " + traRatio + ". Status:");
                        status_Image_transport.setImageResource(R.drawable.green);
                    } else if (transportPercent >= 50 && transportPercent < 100) {
                        progress_ratio_transport.setText(transportPercent + " %" + " used of " + traRatio + ". Status:");
                        status_Image_transport.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_transport.setText(transportPercent + " %" + " used of " + traRatio + ". Status:");
                        status_Image_transport.setImageResource(R.drawable.red);

                    }

                    float foodPercent = (float) ((foodTotal / foodRatio) * 100.0);
                    foodPercent = returnZeroForNaN(foodPercent);
                    if (foodPercent < 50) {
                        progress_ratio_food.setText(foodPercent + " %" + " used of " + foodRatio + ". Status:");
                        status_Image_food.setImageResource(R.drawable.green);
                    } else if (foodPercent >= 50 && foodPercent < 100) {
                        progress_ratio_food.setText(foodPercent + " %" + " used of " + foodRatio + ". Status:");
                        status_Image_food.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_food.setText(foodPercent + " %" + " used of " + foodRatio + ". Status:");
                        status_Image_food.setImageResource(R.drawable.red);

                    }

                    float housePercent = (float) ((houseTotal / houseRatio) * 100.0);
                    housePercent = returnZeroForNaN(housePercent);
                    if (housePercent < 50) {
                        progress_ratio_house.setText(housePercent + " %" + " used of " + houseRatio + ". Status:");
                        status_Image_house.setImageResource(R.drawable.green);
                    } else if (housePercent >= 50 && housePercent < 100) {
                        progress_ratio_house.setText(housePercent + " %" + " used of " + houseRatio + ". Status:");
                        status_Image_house.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_house.setText(housePercent + " %" + " used of " + houseRatio + ". Status:");
                        status_Image_house.setImageResource(R.drawable.red);

                    }

                    float entPercent = (float) ((entTotal / entRatio) * 100.0);
                    entPercent = returnZeroForNaN(entPercent);
                    if (entPercent < 50) {
                        progress_ratio_ent.setText(entPercent + " %" + " used of " + entRatio + ". Status:");
                        status_Image_ent.setImageResource(R.drawable.green);
                    } else if (entPercent >= 50 && entPercent < 100) {
                        progress_ratio_ent.setText(entPercent + " %" + " used of " + entRatio + ". Status:");
                        status_Image_ent.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_ent.setText(entPercent + " %" + " used of " + entRatio + ". Status:");
                        status_Image_ent.setImageResource(R.drawable.red);

                    }

                    float eduPercent = (float) ((eduTotal / eduRatio) * 100.0);
                    eduPercent = returnZeroForNaN(eduPercent);
                    if (eduPercent < 50) {
                        progress_ratio_edu.setText(eduPercent + " %" + " used of " + eduRatio + ". Status:");
                        status_Image_edu.setImageResource(R.drawable.green);
                    } else if (eduPercent >= 50 && eduPercent < 100) {
                        progress_ratio_edu.setText(eduPercent + " %" + " used of " + eduRatio + ". Status:");
                        status_Image_edu.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_edu.setText(eduPercent + " %" + " used of " + eduRatio + ". Status:");
                        status_Image_edu.setImageResource(R.drawable.red);

                    }

                    float chaPercent = (float) ((chaTotal / chaRatio) * 100.0);
                    chaPercent = returnZeroForNaN(chaPercent);
                    if (chaPercent < 50) {
                        progress_ratio_cha.setText(chaPercent + " %" + " used of " + chaRatio + ". Status:");
                        status_Image_cha.setImageResource(R.drawable.green);
                    } else if (chaPercent >= 50 && chaPercent < 100) {
                        progress_ratio_cha.setText(chaPercent + " %" + " used of " + chaRatio + ". Status:");
                        status_Image_cha.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_cha.setText(chaPercent + " %" + " used of " + chaRatio + ". Status:");
                        status_Image_cha.setImageResource(R.drawable.red);

                    }

                    float appPercent = (float) ((appTotal / appRatio) * 100.0);
                    appPercent = returnZeroForNaN(appPercent);
                    if (appPercent < 50) {
                        progress_ratio_app.setText(appPercent + " %" + " used of " + appRatio + ". Status:");
                        status_Image_app.setImageResource(R.drawable.green);
                    } else if (appPercent >= 50 && appPercent < 100) {
                        progress_ratio_app.setText(appPercent + " %" + " used of " + appRatio + ". Status:");
                        status_Image_app.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_app.setText(appPercent + " %" + " used of " + appRatio + ". Status:");
                        status_Image_app.setImageResource(R.drawable.red);

                    }

                    float heaPercent = (float) ((heaTotal / heaRatio) * 100.0);
                    heaPercent = returnZeroForNaN(heaPercent);
                    if (heaPercent < 50) {
                        progress_ratio_hea.setText(heaPercent + " %" + " used of " + heaRatio + ". Status:");
                        status_Image_hea.setImageResource(R.drawable.green);
                    } else if (heaPercent >= 50 && heaPercent < 100) {
                        progress_ratio_hea.setText(heaPercent + " %" + " used of " + heaRatio + ". Status:");
                        status_Image_hea.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_hea.setText(heaPercent + " %" + " used of " + heaRatio + ". Status:");
                        status_Image_hea.setImageResource(R.drawable.red);

                    }


                    float perPercent = (float) ((perTotal / perRatio) * 100.0);
                    perPercent = returnZeroForNaN(perPercent);
                    if (perPercent < 50) {
                        progress_ratio_per.setText(perPercent + " %" + " used of " + perRatio + " . Status:");
                        status_Image_per.setImageResource(R.drawable.green);
                    } else if (perPercent >= 50 && perPercent < 100) {
                        progress_ratio_per.setText(perPercent + " %" + " used of " + perRatio + " . Status:");
                        status_Image_per.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_per.setText(perPercent + " %" + " used of " + perRatio + " . Status:");
                        status_Image_per.setImageResource(R.drawable.red);
                    }


                    float otherPercent = (float) ((othTotal / othRatio) * 100.0);
                    otherPercent = returnZeroForNaN(otherPercent);
                    if (otherPercent < 50) {
                        progress_ratio_oth.setText(otherPercent + " %" + " used of " + othRatio + ". Status:");
                        status_Image_oth.setImageResource(R.drawable.green);
                    } else if (otherPercent >= 50 && otherPercent < 100) {
                        progress_ratio_oth.setText(otherPercent + " %" + " used of " + othRatio + ". Status:");
                        status_Image_oth.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_oth.setText(otherPercent + " %" + " used of " + othRatio + ". Status:");
                        status_Image_oth.setImageResource(R.drawable.red);

                    }

                } else {
                    Toast.makeText(DailyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private float returnZeroForNaN(float number) {
        return Float.isNaN(number) || Float.isInfinite(number) ? (float) 0.0 : number;
    }
}
