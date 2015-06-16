package com.benzino.jsonparser;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/*
*   Created by Anas 15/06/2015
*   ListView to display data from  http://myworklog.herokuapp.com
* */
public class MainActivity extends ListActivity {
    /*Progress dialog to show when loading JSON data*/
    private ProgressDialog progressDialog ;

    /*url to get the json data*/
    private static String url = "http://myworklog.herokuapp.com/logs.json";

    /*JSON Node names*/
    private static String TAG_ID    = "id" ;
    private static String TAG_TITLE = "title" ;
    private static String TAG_BODY  = "body" ;
    private static String TAG_DATE  = "created_at" ;

    /*Logs JSON Array*/
    JSONArray logs = null;

    /*HashMap for ListView*/
    ArrayList<HashMap<String, String>> logsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        logsList = new ArrayList<HashMap<String, String>>();

        ListView listView = getListView();

        Button addLogButton  =(Button) findViewById(R.id.add_log);

        /* Add Log button listener*/
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Starting New log activity*/
                Intent intent = new Intent(getApplicationContext(),
                        NewLogActivity.class);

                startActivity(intent);

            }
        });

        /*List view on item click listener*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Getting values from the selected item */
                String title = ((TextView)view.findViewById(R.id.title))
                                    .getText().toString();
                String body = ((TextView)view.findViewById(R.id.body))
                                    .getText().toString();
                String date = ((TextView)view.findViewById(R.id.date))
                                    .getText().toString();

                /*Starting single log activity*/
                Intent intent = new Intent(getApplicationContext(),
                                        SingleLogActivity.class);

                intent.putExtra(TAG_TITLE, title );
                intent.putExtra(TAG_BODY, body );
                intent.putExtra(TAG_DATE, date );
                startActivity(intent);
            }
        });

        /*Calling the AsyncTask to get the JSON*/
        new GetLogs().execute();
    }

    private class GetLogs extends AsyncTask<Void, Void, Void> {

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*Show the Progress Dialog*/
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading... Be Patient!");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            /*Creating ServiceHandler class instance*/
            ServiceHandler sh = new ServiceHandler();

            /*Making a request to the url and getting a response*/
            String jsonString = sh.makingServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", ">" + jsonString);

            if(jsonString!= null){

                try{
                    /*Getting the json Array */
                    logs = new JSONArray(jsonString);


                    /*Loop through the JSON Array (All logs)*/
                    for(int i= 0; i<logs.length(); i++){
                        JSONObject logsObject = logs.getJSONObject(i);

                        String id    = logsObject.getString(TAG_ID);
                        String title = logsObject.getString(TAG_TITLE);
                        String body  = logsObject.getString(TAG_BODY);
                        String date  = logsObject.getString(TAG_DATE);

                        /*Hashmap for a single log*/
                        HashMap<String, String> log = new HashMap<String, String>();

                        /*Adding each child node to hashmap key => value*/
                        log.put(TAG_ID, id);
                        log.put(TAG_TITLE, title);
                        log.put(TAG_BODY, body);
                        log.put(TAG_DATE, date);

                        /*Adding the log to the LogList*/
                        logsList.add(log);
                    }
                }catch (JSONException ex){
                    ex.printStackTrace();
                }
            }else{
                 /*Hashmap for a single log*/
                HashMap<String, String> log = new HashMap<String, String>();

                        /*Adding each child node to hashmap key => value*/
                log.put(TAG_ID, null);
                log.put(TAG_TITLE, "No data :(");
                log.put(TAG_BODY, "Please check your internet connection...");
                log.put(TAG_DATE, null);
                /*Adding the log to the LogList*/
                logsList.add(log);

                Log.e("ServiceHandler", "Couldn't get any data from the server!");
            }
            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param aVoid The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*Dismiss the progress Dialog*/
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            /*Updating JSON parsed data into listView*/
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, logsList, R.layout.list_item,
                    new String[] {TAG_TITLE, TAG_BODY, TAG_DATE},
                    new int[] {R.id.title, R.id.body, R.id.date});
            setListAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
