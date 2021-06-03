package com.budget.personalbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private CardView budgetCardView, todayCardView, weekCardView, monthCardView, analyticsCardView, historyCardView;

    private TextView weekSpendingTv, budgetSpendingTv, todaySpendingTv, remainingBudgetTv, monthSpendingTv;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef, expenseRef, personalRef;
    private String userId = "";

    private int totalAmountMonth = 0;
    private int totalAmountBudget = 0;
    private int totalAmountBudgetB = 0;
    private int totalAmountBudgetC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        budgetCardView = findViewById(R.id.budgetCardView);
        todayCardView = findViewById(R.id.todayCardView);
        weekCardView = findViewById(R.id.weekCardView);
        monthCardView = findViewById(R.id.monthCardView);
        analyticsCardView = findViewById(R.id.analyticsCardView);
        historyCardView = findViewById(R.id.historyCardView);

        weekSpendingTv = findViewById(R.id.weekSpendingTv);
        budgetSpendingTv = findViewById(R.id.budgetTv);
        todaySpendingTv = findViewById(R.id.todaySpendingTv);
        remainingBudgetTv = findViewById(R.id.remainingBudgetTv);
        monthSpendingTv = findViewById(R.id.monthSpendingTv);

        mAuth = FirebaseAuth.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("budget").child(userId);
        expenseRef = FirebaseDatabase.getInstance().getReference("expenses").child(userId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(userId);


        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeekSpendingActivity.class);
                intent.putExtra("type", "week");
                startActivity(intent);
            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeekSpendingActivity.class);
                intent.putExtra("type", "month");
                startActivity(intent);
            }
        });

        analyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseAnalyticActivity.class);
                startActivity(intent);
            }
        });

        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) snap.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetB += pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetB;
                    personalRef.child("budget").setValue(totalAmountBudgetC);

                } else {
                    personalRef.child("budget").setValue(0);
                    Toast.makeText(MainActivity.this, "Please set a budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getBudgetAmount();
        getTotalSpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        getSavings();
    }

    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) snap.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudget += pTotal;
                    }
                } else {
                    totalAmountBudget = 0;
                }
                budgetSpendingTv.setText("Rs. " + String.valueOf(totalAmountBudget));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalSpentAmount() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(userId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                }
                todaySpendingTv.setText("Rs. " + totalAmount);
                personalRef.child("today").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeekSpentAmount() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(userId);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                }
                weekSpendingTv.setText("Rs. " + totalAmount);
                personalRef.child("week").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthSpentAmount() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(userId);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                }
                monthSpendingTv.setText("Rs. " + totalAmount);
                personalRef.child("month").setValue(totalAmount);
                totalAmountMonth = totalAmount;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSavings() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int budget;
                    if (snapshot.hasChild("budget")) {
                        budget = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    } else {
                        budget = 0;
                    }

                    int monthSpending;
                    if (snapshot.hasChild("month")) {
                        monthSpending = Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    } else {
                        monthSpending = 0;
                    }

                    remainingBudgetTv.setText("Rs. " + (budget - monthSpending));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.account) {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}