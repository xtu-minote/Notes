package net.micode.notes.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import net.micode.notes.R;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 笔记编辑界面Activity
 * 核心功能：提供已有笔记的编辑保存能力
 * 数据流向：接收笔记对象 -> 展示编辑 -> 更新数据库
 */
public class EditActivity extends Activity {
    // 界面组件
    private Note note;                // 当前操作的笔记对象
    private EditText etContent;       // 内容输入框
    private NoteDbOpenHelper mNoteDbOpenHelper;  // 数据库操作类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);  // 设置编辑界面布局

        etContent = findViewById(R.id.sf_et_2);  // 绑定内容输入框
        initData();  // 初始化数据
    }

    /**
     * 数据初始化方法
     * 功能：接收传入的笔记数据并预填充到输入框
     */
    private void initData() {
        // 获取传递的笔记对象（需实现Serializable接口）
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");

        // 存在现有笔记时预填充内容
        if(note != null){
            etContent.setText(note.getContent());
        }

        // 初始化数据库帮助类
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
    }

    /**
     * 保存按钮点击事件
     * 流程：获取输入 -> 更新数据 -> 数据库操作 -> 结果反馈
     */
    public void save(View view) {
        String content = etContent.getText().toString();  // 获取编辑内容

        // 更新笔记对象
        note.setContent(content);
        note.setCreatedTime(getCurrentTimeFormat());  // 更新时间戳

        // 执行数据库更新操作
        long row = mNoteDbOpenHelper.updateData(note);

        // 结果处理
        if(row != -1 && row != 0){
            ToastUtil.toastShort(this,"Edit Success");
            this.finish();  // 成功则关闭界面
        }else{
            ToastUtil.toastShort(this,"Edit Fail");  // 失败保持界面
        }
    }

    /**
     * 获取格式化的当前时间
     * 格式：月份日期 小时:分钟:秒（示例：08月15 14:30:45）
     * 时区：强制设置为中国时区（GMT+8）
     */
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd HH:mm:ss");
        TimeZone time = TimeZone.getTimeZone("Etc/GMT-8");
        TimeZone.setDefault(time);  // 修改全局默认时区
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

}
