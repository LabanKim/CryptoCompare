package com.example.kim.cryptocompare;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class CreateCardActivity extends AppCompatActivity {

    private Button mCreateBtn;
    private Spinner mCurSpinner;
    private EditText mNameInput;
    private TextInputLayout mNameLayout;
    private ArrayList<String> currencyList;

    private String mSelectedItem = null;

    private String [] mCurrencies = {"USD","EUR","KES", "GBP","ZAR","BSD","CHF","COP","CLP","DZD","EGP","ILS","INR","JPY","KPW","KRW","LYD","LRD","NGN","TZS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        mCreateBtn = (Button) findViewById(R.id.create_btn);
        mCurSpinner = (Spinner) findViewById(R.id.currency_spinner);
        mNameInput = (EditText) findViewById(R.id.titleInput);
        mNameLayout = (TextInputLayout) findViewById(R.id.title_layout);

        mCurSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mNameInput.getText())){

                    ArrayHolder.selectedCurrencyList.add(new CustomArray(mNameInput.getText().toString(), mSelectedItem));
                    Toast.makeText(CreateCardActivity.this, "Card Created Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateCardActivity.this, MainActivity.class));

                }else {
                    mNameLayout.setError("Currency title cannot be empty");
                }

            }
        });

        currencyList = new ArrayList<>();

        for (int i = 0; i < mCurrencies.length; i++){

            String title = mCurrencies[i];
            currencyList.add(title);

        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CreateCardActivity.this, android.R.layout.simple_spinner_item, currencyList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        mCurSpinner.setAdapter(myAdapter);

    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            mSelectedItem = currencyList.get(pos);

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

}
