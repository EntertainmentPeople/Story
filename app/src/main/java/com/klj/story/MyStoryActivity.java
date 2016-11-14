package com.klj.story;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.story.entity.StoryInfo;
import com.klj.story.entity.User;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MyStoryActivity extends AppCompatActivity {

    private ImageView ivMyStoryBack;
    private ImageView ivMyStoryEdit;
    private TextView tvMyStoryMineTime;
    private ImageView ivMyStoryPhoto;
    private TextView tvMyStoryNickName;
    private ImageView ivMyStorySex;
    private RelativeLayout rlMyStoryMine;
    private EditText etMyStoryEdit;
    private ScrollView svMyStory;
    private MyListView lvMyStory;
    private int page = 1;                                   //页面
    private User user;                                  //用户信息
    private List<StoryInfo> storyInfos = new ArrayList<>();   //存储获取到故事信息
    MyListViewAdapter myListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        initData();
        setAdapter();
        setListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null!=bundle) {
            user = (User) bundle.getSerializable("user");
        }else {
            user = UserUtils.getUser(MyStoryActivity.this);
        }
        setText();
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "myStorys")
                .params("uid", this.user.getId())
                .params("page", page)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        getJsonData(s);
                        handler.sendEmptyMessage(100);
                    }
                });
    }

    /**
     * 设置值
     */
    private void setText() {
        if(null!=user.getBirthday()){
            String mineTime = user.getBirthday().substring(2, 4);
            mineTime = mineTime.replace(mineTime.charAt(1), '0');
            tvMyStoryMineTime.setText("我是" + mineTime + "后");
        }

        Picasso.with(MyStoryActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PORTRAIT_PATH + user.getPortrait()).placeholder(R.drawable.icon_portrait).into(ivMyStoryPhoto);
        tvMyStoryNickName.setText(user.getNickName());
        String sex = user.getUserSex();
        if (sex.equals("0")) {
            ivMyStorySex.setImageResource(R.drawable.icon_man);
        } else if (sex.equals("1")) {
            ivMyStorySex.setImageResource(R.drawable.icon_woman);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myListViewAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 解析数据
     *
     * @param s
     */
    private void getJsonData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            int result = jsonObject.optInt("result");
            if (result == 1) {
                boolean tag = false;
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.optString("id");
                    String storyTime = object.optString("story_time");
                    String storyInfo = object.optString("story_info");
                    List<String> picList = new ArrayList<>();
                    JSONArray pics = object.optJSONArray("pics");
                    if (null != pics) {
                        for (int j = 0; j < pics.length(); j++) {
                            String pic = pics.optString(j);
                            picList.add(pic);
                        }
                    }
                    String uid = object.optString("uid");
                    String lng = object.optString("lng");
                    String lat = object.optString("lat");
                    String city = object.optString("city");
                    String readcount = object.optString("readcount");
                    String comment = object.optString("comment");
                    StoryInfo story = new StoryInfo(id, storyTime, storyInfo, picList, uid, lng, lat, city, readcount, comment,user);
                    storyInfos.add(story);
                }
            }
            Toast.makeText(MyStoryActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void setListener() {
        ivMyStoryBack.setOnClickListener(new MyOnClickListener());
        ivMyStoryEdit.setOnClickListener(new MyOnClickListener());
        rlMyStoryMine.setOnClickListener(new MyOnClickListener());
        //svMyStory.setOnScrollChangeListener(new MyOnScrollChangeListener());
        lvMyStory.setOnItemClickListener(new MyListViewItemOnClickListener());
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        myListViewAdapter = new MyListViewAdapter(MyStoryActivity.this);
        lvMyStory.setAdapter(myListViewAdapter);
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivMyStoryBack = (ImageView) findViewById(R.id.iv_mystory_back);
        ivMyStoryEdit = (ImageView) findViewById(R.id.iv_mystory_edit);
        rlMyStoryMine = (RelativeLayout) findViewById(R.id.rl_mystory_mine);
        tvMyStoryMineTime = (TextView) findViewById(R.id.tv_mystory_minetime);
        ivMyStoryPhoto = (ImageView) findViewById(R.id.iv_mystory_photo);
        tvMyStoryNickName = (TextView) findViewById(R.id.tv_mystory_nickname);
        ivMyStorySex = (ImageView) findViewById(R.id.iv_mystory_sex);
        etMyStoryEdit = (EditText) findViewById(R.id.et_mystory_edit);
        lvMyStory = (MyListView) findViewById(R.id.lv_mystory_story);
        svMyStory= (ScrollView) findViewById(R.id.sv_mystory);
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
            return storyInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return storyInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                vp = new ViewHelper();
                convertView = inflater.from(context).inflate(R.layout.layout_mystory_listview_item, parent, false);
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
     * 为ListView里面的控件设置值
     */
    private void setValue(ViewHelper vp, int position) {
        StoryInfo storyInfo = storyInfos.get(position);
        vp.tvDate.setText(Utils.getYearAndMonth(storyInfo.getStoryTime()) + "月");
        vp.tvTime.setText(Utils.getDayAndHourAndMinute(storyInfo.getStoryTime()));
        vp.tvInfo.setText(storyInfo.getStoryInfo());
        List<String> pics = storyInfo.getPics();
        if (pics != null) {
            vp.gvImg.setAdapter(new MyGridAdapter(pics));
        }
        vp.tvTalk.setText(storyInfo.getComment());
        vp.tvHeart.setText(storyInfo.getReadcount());
    }

    /**
     * 找到ListView布局文件中控件
     *
     * @param vp
     * @param convertView
     */
    private void findListView(ViewHelper vp, View convertView) {
        vp.ivShare = (ImageView) convertView.findViewById(R.id.iv_news_list_share);
        vp.tvDate = (TextView) convertView.findViewById(R.id.tv_mystory_list_date);
        vp.tvTime = (TextView) convertView.findViewById(R.id.tv_mystory_list_time);
        vp.tvInfo = (TextView) convertView.findViewById(R.id.tv_mystory_list_info);
        vp.llTalk = (LinearLayout) convertView.findViewById(R.id.ll_mystory_list_talk);
        vp.tvTalk = (TextView) convertView.findViewById(R.id.tv_mystory_list_talk);
        vp.llHeart = (LinearLayout) convertView.findViewById(R.id.ll_mystory_list_heart);
        vp.tvHeart = (TextView) convertView.findViewById(R.id.tv_mystory_list_heart);
        vp.gvImg = (GridView) convertView.findViewById(R.id.gv_mystory_list_img);
    }


    /**
     * 模板
     */
    private class ViewHelper {
        public ImageView ivShare, ivImage;
        public GridView gvImg;
        public TextView tvDate, tvTime, tvInfo, tvTalk, tvHeart;
        public LinearLayout llTalk, llHeart;
    }

    /**
     * 为控件设置点击监听事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.iv_mystory_back:
                    finish();
                    break;
                case R.id.iv_mystory_edit:
                    intent = new Intent(MyStoryActivity.this, NewStoryActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_mystory_mine:
                    intent = new Intent(MyStoryActivity.this, MineActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    }

    /**
     * 为GridView设置适配器
     */
    private class MyGridAdapter extends BaseAdapter {
        List<String> pics = new ArrayList<>();
        ViewHelper vp;

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
            if (convertView == null) {
                vp = new ViewHelper();
                convertView = LayoutInflater.from(MyStoryActivity.this).inflate(R.layout.layout_news_listview_gridview_item, parent, false);
                vp.ivImage = (ImageView) convertView.findViewById(R.id.iv_news_list_grid_img);
                convertView.setTag(vp);
            } else {
                vp = (ViewHelper) convertView.getTag();
            }
            Picasso.with(MyStoryActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PICYURE_PATH + pics.get(position)).placeholder(R.drawable.icon_portrait).into(vp.ivImage);
            return convertView;
        }
    }

    /**
     * 为ListView什么Item点击事件
     */
    private class MyListViewItemOnClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MyStoryActivity.this, StoryDetailsActivity.class);
            Bundle bundle = new Bundle();
            StoryInfo storyInfo = storyInfos.get(position);
            bundle.putSerializable("story", storyInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


}

