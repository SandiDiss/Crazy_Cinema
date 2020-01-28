package com.sandali.crazy_cinema.views;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.MenuItemCompat;

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

public class MovieListActivity extends AppCompatActivity{
    ArrayList<HashMap<String, String>> theMovieList;
    //private RecyclerView recyclerView;
    //private DataAdapter mAdapter;
    ArrayList<HashMap<String, String>> moviesList;
    private String TAG = MovieListActivity.class.getSimpleName();
    private ListView lv;
    private FirebaseAuth firebaseAuth;
    MenuItem menuItem;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String keyword = "transformers";
    private EditText searchEditText;
    private SearchView search;

    public void CallMovieList(String value){
            theMovieList = new ArrayList<>();
            setKeyword(value);
            getJson();
    }

    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(MovieListActivity.this, theMovieList,
                R.layout.activity_movie_list, new String[]{ "Title","Year","imdbID"},
                new int[]{R.id.title, R.id.year, R.id.imdbID});
        lv.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        theMovieList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        search = (SearchView) findViewById(R.id.action_search);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                Toast.makeText(getApplicationContext(),
                        "Searching for " + query,
                        Toast.LENGTH_LONG).show();
                MovieListActivity newLis = new MovieListActivity();
                newLis.setKeyword(query);
                newLis.CallMovieList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
/*                Toast.makeText(getApplicationContext(),
                        "Searching for " + newText,
                        Toast.LENGTH_LONG).show();*/
                return false;
            }
        });

        menuItem = menu.findItem(R.id.action_refresh).setVisible(true);
        menuItem = menu.findItem(R.id.action_search).setVisible(true);
        return super.onCreateOptionsMenu(menu);


        // hide the menu item
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_search:
                //this.setKeyword();
                Intent newIntent = new Intent(this, MovieListActivity.class);
                startActivity(newIntent);
                break;

            case R.id.action_refresh:

                startActivity(new Intent(this, MovieListActivity.class));
                break;

            case R.id.LogOut:

                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    public void getJson() {
        HttpHandler httpReq = new HttpHandler();
        String jsonStr = httpReq.HttpHandlerRequest(keyword);
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
                    System.out.println(title);
                    String year = c.getString("Year");
                    System.out.println(year);
                    String imdbID = c.getString("imdbID");
                    System.out.println(imdbID);
                    String type = c.getString("Type");
                    System.out.println(type);


                    // tmp hash map for single movie
                    HashMap<String, String> movies = new HashMap<>();

                    // adding each child node to HashMap key => value
                    movies.put("Title", title);
                    movies.put("Year", year);
                    movies.put("imdbID", imdbID);
                    movies.put("Type", type);

                    System.out.println(movies);

                    // adding movie to movie list
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
        //loadListView();
    }


    private class GetMoviesList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MovieListActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        protected Void doInBackground(Void... arg0) {
            getJson();
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           loadListView();
        }

    }
}
