package itstep.learning.model;

import itstep.learning.data.entity.Story;
import itstep.learning.data.entity.User;

import java.util.TimeZone;

public class StoryViewModel {
    private Story story;
    private User user;

    private StoryViewModel replyStory;

    public StoryViewModel(Story story, User user, StoryViewModel replyStory) {
        this.story = story;
        this.user = user;
        this.replyStory = replyStory;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public StoryViewModel getReplyStory() {
        return replyStory;
    }

    public void setReplyStory(StoryViewModel replyStory) {
        this.replyStory = replyStory;
    }
}
