package imuchnik.android.com.nanoproject1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String STORED_MOVIES = "stored_movies";
    private SharedPreferences prefs;
    private PosterAdapter mMoviePosterAdapter;
    String sortOrder;

    List<Movie> movies = new ArrayList<Movie>();

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.getAll().size() == 0) {
            setSortPreferences(getString(R.string.sort_default));
        }
        sortOrder = getPrefs();

        if (savedInstanceState != null) {
            ArrayList<Movie> storedMovies = new ArrayList<Movie>();
            storedMovies = savedInstanceState.<Movie>getParcelableArrayList(STORED_MOVIES);
            movies.clear();
            movies.addAll(storedMovies);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        String prefSortOrder = getPrefs();

        if (movies.size() > 0 && prefSortOrder.equals(sortOrder)) {
            updatePosterAdapter();
        } else {
            sortOrder = prefSortOrder;
            getMovies();
        }

    }

    private String getPrefs() {
        return prefs.getString(getString(R.string.display_preferences_sort_order_key),
                getString(R.string.sort_default));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMoviePosterAdapter = new PosterAdapter(
                getActivity(),
                R.layout.movie,
                R.id.movie_poster,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.main_movie_grid);
        gridView.setAdapter(mMoviePosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie details = movies.get(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("movies_details", details);
                startActivity(intent);
            }

        });

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> storedMovies = new ArrayList<Movie>();
        storedMovies.addAll(movies);
        outState.putParcelableArrayList(STORED_MOVIES, storedMovies);
    }

    private void getMovies() {

            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(sortOrder);

    }

    private void updatePosterAdapter() {
        mMoviePosterAdapter.clear();

        for (Movie movie : movies) {
            mMoviePosterAdapter.add(movie.getPoster());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.popularity) {
            item.setChecked(true);
            setSortPreferences(getString(R.string.sort_popularity_desc));
            sortOrder = getPrefs();
            movies.clear();
            getMovies();
            return true;
        }
        if (id == R.id.rating) {
            item.setChecked(true);
            setSortPreferences(getString(R.string.sort_vote_average_desc));
            sortOrder = getPrefs();
            movies.clear();
            getMovies();
            return true;
        }
        if (id == R.id.faves) {
            item.setChecked(true);
            setSortPreferences(getString(R.string.sort_vote_average_desc));
            sortOrder = getPrefs();
            movies.clear();
            getMovies();
            return true;
        }
        movies.clear();
        getMovies();

        return super.onOptionsItemSelected(item);
    }

    private void setSortPreferences(String newSortValue) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sortType", newSortValue);
        editor.commit();
    }

    private void makeToast(String msg, Context  context){
        //Context context = getActivity();

        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/";
        private final String MOVIE_POSTER_SIZE = "w185";
        final String SORT_BY = "sort_by";


        private List getMoviesFromJson(String movies)
                throws JSONException {
            ArrayList<Movie> movieList = new ArrayList<Movie>();

            JSONObject moviesJson = new JSONObject(movies);
            JSONArray results = moviesJson.getJSONArray("results");

            for (int i = 0; i < results.length(); ++i) {
                JSONObject rec = results.getJSONObject(i);
                String title = rec.getString("title");
                String description = rec.getString("overview");
                String image = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + rec.getString("poster_path");
                String voteAverage = rec.getString("vote_average");
                String releaseDate = getYear(rec.getString("release_date"));
                Movie movie = new Movie(title, description, image, voteAverage, releaseDate);
                movieList.add(movie);

            }

            return movieList;


        }

        private String getYear(String date) {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            final Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(df.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return Integer.toString(cal.get(Calendar.YEAR));
        }


        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movies = "";
            String sortBy = params[0];

            try {
                String API_KEY = "0d378ca9435b53e5795155172180b8e2";
                final String base_url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                Uri builtUri = Uri.parse(base_url).buildUpon()
                        .appendQueryParameter(SORT_BY, sortBy)
                        .build();
                if (isOnline(getContext())) {
                    URL url = new URL(builtUri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                }
                else{
                  return  null;
                }
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    movies = buffer.toString();
                }
            } catch (IOException e) {
                //makeToast("Something went wrong", getContext());
                Log.e("closed connection", e.getMessage(), e);
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                      //  makeToast("Something went wrong, closing connection", getContext());
                        Log.e("closed connection", e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }

            try {
                return getMoviesFromJson(movies);
            } catch (JSONException e) {
                Log.e("foo", e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List result) {
            if(result!=null) {
                movies.addAll(result);
                updatePosterAdapter();
            }
            else{
                makeToast("No movies found, check internet connection", getContext());
            }
        }

    }

}

