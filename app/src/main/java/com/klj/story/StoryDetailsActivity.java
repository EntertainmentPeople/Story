package com.klj.story;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.story.entity.Comment;
import com.klj.story.entity.StoryInfo;
import com.klj.story.entity.User;
import com.klj.story.sql.DataBaseHelper;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.UserUtils;
import com.klj.story.utils.Utils;
import com.klj.story.view.MyListView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 故事详情页面
 */
public class StoryDetailsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivDot;
    private ImageView ivPortrailt;
    private TextView tvNickName;
    private TextView tvTime;
    private TextView tvCity;
    private TextView tvInfo;
    private GridView grImage;
    //private ListView lvTalk;
    private MyListView lvTalk;
    private TextView tvTalk;
    private TextView tvHeart;
    private ImageView ivFace;
    private ImageView ivSend;
    private LinearLayout llTalk;
    private LinearLayout llHeart;
    private EditText etTalk;
    private List<String> data = new ArrayList<>();
    MyListViewAdapter myListViewAdapter;            //ListView的适配器
    private int page = 1;                           //页面
    List<Comment> comments = new ArrayList<>();      //获取到的评论集合
    StoryInfo storyInfo = null;                      //故事信息
    User user;                                         //用户信息
    DataBaseHelper dbHelper=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        initData();
        saveRecode();
        setAdapter();
        setListener();
    }

    /**
     * 保存浏览记录
     */
    private void saveRecode() {
        dbHelper=new DataBaseHelper(StoryDetailsActivity.this);
        long time = (int) System.currentTimeMillis();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(storyInfo);
            objectOutputStream.flush();
            byte[] data = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            if(isExit()){
                updateRecode(data,time);
            }else {
                insertRecode(data,time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改记录
     * @param data
     * @param time
     */
    private void updateRecode(byte[] data, long time){
        ContentValues values=new ContentValues();
        values.put("storyInfo", data);
        values.put("readTime",time);
        dbHelper.update("story",values,"sid",new String[]{storyInfo.getId()});
    }
    /**
     *
     */
    private void insertRecode(byte[] data, long time){
        ContentValues values=new ContentValues();
        values.put("sid",storyInfo.getId());
        values.put("storyInfo",data);
        values.put("readTime",time);
        dbHelper.insert("story",values);
    }

    /**
     * 判断该故事是否已浏览
     */
    private boolean isExit(){
        Cursor cursor = dbHelper.query("story", "sid=?", new String[]{storyInfo.getId()},null);
        if (cursor != null) {
            return true;
        }
        return false;
    }

    /**
     * 设置监听
     */
    private void setListener() {
        ivBack.setOnClickListener(new MyOnClickListener());
        ivFace.setOnClickListener(new MyOnClickListener());
        ivSend.setOnClickListener(new MyOnClickListener());
        lvTalk.setOnScrollListener(new MyScrollListener());
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        myListViewAdapter = new MyListViewAdapter(StoryDetailsActivity.this);
        lvTalk.setAdapter(myListViewAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        storyInfo = (StoryInfo) bundle.getSerializable("story");
        resdStory();
        getComments(storyInfo.getId());
    }

    /**
     * 更新阅读故事
     */
    private void resdStory() {
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "readStorys")
                .params("sid", storyInfo.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                storyInfo.setReadcount((Integer.parseInt(storyInfo.getReadcount()) + 1) + "");
                                handler.sendEmptyMessage(300);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取评论数据
     *
     * @param sId
     */
    private void getComments(String sId) {
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "getComments")
                .params("sid", sId)
                .params("page", page)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        getJsonData(s);
                        handler.sendEmptyMessage(200);
                    }
                });
    }

    /**
     * 解析数据
     */
    private void getJsonData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            int result = jsonObject.optInt("result");
            if (result == 1) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.optJSONObject(i);
                    String id = object.optString("id");
                    String comments = object.optString("comments");
                    String time = object.optString("time");
                    String sid = object.optString("sid");
                    String uid = object.optString("uid");
                    String cid = object.optString("cid");
                    // JSONObject comm = object.optJSONObject("comm");


                    JSONObject user1 = object.optJSONObject("user");
                    String userId = user1.optString("id");
                    String username = user1.optString("username");
                    String userSex = user1.optString("usersex");
                    String userEmail = user1.optString("useremail");
                    String nickName = user1.optString("nickname");
                    String birthday = user1.optString("birthday");
                    String portrait = user1.optString("portrait");
                    String signature = user1.optString("signature");
                    User user = new User(userId, username, userSex, userEmail, nickName, birthday, portrait, signature);
                    Comment comment = new Comment(id, comments, time, sid, uid, cid, user);
                    StoryDetailsActivity.this.comments.add(comment);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    myListViewAdapter.notifyDataSetChanged();
                    break;
                case 300:
                    setText();
                    break;
            }

        }
    };

    /**
     * 设置值
     */
    private void setText() {
        user = storyInfo.getUser();
        Picasso.with(StoryDetailsActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PORTRAIT_PATH + user.getPortrait()).placeholder(R.drawable.icon_portrait).into(ivPortrailt);
        tvNickName.setText(user.getNickName());
        tvTime.setText(Utils.toTransferTime(storyInfo.getStoryTime()));
        tvCity.setText(storyInfo.getCity());
        tvInfo.setText(storyInfo.getStoryInfo());
        tvTalk.setText(storyInfo.getComment());
        tvHeart.setText(storyInfo.getReadcount());
        List<String> pics = storyInfo.getPics();
        if (pics != null) {
            grImage.setAdapter(new MyGridAdapter(pics));
        }
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivBack = (ImageView) findViewById(R.id.iv_storydetials_back);
        ivDot = (ImageView) findViewById(R.id.iv_storydetials_dot);
        ivPortrailt = (ImageView) findViewById(R.id.iv_storydetials_photo);
        tvNickName = (TextView) findViewById(R.id.tv_storydetials_nickname);
        tvTime = (TextView) findViewById(R.id.tv_storydetials_time);
        tvCity = (TextView) findViewById(R.id.tv_storydetials_city);
        tvInfo = (TextView) findViewById(R.id.tv_storydetials_info);
        grImage = (GridView) findViewById(R.id.gr_storydetails_img);
        //lvTalk = (ListView) findViewById(R.id.lv_storydetials_talk);
        lvTalk = (MyListView) findViewById(R.id.lv_storydetials_talk);
        tvTalk = (TextView) findViewById(R.id.tv_storydetials_talk);
        tvHeart = (TextView) findViewById(R.id.tv_storydetials_heart);
        llTalk = (LinearLayout) findViewById(R.id.ll_storydetials_talk);
        llHeart = (LinearLayout) findViewById(R.id.ll_storydetials_heart);
        ivFace = (ImageView) findViewById(R.id.iv_storydetials_face);
        ivSend = (ImageView) findViewById(R.id.iv_storydetials_send);
        etTalk = (EditText) findViewById(R.id.et_speak);
    }


    /**
     * 为GridView设置适配器
     */
    private class MyGridAdapter extends BaseAdapter {
        List<String> pics = new ArrayList<>();

        public MyGridAdapter(List<String> pics) {
            this.pics = pics;
        }

        @Override
        public int getCount() {
            return pics.size();
        }

        @Override
        public Object getItem(int position) {
            return pics.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHelper vp;
            if (convertView == null) {
                vp = new ViewHelper();
                convertView = LayoutInflater.from(StoryDetailsActivity.this).inflate(R.layout.layout_news_listview_gridview_item, parent, false);
                vp.ivImage = (ImageView) convertView.findViewById(R.id.iv_news_list_grid_img);
                convertView.setTag(vp);
            } else {
                vp = (ViewHelper) convertView.getTag();
            }
            Picasso.with(StoryDetailsActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PICYURE_PATH + pics.get(position)).tag(StoryDetailsActivity.this).placeholder(R.drawable.icon_portrait).into(vp.ivImage);
            return convertView;
        }
    }

    private class ViewHelper {
        public ImageView ivPortrailt, ivImage, ivCommNum;
        public TextView tvNickName, tvTime, tvTalk;
        public LinearLayout llMineInfo;
    }

    /**
     * 为ListView设置适配器
     */
    private class MyListViewAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ViewHelper vp;

        public MyListViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                vp = new ViewHelper();
                convertView = inflater.from(context).inflate(R.layout.layout_story_listview_item, parent, false);
                findListView(vp, convertView);
                convertView.setTag(vp);
            } else {
                vp = (ViewHelper) convertView.getTag();
            }
            setValue(vp, position);
            return convertView;
        }
    }

    /**
     * 设置值
     *
     * @param vp
     * @param position
     */
    private void setValue(ViewHelper vp, int position) {
        Comment comment = comments.get(position);
        User user = comment.getUser();
        Picasso.with(StoryDetailsActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PICYURE_PATH + user.getPortrait()).placeholder(R.drawable.icon_portrait).into(vp.ivPortrailt);
        vp.tvNickName.setText(user.getNickName());
        vp.tvTalk.setText(comment.getComments());
        vp.tvTime.setText(Utils.toTransferTime(comment.getTime()));
    }

    /**
     * 找到ListView中的控件
     *
     * @param vp
     * @param convertView
     */
    private void findListView(ViewHelper vp, View convertView) {
        vp.ivPortrailt = (ImageView) convertView.findViewById(R.id.iv_story_list_photo);
        vp.tvNickName = (TextView) convertView.findViewById(R.id.tv_story_list_nickname);
        vp.tvTalk = (TextView) convertView.findViewById(R.id.tv_story_list_comment);
        vp.ivCommNum = (ImageView) convertView.findViewById(R.id.iv_story_list_commnum);
        vp.tvTime = (TextView) convertView.findViewById(R.id.tv_story_list_time);
    }

    /**
     * 为控件设置点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_storydetials_back:
                    finish();
                    break;
                case R.id.iv_storydetials_face:

                    break;
                case R.id.iv_storydetials_send:
                    send();
                    break;
            }
        }
    }

    /**
     * 发送评论
     */
    private void send() {
        final User user = UserUtils.getUser(StoryDetailsActivity.this);
        String talk = etTalk.getText().toString().trim();
        if (!"".equals(talk) && null != talk) {
            try {
                OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "sendComment")
                        .params("uid", user.getId())
                        .params("sid", storyInfo.getId())
                        .params("userpass", user.getUserPass())
                        .params("comments", talk)
                        .params("cid", 0)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int result = jsonObject.optInt("result");
                                    if (result == 1) {
                                        JSONObject data = jsonObject.optJSONObject("data");
                                        String id = data.optString("id");
                                        String comments = data.optString("comments");
                                        String time = data.optString("time");
                                        String sid = data.optString("sid");
                                        String uid = data.optString("uid");
                                        Comment comment = new Comment(id, comments, time, sid, uid, null, user);
                                        StoryDetailsActivity.this.comments.add(comment);
                                        etTalk.setText("");
                                        handler.sendEmptyMessage(200);
                                    }
                                    Toast.makeText(StoryDetailsActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 为ListView设置滑动事件
     */
    private class MyScrollListener implements AbsListView.OnScrollListener {
        boolean isBottom=false;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(isBottom&&scrollState==SCROLL_STATE_IDLE){
                page++;
                initData();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            isBottom=firstVisibleItem+visibleItemCount==totalItemCount;
        }
    }
}
