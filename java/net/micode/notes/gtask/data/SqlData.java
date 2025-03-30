/*
 SqlData 类用于支持小米便签最底层的数据库相关操作，和sqlnote的关系上是子集关系，即data是note的子集（节点）。
 提供了从 JSON 对象设置内容，从数据库 Cursor 加载数据，以及提交数据更新到数据库的功能。
 */
package net.micode.notes.gtask.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.DataConstants;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.data.NotesDatabaseHelper.TABLE;
import net.micode.notes.gtask.exception.ActionFailureException;

import org.json.JSONException;
import org.json.JSONObject;


public class SqlData {
    //调用getSimpleName ()函数，得到类的简写名称存入字符串TAG中
    private static final String TAG = SqlData.class.getSimpleName();

    //为无效ID常量置初始值-99999
    private static final int INVALID_ID = -99999;

    //来自Notes类中定义的DataColumn中的一些常量，查询时使用的字段投影
    public static final String[] PROJECTION_DATA = new String[]{
            DataColumns.ID, DataColumns.MIME_TYPE, DataColumns.CONTENT, DataColumns.DATA1,
            DataColumns.DATA3
    };

    //字段在Cursor中的索引，即sql表中5列的编号
    public static final int DATA_ID_COLUMN = 0;
    public static final int DATA_MIME_TYPE_COLUMN = 1;
    public static final int DATA_CONTENT_COLUMN = 2;
    public static final int DATA_CONTENT_DATA_1_COLUMN = 3;
    public static final int DATA_CONTENT_DATA_3_COLUMN = 4;

    //ContentResolver用于操作内容提供者
    private ContentResolver mContentResolver;

    //标记当前对象是创建状态还是更新状态
    private boolean mIsCreate;

    //数据项ID
    private long mDataId;

    //数据项的MIME类型
    private String mDataMimeType;

    //数据项的内容
    private String mDataContent;

    //数据项的附加数据1
    private long mDataContentData1;

    //数据项的附加数据3
    private String mDataContentData3;

    //存储与数据库不同步的数据变化
    private ContentValues mDiffDataValues;


    /*
    SqlData 构造函数，用于创建新的数据项。
    参数 context 上下文对象，用于获取ContentResolver。
    */
    public SqlData(Context context) {
        //获取ContentProvider提供的数据赋值给mContentResolver
        mContentResolver = context.getContentResolver();
        //mIsCreate = true表示当前是创建状态
        mIsCreate = true;
        //mDataId置初始值-99999
        mDataId = INVALID_ID;
        mDataMimeType = DataConstants.NOTE;
        mDataContent = "";
        mDataContentData1 = 0;
        mDataContentData3 = "";
        mDiffDataValues = new ContentValues();
    }

    /*
     SqlData 构造函数，用于加载现有数据项。
     参数 context 上下文对象，用于获取ContentResolver。
     参数 c 数据项的Cursor对象，用于加载数据。
     */
    public SqlData(Context context, Cursor c) {
        //获取ContentProvider提供的数据赋值给mContentResolver
        mContentResolver = context.getContentResolver();
        //mIsCreate = false表示当前是更新状态
        mIsCreate = false;
        loadFromCursor(c);
        mDiffDataValues = new ContentValues();
    }

     /*
     从当前的光标处将五列的数据加载到该类的对象
     参数 c 数据项的Cursor对象。
     */
    private void loadFromCursor(Cursor c) {
        mDataId = c.getLong(DATA_ID_COLUMN);
        mDataMimeType = c.getString(DATA_MIME_TYPE_COLUMN);
        mDataContent = c.getString(DATA_CONTENT_COLUMN);
        mDataContentData1 = c.getLong(DATA_CONTENT_DATA_1_COLUMN);
        mDataContentData3 = c.getString(DATA_CONTENT_DATA_3_COLUMN);
    }

