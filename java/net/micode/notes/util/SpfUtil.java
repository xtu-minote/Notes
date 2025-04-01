package net.micode.notes.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 * 提供基础数据类型(字符串/整型)的存储与读取功能
 * 采用单文件统一管理，所有数据存储在名为"noteSpf"的配置文件中
 */
public class SpfUtil {
    // 配置文件名（建议通过资源文件配置）
    private static String SPF_NAME = "noteSpf";

    /**
     * 存储字符串数据
     * @param context 上下文对象
     * @param key 存储键名
     * @param value 字符串值（支持空字符串）
     * 使用场景：保存用户昵称、文本配置等
     */
    public static void saveString(Context context, String key, String value) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit()
                .putString(key, value)
                .apply(); // 异步提交，无返回值确认
    }

    /**
     * 读取字符串数据
     * @return 当key不存在时返回空字符串（非null）
     */
    public static String getString(Context context, String key) {
        return context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)
                .getString(key, "");
    }

    /**
     * 存储整型数据
     * @param value 支持负数，默认使用-1表示未初始化状态
     * 使用场景：保存设置项开关状态、数量限制等
     */
    public static void saveInt(Context context, String key, int value) {
        context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * 读取整型数据（默认值-1）
     * 注意：-1可能产生二义性，需业务层自行判断是否有效值
     */
    public static int getInt(Context context, String key) {
        return context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)
                .getInt(key, -1);
    }

    /**
     * 读取整型数据（自定义默认值）
     * @param defValue 可指定的默认值（推荐使用）
     * 使用示例：获取消息数量（默认0条）
     */
    public static int getIntWithDefault(Context context, String key, int defValue) {
        return context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)
                .getInt(key, defValue);
    }

}