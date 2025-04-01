package net.micode.notes.ui.adapter;

// 导入必要的Android类
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import net.micode.notes.R;
import net.micode.notes.ui.EditActivity;
import net.micode.notes.ui.NoteDbOpenHelper;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;
import java.util.List;

/**
 * RecyclerView适配器 - 实现笔记列表展示与交互
 * 核心功能：列表渲染、点击编辑、长按删除、数据同步
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    // 数据与工具对象
    private List<Note> mBeanList;         // 笔记数据源
    private LayoutInflater mLayoutInflater;// 布局加载器
    private Context mContext;             // 上下文对象
    private NoteDbOpenHelper mNoteDbOpenHelper; // 数据库操作类

    // 构造方法
    public MyAdapter(Context context, List<Note> mBeanList){
        this.mBeanList = mBeanList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mNoteDbOpenHelper = new NoteDbOpenHelper(mContext); // 初始化数据库帮助类
    }

    // region 核心适配器方法
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item_layout, parent, false);
        return new MyViewHolder(view); // 创建列表项视图持有者
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Note note = mBeanList.get(position);
        // 数据绑定
        holder.mTvContent.setText(note.getContent());
        holder.mTvTime.setText(note.getCreatedTime());

        // 点击事件：跳转编辑界面
        holder.rlContainer.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditActivity.class);
            intent.putExtra("note", note);
            mContext.startActivity(intent);
        });

        // 长按事件：显示删除对话框
        holder.rlContainer.setOnLongClickListener(v -> {
            showDeleteDialog(position, note);
            return true; // 消费长按事件
        });
    }

    @Override
    public int getItemCount() {
        return mBeanList.size(); // 返回数据项总数
    }
    // endregion

    // region 数据操作
    /**
     * 刷新整个数据集
     * @param notes 新数据集
     */
    public void refreshData(List<Note> notes){
        this.mBeanList = notes;
        notifyDataSetChanged(); // 全量刷新（建议优化为DiffUtil）
    }

    /**
     * 删除指定位置数据项
     * @param pos 要删除的索引位置
     */
    public void removeData(int pos){
        mBeanList.remove(pos);
        notifyItemRemoved(pos); // 带动画删除
    }
    // endregion

    // region 交互逻辑
    /**
     * 显示删除确认对话框
     */
    private void showDeleteDialog(int position, Note note) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        View view = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);
        TextView tvDelete = view.findViewById(R.id.tv_delete);

        tvDelete.setOnClickListener(v -> {
            int row = mNoteDbOpenHelper.deleteFromDbById(note.getId());
            if (row > 0) {
                removeData(position); // UI更新
                ToastUtil.toastShort(mContext,"Delete Success");
            } else {
                ToastUtil.toastShort(mContext,"Delete Fail");
            }
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }
    // endregion

    // region ViewHolder定义
    /**
     * 列表项视图持有者
     * 包含：内容文本、时间文本、根布局容器
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTvContent;
        TextView mTvTime;
        ViewGroup rlContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvContent = itemView.findViewById(R.id.tv_content);
            mTvTime = itemView.findViewById(R.id.tv_time);
            rlContainer = itemView.findViewById(R.id.rl_item_container);
        }
    }
    // endregion

}