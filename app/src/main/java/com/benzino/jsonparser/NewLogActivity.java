package com.benzino.jsonparser;

import android.app.ProgressDialog;
import android.app.Service;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;


public class NewLogActivity extends ActionBarActivity {

    private static String url = "http://myworklog.herokuapp.com/logs.json";

    private static int id = 3000 ;
    private static String title   ;
    private static String body ;
    private static String created_at = "2015-02-16T13:38:23.036Z" ;
    private static String updated_at = "2015-02-16T13:38:23.036Z";

    private EditText editTitle ;
    private EditText editBody ;

    private Button saveLog ;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_log);

        /*New title text */
        editTitle = (EditText)findViewById(R.id.edit_title);

        /*New body text*/
        editBody = (EditText)findViewById(R.id.edit_body);

        /*Save Log button*/
        Button saveLog = (Button) findViewById(R.id.button_save_log);

        /*Save Log button listener*/

        saveLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editTitle.getText().toString();
                body = editBody.getText().toString();

                if(title.length() == 0 || body.length() == 0){
                    //Display a toast if the input fields are empty
                    Toast.makeText(NewLogActivity.this, "Please fill up all the fields",Toast.LENGTH_LONG).show();
                }else{
                    /*Calling the AsyncTask to post the JSON data*/
                    new NewLogTask().execute();

                }
            }
        });
    }

    private class NewLogTask extends AsyncTask<String, String, JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*Show the Progress Dialog*/
            progressDialog = new ProgressDialog(NewLogActivity.this);
            progressDialog.setMessage(this.getStatus().toString());
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject postparams = new JSONObject();

            try {
                postparams.put("id", id);
                postparams.put("title", title);
                postparams.put("body", body);
                postparams.put("created_at", created_at);
                postparams.put("updated_at", updated_at);

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Accept", "application/json");
                StringEntity entity = new StringEntity(postparams.toString());
                httpPost.setEntity(entity);

                HttpResponse httpResponse = httpClient.execute(httpPost);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            /*Dismiss the progress Dialog*/
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            //Display a toast if the input fields are empty
            Toast.makeText(NewLogActivity.this, this.getStatus().toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_log, menu);
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
