package com.pisces.addressbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.sql.SQLOutput;

import addressbook.R;

public class DetailsActivity extends AppCompatActivity {
    private ImageButton backButton;

    private TextView nameTopTextView;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView workTextView;
    private TextView addressTextView;

    private Button updateButton;
    private Button deleteButton;

    private EditText et_name, et_phone, et_work, et_address;
    private MyAdapter myAdapter;
    private DBHelper dbHelper;

    private String id;

    private Intent intent;

    private static final String TAG = "DetailsActivity";

    private static MainActivity mainActivity;

    public static void setMainActivity(MainActivity mainActivity) {
        DetailsActivity.mainActivity = mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        backButton = findViewById(R.id.back);
        nameTopTextView = findViewById(R.id.name_top);
        nameTextView = findViewById(R.id.name);
        phoneTextView = findViewById(R.id.phone);
        addressTextView = findViewById(R.id.address);
        workTextView = findViewById(R.id.work);
        updateButton = findViewById(R.id.button_update);
        deleteButton = findViewById(R.id.button_delete);
        dbHelper = new DBHelper(DetailsActivity.this, "user.db", null, 1);

        //接收参数
        intent = this.getIntent();
        id = intent.getStringExtra("id");
        Log.w(TAG, id);
        //获取并设置当前用户信息
        //        getUser(id);
        User user = (User) dbHelper.getById(id);
        nameTopTextView.setText(user.getName());
        nameTextView.setText(user.getName());
        phoneTextView.setText(user.getPhone());
        workTextView.setText(user.getWork());
        addressTextView.setText(user.getAddress());
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(DetailsActivity.this.id);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(DetailsActivity.this.id);
            }
        });

    }

    private void updateData(String position) {
        View dialogView = View.inflate(DetailsActivity.this, R.layout.layout_dialog, null);
        User user = (User) dbHelper.getById(position);
        et_name = dialogView.findViewById(R.id.et_name);
        et_phone = dialogView.findViewById(R.id.et_phone);
        et_work = dialogView.findViewById(R.id.et_work);
        et_address = dialogView.findViewById(R.id.et_address);
        et_name.setText(user.getName());
        et_phone.setText(user.getPhone());
        et_work.setText(user.getWork());
        et_address.setText(user.getAddress());
        String findId = user.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setIcon(R.drawable.icon)
                .setTitle("修改联系人")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_name.getText().toString();
                        String phone = et_phone.getText().toString();
                        String work = et_work.getText().toString();
                        String address = et_address.getText().toString();
//                        User user = new User(name, phone, work, address);
                        user.setName(name);
                        user.setPhone(phone);
                        user.setWork(work);
                        user.setAddress(address);
                        if (phone.length() != 11) {
                            showToast("电话号码长度不符合要求");
                        } else {

                            if (dbHelper.update(findId, user)) {
                                showToast("修改成功");
                                boolean change = false;

                                if(mainActivity.getMostNewUpdateList().size() > 0 && mainActivity.getMostNewUpdateList().get(0).getId() == user.getId()) {
                                    mainActivity.getMostNewUpdateList().set(0, user);
                                    change = true;
                                }

                                if(change == false) {
                                    if(mainActivity.getMostNewUpdateList().size() >= 3) {
                                        mainActivity.getMostNewUpdateList().remove(
                                                mainActivity.getMostNewUpdateList().size() - 1
                                        );
                                    }
                                    mainActivity.getMostNewUpdateList().push(user);
                                }

                                //获取并设置当前用户信息
                                getUser(id);

                            } else {
                                showToast("修改失败");
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

    private void deleteData(String position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setIcon(R.drawable.icon)
                .setTitle("提示")
                .setMessage("是否删除该联系人？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dbHelper.delete(position)) {
                            showToast("删除成功");
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
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

    public void getUser(String id) {
        User user = (User) dbHelper.getById(id);
        nameTopTextView.setText(user.getName());
        nameTextView.setText(user.getName());
        phoneTextView.setText(user.getPhone());
        workTextView.setText(user.getWork());
        addressTextView.setText(user.getAddress());
    }

    public void showToast(String msg) {
        Toast.makeText(DetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}