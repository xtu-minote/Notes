package net.micode.notes.ui;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import net.micode.notes.R;
import net.micode.notes.util.ToastUtil;


/**
 * 密码验证Activity，用于保护安全文件夹的访问
 * 继承自NotesListActivity，可能复用笔记列表相关功能
 */
public class pwdActivity extends NotesListActivity {
    // 声明UI组件
    public Button mBtnpwd;
    public EditText mEtpwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);// 设置布局文件
        // 初始化UI组件
        mBtnpwd = findViewById(R.id.btn_pwd);
        mEtpwd = findViewById(R.id.et_1);

        // 设置密码验证按钮的点击监听器
        mBtnpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取用户输入的密码
                String pwd = mEtpwd.getText().toString();
                Intent intent=null;
                // 验证密码（注：实际场景应使用安全存储方式，此处为演示用途）
                if(pwd.equals("123")){// 硬编码密码，仅用于测试
                    // 密码正确，跳转到安全文件夹
                    intent = new Intent(pwdActivity.this,SafeFolderActivity.class);
                    startActivity(intent);// 结束当前Activity，避免返回
                    finish();
                }else{
                    // 密码错误提示
                    ToastUtil.toastShort(pwdActivity.this,"ERROR");
                    mEtpwd.setText("");// 清空输入框
                }

            }
        });


    }

}