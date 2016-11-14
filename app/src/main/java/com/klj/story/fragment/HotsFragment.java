package com.klj.story.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.story.MyStoryActivity;
import com.klj.story.R;
import com.klj.story.StoryDetailsActivity;
import com.klj.story.entity.StoryInfo;
import com.klj.story.entity.User;
import com.klj.story.utils.StoryUtil;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.Utils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 最热的Fragment
 */
public class HotsFragment extends Fragment {
    private ListView lvShow;
    private List<String> data = new ArrayList<>();
    MyListViewAdapter myListViewAdapter;
    private int page = 1;
    List<StoryInfo> storyInfos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hots, container, false);
        findView(view);
        return view;

    }

    /**
     * 找到fragment里面的控件
     *
     * @param view
     */
    private void findView(View view) {
        lvShow = (ListView) view.findViewById(R.id.lv_hots_show);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initData();
        addView();
        setAdapter();
        setListener();
    }

    /**
     * 为ListView添加头和尾
     */
    private void addView() {

    }

    /**
     * 初始化数据
     */
    private void initData() {
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "getStorys")
                .params("type", "hot")
                .params("page", page)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        getJsonData(s);
                        StoryUtil.setStoryInfos(storyInfos);
                        handler.sendEmptyMessage(100);
                    }
                });
    }

    /**
     * 更新适配器
     */
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
                    StoryInfo story = new StoryInfo(id, storyTime, storyInfo, picList, uid, lng, lat, city, readcount, comment, user);
                    storyInfos.add(story);
                }

            }
            Toast.makeText(getActivity(), jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听
     */
    private void setListener() {
        lvShow.setOnItemClickListener(new MyListViewItemClickListener());
        lvShow.setOnScrollListener(new MyListViewScrollListener());
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        myListViewAdapter = new MyListViewAdapter(getActivity());
        lvShow.setAdapter(myListViewAdapter);
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
            setItemListener(vp,position);
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
        Picasso.with(getActivity()).load(UrlUtils.ROOT_PATH + UrlUtils.PORTRAIT_PATH + user.getPortrait()).placeholder(R.drawable.icon_portrait).into(vp.ivPhoto);
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
        //setItemListener(vp);
    }

   /* private void setItemListener(ViewHelper vp) {
        vp.llMineInfo.setOnClickListener(new MyOnClickListener());
    }*/

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

    private void toNextActivity(int position){
        Intent intent = new Intent(getActivity(), StoryDetailsActivity.class);
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
        int position=0;
        public MyOnClickListener(int position){
            this.position=position;
        }
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.ll_news_list_mineinfo:
                    intent = new Intent(getActivity(), MyStoryActivity.class);
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
     * 为ListView设置滑动监听
     */
    private class MyListViewScrollListener implements AbsListView.OnScrollListener {
        private boolean isBottom;
        private boolean isTop = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                page++;
                initData();
                myListViewAdapter.notifyDataSetChanged();
            } else if (isTop && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                page = 1;
                initData();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            isBottom = firstVisibleItem + visibleItemCount == totalItemCount;

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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_news_listview_gridview_item, parent, false);
                vp.ivImage = (ImageView) convertView.findViewById(R.id.iv_news_list_grid_img);
                convertView.setTag(vp);
            } else {
                vp = (ViewHelper) convertView.getTag();
            }
            Picasso.with(getActivity()).load(UrlUtils.ROOT_PATH + UrlUtils.PICYURE_PATH + pics.get(position)).tag(getActivity()).placeholder(R.drawable.icon_portrait).into(vp.ivImage);
            return convertView;
        }
    }


}
