//MetaData类，继承自Task类，用于处理与任务相关的元数据。

package net.micode.notes.gtask.data;

import android.database.Cursor;
import android.util.Log;

import net.micode.notes.tool.GTaskStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MetaData extends Task {
    //调用getSimpleName ()函数，得到类的简写名称存入字符串TAG中
    private final static String TAG = MetaData.class.getSimpleName();
    //为mRelatedGid置初始值nul
    private String mRelatedGid = null;

    //设置元数据
    public void setMeta(String gid, JSONObject metaInfo) {
        try {
            //将这对键值放入metaInfo这个jsonobject对象中
            metaInfo.put(GTaskStringUtils.META_HEAD_GTASK_ID, gid);
        } catch (JSONException e) {
            //输出错误信息
            Log.e(TAG, "failed to put related gid");
        }
        setNotes(metaInfo.toString()); //将元信息设置为任务的笔记
        setName(GTaskStringUtils.META_NOTE_NAME); //设置任务的名称为特定的元数据标志名称
    }

    //获取相关联的Gid
    public String getRelatedGid() {
        return mRelatedGid;
    }

    //判断当前数据是否为空，若为空则返回真 即 值得保存
    @Override
    public boolean isWorthSaving() {
        return getNotes() != null;
    }

    //使用远程json数据对象设置元数据内容
    @Override
    public void setContentByRemoteJSON(JSONObject js) {
        super.setContentByRemoteJSON(js);
        if (getNotes() != null) {
            try {
                JSONObject metaInfo = new JSONObject(getNotes().trim());
                mRelatedGid = metaInfo.getString(GTaskStringUtils.META_HEAD_GTASK_ID); // 从笔记中提取相关的全局ID
            } catch (JSONException e) {
                Log.w(TAG, "failed to get related gid");//输出错误信息
                mRelatedGid = null; //提取失败时，设置相关ID为null
            }
        }
    }

    //使用本地json数据对象设置元数据内容，一般不会用到，若用到，则抛出异常（传递非法参数异常）
    @Override
    public void setContentByLocalJSON(JSONObject js) {
        //this function should not be called
        throw new IllegalAccessError("MetaData:setContentByLocalJSON should not be called");
    }

    //从元数据内容中获取本地json对象，一般不会用到，若用到，则抛出异常（传递非法参数异常）
    @Override
    public JSONObject getLocalJSONFromContent() {
        //this function should not be called
        throw new IllegalAccessError("MetaData:getLocalJSONFromContent should not be called");
    }

    //获取同步动作状态，一般不会用到，若用到，则抛出异常（传递非法参数异常）
    @Override
    public int getSyncAction(Cursor c) {
        //this function should not be called
        throw new IllegalAccessError("MetaData:getSyncAction should not be called");
    }

}
