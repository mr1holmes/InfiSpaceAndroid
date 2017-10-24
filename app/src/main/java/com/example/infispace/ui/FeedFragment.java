package com.example.infispace.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.infispace.AddStory;
import com.example.infispace.R;
import com.example.infispace.data.Story;
import com.example.infispace.util.AccountsUtil;
import com.example.infispace.util.LogUtil;
import com.example.infispace.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.app.Activity.RESULT_OK;
import static com.facebook.AccessTokenManager.TAG;

public class FeedFragment extends Fragment {

    private FloatingActionButton mFlotingButton;
    private RecyclerView mFeedList;
    private LinearLayoutManager mLayoutManager;
    private FeedAdapter mAdapter;
    private ArrayList<Story> myDataset;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeLayout;
    private int REQ = 2000;

    private void fetchFeed() {
        swipeLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                AccountsUtil.getServerUrl(getActivity()) + "/story/" + AccountsUtil.getUserId(getActivity()), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.hide();
                swipeLayout.setRefreshing(false);
                LogUtil.LOGV(TAG, "Got stories from server" + response);
                try {
                    JSONArray stories = response.getJSONArray("stories");
                    if (stories.length() > 0) {
                        myDataset.clear();
                    }
                    for (int i = 0; i < stories.length(); i++) {
                        JSONObject storyJson = (JSONObject) stories.get(i);
                        Story story = new Story();
                        story.setSharedByName(storyJson.getString("shared_by_name"));
                        story.setSharedByPicUrl(storyJson.getString("shared_by_pic_url").replaceAll("\'", ""));
                        story.setStoryTitle(storyJson.getString("title"));
                        story.setStoryUrl(storyJson.getString("url"));
                        story.setTimestamp(storyJson.getString("timestamp"));
                        myDataset.add(story);
                    }
                    Collections.sort(myDataset, new Comparator<Story>() {
                        @Override
                        public int compare(Story s1, Story s2) {
                            return s1.getTimestamp().compareTo(s2.getTimestamp());
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Some network problem!!", Toast.LENGTH_SHORT).show();
                mProgressDialog.hide();
                swipeLayout.setRefreshing(false);
                LogUtil.LOGD(TAG, "Something went wrong while fetching stories from server");
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQ) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                fetchFeed();
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "Refreshing feed", Toast.LENGTH_SHORT).show();
                fetchFeed();
            }
        });


        mFlotingButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFeedList = (RecyclerView) rootView.findViewById(R.id.feed_list);
        mFlotingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddStory.class);
                getActivity().startActivityForResult(intent, REQ);
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Fetching stories from server...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        mFeedList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mFeedList.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();

        mAdapter = new FeedAdapter(myDataset, getActivity());
        mFeedList.setAdapter(mAdapter);


        fetchFeed();

        return rootView;
    }
}
