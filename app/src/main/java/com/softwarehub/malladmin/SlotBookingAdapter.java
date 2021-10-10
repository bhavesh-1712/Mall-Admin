package com.softwarehub.malladmin;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlotBookingAdapter extends RecyclerView.Adapter<SlotBookingAdapter.ViewHolder>{
    private List<SlotBookingModel> slotBookingModelList;
    private Activity activity;

    public SlotBookingAdapter(List<SlotBookingModel> slotBookingModelList, Activity activity) {
        this.slotBookingModelList = slotBookingModelList;
        this.activity = activity;
    }
    @NonNull
    @NotNull
    @Override
    public SlotBookingAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_booking_layout, parent, false);

        return new SlotBookingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SlotBookingAdapter.ViewHolder holder, int position) {
        String mobileNo = slotBookingModelList.get(position).getMobileNo();
        String email = slotBookingModelList.get(position).getEmail();
        String uid = slotBookingModelList.get(position).getUserId();
        String noMember = slotBookingModelList.get(position).getNoMember();

        holder.setMember(mobileNo,email,uid,noMember);
    }

    @Override
    public int getItemCount() {
        return slotBookingModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmail,tvMobileNo;
        private RecyclerView rvSub;
        private List<MemberModel> memberModelList;
        private MemberAdapter adapter;
        private FirebaseDatabase firebaseDatabase;
        private DatabaseReference userDatabaseRef;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            firebaseDatabase = FirebaseDatabase.getInstance();
            userDatabaseRef = firebaseDatabase.getReference("USERS");

            tvEmail = itemView.findViewById(R.id.tv_email);
            tvMobileNo = itemView.findViewById(R.id.tv_mobile_number);
            rvSub = itemView.findViewById(R.id.rv_sub);

            memberModelList = new ArrayList<>();
            adapter = new MemberAdapter(memberModelList,activity);
        }

        private void setMember(String mobileNo,String email,String uid,String noMember){
            tvEmail.setText(email);
            tvMobileNo.setText(mobileNo);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvSub.setLayoutManager(linearLayoutManager);
            rvSub.setAdapter(adapter);
            //Log.e("METHOD_CALL", String.valueOf(slotBookingModelList.size()));
            fetchData(uid);
        }

        private void fetchData(String uid){
            memberModelList.clear();
            userDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        memberModelList.add(dataSnapshot1.getValue(MemberModel.class));
                    }
                    Log.e("METHOD_CALL", String.valueOf(memberModelList.size()));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MEMBER", error.getMessage());
                    //dialog.dismiss();
                }
            });
        }
    }

}
