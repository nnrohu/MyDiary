package com.example.nnroh.mydiary.App;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nnroh.mydiary.Adapter.MonthlyCursorAdapter;
import com.example.nnroh.mydiary.Contract.MonthlyContract.MonthlyEntry;
import com.example.nnroh.mydiary.R;

public class Monthly extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MONTHLY_LOADER = 1;
    private MonthlyCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);
        Toolbar toolbar = (Toolbar) findViewById(R.id.Monthly_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(Monthly.this, MonthlyEditor.class);
               startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.Monthly_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //setup an adapter to create list item for each row
        //there is no data so pass null
        mCursorAdapter = new MonthlyCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        //set item click litsener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Monthly.this, MonthlyEditor.class);
                Uri currentMonthlyUri = ContentUris.withAppendedId(MonthlyEntry.CONTENT_URI, id);
                intent.setData(currentMonthlyUri);
                startActivity(intent);
            }
        });

        //kick off the Loader
        getSupportLoaderManager().initLoader(MONTHLY_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */

    private void deleteAllMonthly() {
        int rowsDeleted = getContentResolver().delete(MonthlyEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from money database");
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
        switch(item.getItemId()){
            case R.id.action_delete_all_entries:
                deleteAllMonthly();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MonthlyEntry._ID,
                MonthlyEntry.COLUMN_DATE,
                MonthlyEntry.COLUMN_ITEM,
                MonthlyEntry.COLUMN_PRICE,
                MonthlyEntry.COLUMN_PAYMENY_MODE};
        //this Loader will execute the contentProvider in background
        return new CursorLoader(this,
                MonthlyEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
