package com.budget.personalbudget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseAnalyticActivity extends AppCompatActivity {

    private CardView todayAnalytics, weekCardView, monthCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_analytic);

        todayAnalytics = findViewById(R.id.todayCardView);
        weekCardView = findViewById(R.id.weekCardView);
        monthCardView = findViewById(R.id.monthCardView);

        todayAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAnalyticActivity.this, DailyAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAnalyticActivity.this, WeeklyAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAnalyticActivity.this, MonthlyAnalyticsActivity.class);
                startActivity(intent);
            }
        });
    }
}