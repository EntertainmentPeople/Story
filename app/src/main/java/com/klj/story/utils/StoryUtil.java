package com.klj.story.utils;

import com.klj.story.entity.StoryInfo;

import java.util.List;

/**
 * 故事信息工具类
 */
public class StoryUtil {
    private static List<StoryInfo> storyInfos;

    public static List<StoryInfo> getStoryInfo() {
        return storyInfos;
    }

    public static void setStoryInfos(List<StoryInfo> storyInfos) {
        StoryUtil.storyInfos = storyInfos;
    }
}
