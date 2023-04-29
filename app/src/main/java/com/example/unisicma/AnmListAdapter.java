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

public class AnmListAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public AnmListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public void add(@Nullable AnmList object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return super.getCount();
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
        AnmListHolder anmListHolder;
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.anm_list,parent,false);
            anmListHolder = new AnmListHolder();
            anmListHolder.request_text = row.findViewById(R.id.Request_txt);
            anmListHolder.request_id = row.findViewById(R.id.id_value);
            row.setTag(anmListHolder);
        }
        else{
            anmListHolder = (AnmListHolder) row.getTag();

        }

        AnmList anmList = (AnmList) this.getItem(position);
        assert anmList != null;
        anmListHolder.request_text.setText(anmList.getRequest_text());
        anmListHolder.request_id.setText(anmList.getRequest_id());

        return row;

    }

    static class AnmListHolder{
        TextView request_text,request_id;
    }
}
