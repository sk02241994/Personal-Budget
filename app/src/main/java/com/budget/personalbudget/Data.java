package com.budget.personalbudget;

public class Data {
    String item, date, id, itemNDay, itemNWeek, itemNMonth;
    String notes;
    int amount, month, week;

    public Data() {
    }

    public Data(String item, String date, String id, String itemNDay, String itemNWeek, String itemNMonth, String notes, int amount, int month, int week) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.itemNDay = itemNDay;
        this.itemNWeek = itemNWeek;
        this.itemNMonth = itemNMonth;
        this.notes = notes;
        this.amount = amount;
        this.month = month;
        this.week = week;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemNDay() {
        return itemNDay;
    }

    public void setItemNDay(String itemNDay) {
        this.itemNDay = itemNDay;
    }

    public String getItemNWeek() {
        return itemNWeek;
    }

    public void setItemNWeek(String itemNWeek) {
        this.itemNWeek = itemNWeek;
    }

    public String getItemNMonth() {
        return itemNMonth;
    }

    public void setItemNMonth(String itemNMonth) {
        this.itemNMonth = itemNMonth;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
