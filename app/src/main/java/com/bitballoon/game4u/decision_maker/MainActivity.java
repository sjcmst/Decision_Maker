package com.bitballoon.game4u.decision_maker;


import android.util.Log;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //String TAG = "LOG";

    Locale chi = new Locale("zh","HK");
    Locale eng = new Locale("en");
    Locale currentlocale;

    Spinner spinner_mode;
    ArrayAdapter<CharSequence> adapter_mode;
    TextView display;

    TextView from;
    TextView one;
    TextView to;
    Spinner spinner_range;
    ArrayAdapter<Integer> adapter_range;


    EditText initvalue;

    EditText finalvalue;

    CheckBox checkbox_range;
    Button run;

    Button stop;
    TextView orderprogress;
    Button showlist;


    int colour=0;
    int[] selected_storage = new int[1000];
    boolean[] selected_history = new boolean[1000];
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent start = getIntent();
        if(start.getExtras()!=null){
            if(start.getExtras().getString("com.bitballoon.game4u.decision_maker_Language", Locale.getDefault().getLanguage()).equals("zh-HK")) {
                currentlocale = chi;
            }else{
                currentlocale = eng;
            }
        }else{
            currentlocale = eng;
        }

        Configuration config = new Configuration();
        config.locale=currentlocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_main);

        if ( getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.AndroidManifestXML_title);
        }

        spinner_mode = findViewById(R.id.spinner_mode);
        adapter_mode = ArrayAdapter.createFromResource(this, R.array.MainActivityJAVA_spinner_mode, android.R.layout.simple_spinner_item);
        adapter_mode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_mode.setAdapter(adapter_mode);
        spinner_mode.setSelection(0);
        display = findViewById(R.id.display);

        from = findViewById(R.id.from);
        one = findViewById(R.id.one);
        to = findViewById(R.id.to);
        spinner_range = findViewById(R.id.spinner_range);
        Integer[] array = new Integer[]{2,3,4,5,6,7,8,9,10};
        adapter_range = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);
        spinner_range.setAdapter(adapter_range);

        initvalue = findViewById(R.id.initvalue);
        finalvalue = findViewById(R.id.finalvalue);

        checkbox_range = findViewById(R.id.checkbox_range);
        run = findViewById(R.id.run);

        stop = findViewById(R.id.stop);
        orderprogress = findViewById(R.id.orderprogress);
        showlist = findViewById(R.id.showlist);

        run.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                display.setText(Integer.toString(run()));
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                reset();
            }
        });
        showlist.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                list();
            }
        });
        spinner_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    showlist.setVisibility(View.VISIBLE);
                }else{
                    showlist.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        checkbox_range.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if(checkbox_range.isChecked()){
                    spinner_range.setVisibility(View.INVISIBLE);
                    one.setVisibility(View.INVISIBLE);

                    initvalue.setVisibility(View.VISIBLE);
                    finalvalue.setVisibility(View.VISIBLE);
                }else{
                    spinner_range.setVisibility(View.VISIBLE);
                    one.setVisibility(View.VISIBLE);

                    initvalue.setVisibility(View.INVISIBLE);
                    finalvalue.setVisibility(View.INVISIBLE);
                }
            }

        });


    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater minf = getMenuInflater();
        minf.inflate(R.menu.overflow_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.language){
            languagechange();
        }
        return true;
    }
    private void list() {
        StringBuffer bstr = new StringBuffer("");
        int initnumber;
        int finalnumber;
        if(checkbox_range.isChecked()) {
            if(checkdata()) return;
            initnumber = Integer.parseInt(initvalue.getText().toString());
            finalnumber = Integer.parseInt(finalvalue.getText().toString());
        }else{
            initnumber=1;
            finalnumber = (int) spinner_range.getSelectedItem();
        }
        if(orderprogress.getText().equals("0/0")){
            createorder(initnumber,finalnumber);
            orderprogress.setText("0/" + Integer.toString(finalnumber-initnumber+1));
            count=-1;
            // -1: (used in run() function)it will display the data in index 0, but createorder() function will not be called
        }
        bstr.append(selected_storage[0]);
        for(int i=1; i < finalnumber-initnumber+1; i++) {
            bstr.append(", ");
            bstr.append(selected_storage[i]);

        }
        final String result = bstr.toString();
        AlertDialog.Builder diag = new AlertDialog.Builder(this);
        diag.setTitle(R.string.MainActivityJAVA_dialog1_title);
        diag.setMessage(result);
        diag.setPositiveButton(R.string.MainActivityJAVA_dialog1_posbtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        diag.setNegativeButton(R.string.MainActivityJAVA_dialog1_negbtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Decision_Maker",result);
                cb.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, R.string.MainActivityJAVA_toast5,Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        diag.show();
    }


    private int run() {
        int initnumber;
        int finalnumber;
        int selected;
        if(checkbox_range.isChecked()){
            if(checkdata()) return Integer.parseInt(display.getText().toString());
            initnumber = Integer.parseInt(initvalue.getText().toString());
            finalnumber = Integer.parseInt(finalvalue.getText().toString());
        }else {
            initnumber = 1;
            finalnumber = (int) spinner_range.getSelectedItem();
        }

        if(spinner_mode.getSelectedItemPosition()==1){
            if(count==0) {
                createorder(initnumber,finalnumber);
            }
            if(count==-1) count=0;
            selected = selected_storage[count];
            orderprogress.setText(Integer.toString(count+1) + "/" + Integer.toString(finalnumber-initnumber+1));
            count++;
            if(count>=finalnumber-initnumber+1){
                reset();
            }
        }else{
            selected = (int) (Math.random()*(finalnumber-initnumber+1))+initnumber;
        }

        colour++;
        switch(colour){
            case 1:
                display.setTextColor(Color.RED);
                break;
            case 2:
                display.setTextColor(Color.BLUE);
                break;
            case 3:
                display.setTextColor(Color.rgb(52,171,43));
                break;
            case 4:
                display.setTextColor(Color.BLACK);
                colour=0;
                break;
            default:
                colour=0;
                break;
        }


        return selected;
    }
    private void reset(){
        count=0;

        spinner_mode.setEnabled(true);
        spinner_mode.setClickable(true);
        spinner_range.setEnabled(true);
        spinner_range.setClickable(true);
        stop.setVisibility(View.INVISIBLE);
        orderprogress.setVisibility(View.INVISIBLE);
        orderprogress.setText("0/0");

        //checkbox_order.setClickable(true);
        //checkbox_order.setTextColor(Color.rgb(0,0,0));
        checkbox_range.setClickable(true);
        checkbox_range.setTextColor(Color.rgb(0,0,0));


        one.setTextColor(Color.rgb(0,0,0));

        initvalue.setFocusableInTouchMode(true);
        initvalue.setTextColor(Color.rgb(0,0,0));
        from.setTextColor(Color.rgb(0,0,0));
        finalvalue.setFocusableInTouchMode(true);
        finalvalue.setTextColor(Color.rgb(0,0,0));
        to.setTextColor(Color.rgb(0,0,0));
        return;
    }
    public void createorder(int initnumber, int finalnumber){
        spinner_mode.setEnabled(false);
        spinner_mode.setClickable(false);
        spinner_range.setEnabled(false);
        spinner_range.setClickable(false);
        stop.setVisibility(View.VISIBLE);
        orderprogress.setVisibility(View.VISIBLE);
        checkbox_range.setClickable(false);
        checkbox_range.setTextColor(Color.rgb(120,120,120));
        one.setTextColor(Color.rgb(120,120,120));
        initvalue.setFocusable(false);
        //initvalue.setClickable(false);
        initvalue.setTextColor(Color.rgb(120,120,120));
        from.setTextColor(Color.rgb(120,120,120));
        finalvalue.setFocusable(false);
        //finalvalue.setClickable(false);
        finalvalue.setTextColor(Color.rgb(120,120,120));
        to.setTextColor(Color.rgb(120,120,120));


        for (int i = 0; i < finalnumber-initnumber+1; i++) {
            selected_history[i] = false;
        }
        for (int i = 0; i < finalnumber-initnumber+1; i++) {
            do {
                selected_storage[i] = (int) (Math.random() * (finalnumber - initnumber + 1)) + initnumber;
            } while (selected_history[selected_storage[i]-initnumber]);
            selected_history[selected_storage[i]-initnumber] = true;
        }
    }
    public boolean checkdata(){
        int initnumber;
        int finalnumber;
        try {
            initnumber = Integer.parseInt(initvalue.getText().toString());
            finalnumber = Integer.parseInt(finalvalue.getText().toString());
        }catch(NumberFormatException nfe){
            Toast.makeText(this, R.string.MainActivityJAVA_toast1,Toast.LENGTH_SHORT).show();
            return true;
        }
        if(initnumber>99999||finalnumber>99999) {
            Toast.makeText(this, R.string.MainActivityJAVA_toast2, Toast.LENGTH_SHORT).show();
            return true;
        }
        if(initnumber>finalnumber){
            Toast.makeText(this, R.string.MainActivityJAVA_toast3,Toast.LENGTH_SHORT).show();
            return true;
        }
        if(spinner_mode.getSelectedItemPosition()==1&&(finalnumber-initnumber+1>1000)){
            Toast.makeText(this, R.string.MainActivityJAVA_toast4,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    public void languagechange(){
        if(currentlocale==eng){
            currentlocale=chi;
        }else{
            currentlocale=eng;

        }
        Configuration config = new Configuration();
        config.locale=currentlocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        Intent refresh = new Intent(this,MainActivity.class);
        if(currentlocale==chi) {
            refresh.putExtra("com.bitballoon.game4u.decision_maker_Language", "zh-HK");
        }else{
            refresh.putExtra("com.bitballoon.game4u.decision_maker_Language", "en");
        }
        startActivity(refresh);
    }
}
