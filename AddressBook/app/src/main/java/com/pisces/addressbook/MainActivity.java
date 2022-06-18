package com.pisces.addressbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import addressbook.R;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_add;
    private ListView lv_show;
    private EditText et_name, et_phone, et_work, et_address;
    private MyAdapter myAdapter;
    private DBHelper dbHelper; //DBHelper
    private SearchView searchView;
    private List<User> userList;
    private LinkedList<User> mostNewAddList;
    private LinkedList<User> mostNewUpdateList;

    private static final String TAG = "MainActivity";

    public LinkedList<User> getMostNewAddList() {
        return mostNewAddList;
    }
    public LinkedList<User> getMostNewUpdateList() {
        return mostNewUpdateList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "onActivityResult");
        updateListView();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_add = findViewById(R.id.iv_add);
        lv_show = findViewById(R.id.lv_show);
        searchView = findViewById(R.id.searchview);


        if (userList != null) {
            userList.clear();
        }

        dbHelper = new DBHelper(MainActivity.this, "user.db", null, 1);

        updateListView();
        mostNewAddList = new LinkedList<>();
        mostNewUpdateList = new LinkedList<>();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int size = MainActivity.this.myAdapter.getList().size();
                String showAdd = "";
                for(User user : mostNewAddList) {
                    showAdd += user.getName() + "(" + user.getPhone() + ")";
                }

                if("".equals(showAdd)) {
                    showAdd = "暂无";
                }


                String showUpdate = "";
                for(User user : mostNewUpdateList) {
                    showUpdate += user.getName() + "(" + user.getPhone() + ")";
                }

                if("".equals(showUpdate)) {
                    showUpdate = "暂无";
                }

                Toast.makeText(MainActivity.this, "当前电话簿数目："+size + "", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "最新增加的内容："+showAdd + "", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "最新修改的内容："+showUpdate + "", Toast.LENGTH_SHORT).show();

                handler.postDelayed(this, 10000);// 50是延时时长
            }
        };
        handler.postDelayed(runnable,10000);// 打开定时器，执行操作

// 增加新联系人选项
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
// 联系人列表选项
        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                DetailsActivity.setMainActivity(MainActivity.this);
                Log.w(TAG, position + "");
                User user = (User) myAdapter.getItem(position);
                intent.putExtra("id", user.getId());
                startActivityForResult(intent, 1);
            }
        });
        lv_show.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteData(position);
                return true;
            }
        });
//        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateListView();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String s) {
                Log.w(TAG, s);
                Query(s);

                return true;
            }
        });
    }

    private void addData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = View.inflate(MainActivity.this, R.layout.layout_dialog, null);
        builder.setIcon(R.drawable.icon)
                .setTitle("添加联系人")
                .setView(dialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_name = dialogView.findViewById(R.id.et_name);
                et_phone = dialogView.findViewById(R.id.et_phone);
                et_work = dialogView.findViewById(R.id.et_work);
                et_address = dialogView.findViewById(R.id.et_address);
                String name = et_name.getText().toString();
                String phone = et_phone.getText().toString();
                String work = et_work.getText().toString();
                String address = et_address.getText().toString();
                User user = new User(name, phone, work, address);
                if (phone.length() != 11) {
                    showToast("电话号码长度不符合要求");
                } else {

                    if (dbHelper.insert(user)) {
                        showToast("添加成功");
                        if(MainActivity.this.getMostNewAddList().size() >= 3) {
                            MainActivity.this.getMostNewAddList().remove(
                                    MainActivity.this.getMostNewAddList().size() - 1
                            );
                        }
                        MainActivity.this.getMostNewAddList().push(user);
                        updateListView();
                    } else {
                        showToast("添加失败");
                    }

                }
            }
        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteData(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.icon)
                .setTitle("提示")
                .setMessage("是否删除该联系人？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = (User) myAdapter.getItem(position);
                        String deleteId = user.getId();
                        if (dbHelper.delete(deleteId)) {
                            updateListView();
                            showToast("删除成功");
                        } else {
                            showToast("删除失败");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void Query(String name) {
        userList = dbHelper.query(name);
        myAdapter = new MyAdapter(userList, MainActivity.this);
        lv_show.setAdapter(myAdapter);
    }

    public void updateListView() {
        userList = dbHelper.query();
        myAdapter = new MyAdapter(userList, MainActivity.this);
        lv_show.setAdapter(myAdapter);
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}