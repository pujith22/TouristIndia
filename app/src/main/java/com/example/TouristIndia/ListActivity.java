package com.example.TouristIndia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent i = getIntent();
        String state = i.getExtras().getString("state");
        String city = i.getExtras().getString("city");
        listview = findViewById(R.id.listplace);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        PyObject pyf = py.getModule("test");
        List<PyObject> obj = pyf.callAttr("main_funct", state, city).asList();
        ArrayAdapter<PyObject> adapter = new ArrayAdapter<PyObject>(this, android.R.layout.simple_list_item_1, obj);
        listview.setAdapter(adapter);

    }

}
