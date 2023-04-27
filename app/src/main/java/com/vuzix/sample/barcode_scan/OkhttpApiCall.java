package com.vuzix.sample.barcode_scan;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpApiCall {
    final OkHttpClient client = new OkHttpClient();

    void init(){
        System.out.println("### initializing ApiCaller ###");
    }

    private String run(final String inputUrl, final String inputJsonBody, final String inputHeader) throws IOException {
        Thread thread = new Thread(new Runnable() {
            private String url, jsonBody, header;
            private OkHttpClient webClient = client;
            {
                this.url = inputUrl;
                this.jsonBody = inputJsonBody;
                this.header = inputHeader;
            }
            @Override
            public void run() {
                System.out.println("\t###\tAttempting API call: "+url+"\t###");
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonBody);
                Request request = new Request.Builder()
                        .url(url)
                        .put(body)  //PUT
                        .addHeader("hue-application-key",header)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String result = response.body().string();
                    System.out.println("\t###\tAPI call result: "+result+"\t###");
//                    return result;
                }
                catch (IOException e){
                    System.out.println("\t###\tAPI call FAILED\t###");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        return "thread created";
    }

    public void turnOn(LampInfo lamp) throws IOException {
        if(!lamp.isComplete) return;
        String url = "https://"+lamp.bridgeAddress+"/clip/v2/resource/"+lamp.rtype+"/"+lamp.rid;
        String response = this.run(url, "{\"on\":{\"on\":true}}", lamp.hueAppKey);
    }

    public void turnOff(LampInfo lamp) throws IOException {
        if(!lamp.isComplete) return;
        String url = "https://"+lamp.bridgeAddress+"/clip/v2/resource/"+lamp.rtype+"/"+lamp.rid;
        String response = this.run(url, "{\"on\":{\"on\":false}}", lamp.hueAppKey);
    }


}
