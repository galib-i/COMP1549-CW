package common.util;

import common.model.Message;

public class MessageFormatter {
    private static final String SEPARATOR = "__";
    private static final String CONTENT_SEPARATOR = "::";
    
    public static String format(Message message) {
        String formattedMessage = message.getType() + SEPARATOR + message.getSender() + SEPARATOR;
        
        if (message.getContent() == null) {
            formattedMessage += "null";
        } else if (message.getContent() instanceof String) {
            formattedMessage += message.getContent();
        } else if (message.getContent() instanceof String[]) {
            String[] contentArray = (String[])message.getContent();
            formattedMessage += String.join(CONTENT_SEPARATOR, contentArray);
        }
        
        return formattedMessage;
    }
    
    public static Message parse(String messageString) {
        String[] parts = messageString.split(SEPARATOR, 3);        
        Message.Type type = Message.Type.valueOf(parts[0]);
        String sender = parts[1];
        String content = parts[2];
        
        if (content.equals("null")) {
            return new Message(type, sender, null);
        }
        
        return switch (type) {
            case USER_LIST, USER_DETAILS_RESPONSE -> 
                new Message(type, sender, content.split(CONTENT_SEPARATOR));
            default -> 
                new Message(type, sender, content);
        };
    }
}