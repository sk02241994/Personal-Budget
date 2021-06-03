package com.budget.personalbudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;

    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> dataList;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expenseRef, personalRef;

    private Toolbar toolbar;
    private Button search;
    private TextView historyTotalAmountSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar = findViewById(R.id.my_feed_toolbar);

        search = findViewById(R.id.search);
        historyTotalAmountSpent = findViewById(R.id.historyTotalAmountSpent);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recycler_view_id_feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        dataList = new ArrayList<>();
        todayItemsAdapter = new TodayItemsAdapter(HistoryActivity.this, dataList);
        recyclerView.setAdapter(todayItemsAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this,
                HistoryActivity.this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int months = month + 1;
        String date = dayOfMonth + "-" + "0" + months + "-" + year;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataList.add(dataSnapshot.getValue(Data.class));
                }
                todayItemsAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                int totalAmount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                }

                if (totalAmount > 0) {
                    historyTotalAmountSpent.setVisibility(View.VISIBLE);
                    historyTotalAmountSpent.setText("This day you have spent: Rs." + totalAmount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}