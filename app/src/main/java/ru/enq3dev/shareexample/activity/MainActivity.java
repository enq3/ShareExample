package ru.enq3dev.shareexample.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import ru.enq3dev.shareexample.R;
import ru.enq3dev.shareexample.adapter.FragmentAdapter;
import ru.enq3dev.shareexample.core.Constants;
import ru.enq3dev.shareexample.core.DepthPageTransformer;
import ru.enq3dev.shareexample.core.RestClient;


public class MainActivity extends ActionBarActivity {

    ViewPager pager;
    FragmentAdapter adapter;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init imageloader
        initLoader();

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setPageTransformer(false, new DepthPageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            RestClient.get("http://api-fotki.yandex.ru/api/podhistory/?format=json&limit=10", null, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    JsonObject result = new JsonParser().parse(responseString).getAsJsonObject();
                    JsonArray images = result.get("entries").getAsJsonArray();
                    for (JsonElement jsonElement : images) {
                        JsonObject img = jsonElement.getAsJsonObject().get("img").getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entrySet = img.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            if (entry.getKey().equalsIgnoreCase("L")) {
                                adapter.addEntry(entry.getValue().getAsJsonObject().get("href").getAsString());
                                break;
                            }
                        }
                    }

                }
            });
        } else {
            Toast.makeText(this, "No network", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            if(adapter.getCount() > 0) {
                String currentUrl = adapter.getEntry(currentPage);

                ImageLoader.getInstance().loadImage(currentUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (bitmap != null) {
                            try {
                                File file = new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES), "shareexample");
                                file.mkdirs();
                                file = new File(Environment.getExternalStorageDirectory().getPath(), Constants.TEMPFILENAME);
                                FileOutputStream fileOutput = null;
                                fileOutput = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutput);
                                fileOutput.flush();
                                fileOutput.close();

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setType("image/jpeg");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + Environment.getExternalStorageDirectory().getPath() + File.separator + Constants.TEMPFILENAME));
                                startActivity(Intent.createChooser(shareIntent, ""));

                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void initLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(250, true, true, true))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(500, 500)
                        //.writeDebugLogs()
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
