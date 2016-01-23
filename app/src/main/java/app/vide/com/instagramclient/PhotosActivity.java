package app.vide.com.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.vide.com.instagramclient.model.InstagramPhoto;
import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";

    private List<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    @Bind( R.id.swipeRefreshLayout) SwipeRefreshLayout swipeContainer;
    @Bind(R.id.lvPhotos) RecyclerView lvPhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        photos = new ArrayList<>();
        aPhotos= new InstagramPhotosAdapter(photos, getApplicationContext());
        lvPhotos.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lvPhotos.setLayoutManager(llm);

        lvPhotos.setAdapter(aPhotos);
        //bind adapter to populate listview

        //send out API REQUEST to popular photos
        fetchPopularPhotos();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

    }

    public void fetchPopularPhotos() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        client.get(url, null, new JsonHttpResponseHandler() {
            //on success


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Expecting JSON Object
                Log.i("DEBUG", response.toString());
                aPhotos.clear();
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data");
                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        InstagramPhoto p = new InstagramPhoto();
                        p.setUsername(photoJSON.getJSONObject("user").getString("username"));
                        String caption = photoJSON.getJSONObject("caption") != null ? photoJSON.getJSONObject("caption").getString("text") : "";
                        p.setCaption(caption);
                        p.setType(photoJSON.getString("type"));
                        if (p.getType().equals("video")) {
                            p.setUrl(photoJSON.getJSONObject("videos").getJSONObject("standard_resolution").getString("url"));
                        } else {
                            p.setUrl(photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                        }
                        p.setLikesCount(photoJSON.getJSONObject("likes").getInt("count"));
                        p.setImageHeight(photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height"));
                        photos.add(p);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                aPhotos.addAll(photos);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            //on failure


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
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
