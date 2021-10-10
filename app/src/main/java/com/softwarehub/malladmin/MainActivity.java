package com.softwarehub.malladmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private AutoCompleteTextView etTimeSlot;
    private String date, timeslot;
    private Button btnSearch;
    private RecyclerView rvMain;
    private SlotBookingAdapter slotAdapter;

    private String[] timeSlotList = {"10:00AM to 11:00AM",
            "11:00AM to 12:00PM",
            "12:00PM to 01:00PM",
            "01:00PM to 02:00PM",
            "02:00PM to 03:00PM",
            "03:00PM to 04:00PM",
            "04:00PM to 05:00PM",
            "05:00PM to 06:00PM",
            "06:00PM to 07:00PM"};

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;

    private List<SlotBookingModel> bookingModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        dialog = new ProgressDialog(MainActivity.this);

        etDate = findViewById(R.id.et_date_schedule);
        etTimeSlot = findViewById(R.id.dropdown_menu_time);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, timeSlotList);
        etTimeSlot.setThreshold(1);
        etTimeSlot.setAdapter(adapter);
        btnSearch = findViewById(R.id.btn_search);
        rvMain = findViewById(R.id.rv_main);
        bookingModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvMain.setLayoutManager(linearLayoutManager);
        slotAdapter = new SlotBookingAdapter(bookingModelList,MainActivity.this);
        rvMain.setAdapter(slotAdapter);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                etDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        etTimeSlot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeslot = timeSlotList[position];
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = etDate.getText().toString();
                if (!TextUtils.isEmpty(date)) {
                    if (!TextUtils.isEmpty(timeslot)) {
                        getData(date,timeslot);
                    } else {
                        etTimeSlot.setError("Please select timeslot");
                    }
                } else {
                    etDate.setError("Please select date");
                }
            }
        });
    }

    private void getData(String date,String timeslot){
        dialog.setMessage("Fetching data...");
        dialog.show();
        bookingModelList.clear();
        databaseReference.child("SCHEDULE").child(date).child(timeslot).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    String value = dataSnapshot1.getValue().toString();
                    String key = dataSnapshot1.getKey().toString();
                    SlotBookingModel model = new SlotBookingModel(key,value);
                    bookingModelList.add(model);
                    //Log.d("SLOT_DATA",key+ " : " +value );
                }
                getEmailMobile();
                //dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MEMBER", error.getMessage());
                dialog.dismiss();
            }
        });
    }
    private void getEmailMobile(){
        for (int i = 0; i < bookingModelList.size(); i++){
            int finalI = i;
            firebaseFirestore.collection("USERS")
                    .document(bookingModelList.get(i).getUserId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                bookingModelList.get(finalI).setEmail(task.getResult().getString("email"));
                                bookingModelList.get(finalI).setMobileNo(task.getResult().getString("mobile_no"));

//                                Log.d("COMPLETE_DATA",String.valueOf(bookingModelList.get(finalI).getEmail()+
//                                        bookingModelList.get(finalI).getMobileNo()+
//                                        bookingModelList.get(finalI).getNoMember()+
//                                        bookingModelList.get(finalI).getUserId()));
                                slotAdapter.notifyDataSetChanged();
                            }else {
                                dialog.dismiss();
                            }
                        }
                    });
        }
    }
}