package imuchnik.android.com.nanoproject1;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import imuchnik.android.com.nanoproject1.MovieDetailFragment;
import imuchnik.android.com.nanoproject1.R;

/*
*
* Detail activity and displays the detail view of a movie
* Inflates MovieDetailFragment for the main UI
*
* */

public class MovieDetailActivity extends ActionBarActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private FragmentManager fragmentManager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, new MovieDetailFragment())
                    .commit();
        }
        getSupportActionBar().setElevation(0f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
