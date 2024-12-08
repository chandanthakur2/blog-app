package com.example.blog.context;

import com.example.blog.model.User;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserContext {

    private final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public User getCurrentUser() {
        return currentUser.get();
    }

    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public void clear() {
        currentUser.remove();
    }
}
