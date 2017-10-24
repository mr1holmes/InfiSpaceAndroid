package com.example.infispace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.infispace.util.AccountsUtil;
import com.example.infispace.util.LogUtil;
import com.example.infispace.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class AddStory extends AppCompatActivity {

    private AppCompatButton mPostButton;
    private EditText mTitleEt;
    private EditText mUrlEt;
    private ProgressDialog mProgressDialog;
    private final String TAG = LogUtil.makeLogTag(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Sending your story to server...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);

        mTitleEt = (EditText) findViewById(R.id.story_title_et);
        mUrlEt = (EditText) findViewById(R.id.story_url_et);

        mPostButton = (AppCompatButton) findViewById(R.id.post_button);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitleEt.getText().toString();
                String url = mUrlEt.getText().toString();
                if (title.length() != 0 && url.length() != 0) {
                    JSONObject payload = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("type", "url");
                        data.put("shared_by_id", AccountsUtil.getUserId(AddStory.this));
                        data.put("title", mTitleEt.getText());
                        data.put("url", mUrlEt.getText());
                        data.put("timestamp", AccountsUtil.getTimeStamp());

                        payload.put("data", data);


                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                AccountsUtil.getServerUrl(AddStory.this) + "/story", payload, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LogUtil.LOGD(TAG, "Successfully sent story to server");
                                mProgressDialog.hide();
                                Intent intent=new Intent();
                                setResult(2000,intent);
                                AddStory.this.finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LogUtil.LOGD(TAG, "Something went wrong while adding friends on server");
                                mProgressDialog.hide();
                            }
                        });

                        VolleySingleton.getInstance(AddStory.this).addToRequestQueue(jsonObjectRequest);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        mProgressDialog.hide();
                    }
                } else {
                    if (title.length() == 0) {
                        mTitleEt.setError("Cannot be Empty");
                    } else if (url.length() == 0) {
                        mUrlEt.setError("Cannot be Empty");
                    }
                }
            }
        });
    }
}
