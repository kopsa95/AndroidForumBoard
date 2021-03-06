package com.tonyanziano.cs496finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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

import java.util.Date;

public class CreatePost extends AppCompatActivity {
    //entry point URL
    final public String entryPointURL = "http://www.tonyanziano.com:8080/";
    //reference to calling user's username
    public String username;
    //references to post title and content
    public EditText postTitle, postContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //grab username from extras
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        //set up references
        postTitle = (EditText) findViewById(R.id.create_post_title);
        postContent = (EditText) findViewById(R.id.create_post_content);
    }

    //cancels post creation screen (go back to home screen)
    public void cancelPost(View view){
        //create an intent with the calling user's username
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("username", username);
        //call the Home activity
        startActivity(intent);
    }

    //submits the post to the database / forums
    public void createPost(View view){
        //grab necessary info for database entry (author, content, title, date)
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();
        String author = username;
        //get timestamp since epoch
        Long timeStamp = System.currentTimeMillis();
        String ts = timeStamp.toString();
        //make url
        String url = entryPointURL + "posts";

        //create Volley queue
        RequestQueue queue = Volley.newRequestQueue(this);
        //create JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("author", author);
            jsonBody.put("title", title);
            jsonBody.put("content", content);
            jsonBody.put("time", ts);
        } catch(JSONException e) {
            Log.e("Error", "Error creating JSON object: " + e);
        }
        //create request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //grab status and check
                try {
                    int responseStatus = response.getInt("status");
                    if(responseStatus == 1){
                        //good to go
                        Log.d("Debug", "Post successfully submitted.");
                        //create toast and redirect back to home
                        Toast success = Toast.makeText(CreatePost.this, R.string.create_post_success, Toast.LENGTH_SHORT);
                        success.setGravity(Gravity.BOTTOM, 0, 50);
                        success.show();
                        //create an intent with the calling user's username
                        Intent intent = new Intent(CreatePost.this, Home.class);
                        intent.putExtra("username", username);
                        //call the Home activity
                        startActivity(intent);
                    } else {
                        Log.e("Error", "Error submitted post.");
                    }
                } catch (JSONException e) {
                    Log.e("Error", "Error grabbing status from response: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Volley error submitting post: " + error);
            }
        });
        //submit request by adding to queue
        queue.add(request);
    }
}
