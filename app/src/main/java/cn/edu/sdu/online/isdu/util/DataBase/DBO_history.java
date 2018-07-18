package cn.edu.sdu.online.isdu.util.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBO_history extends SQLiteOpenHelper {
    private Context context;
    private static int VERSION=1;
    private static  final String DB_name ="History.db";
    private static  final String table_name ="tb_history";
    public static final String Creat_DB="CREATE TABLE tb_history("+
            "id integer primary key autoincrement,"+
            "title text,"+
            "subject text,"+
            "time integer,"+
            "url text"+
            ")";
    public DBO_history(Context context){
        super(context,DB_name,null,VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Creat_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists tb_history");
        onCreate(sqLiteDatabase);
    }
}
