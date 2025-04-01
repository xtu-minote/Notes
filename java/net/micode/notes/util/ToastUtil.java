package net.micode.notes.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast提示工具类
 * 功能：统一管理应用内所有Toast提示，简化调用流程
 * 特性：支持短时/长时两种显示模式
 */
public class ToastUtil {

    /**
     * 显示短时Toast（约2秒）
     * @param context 上下文对象（建议使用ApplicationContext避免内存泄漏）
     * @param msg 显示内容（支持纯文本/格式字符串）
     * 使用示例：ToastUtil.toastShort(getApplicationContext(), "保存成功");
     */
    public static void toastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长时Toast（约3.5秒）
     * @param msg 建议用于重要操作提示或错误信息
     * 典型场景：网络连接超时、重要数据删除确认
     */
    public static void toastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}