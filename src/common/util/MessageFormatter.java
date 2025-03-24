package common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.model.Message;


public class MessageFormatter {  
    public static String format(Message message) {
        String formattedMessage = "type=" + message.getType() + "&" + "sender=" + message.getSender() + "&" + "content=" + message.getContent();

        return formattedMessage;
    }
    
    public static Message parse(String messageString) {
        String[] parts = messageString.split("&", 3);

        Message.Type type = Message.Type.valueOf(parts[0].replace("type=", ""));
        String sender = parts[1].replace("sender=", "");
        String content = parts[2].replace("content=", "");

        Object parsedContent;

        if (type == Message.Type.USER_LIST) {
            Map<String, Map<String, String>> parsedMap = parseStringToNestedMap(content);
            parsedContent = parsedMap;
        } else if (type == Message.Type.USER_DETAILS_RESPONSE) {
            Map<String, String> parsedMap = parseStringToMap(content);
            parsedContent = parsedMap;
        } else {
            parsedContent = content;
        }

        return new Message(type, sender, parsedContent);
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
