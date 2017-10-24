package com.example.infispace.data;


public class Story {
    private String sharedByPicUrl;
    private String sharedByName;
    private String storyTitle;
    private String storyUrl;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSharedByName() {
        return sharedByName;
    }

    public void setSharedByName(String sharedByName) {
        this.sharedByName = sharedByName;
    }

    public String getSharedByPicUrl() {
        return sharedByPicUrl;
    }

    public void setSharedByPicUrl(String sharedByPicUrl) {
        this.sharedByPicUrl = sharedByPicUrl;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public void setStoryUrl(String storyUrl) {
        this.storyUrl = storyUrl;
    }
}
