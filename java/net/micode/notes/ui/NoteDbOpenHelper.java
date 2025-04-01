package net.micode.notes.ui;

// 导入必要的Android数据库类
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

// 导入自定义的Note数据模型类
import net.micode.notes.ui.bean.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite数据库帮助类 - 负责笔记数据的持久化存储
 * 功能：完成数据库创建/升级，提供CRUD操作接口
 * 注意：当前设计为单表结构，存储基础笔记信息
 */
public class NoteDbOpenHelper extends SQLiteOpenHelper {

    // 数据库配置常量
    private static final String DB_NAME = "noteSQLite.db";          // 数据库文件名
    private static final String TABLE_NAME_NOTE = "note";           // 表名
    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME_NOTE
            + " (id integer primary key autoincrement, content text, create_time text)";  // 建表语句

    // 构造方法
    public NoteDbOpenHelper(Context context){
        super(context, DB_NAME, null, 1);  // 创建版本1的数据库
    }

    // region 生命周期回调
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);  // 首次创建数据库时建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 预留数据库升级逻辑（当前未实现）
    }
    // endregion

    // region 数据操作接口
    /**
     * 插入新笔记
     * @param note 包含内容和创建时间的笔记对象
     * @return 插入行的ID（-1表示失败）
     */
    public long insertData(Note note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("create_time", note.getCreatedTime());
        return db.insert(TABLE_NAME_NOTE, null, values);
    }

    /**
     * 按ID删除笔记（注意：使用LIKE条件）
     * @param id 要删除的笔记ID
     * @return 受影响的行数
     */
    public int deleteFromDbById(String id){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME_NOTE, "id like ?", new String[]{id});
    }

    /**
     * 更新笔记数据
     * @param note 必须包含有效ID的笔记对象
     * @return 受影响的行数
     */
    public int updateData(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("create_time", note.getCreatedTime());
        return db.update(TABLE_NAME_NOTE, values, "id like ?", new String[]{note.getId()});
    }

    /**
     * 查询全部笔记（无过滤条件）
     * @return 笔记对象列表（可能为空）
     */
    public List<Note> queryAllFromDb(){
        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();

        try (Cursor cursor = db.query(TABLE_NAME_NOTE, null, null, null, null, null, null)) {
            while (cursor.moveToNext()){
                Note note = new Note();
                note.setId(cursor.getString(cursor.getColumnIndex("id")));
                note.setContent(cursor.getString(cursor.getColumnIndex("content")));
                note.setCreatedTime(cursor.getString(cursor.getColumnIndex("create_time")));
                noteList.add(note);
            }
        } // 自动关闭Cursor
        return noteList;
    }

    /**
     * 按内容模糊查询笔记
     * @param content 搜索关键字（空值返回全部）
     * @return 匹配的笔记列表
     */
    public List<Note> queryFromDbByContent(String content){
        if (TextUtils.isEmpty(content)){
            return queryAllFromDb();
        }

        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_NAME_NOTE, null, "content like ?",
                new String[]{"%"+content+"%"}, null, null, null)) {
            while (cursor.moveToNext()){
                Note note = new Note();
                note.setId(cursor.getString(cursor.getColumnIndex("id")));
                note.setContent(cursor.getString(cursor.getColumnIndex("content")));
                note.setCreatedTime(cursor.getString(cursor.getColumnIndex("create_time")));
                noteList.add(note);
            }
        }
        return noteList;
    }
    // endregion
}
