package com.example.infispace.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.infispace.R;
import com.example.infispace.SplashScreen;
import com.example.infispace.data.InfiContract;
import com.example.infispace.util.AccountsUtil;
import com.example.infispace.util.CircleTransform;
import com.example.infispace.util.LogUtil;
import com.example.infispace.util.VolleySingleton;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ProfileFragment extends Fragment {


    private AppCompatButton mLogoutButton;
    private ImageView mProfileImage;
    private ImageLoader mImageLoader;
    private TextView mProfileName;
    private final String TAG = LogUtil.makeLogTag(this.getClass());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileImage = (ImageView) rootView.findViewById(R.id.profile_picture);
        mProfileName = (TextView) rootView.findViewById(R.id.profile_name);
        mLogoutButton = (AppCompatButton) rootView.findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), SplashScreen.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();

        String user_id = AccountsUtil.getUserId(getActivity());

        Uri userUri = InfiContract.TABLE_USER.buildUserUri(Long.parseLong(user_id));
        Cursor mCursor = getActivity().getContentResolver().query(userUri, null, null, null, null);
        try {
            while (mCursor.moveToNext()) {
                LogUtil.LOGD(TAG, "firstName = " + mCursor.getString(mCursor.getColumnIndex(InfiContract.TABLE_USER.COLUMN_FIRST_NAME)));
                String profile_image_url = mCursor.getString(mCursor.getColumnIndex(InfiContract.TABLE_USER.COLUMN_PROFILE_URL));
                LogUtil.LOGD(TAG, "something " + profile_image_url.replaceAll("\'", ""));
                Picasso.with(getActivity())
                        .load(profile_image_url.replaceAll("\'", ""))
                        .transform(new CircleTransform())
                        .into(mProfileImage);
                String firstName = mCursor.getString(mCursor.getColumnIndex(InfiContract.TABLE_USER.COLUMN_FIRST_NAME));
                String lastName = mCursor.getString(mCursor.getColumnIndex(InfiContract.TABLE_USER.COLUMN_LAST_NAME));
                mProfileName.setText(firstName + " " + lastName);

                // we only need the first result
                break;
            }
        } finally {
            mCursor.close();
        }

        return rootView;
    }

}
