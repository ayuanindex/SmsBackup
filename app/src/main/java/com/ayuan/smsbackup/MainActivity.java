package com.ayuan.smsbackup;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button backup = (Button) findViewById(R.id.btn_backup);
        //点击按钮查询短信数据库内容 然后备份
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri parse = Uri.parse("content://sms/");
                    Cursor query = getContentResolver().query(parse, new String[]{"address", "date", "body"}, null, null, null);
                    //获取xml序列化的实例
                    XmlSerializer serializer = Xml.newSerializer();
                    //设置序列化的参数
                    FileOutputStream fileOutputStream = openFileOutput("smsBackUp.xml", MODE_APPEND);
                    serializer.setOutput(fileOutputStream, "utf-8");
                    //开始写xml文档的开头
                    serializer.startDocument("utf-8", true);
                    //开始写根节点
                    serializer.startTag(null, "smss");
                    //由于短信数据库系统也是通过内容提供者暴露出来的所以我们只需要通过内容解析者操作数据库就可以了
                    while (query.moveToNext()) {
                        String address = query.getString(0);
                        String date = query.getString(1);
                        String body = query.getString(2);
                        //写sms节点
                        serializer.startTag(null, "sms");
                        //写address节点
                        serializer.startTag(null, "address");
                        serializer.text(address);
                        serializer.endTag(null, "address");
                        //写body节点
                        serializer.startTag(null, "body");
                        serializer.text(body);
                        serializer.endTag(null, "body");
                        //写date节点
                        serializer.startTag(null, "date");
                        serializer.text(date);
                        serializer.endTag(null, "date");
                        serializer.endTag(null, "sms");
                        Log.i(TAG, "address:" + address + "date:" + date + "body:" + body);
                    }
                    serializer.endTag(null, "smss");
                    serializer.endDocument();
                } catch (Exception e) {
                    Log.i(TAG, "这里");
                    e.printStackTrace();
                }
            }
        });
    }
}
