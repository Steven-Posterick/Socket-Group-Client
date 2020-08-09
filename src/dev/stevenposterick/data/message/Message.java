package dev.stevenposterick.data.message;

import dev.stevenposterick.core.Controller;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private final String from;
    private final String message;
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("from='(.*)', message='(.*)'");
    private final String time;

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
        this.time = Controller.timeFormatter.format(LocalDateTime.now());
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "from='" + from + '\'' +
                ", message='" + message + '\'';
    }

    public static Message createFromString(String string){
        Matcher matcher = MESSAGE_PATTERN.matcher(string);
        String[] data = new String[2];

        if (!matcher.find()) {
            return null;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = matcher.group(i + 1);
        }

        return new Message(data[0], data[1]);
    }
}
