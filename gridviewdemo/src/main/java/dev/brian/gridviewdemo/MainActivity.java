package dev.brian.gridviewdemo;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ActionBar actionBar;
    private Button btnCancel, btnDel, btnSelectAll, btnClear;
    private RelativeLayout headerLayout;
    private TextView numText;
    private GridView gridView;
    private ArrayList<HashMap<String, Object>> items; //每一个item
    private ArrayList<Boolean> selectItems; //用于存储已选中项目的位置
    private MyAdapter adapter;
    private boolean isState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("GridView");

        numText = (TextView) findViewById(R.id.number);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnDel = (Button) findViewById(R.id.btn_del);
        btnSelectAll = (Button) findViewById(R.id.btn_select_all);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnCancel.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnSelectAll.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        headerLayout = (RelativeLayout) findViewById(R.id.header);
        gridView = (GridView) findViewById(R.id.grid_view);
        items = new ArrayList<>();
        selectItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("image", R.drawable.test_3);
            items.add(item);
        }
        adapter = new MyAdapter(this, items, R.layout.item, new String[]{"image"}, new int[]{R.id.my_image});
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

    }

    public ArrayList<Boolean> getSelectItems() {
        return selectItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel :
                selectItems.clear();
                numText.setText("已选择1项");
                adapter.setIsState(false);
                setState(false);
                break;
            case R.id.btn_clear :
                setSelectAll(false);
                break;
            case R.id.btn_select_all:
                setSelectAll(true);
                break;
            case R.id.btn_del:
                delSelections();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isState) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                selectItems.set(position, false);
            } else {
                checkBox.setChecked(true);
                selectItems.set(position, true);
            }
            adapter.notifyDataSetChanged();
            setSelectNum();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isState) {
            selectItems = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                selectItems.add(false);
            }
            CheckBox box = (CheckBox) view.findViewById(R.id.check_box);
            box.setChecked(true);
            selectItems.set(position, true);
            setState(true);
            adapter.setIsState(true);
            setSelectNum();
        }
        return false;
    }

    //设置当前状态 是否在多选模式
    private void setState(boolean b) {
        isState = b;
        if (b) {
            headerLayout.setVisibility(View.VISIBLE);
            btnDel.setVisibility(View.VISIBLE);
            actionBar.hide();
        } else {
            headerLayout.setVisibility(View.GONE);
            btnDel.setVisibility(View.GONE);
            actionBar.show();
        }
    }

    //显示已选项数目
    private void setSelectNum() {
        int num = 0;
        for (Boolean b : selectItems) {
            if (b)
                num ++;
        }
        numText.setText("已选择" + num + "项");
    }

    //全选
    private void setSelectAll(boolean b) {
        for (int i = 0; i < selectItems.size(); i++) {
            selectItems.set(i, b);
            adapter.notifyDataSetChanged();
            setSelectNum();
        }
        btnSelectAll.setVisibility(b? View.GONE : View.VISIBLE);
        btnClear.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    //删除
    private void delSelections() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!selectItems.contains(true)) {
            builder.setTitle("提示").setMessage("当前未选中项目").setPositiveButton("确认", null).create().show();
        } else {
            builder.setTitle("提示");
            builder.setMessage("确认删除所选项目？");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 0; i < items.size(); i++) {
                        if (selectItems.get(i)) {
                            items.set(i, null);
                        }
                    }
                    while (items.contains(null)) {
                        items.remove(null);
                    }
                    selectItems = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {
                        selectItems.add(false);
                    }

                    adapter.setData(items);
                    adapter.notifyDataSetChanged();
                    if (items.size() == 0) {
                        adapter.setIsState(false);
                        setState(false);
                        return;
                    }
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
        }

    }

    private long mExitTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isState) {
                selectItems.clear();
                numText.setText("已选择1项");
                adapter.setIsState(false);
                setState(false);
                return true;
            } else {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                }
                else finish();
                return true;
            }

        }

        return super.onKeyDown(keyCode, event);
    }
}
