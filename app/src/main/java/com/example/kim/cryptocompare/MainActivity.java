package com.example.kim.cryptocompare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Currency> currencyList = new ArrayList<>();
    private RecyclerView mMainRecycler;
    private Button mRetryBtn;

    private CurrencyAdapter mAdapter;
    private ProgressDialog mProgresss;

    String jsonURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ArrayHolder.selectedCurrencyList.size()==0){

            startActivity(new Intent(MainActivity.this, CreateCardActivity.class));

        }

        StringBuilder builder = new StringBuilder();
        for (CustomArray curList : ArrayHolder.selectedCurrencyList){

            builder.append(curList.getCurr()+",");

        }

        jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms="+builder.toString();

        mAdapter = new CurrencyAdapter(currencyList);

        new GetCurrencies().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_card) {

            startActivity(new Intent(MainActivity.this, CreateCardActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {


        private List<Currency> currencyList;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView etheriumText, btcCurrency, currText, cardHeader;

            public MyViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                etheriumText = view.findViewById(R.id.eth_text);
                btcCurrency = view.findViewById(R.id.btc_text);
                cardHeader = view.findViewById(R.id.card_header);
                currText = view.findViewById(R.id.card_header2);
            }

            @Override
            public void onClick(View view) {

                final int selectedItemPosition = mMainRecycler.getChildPosition(view);

                final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose conversion type:");
                builder.setCancelable(false);
                final ListView optionList=new ListView(MainActivity.this);
                final String[] optionsArray= new String[]{"Bitcoin","Etherium"};
                final ArrayAdapter<String> adapter=new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_selectable_list_item,optionsArray);
                optionList.setAdapter(adapter);

                builder.setView(optionList);
                builder.setCancelable(true);

                optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String option=adapter.getItem(position);

                        if (option.equals("Bitcoin")){

                            Intent btcIntent=new Intent(MainActivity.this, BitcoinCoversionActivity.class);
                            btcIntent.putExtra("currency",currencyList.get(selectedItemPosition).getTitle() );
                            btcIntent.putExtra("bitcoin",currencyList.get(selectedItemPosition).getBtcValue());
                            startActivity(btcIntent);


                        }else if (option.equals("Etherium")){

                            Intent ethIntent=new Intent(MainActivity.this, EtheriumConversionActivity.class);
                            ethIntent.putExtra("currency",currencyList.get(selectedItemPosition).getTitle() );
                            ethIntent.putExtra("etherium",currencyList.get(selectedItemPosition).getEthValue());
                            startActivity(ethIntent);

                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();

            }
        }


        public CurrencyAdapter(List<Currency> currencyList) {
            this.currencyList = currencyList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.currency_card, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Currency cs = currencyList.get(position);
            holder.etheriumText.setText("ETH: "+cs.getEthValue());
            holder.btcCurrency.setText("BTC: "+cs.getBtcValue());
            holder.cardHeader.setText(ArrayHolder.selectedCurrencyList.get(position).getTitle());
            holder.currText.setText("( "+cs.getTitle()+" )");
        }

        @Override
        public int getItemCount() {
            return currencyList.size();
        }

    }

    private class GetCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgresss = new ProgressDialog(MainActivity.this);
            mProgresss.setCanceledOnTouchOutside(false);
            mProgresss.setMessage("Loading data from JSON...");
            mProgresss.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                try {
                    JSONObject btcObj = new JSONObject(jsonStr).getJSONObject("BTC");
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject("ETH");


                    for (int i =0; i < ArrayHolder.selectedCurrencyList.size(); i++){

                        Double btcVal = btcObj.getDouble(ArrayHolder.selectedCurrencyList.get(i).getCurr());
                        Double ethVal = ethObj.getDouble(ArrayHolder.selectedCurrencyList.get(i).getCurr());
                        Currency sCurrency = new Currency(ArrayHolder.selectedCurrencyList.get(i).getCurr(),btcVal , ethVal);
                        currencyList.add(sCurrency);

                    }

                } catch (final JSONException e) {


                }
                mProgresss.dismiss();

            } else {
                mProgresss.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mAdapter.notifyDataSetChanged();

            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mMainRecycler = (RecyclerView) findViewById(R.id.main_recyclerview);
            mMainRecycler.setItemAnimator(new DefaultItemAnimator());
            mMainRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            mMainRecycler.setAdapter(mAdapter);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CreateCardActivity.class));
                }
            });
            mProgresss.dismiss();

        }
    }

}
