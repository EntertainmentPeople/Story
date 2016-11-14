package com.klj.story;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.klj.story.entity.StoryInfo;
import com.klj.story.entity.User;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.UserUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 发表新故事页面
 */
public class NewStoryActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivSend;
    private EditText etStory;
    private ListView lvStory;
    private Button btnAlbum;
    private Button btnCamera;
    User user;                                      //用户信息
    List<String> paths=new ArrayList<>();           //保存本地图片地址的集合
    MyListViewAdapter myListViewAdapter;            //ListView的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        setAdapter();
        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        ivBack.setOnClickListener(new MyOnClickListener());
        ivSend.setOnClickListener(new MyOnClickListener());
        btnAlbum.setOnClickListener(new MyOnClickListener());
        btnCamera.setOnClickListener(new MyOnClickListener());
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        myListViewAdapter=new MyListViewAdapter();
        lvStory.setAdapter(myListViewAdapter);
    }


    Uri uri = null;     //图片的uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            getAbsoluteImagePath(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 选择图片
     */
    private void choosePic() {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
    }

    /**
     * 更新适配器
     */
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myListViewAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 通过uri获取文件的绝对路径
     * @param uri
     * @return
     */
    private void getAbsoluteImagePath(Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        if (uri != null) {
            Cursor cursor = NewStoryActivity.this.managedQuery(uri, proj,
                    null,
                    null,
                    null);
            //Cursor cursor=getContentResolver().query(uri,proj,null,null,null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    paths.add(cursor.getString(column_index));
                    handler.sendEmptyMessage(100);
                }
            }
        }
        //return imagePath;
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivBack = (ImageView) findViewById(R.id.iv_newstory_back);
        ivSend = (ImageView) findViewById(R.id.iv_newstory_send);
        etStory = (EditText) findViewById(R.id.et_newstory_edit);
        lvStory = (ListView) findViewById(R.id.lv_newstory_img);
        btnAlbum = (Button) findViewById(R.id.btn_newstory_album);
        btnCamera = (Button) findViewById(R.id.btn_newstory_camera);
    }

    /**
     * 为ListView设置适配器
     */
    private class MyListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public Object getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHelper vp=null;
            if(convertView==null){
                vp=new ViewHelper();
                convertView=LayoutInflater.from(NewStoryActivity.this).inflate(R.layout.layout_newstory_listview_item,parent,false);
                vp.ivPic= (ImageView) convertView.findViewById(R.id.iv_newstory_list_img);
                convertView.setTag(vp);
            }else {
                vp= (ViewHelper) convertView.getTag();
            }
            vp.ivPic.setImageBitmap(BitmapFactory.decodeFile(paths.get(position)));
            return convertView;
        }
    }

    /**
     * 模板
     */
    private class ViewHelper{
        ImageView ivPic;
    }

    /**
     * 设置点击监听事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_newstory_back:
                    finish();
                    break;
                case R.id.iv_newstory_send:
                    send();
                    break;
                case R.id.btn_newstory_album:
                    choosePic();

                    break;
                case R.id.btn_newstory_camera:
                    break;
            }
        }
    }

    /**
     * 发表故事
     */
    private void send() {
        user= UserUtils.getUser(NewStoryActivity.this);
        List<File> files=new ArrayList<>();
        for (String path : paths) {
            files.add(new File(path));
        }
        String story = etStory.getText().toString().trim();
        if(!"".equals(story)&&null!=story) {
            OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "sendStory")
                    .params("uid", user.getId())
                    .params("story_info", story)
                    .params("userpass",user.getUserPass() )
                    .params("lng", 30.01)
                    .params("lat", 105.56)
                    .params("city", "广安")
                    .addFileParams("photo[]",files)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.e("----",s);
                            getJsonData(s);
                            finish();
                        }
                    });
        }
    }

    /**
     * 解析数据
     * @param s
     */
    private void getJsonData(String s){
        try {
            JSONObject jsonObject=new JSONObject(s);
            int result = jsonObject.optInt("result");
            if(result==1){
                JSONObject data = jsonObject.optJSONObject("data");
                String id = data.optString("id");
                String storyTime = data.optString("story_time");
                String storyInfo = data.optString("story_info");
                List<String> picList = new ArrayList<>();
                JSONArray pics = data.optJSONArray("pics");
                if (null != pics) {
                    for (int j = 0; j < pics.length(); j++) {
                        String pic = pics.optString(j);
                        picList.add(pic);
                    }
                }
                String uid = data.optString("uid");
                String lng = data.optString("lng");
                String lat = data.optString("lat");
                String city = data.optString("city");
            }
            Toast.makeText(NewStoryActivity.this,jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}