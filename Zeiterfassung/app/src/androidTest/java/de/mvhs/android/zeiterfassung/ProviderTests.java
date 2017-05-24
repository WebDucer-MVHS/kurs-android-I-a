package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeContract;
import de.mvhs.android.zeiterfassung.db.TimeDataProvider;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProviderTests extends ProviderTestCase2<TimeDataProvider> {

    /**
     * Constructor.
     */
    public ProviderTests() {
        super(TimeDataProvider.class, TimeContract.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void newDatabase_HasNoContent() {
        // Arrange

        // Act
        Cursor data = getMockContentResolver()
                .query(TimeContract.TimeData.CONTENT_URI, null, null, null, null);

        // Assert
        assertThat(data.getCount(), is(0));
    }

    @Test
    public void databaseResult_HasAllColumns() {
        // Arrange
        final String[] expected = {
                TimeContract.TimeData.Columns._ID,
                TimeContract.TimeData.Columns.START,
                TimeContract.TimeData.Columns.END,
                TimeContract.TimeData.Columns.PAUSE,
                TimeContract.TimeData.Columns.COMMENT
        };

        // Act
        Cursor data = getMockContentResolver()
                .query(TimeContract.TimeData.CONTENT_URI, null, null, null, null, null);

        // Assert
        assertThat(data.getColumnNames(), arrayContainingInAnyOrder(expected));
    }

    @Test
    public void insert_WithoutStart_StoreNoEntry() {
        // Arrange
        ContentValues values = new ContentValues();
        values.put(TimeContract.TimeData.Columns.END,
                TimeContract.Converters.formatForDb(Calendar.getInstance()));

        // Act
        Uri insertUri = getMockContentResolver().insert(TimeContract.TimeData.CONTENT_URI, values);

        // Assert
        assertThat(insertUri, is(nullValue()));
    }

    @Test
    public void insertValue_StoreNewEntry(){
        // Arrange
        final String start = "2017-05-24T19:48";
        final String end = "2017-05-24T22:25";
        final int pause = 15;
        final String comment = "MVHS";
        ContentValues values = new ContentValues();
        values.put(TimeContract.TimeData.Columns.START, start);
        values.put(TimeContract.TimeData.Columns.END, end);
        values.put(TimeContract.TimeData.Columns.PAUSE, pause);
        values.put(TimeContract.TimeData.Columns.COMMENT, comment);

        // Act
        Uri insertUri = getMockContentResolver().insert(TimeContract.TimeData.CONTENT_URI, values);

        // Assert
        assertThat(insertUri, is(not(nullValue())));

        Cursor data = getMockContentResolver().query(insertUri, null, null, null, null);
        assertThat(data.getCount(), is(1));
        data.moveToFirst();

        assertThat(data.getString(data.getColumnIndex(TimeContract.TimeData.Columns.START)), is(start));
        assertThat(data.getString(data.getColumnIndex(TimeContract.TimeData.Columns.END)), is(end));
        assertThat(data.getInt(data.getColumnIndex(TimeContract.TimeData.Columns.PAUSE)), is(pause));
        assertThat(data.getString(data.getColumnIndex(TimeContract.TimeData.Columns.COMMENT)), is(comment));
    }

















}
