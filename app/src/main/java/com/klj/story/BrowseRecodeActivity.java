package com.klj.story;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.lang.UCharacter;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.story.entity.Comment;
import com.klj.story.entity.StoryInfo;
import com.klj.story.entity.User;
import com.klj.story.sql.DataBaseHelper;
import com.klj.story.utils.StoryUtil;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.Utils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 浏览记录页面
 */
public class BrowseRecodeActivity extends AppCompatActivity {
    private ImageView ivBack;
    private ImageView ivEdit;
    private ListView lvStory;
    private List<String> data = new ArrayList<>();
    MyListViewAdapter myListViewAdapter;
    private int page = 1;
    List<StoryInfo> storyInfos = new ArrayList<>();
    DataBaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_recode);
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
     * 找到控件
     */
    private void findView() {
        ivBack = (ImageView) findViewById(R.id.iv_browse_back);
        ivEdit = (ImageView) findViewById(R.id.iv_browse_edit);
        lvStory = (ListView) findViewById(R.id.lv_browse_story);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        getRecode();
    }

    /**
     * 获取记录数据
     */
    private void getRecode() {
        try {


            dbHelper = new DataBaseHelper(BrowseRecodeActivity.this);
            Cursor cursor = dbHelper.query("story", null, null, "readTime desc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Log.d("data-id", cursor.getString(0));
                    byte data[] = cursor.getBlob(cursor.getColumnIndex("storyInfo"));
                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                        StoryInfo storyInfo = (StoryInfo) inputStream.readObject();
                        storyInfos.add(storyInfo);
                        inputStream.close();
                        arrayInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("Persons-Count", Integer.toString(storyInfos.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return storyInfos;
    }


    /**
     * 设置监听
     */
    private void setListener() {
        lvStory.setOnItemClickListener(new MyListViewItemClickListener());
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        myListViewAdapter = new MyListViewAdapter(BrowseRecodeActivity.this);
        lvStory.setAdapter(myListViewAdapter);
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
                convertView = inflater.from(context).inflate(R.layout.layout_news_listview_item, parent, false);
                findListView(vp, convertView);
                convertView.setTag(vp);
            } else {
                vp = (ViewHelper) convertView.getTag();
            }
            setValue(vp, position);
            setItemListener(vp, position);
            return convertView;
        }
    }

    private void setItemListener(ViewHelper vp, int position) {
        vp.llMineInfo.setOnClickListener(new MyOnClickListener(position));
        vp.llTalk.setOnClickListener(new MyOnClickListener(position));
        vp.llHeart.setOnClickListener(new MyOnClickListener(position));
        vp.ivShare.setOnClickListener(new MyOnClickListener(position));
    }

    /**
     * 为ListView里面的控件设置值
     */
    private void setValue(ViewHelper vp, int position) {
        StoryInfo storyInfo = storyInfos.get(position);
        User user = storyInfo.getUser();
        Picasso.with(BrowseRecodeActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PORTRAIT_PATH + user.getPortrait()).placeholder(R.drawable.icon_portrait).into(vp.ivPhoto);
        vp.tvNickName.setText(user.getNickName());
        vp.tvTime.setText(Utils.toTransferTime(storyInfo.getStoryTime()));
        vp.tvCity.setText(storyInfo.getCity());
        vp.tvInfo.setText(storyInfo.getStoryInfo());
        List<String> pics = storyInfo.getPics();
        if (pics != null) {
            vp.grImage.setAdapter(new MyGridAdapter(pics));
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
        vp.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_news_list_photo);
        vp.grImage = (GridView) convertView.findViewById(R.id.gr_news_list_img);
        vp.ivShare = (ImageView) convertView.findViewById(R.id.iv_news_list_share);
        vp.tvNickName = (TextView) convertView.findViewById(R.id.tv_news_list_nickname);
        vp.tvTime = (TextView) convertView.findViewById(R.id.tv_news_list_time);
        vp.tvCity = (TextView) convertView.findViewById(R.id.tv_news_list_city);
        vp.tvInfo = (TextView) convertView.findViewById(R.id.tv_news_list_info);
        vp.llTalk = (LinearLayout) convertView.findViewById(R.id.ll_news_list_talk);
        vp.tvTalk = (TextView) convertView.findViewById(R.id.tv_news_list_talk);
        vp.llHeart = (LinearLayout) convertView.findViewById(R.id.ll_news_list_heart);
        vp.tvHeart = (TextView) convertView.findViewById(R.id.tv_news_list_heart);
        vp.llMineInfo = (LinearLayout) convertView.findViewById(R.id.ll_news_list_mineinfo);
    }


    /**
     * 模板
     */
    private class ViewHelper {
        public ImageView ivPhoto, ivShare, ivImage;
        public TextView tvNickName, tvTime, tvCity, tvInfo, tvTalk, tvHeart;
        public LinearLayout llMineInfo, llTalk, llHeart;
        public GridView grImage;
    }

    /**
     * 为ListView设置Item点击监听
     */
    private class MyListViewItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toNextActivity(position);
        }
    }

    private void toNextActivity(int position) {
        Intent intent = new Intent(BrowseRecodeActivity.this, StoryDetailsActivity.class);
        Bundle bundle = new Bundle();
        StoryInfo storyInfo = storyInfos.get(position);
        bundle.putSerializable("story", storyInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 为ListView中的字控件设置点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        int position = 0;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.ll_news_list_mineinfo:
                    intent = new Intent(BrowseRecodeActivity.this, MyStoryActivity.class);
                    Bundle bundle = new Bundle();
                    User user = storyInfos.get(position).getUser();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.ll_news_list_talk:
                    toNextActivity(position);
                    break;
                case R.id.ll_news_list_heart:

                    break;
                case R.id.iv_news_list_share:
                    break;
            }

        }
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
                convertView = LayoutInflater.from(BrowseRecodeActivity.this).inflate(R.layout.layout_news_listview_gridview_item, parent, false);
                vp.ivImage = (ImageView) convertView.findViewById(R.id.iv_news_list_grid_img);
                convertView.setTag(vp);
            } else {
                vp = (ViewHelper) convertView.getTag();
            }
            Picasso.with(BrowseRecodeActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PICYURE_PATH + pics.get(position)).placeholder(R.drawable.icon_portrait).into(vp.ivImage);
            return convertView;
        }
    }

}
