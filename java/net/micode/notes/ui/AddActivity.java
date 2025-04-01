package net.micode.notes.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import net.micode.notes.R;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 新增笔记界面Activity
 * 核心功能：提供新建笔记的输入与持久化存储
 * 数据流向：用户输入 -> 创建Note对象 -> 插入数据库
 */
public class AddActivity extends Activity {
    // 界面组件
    private EditText etContent;          // 内容输入框（布局ID：sf_et_1）
    private NoteDbOpenHelper mNoteDbOpenHelper; // 数据库操作类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);  // 设置新增笔记界面布局

        // 初始化视图组件
        etContent = findViewById(R.id.sf_et_1);
        // 初始化数据库访问对象
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
    }

    /**
     * 添加按钮点击事件
     * 流程：获取输入 -> 创建Note对象 -> 插入数据库 -> 结果反馈
     */
    public void add(View view) {
        String content = etContent.getText().toString();  // 获取输入内容

        // 创建新笔记对象
        Note note = new Note();
        note.setContent(content);
        note.setCreatedTime(getCurrentTimeFormat());  // 设置本地格式化时间

        // 执行数据库插入操作
        long rowId = mNoteDbOpenHelper.insertData(note);

        // 结果处理
        if(rowId != -1){  // SQLite插入成功返回行ID，失败返回-1
            ToastUtil.toastShort(this,"Add Success");
            this.finish();  // 成功则关闭当前界面
        }else{
            ToastUtil.toastShort(this,"Add Fail");  // 失败保持界面继续操作
        }
    }

    /**
     * 获取本地格式化的当前时间
     * 格式：月份日期 小时:分钟:秒（示例：08月15 14:30:45）
     * 注意：使用系统默认时区，未显式设置时区
     */
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}