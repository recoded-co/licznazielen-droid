package pl.org.sendzimir.licznazielen.helper;

/**
 *
 * @author LeRafiK
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "fav_data.db";
	private static final int DATABASE_VERSION = 5;

	public static final String TABLE_NAME_FAV = "FavoriteList";

	public static final String KEY_ID = "_id";
	public static final String KEY_CREATED_AT = "created_at";
	public static final String KEY_UPDETED_AT = "updeated_at";

	public static final String COLUMN_FAV_OBJID = "objid";
	public static final String COLUMN_FAV_POPULARITY = "popularity";
	public static final String COLUMN_FAV_NAME = "name";
	public static final String COLUMN_FAV_LATITUDE = "latitude";
	public static final String COLUMN_FAV_LONGITUDE = "longitude";
	public static final String COLUMN_FAV_MYOBJECT = "my";
	public static final String COLUMN_FAV_ICONS = "icons";
	public static final String COLUMN_FAV_ANSWRWS = "ANSWRWS";

	private static final String CREATE_TABLE_FAVORITE_LIST = "create table "
			+ TABLE_NAME_FAV + "(" + KEY_ID
			+ " integer primary key autoincrement, " + COLUMN_FAV_OBJID
			+ " integer," + COLUMN_FAV_POPULARITY + " integer,"
			+ COLUMN_FAV_LATITUDE + " real," + COLUMN_FAV_LONGITUDE + " real,"
			+ COLUMN_FAV_MYOBJECT + " integer," + COLUMN_FAV_ICONS + " text,"
			+ COLUMN_FAV_ANSWRWS + " text," + COLUMN_FAV_NAME + " text" + ");";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_FAVORITE_LIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAV);

		onCreate(db);
	}

}
