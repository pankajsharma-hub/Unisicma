package com.example.unisicma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ExListAdapter extends BaseExpandableListAdapter {

    Context context;
    List<String> facility_list;

    public ExListAdapter(Context context, List<String> facility_list, Map<String, List<String>> facility_details) {
        this.context = context;
        this.facility_list = facility_list;
        this.facility_details = facility_details;
    }

    Map<String, List<String>> facility_details;


    @Override
    public int getGroupCount() {
        return facility_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return facility_details.get(facility_list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return facility_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return facility_details.get(facility_list.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String topic = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_list,null);
        }
        TextView txt_parent = convertView.findViewById(R.id.ex_parent);
        txt_parent.setText(topic);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String fac_details = (String) getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ex_childlist,null);
        }
        TextView txt_child = convertView.findViewById(R.id.ex_child);
        txt_child.setText(fac_details);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
