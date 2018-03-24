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

import com.example.nnroh.mydiary.Adapter.MoneyCursorAdapter;
import com.example.nnroh.mydiary.R;
import com.example.nnroh.mydiary.Contract.LoanContract.MoneyEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MONEY_LOADER = 0;
    private MoneyCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.Loan_toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Monthly.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.Loan_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //setup an adapter to create list item for each row
        //there is no data so pass null
        mCursorAdapter = new MoneyCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        //set item click litsener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentMoneyUri = ContentUris.withAppendedId(MoneyEntry.CONTENT_URI, id);
                intent.setData(currentMoneyUri);
                startActivity(intent);
            }
        });

        //kick off the Loader
        getSupportLoaderManager().initLoader(MONEY_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */

    private void deleteAllMoney() {
        int rowsDeleted = getContentResolver().delete(MoneyEntry.CONTENT_URI, null, null);
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
                deleteAllMoney();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MoneyEntry._ID,
                MoneyEntry.COLUMN_DATE,
                MoneyEntry.COLUMN_NAME,
                MoneyEntry.COLUMN_MONEY,
                MoneyEntry.COLUMN_PURPOSE,
                MoneyEntry.COLUMN_PAYMENY_MODE};
        //this Loader will execute the contentProvider in background
        return new CursorLoader(this,
                MoneyEntry.CONTENT_URI,
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