     /*
     根据JSON对象设置数据项内容，并提供异常抛出与处理机制
     参数 js JSON对象，包含数据项的内容。
     throws JSONException 如果解析JSON时出错。
     */
    public void setContent(JSONObject js) throws JSONException {
        //如果传入的JSONObject对象中有DataColumns.ID这一项，则设置，否则设为INVALID_ID
        long dataId = js.has(DataColumns.ID) ? js.getLong(DataColumns.ID) : INVALID_ID;
        if (mIsCreate || mDataId != dataId) {
            mDiffDataValues.put(DataColumns.ID, dataId);
        }
        mDataId = dataId;

        String dataMimeType = js.has(DataColumns.MIME_TYPE) ? js.getString(DataColumns.MIME_TYPE)
                : DataConstants.NOTE;
        if (mIsCreate || !mDataMimeType.equals(dataMimeType)) {
            mDiffDataValues.put(DataColumns.MIME_TYPE, dataMimeType);
        }
        mDataMimeType = dataMimeType;

        String dataContent = js.has(DataColumns.CONTENT) ? js.getString(DataColumns.CONTENT) : "";
        if (mIsCreate || !mDataContent.equals(dataContent)) {
            mDiffDataValues.put(DataColumns.CONTENT, dataContent);
        }
        mDataContent = dataContent;

        long dataContentData1 = js.has(DataColumns.DATA1) ? js.getLong(DataColumns.DATA1) : 0;
        if (mIsCreate || mDataContentData1 != dataContentData1) {
            mDiffDataValues.put(DataColumns.DATA1, dataContentData1);
        }
        mDataContentData1 = dataContentData1;

        String dataContentData3 = js.has(DataColumns.DATA3) ? js.getString(DataColumns.DATA3) : "";
        if (mIsCreate || !mDataContentData3.equals(dataContentData3)) {
            mDiffDataValues.put(DataColumns.DATA3, dataContentData3);
        }
        mDataContentData3 = dataContentData3;
    }

     /*
     获取数据项的内容，转换为JSON对象，并提供异常抛出与处理机制
     返回值 JSON对象，包含数据项的内容。
     throws JSONException 如果构建JSON对象时出错。
     */
    public JSONObject getContent() throws JSONException {
        if (mIsCreate) {
            Log.e(TAG, "it seems that we haven't created this in database yet");
            return null;
        }
        //创建JSONObject对象。并将相关数据放入其中，并返回。
        JSONObject js = new JSONObject();
        js.put(DataColumns.ID, mDataId);
        js.put(DataColumns.MIME_TYPE, mDataMimeType);
        js.put(DataColumns.CONTENT, mDataContent);
        js.put(DataColumns.DATA1, mDataContentData1);
        js.put(DataColumns.DATA3, mDataContentData3);
        return js;
    }

     /*
     将数据项提交到数据库，如果是新数据项则插入，否则更新。
     参数 noteId 符合此数据项的笔记ID。
     参数 validateVersion 是否验证版本号。
     参数 version 数据项的版本号。
     */
    public void commit(long noteId, boolean validateVersion, long version) {

        if (mIsCreate) {
            //处理新数据项的插入
            if (mDataId == INVALID_ID && mDiffDataValues.containsKey(DataColumns.ID)) {
                mDiffDataValues.remove(DataColumns.ID);
            }

            mDiffDataValues.put(DataColumns.NOTE_ID, noteId);
            Uri uri = mContentResolver.insert(Notes.CONTENT_DATA_URI, mDiffDataValues);
            try {
                mDataId = Long.valueOf(uri.getPathSegments().get(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Get note id error :" + e.toString());
                throw new ActionFailureException("create note failed");
            }
        } else {
            //处理现有数据项的更新
            if (mDiffDataValues.size() > 0) {
                int result = 0;
                if (!validateVersion) {
                    //不验证版本号时直接更新
                    result = mContentResolver.update(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mDataId), mDiffDataValues, null, null);
                } else {
                    //验证版本号时条件更新
                    result = mContentResolver.update(ContentUris.withAppendedId(
                                    Notes.CONTENT_DATA_URI, mDataId), mDiffDataValues,
                            " ? in (SELECT " + NoteColumns.ID + " FROM " + TABLE.NOTE
                                    + " WHERE " + NoteColumns.VERSION + "=?)", new String[]{
                                    String.valueOf(noteId), String.valueOf(version)
                            });
                }
                if (result == 0) {
                    Log.w(TAG, "there is no update. maybe user updates note when syncing");
                }
            }
        }

        //清理并重置状态
        mDiffDataValues.clear();
        mIsCreate = false;
    }

     /*
     获取数据项的ID。
     返回值 数据项的ID。
     */
    public long getId() {
        return mDataId;
    }
}

