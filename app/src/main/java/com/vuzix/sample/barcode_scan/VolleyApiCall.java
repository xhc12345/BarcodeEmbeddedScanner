package com.vuzix.sample.barcode_scan;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyApiCall {
    public static RequestQueue requestQueue = null;

    public static void init(Context context){
        requestQueue = Volley.newRequestQueue(context);
        /* ### REMOVE FOR PRODUCTION ### */
        NukeSSLCerts.nuke();    // ignore all SSL certification errors
    }

    /**
     * send GET request to target
     * @param output UI element to be updated
     * @param url target url
     * @param inputLamp lamp information
     */
    public static void get(final TextView output, String url, final LampInfo inputLamp) {
        // Request a JSON response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override public void onResponse(JSONObject response) {
                    System.out.println("\t###\tAPI call result: "+response+"\t###");
                    output.setText(response.toString());
                    // checks the information of the lamp
                    try {
                        JSONArray data = response.getJSONArray("data");
                        boolean found = false;
                        for(int i=0; i<data.length(); i++){
                            JSONObject lamp = data.getJSONObject(i);
                            if(lamp.getString("id").equals(inputLamp.rid)){
                                // if here then the target lamp has been found
                                found = true;
                                JSONObject on = lamp.getJSONObject("on");
                                boolean lampIsOn = on.getBoolean("on");
                                System.out.println("lamp is "+(lampIsOn?"on":"off"));
                                //TODO: give UI option to turn lamp on/off depend on current state

                                break;
                            }
                        }
                        if(!found) throw new Exception("target lamp not found");
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override public void onErrorResponse(VolleyError error) {
                    System.out.println("\t###\tAPI call FAILED\t###");
                    error.printStackTrace();
                    output.setText("API CALL FAILED");
                }
            }
        ){
            @Override public Map<String, String> getHeaders() {
                Map<String, String>  Headers = new HashMap<String, String>();
                Headers.put("hue-application-key", inputLamp.hueAppKey);
                return Headers;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    /**
     * send PUT request to target
     * @param output UI element to be updated
     * @param url target url
     * @param inputJsonBody body information
     * @param inputHeader header information
     */
    public static void put(final TextView output, String url, final String inputJsonBody, final String inputHeader) {
        // Request a JSON response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
            new Response.Listener<JSONObject>() {
                @Override public void onResponse(JSONObject response) {
                    System.out.println("\t###\tAPI call result: "+response+"\t###");
                    output.setText(response.toString());
                }
            },
            new Response.ErrorListener() {
                @Override public void onErrorResponse(VolleyError error) {
                    System.out.println("\t###\tAPI call FAILED\t###");
                    error.printStackTrace();
                    output.setText("API CALL FAILED");
                }
            }
        ){
            @Override public Map<String, String> getHeaders() {
                Map<String, String>  Headers = new HashMap<String, String>();
                Headers.put("hue-application-key", inputHeader);
                return Headers;
            }

            @Override public byte[] getBody() {
                String httpPostBody = inputJsonBody;
                return httpPostBody.getBytes();
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    public static void check(final TextView output, LampInfo lamp){
        if(!lamp.isComplete) return;
        String url = "https://"+lamp.bridgeAddress+"/clip/v2/resource/"+lamp.rtype+"/"+lamp.rid;
        get(output, url, lamp);
    }

    public static void turnOn(final TextView output, LampInfo lamp){
        if(!lamp.isComplete) return;
        String url = "https://"+lamp.bridgeAddress+"/clip/v2/resource/"+lamp.rtype+"/"+lamp.rid;
        put(output, url, "{\"on\":{\"on\":true}}", lamp.hueAppKey);
    }

    public static void turnOff(final TextView output, LampInfo lamp){
        if(!lamp.isComplete) return;
        String url = "https://"+lamp.bridgeAddress+"/clip/v2/resource/"+lamp.rtype+"/"+lamp.rid;
        put(output, url, "{\"on\":{\"on\":false}}", lamp.hueAppKey);
    }
}
