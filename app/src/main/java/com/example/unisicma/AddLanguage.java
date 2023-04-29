package com.example.unisicma;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddLanguage extends AppCompatActivity {

    EditText ChildName_hi, FatherName_hi, MotherName_hi, ChildName_tm, FatherName_tm, MotherName_tm;
    SQLiteInsert sqLiteInsert;
    SQLite_db sqLite_db;
    TextView Child_id, Child_name, Mother_name, Father_name;
    String child_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);

        sqLite_db = SQLite_db.getInstance(getApplicationContext());

        ChildName_hi = findViewById(R.id.childName_hi);
        FatherName_hi = findViewById(R.id.fatherName_hi);
        MotherName_hi = findViewById(R.id.motherName_hi);

        ChildName_tm = findViewById(R.id.childName_tm);
        FatherName_tm = findViewById(R.id.fatherName_tm);
        MotherName_tm = findViewById(R.id.motherName_tm);
        Child_id = findViewById(R.id.child_id);
        Child_name = findViewById(R.id.child_name);
        Mother_name = findViewById(R.id.mother_name);
        Father_name = findViewById(R.id.father_name);

        sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        child_id = bundle.getString("child_id");
        Child_id.append(child_id);
        Cursor cursor = sqLite_db.DueDetails(child_id);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String child_name = cursor.getString(8);
            String mother_name = cursor.getString(1);
            String father_name = cursor.getString(4);
            Child_name.append(child_name);
            Mother_name.append(mother_name);
            Father_name.append(father_name);

        }

    }

    public void InsertColumns(View view) {

        final String C_hi = ChildName_hi.getText().toString();
        final String F_hi = FatherName_hi.getText().toString();
        final String M_hi = MotherName_hi.getText().toString();
        final String C_tm = ChildName_tm.getText().toString();
        final String F_tm = FatherName_tm.getText().toString();
        final String M_tm = MotherName_tm.getText().toString();

        if (C_hi.isEmpty() || F_hi.isEmpty() || M_hi.isEmpty() ||
                C_tm.isEmpty() || F_tm.isEmpty() || M_tm.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddLanguage.this);
            builder.setMessage("Some Values are Empty. Are you sure to insert.?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (sqLiteInsert.LanguageInteface(child_id, C_hi, M_hi, F_hi, C_tm, F_tm, M_tm)) {
                        Toast.makeText(getApplicationContext(), "Inserted Successfully.!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Inserting", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            androidx.appcompat.app.AlertDialog mDialog = builder.create();
            mDialog.show();
        } else {
            if (sqLiteInsert.LanguageInteface(child_id, C_hi, M_hi, F_hi, C_tm, F_tm, M_tm)) {
                Toast.makeText(getApplicationContext(), "Inserted Successfully.!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error while Inserting", Toast.LENGTH_LONG).show();
            }
        }
    }
}
