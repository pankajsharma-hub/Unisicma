package com.example.unisicma;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class DynamicViews {
Context context;

    public DynamicViews(Context context) {
    this.context = context;
    }

    public CheckBox vaccineCheckBox(Context context, String vaccine){
        final ViewGroup.LayoutParams inparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        CheckBox checkBox = new CheckBox(context);
        checkBox.setLayoutParams(inparams);
        checkBox.setText(vaccine);
        checkBox.setMinEms(3);
        checkBox.setChecked(true);
        checkBox.setPadding(3,3,3,3);


        return checkBox;
    }

    public TextView vaccineTextView(Context context,String vaccine){
        final ViewGroup.LayoutParams inparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setLayoutParams(inparams);
        textView.setTextSize(16);
        textView.setBackgroundResource(R.drawable.textviews);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setText(""+vaccine+"");
        textView.setMinEms(3);
        textView.setPadding(3,3,3,3);


        return textView;
    }

    public TextView vaccine_name(Context context,String vName){
        final ViewGroup.LayoutParams inparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setLayoutParams(inparams);
        textView.setTextSize(16);
        textView.setBackgroundResource(R.drawable.textviews);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setText(""+vName+"");
        textView.setMinEms(3);
        textView.setPadding(3,3,3,3);

        return textView;
    }

    public TextView dosage(Context context,String dosage){
        final ViewGroup.LayoutParams inparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setLayoutParams(inparams);
        textView.setTextSize(16);
        textView.setBackgroundResource(R.drawable.textviews);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setText(""+dosage+"");
        textView.setMinEms(3);
        textView.setPadding(3,3,3,3);

        return textView;
    }
}
