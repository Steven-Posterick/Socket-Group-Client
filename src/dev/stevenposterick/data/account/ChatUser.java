package dev.stevenposterick.data.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUser {

    private final String name;
    private static final Pattern USER_PATTERN = Pattern.compile("name='(.*)'");

    public ChatUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "name='" + name + "'";
    }

    public static ChatUser createFromString(String string){
        Matcher matcher = USER_PATTERN.matcher(string);
        String[] data = new String[1];

        if (!matcher.find()) {
            return null;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = matcher.group(i + 1);
        }

        return new ChatUser(data[0]);
    }
}
