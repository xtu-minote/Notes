package net.micode.notes.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import net.micode.notes.R;
import net.micode.notes.ui.adapter.MyAdapter;
import net.micode.notes.ui.bean.Note;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 安全文件夹主界面Activity
 * 功能：展示加密笔记列表、支持搜索和新建加密笔记
 */
public class SafeFolderActivity extends Activity {
    // UI组件
    private RecyclerView mRecyclerView;  // 笔记列表容器
    private Button mBtnNew;              // 新建笔记按钮（未实际使用）
    private List<Note> mNotes;           // 笔记数据集合
    private MyAdapter mMyAdapter;        // 列表适配器
    private NoteDbOpenHelper mNoteDbOpenHelper;  // 数据库操作帮助类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_folder);

        initializeComponents();  // 三阶段初始化模式
    }

    /**
     * 组件初始化三步骤
     */
    private void initializeComponents() {
        initView();    // 初始化界面元素
        initData();    // 初始化数据相关
        initEvent();   // 初始化事件监听
    }

    // region 生命周期管理
    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromDb();  // 每次界面可见时刷新数据
    }
    // endregion

    // region 数据操作
    private void refreshDataFromDb() {
        mNotes = getDataFromDB();      // 从数据库获取最新数据
        mMyAdapter.refreshData(mNotes); // 更新适配器数据
    }

    private void initData() {
        mNotes = new ArrayList<>();
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);  // 初始化数据库帮助类
    }

    private List<Note> getDataFromDB() {
        return mNoteDbOpenHelper.queryAllFromDb();  // 查询全部数据
    }
    // endregion

    // region 界面初始化
    private void initView() {
        mRecyclerView = findViewById(R.id.sf_list);  // 绑定列表视图
    }

    private void initEvent() {
        // 配置RecyclerView
        mMyAdapter = new MyAdapter(this, mNotes);
        mRecyclerView.setAdapter(mMyAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  // 线性布局
    }
    // endregion

    // region 功能方法
    /**
     * 新建笔记按钮点击事件
     */
    public void newNoteAction(View view) {
        startActivity(new Intent(this, AddActivity.class));  // 跳转添加界面
    }

    /**
     * 获取格式化时间字符串（未实际使用）
     */
    private String getCurrentTimeFormat() {
        return new SimpleDateFormat("MM月dd HH:mm:ss").format(new Date());
    }
    // endregion

    // region 菜单操作
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.safe, menu);  // 加载菜单布局

        // 初始化搜索功能
        initializeSearchFeature(menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 初始化搜索功能
     */
    private void initializeSearchFeature(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.sf_menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            // 设置搜索监听器
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;  // 不处理提交事件
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // 实时搜索（需注意性能优化）
                    mNotes = mNoteDbOpenHelper.queryFromDbByContent(newText);
                    mMyAdapter.refreshData(mNotes);
                    return true;
                }
            });
        } else {
            Log.e("SearchView", "SearchView初始化失败");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 预留其他菜单项处理
        return super.onOptionsItemSelected(item);
    }
    // endregion

}