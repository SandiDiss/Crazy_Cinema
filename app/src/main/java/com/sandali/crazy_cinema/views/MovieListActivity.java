package com.sandali.crazy_cinema.views;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.HashMap;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sandali.crazy_cinema.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.net.HttpURLConnection;

public class MovieListActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> theMovieList;
    //private RecyclerView recyclerView;
    //private DataAdapter mAdapter;
    ArrayList<HashMap<String, String>> moviesList;
    private String TAG = MovieListActivity.class.getSimpleName();
    private ListView lv;
    private FirebaseAuth firebaseAuth;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        theMovieList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        firebaseAuth = FirebaseAuth.getInstance();

        new GetMoviesList().execute();
/*        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mAdapter = new DataAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);*/

        if (android.os.Build.VERSION.SDK_INT > 9)
        {

            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // String API_Output = HttpHandler();
        //System.out.println("This is ouside function : " + API_Output);
        //TextView textView = findViewById(R.id.textView);
        //textView.setText(API_Output);

//        initmovieData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        menuItem = menu.findItem(R.id.action_refresh).setVisible(true);
        menuItem = menu.findItem(R.id.action_home).setVisible(true);
        return super.onCreateOptionsMenu(menu);


        // hide the menu item
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_home:

                startActivity(new Intent(this, MovieListActivity.class));
                break;

            case R.id.action_refresh:

                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.LogOut:

                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    private class GetMoviesList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MovieListActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        protected Void doInBackground(Void... arg0) {
            String jsonStr = HttpHandler();
            // Making a request to url and getting response
            //String url = "http://api.androidhive.info/contacts/";
            //String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray movielist = jsonObj.getJSONArray("Search");
                    System.out.println(movielist.length());
                    // looping through All movielist
                    for (int i = 0; i < movielist.length(); i++) {
                        JSONObject c = movielist.getJSONObject(i);
                        String title = c.getString("Title");
                        String year = c.getString("Year");
                        String imdbID = c.getString("imdbID");
                        String type = c.getString("Type");

                        // Phone node is JSON Object
/*                    JSONObject phone = c.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    String home = phone.getString("home");
                    String office = phone.getString("office");*/

                        // tmp hash map for single contact
                        HashMap<String, String> movies = new HashMap<>();

                        // adding each child node to HashMap key => value
                        movies.put("Title", title);
                        movies.put("Year", year);
                        movies.put("imdbID", imdbID);
                        movies.put("Type", type);

                        // adding contact to contact list
                        theMovieList.add(movies);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MovieListActivity.this, theMovieList,
                    R.layout.activity_movie_list, new String[]{ "Title","Year","imdbID"},
                    new int[]{R.id.title, R.id.year, R.id.imdbID});
            lv.setAdapter(adapter);
        }

        protected String HttpHandler() {
            String api_url = "http://www.omdbapi.com/?apikey=";
            String API_KEY = "2f9c3869";
            // Do some validation here

            try {
                URL url = new URL(api_url + API_KEY+"&s=transformers");


                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {


                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }
}
