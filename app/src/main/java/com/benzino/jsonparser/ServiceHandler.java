package com.benzino.jsonparser;

import android.content.Entity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Created by Anas on 14/06/2015.
 */
public class ServiceHandler {
    public static String response = null ;
    public static int GET=1, POST=2, DELETE=3 ;

    public ServiceHandler() {
    }

    public String makingServiceCall(String url, int method){
        return this.makingServiceCall(url, method, null);
    }

    /*
    * method to make a service call
    * @url - url to make the request
    * @method - http request method (GET, POST, DELETE, PUT)
    * */
    public String makingServiceCall(String url, int method, List<NameValuePair> params){
        try {
            /*HTTP Client*/
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            /*Checking http request method type*/
            if(method == POST){
                HttpPost post = new HttpPost(url);
                post.setHeader("Content-Type", "application/json");
                post.setHeader("Accept", "application/json");
                //Adding post params
                if(params!=null) post.setEntity(new UrlEncodedFormEntity(params));

                httpResponse = httpClient.execute(post);

            }
            else if(method == GET){
                //Appending params to url
                if(params!= null){
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
                HttpPost httpPost = new HttpPost(url);

                httpResponse = httpClient.execute(httpGet);

            }else if(method == DELETE){

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return response;
    }
}
