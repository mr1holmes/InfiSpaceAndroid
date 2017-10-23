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

        Cursor mCursor = getActivity().getContentResolver().query(InfiContract.TABLE_USER.CONTENT_URI, null,
                InfiContract.TABLE_USER.COLUMN_USER_ID + "=?", new String[]{user_id}, null);
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
            }
        } finally {
            mCursor.close();
        }

        return rootView;
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
