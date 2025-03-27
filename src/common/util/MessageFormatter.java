package common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.model.Message;


public class MessageFormatter {  
    public static String format(Message message) {
        return "type=%s&sender=%s&recipient=%s&content=%s".formatted(message.getType(), message.getSender(), message.getRecipient(), message.getContent());
}
    
    public static Message parse(String messageString) {
        String[] parts = messageString.split("&", 4);

        Message.Type type = Message.Type.valueOf(parts[0].replace("type=", ""));
        String sender = parts[1].replace("sender=", "");
        String recipient = parts[2].replace("recipient=", "");
        String content = parts[3].replace("content=", "");

        Object parsedContent;

        switch (type) {
            case USER_LIST -> parsedContent = parseStringToNestedMap(content);
            case USER_DETAILS_RESPONSE -> parsedContent = parseStringToMap(content);
            default -> parsedContent = content;
        }

        return new Message(type, sender, recipient, parsedContent);
    }

    private static Map<String, Map<String, String>> parseStringToNestedMap(String input) {
        Map<String, Map<String, String>> outerMap = new LinkedHashMap<>();

        Pattern outerPattern = Pattern.compile("(\\w+)=\\{(.*?)}");
        Matcher outerMatcher = outerPattern.matcher(input);

        while (outerMatcher.find()) {
            String outerKey = outerMatcher.group(1);
            String innerString = outerMatcher.group(2);

            Map<String, String> nestedMap = parseStringToMap(innerString);
            outerMap.put(outerKey, nestedMap);
        }

        return outerMap;
    }

    private static Map<String, String> parseStringToMap(String string) {
        if (string.startsWith("{") && string.endsWith("}")) {
            string = string.substring(1, string.length() - 1);
        }

        Map<String, String> innerMap = new LinkedHashMap<>();

        Pattern nestedPattern = Pattern.compile("(\\w+)=([^,]+)");
        Matcher nestedMatcher = nestedPattern.matcher(string);

        while (nestedMatcher.find()) {
            String key = nestedMatcher.group(1);
            String value = nestedMatcher.group(2).trim();
            innerMap.put(key, value);
        }

        return innerMap;
    }
}
