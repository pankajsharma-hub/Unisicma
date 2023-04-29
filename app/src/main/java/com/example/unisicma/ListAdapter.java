package com.example.unisicma;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import java.util.List;

public class ListAdapter extends ArrayAdapter {
    long diffInDays, diff1;

    List list = new ArrayList();

    public ListAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(Contacts object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
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
        contactholder.tx_mother_id.setText("Mother ID:" + contacts.getMother_Id());
        contactholder.tx_id.setText("Child ID:" + contacts.getId());
        contactholder.tx_facility.setText("DOB:" + contacts.getFacility());
        contactholder.tx_lastVisit.setText(contacts.getLast_date());

//----------------------------------------------------------------------------------------------------------------------------------------------------------------
        return row;
    }

    static class contactHolder {
        TextView tx_id, tx_name, tx_facility, tx_mother_id, tx_lastVisit;
    }
}
