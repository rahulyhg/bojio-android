package com.dimorinny.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.Date;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    public String to_be_printed = "";
    public ArrayList<iCalParser> icallist = new ArrayList<iCalParser>();
    public static ArrayList<String> paths = new ArrayList<String>();
    public static ArrayList<String> filenames = new ArrayList<String>();
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    public static final int RESET_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pickButton = (Button) findViewById(R.id.pick_from_activity);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndOpenFilePicker();
            }
        });
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    private void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .withTitle("Select Calendars")
                .start();
    }

    public void resetArray() {
        paths = new ArrayList<String>();
        filenames = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, filenames);
        mainListView = (ListView) findViewById(R.id.mainListView);

        mainListView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        TextView txtView = (TextView)findViewById(R.id.output);
        txtView.setText("");
        Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
    }

    public void generate(){
        Toast.makeText(this, "GUI GUI GUI LALALALALALALALALA", Toast.LENGTH_SHORT).show();
        TextView txtView = (TextView)findViewById(R.id.output);
        txtView.setText(to_be_printed);
    }
//}

//    public void reset() {
//        Button pickButton2 = (Button) findViewById(R.id.reset);
//        pickButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetArray();
//            }
//        });
//                      my beautiful method that i wanted to work
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        Button pickButton = (Button) findViewById(R.id.reset);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetArray();
            }
        });

        Button pickButton2 = (Button) findViewById(R.id.generate);
        pickButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate();
            }
        });

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            String filename = path.substring(path.lastIndexOf("/") + 1);
            String filenameArray[] = filename.split("\\.");
            String extension = filenameArray[filenameArray.length - 1];



            listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, filenames);
            mainListView = (ListView) findViewById(R.id.mainListView);

            if (path != null) {
                if (!extension.equals("ics")) {
                    Toast.makeText(this, "File selected is not an .ics file. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    if (filenames.contains(filename)) {
                        Toast.makeText(this, "File already exists. Please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Path: ", path);
                        Toast.makeText(this, "Picked file: " + filename, Toast.LENGTH_SHORT).show();
                        paths.add(path);
                        filenames.add(filename);
                        // Set the ArrayAdapter as the ListView's adapter.
                        mainListView.setAdapter(listAdapter);
                        Toast.makeText(this, "Array Length: " + paths.size(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, "Array Element1: " + filenames.get(0), Toast.LENGTH_SHORT).show();

                        for(int i =0; i < paths.size(); i++) {
                            File myFile = new File(paths.get(i));
                            try {
                                iCalParser temp = new iCalParser(myFile);
                                icallist.add(temp);
                            } catch (ParseException | FileNotFoundException e) {

                            }
                    }
                        SimpleDateFormat start = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                        SimpleDateFormat end = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                        try {
                            Date startDate = start.parse("20170111T000000Z");
                            Date endDate = end.parse("20170118T110000Z");
                            getFreeSlots getSlots = new getFreeSlots(icallist, startDate, endDate);

                            ArrayList<Date> free = getSlots.FreeSlots();
                            for (int i = 0; i < free.size(); i++) {
                                to_be_printed = to_be_printed + free.get(i) + "\n";
                            }
                            } catch (ParseException e) {

                        }

//                        for (int i = 0; i < getSlots.allEvents.size(); i++) {
//                            eventObject event = getSlots.allEvents.get(i);
//                            to_be_printed = to_be_printed + event.getName() +"\n";
//                            to_be_printed = to_be_printed + event.getType()+ "\n";
//                        }

                }
            }
        }
    }
}
}
