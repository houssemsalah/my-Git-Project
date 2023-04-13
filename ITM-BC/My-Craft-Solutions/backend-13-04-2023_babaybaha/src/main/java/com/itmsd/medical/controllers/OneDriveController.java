package com.itmsd.medical.controllers;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

//import org.springframework.http.HttpEntity;
@RestController
@RequestMapping("onedrive")
public class OneDriveController {



    @GetMapping("/token")
    public String getToken() {
        String token = null;

        String url = "https://login.microsoft.com/adf738d9-58c7-4d3b-a4ab-c0ec7b275667/oauth2/token";
        String clientId = "13bbe31d-5b7b-428f-a0bd-b1f3e3c19663";
        String clientSecret = "M2u8Q~aNDhzs9oAPbqX1Jhm5qYVyrKS8OmJDcc~E";
        String resource = "https://graph.microsoft.com";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        try {
            String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret + "&resource=" + resource;
            StringEntity entity = new StringEntity(body);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            CloseableHttpResponse response = httpClient.execute(httpPost);

            try {
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(responseString);
                token = jsonResponse.getString("access_token");
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return token;
    }


  /*  @GetMapping("/getfile")
    public ResponseEntity<ByteArrayResource> getOneDriveFile(@RequestBody String url) {
        String authToken= getToken();





        }*/




}