package com.budget.personalbudget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemsAdapter extends  RecyclerView.Adapter<TodayItemsAdapter.ViewHolder>{

    private Context mContext;
    private List<Data> myDataList;

    private String note = "";
    private String postKey = "";
    private String item = "";
    private int amount = 0;

    public TodayItemsAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout, parent, false);
        return new TodayItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayItemsAdapter.ViewHolder holder, int position) {

        final Data data = myDataList.get(position);
        holder.item.setText("Item: " + data.getItem());
        holder.amount.setText("Amount: " + data.getAmount());
        holder.date.setText("On: " + data.getDate());
        holder.notes.setText("Note: " + data.getNotes());

        switch (data.getItem()){
            case "Transport":
                holder.imageView.setImageResource(R.drawable.ic_transport);
                break;
            case "Food":
                holder.imageView.setImageResource(R.drawable.ic_food);
                break;
            case "House":
                holder.imageView.setImageResource(R.drawable.ic_house);
                break;
            case "Entertainment":
                holder.imageView.setImageResource(R.drawable.ic_entertainment);
                break;
            case "Education":
                holder.imageView.setImageResource(R.drawable.ic_education);
                break;
            case "Charity":
                holder.imageView.setImageResource(R.drawable.ic_consultancy);
                break;
            case "Apparel":
                holder.imageView.setImageResource(R.drawable.ic_shirt);
                break;
            case "Health":
                holder.imageView.setImageResource(R.drawable.ic_health);
                break;
            case "Personal":
                holder.imageView.setImageResource(R.drawable.ic_personalcare);
                break;
            case "Other":
                holder.imageView.setImageResource(R.drawable.ic_other);
                break;

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postKey = data.getId();
                item = data.getItem();
                amount = data.getAmount();
                note = data.getNotes();
                updateData();
            }
        });
    }

    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(view);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = view.findViewById(R.id.itemName);
        final EditText mAmount = view.findViewById(R.id.amount);
        final EditText mNote = view.findViewById(R.id.note);

        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());
        mNote.setText(note);
        mNote.setSelection(note.length());

        Button delBtn = view.findViewById(R.id.btnDelete);
        Button updateBtn = view.findViewById(R.id.btnUpdate);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(mAmount.getText().toString());
                note = mNote.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);

                String itemNDay = item + date;
                String itemNWeek = item + weeks.getWeeks();
                String itemNMonth = item + months.getMonths();

                Data data = new Data(item, date, postKey, itemNDay, itemNWeek, itemNMonth, note, amount, months.getMonths(), weeks.getWeeks());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(postKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item, amount, date, notes;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            notes = itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
