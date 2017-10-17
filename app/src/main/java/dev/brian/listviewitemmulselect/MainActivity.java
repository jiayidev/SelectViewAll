package dev.brian.listviewitemmulselect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnShowItemClickListener {

    private ListView listView;
    private List<ItemBean> dataList;
    private List<ItemBean> selectList;
    private MyAdapter adapter;
    private RelativeLayout rootView;
    private LinearLayout opreateView;
    private static boolean isShow; // 是否显示CheckBox标识

    private LinearLayout lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.main_listview);
        lay= (LinearLayout) findViewById(R.id.lay);
        dataList = new ArrayList<ItemBean>();
        selectList = new ArrayList<ItemBean>();
        for (int i = 0; i < 20; i++) {
            dataList.add(new ItemBean("item " + i, false, false));
        }
        adapter = new MyAdapter(dataList, this);
        listView.setAdapter(adapter);
        adapter.setOnShowItemClickListener(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (isShow) {
                    return false;
                } else {
                    isShow = true;
                    for (ItemBean bean : dataList) {
                        bean.setShow(true);
                    }
                    adapter.notifyDataSetChanged();
                    showOpervate();
                    listView.setLongClickable(false);
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (isShow) {
                    ItemBean bean = dataList.get(position);
                    boolean isChecked = bean.isChecked();
                    if (isChecked) {
                        bean.setChecked(false);
                    } else {
                        bean.setChecked(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, dataList.get(position).getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onShowItemClick(ItemBean bean) {
        if (bean.isChecked() && !selectList.contains(bean)) {
            selectList.add(bean);
        } else if (!bean.isChecked() && selectList.contains(bean)) {
            selectList.remove(bean);
        }
    }

    /**
     * 显示操作界面
     */
    private void showOpervate() {
        lay.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.operate_in);
        lay.setAnimation(anim);
        // 返回、删除、全选和反选按钮初始化及点击监听
        TextView tvBack =(TextView) findViewById(R.id.operate_back);
        TextView tvDelete = (TextView) findViewById(R.id.operate_delete);
        TextView tvSelect = (TextView) findViewById(R.id.operate_select);
        TextView tvInvertSelect = (TextView) findViewById(R.id.invert_select);

        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    isShow = false;
                    listView.setLongClickable(true);
                    dismissOperate();
                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (ItemBean bean : dataList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        tvInvertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ItemBean bean : dataList){
                    if (!bean.isChecked()){
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }else {
                        bean.setChecked(false);
                        if (!selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectList != null && selectList.size() > 0) {
                    dataList.removeAll(selectList);
                    adapter.notifyDataSetChanged();
                    selectList.clear();
                } else {
                    Toast.makeText(MainActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 隐藏操作界面
     */
    private void dismissOperate() {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.operate_out);
        lay.setVisibility(View.GONE);
        lay.setAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            selectList.clear();
            for (ItemBean bean : dataList) {
                bean.setChecked(false);
                bean.setShow(false);
            }
            adapter.notifyDataSetChanged();
            isShow = false;
            listView.setLongClickable(true);
            dismissOperate();
        } else {
            super.onBackPressed();
        }
    }


}
