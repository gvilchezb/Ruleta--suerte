
package mikodes.watch.earns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 3; // Subir versión

    public static final String TABLE_MARCADORES = "marcadores";
    public static final String COL_ID = "id";
    public static final String COL_FECHA = "fecha";
    public static final String COL_PUNTUACION = "puntuacion";
    public static final String COL_LAT = "latitud";
    public static final String COL_LON = "longitud";
    public static final String COL_CITY = "ciudad";
    public static final String COL_COUNTRY = "pais";

    public ScoreDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MARCADORES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_FECHA + " TEXT," +
                COL_PUNTUACION + " INTEGER," +
                COL_LAT + " REAL," +
                COL_LON + " REAL," +
                COL_CITY + " TEXT," +
                COL_COUNTRY + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_MARCADORES + " ADD COLUMN " + COL_LAT + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_MARCADORES + " ADD COLUMN " + COL_LON + " REAL");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_MARCADORES + " ADD COLUMN " + COL_CITY + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_MARCADORES + " ADD COLUMN " + COL_COUNTRY + " TEXT");
        }
    }
}
