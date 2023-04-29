package com.example.unisicma;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactAdapter extends ArrayAdapter {
    long vaccine_dobDiff, cur_vaccineDiff;
    public static int listSize;

    List list = new ArrayList();

    public ContactAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(Contacts object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        listSize = list.size();
        return listSize;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        row = convertView;
        contactHolder contactholder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.display_layout, parent, false);
            contactholder = new contactHolder();
            contactholder.tx_id = row.findViewById(R.id.child_Id);
            contactholder.tx_name = row.findViewById(R.id.child_name);
            contactholder.tx_facility = row.findViewById(R.id.facility);
            contactholder.tx_mother_id = row.findViewById(R.id.mother_Id);
            contactholder.tx_lastVisit = row.findViewById(R.id.LastVisitDate);


            row.setTag(contactholder);
        } else {
            contactholder = (contactHolder) row.getTag();


            //----------------------Setting TextViews------------------------------------------------------------

        }
        final Contacts contacts = (Contacts) this.getItem(position);
        contactholder.tx_name.setText(contacts.getChild_name());
        contactholder.tx_mother_id.setText(row.getResources().getString(R.string.m_id) +": "+ contacts.getMother_Id());
        contactholder.tx_id.setText(row.getResources().getString(R.string.child_id) +": "+ contacts.getId());
        contactholder.tx_facility.setText(row.getResources().getString(R.string.dob) +": " + contacts.getFacility());
        contactholder.tx_lastVisit.setText(contacts.getLast_date());
//------------------------color assignment --------------------------------------------


        Calendar calendar = Calendar.getInstance();
        String lastVaccine = contacts.getLast_date();
        String dob = contacts.getFacility();

        DateFormat simpledate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String startDate = simpledate.format(calendar.getTime());
        try {
            Date Last_vaccine = simpledate.parse(lastVaccine);
            Date DOB = simpledate.parse(dob);
            Date currDate = simpledate.parse(startDate);

            assert Last_vaccine != null;
            assert DOB != null;
            vaccine_dobDiff = ((Last_vaccine.getTime() - DOB.getTime()) / (1000 * 60 * 60 * 24));
            assert currDate != null;
            cur_vaccineDiff = ((currDate.getTime() - Last_vaccine.getTime()) / (1000 * 60 * 60 * 24));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // vaccine_dofDiff means difference between last given vaccine and date of birth
        // cur_vaccineDiff is the difference between current date and last given vaccine
        if (((vaccine_dobDiff == 0) && (cur_vaccineDiff >= 102)) || ((vaccine_dobDiff >= 40) && (cur_vaccineDiff >= 88) && (vaccine_dobDiff < 70)) || ((vaccine_dobDiff >= 70) && (cur_vaccineDiff >= 88) && (vaccine_dobDiff < 98)) || ((vaccine_dobDiff >= 98) && (cur_vaccineDiff >= 232) && (vaccine_dobDiff < 270)) || ((vaccine_dobDiff >= 270) && (cur_vaccineDiff >= 270) && (vaccine_dobDiff < 480))
                || ((vaccine_dobDiff >= 480) && (cur_vaccineDiff >= 300) && (vaccine_dobDiff < 720)) || ((vaccine_dobDiff >= 720) && (cur_vaccineDiff >= 90))) {
            row.setBackgroundColor(Color.rgb(255, 102, 102));//red
        } else if (((vaccine_dobDiff == 0) && (cur_vaccineDiff >= 49)) || ((vaccine_dobDiff >= 40) && (cur_vaccineDiff >= 35) && (vaccine_dobDiff < 70)) || ((vaccine_dobDiff >= 70) && (cur_vaccineDiff >=35) && (vaccine_dobDiff <98)) || ((vaccine_dobDiff >= 98) && (cur_vaccineDiff >= 179) && (vaccine_dobDiff < 270)) || ((vaccine_dobDiff >= 270) && (cur_vaccineDiff > 217) && (vaccine_dobDiff < 480))
                || ((vaccine_dobDiff >= 480) && (cur_vaccineDiff >= 247)) || ((vaccine_dobDiff >= 720) && (cur_vaccineDiff > 34))) {
            row.setBackgroundColor(Color.rgb(255, 255, 204));//yellow
            //60
        } else {
            row.setBackgroundColor(Color.rgb(255, 102, 102));//red
           // row.setBackgroundColor(Color.rgb(144, 238, 144));//green
        }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------
        return row;
    }

    static class contactHolder {
        TextView tx_id, tx_name, tx_facility, tx_mother_id, tx_lastVisit;

    }
}
