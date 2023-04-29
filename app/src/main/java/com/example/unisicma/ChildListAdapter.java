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

public class ChildListAdapter extends ArrayAdapter {

    List list = new ArrayList();


    public ChildListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public void add(@Nullable ChildList object) {
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
        ChildListAdapter.ChildListHolder childListHolder;
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.child_list,parent,false);
            childListHolder = new ChildListHolder();
            childListHolder.child_name_txt = row.findViewById(R.id.child_name);
            childListHolder.child_id_txt = row.findViewById(R.id.id_value);
            row.setTag(childListHolder);
        }
        else{
            childListHolder = (ChildListHolder) row.getTag();

        }

        ChildList childList = (ChildList) this.getItem(position);

        assert childList != null;
        childListHolder.child_name_txt.setText(childList.getChild_name());
        childListHolder.child_id_txt.setText(childList.getChild_id());

        return row;

    }

    static class ChildListHolder{
        TextView child_name_txt,child_id_txt;
    }
}
