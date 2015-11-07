package jhernandez.gameclock.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(AlarmDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.
        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the Alarm table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(AlarmContract.AlarmEntry.TABLE_NAME);

        mContext.deleteDatabase(AlarmDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new AlarmDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and Alarm entry tables
        assertTrue("Error: Your database was created without both the location entry and Alarm entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + AlarmContract.AlarmEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> alarmColumnHashSet = new HashSet<>();
        alarmColumnHashSet.add(AlarmContract.AlarmEntry._ID);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_NAME);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_TIME);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_SUN);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_MON);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_TUE);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_WED);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_THR);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_FRI);
        alarmColumnHashSet.add(AlarmContract.AlarmEntry.COLUMN_SAT);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            alarmColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                alarmColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {
        insertLocation();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createAlarmValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testAlarmTable() {
        // First insert the location, and then use the locationRowId to insert
        // the Alarm. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        long locationRowId = insertLocation();

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        AlarmDbHelper dbHelper = new AlarmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Alarm): Create Alarm values
        ContentValues AlarmValues = TestUtilities.createAlarmValues(locationRowId);

        // Third Step (Alarm): Insert ContentValues into database and get a row ID back
        long AlarmRowId = db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, AlarmValues);
        assertTrue(AlarmRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor AlarmCursor = db.query(
                AlarmContract.AlarmEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", AlarmCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb AlarmEntry failed to validate",
                AlarmCursor, AlarmValues);

        // Move the cursor to demonstrate that there is only one record in the database
//        assertFalse( "Error: More than one record returned from Alarm query",
//                AlarmCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        AlarmCursor.close();
        dbHelper.close();
    }


    /*
        Students: This is a helper method for the testAlarmTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testAlarmTable and testLocationTable.
     */
    public long insertLocation() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        AlarmDbHelper dbHelper = new AlarmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createTestAlarmValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                AlarmContract.AlarmEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return locationRowId;
    }
}
