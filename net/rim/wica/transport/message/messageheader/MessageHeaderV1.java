package net.rim.wica.transport.message.messageheader;

public interface MessageHeaderV1 {
   int getMessageCode();

   void setMessageCode(int var1);

   boolean isNotification();

   boolean backgroundProcessingEnabled();

   boolean keepLast();

   void setNotification(boolean var1, boolean var2);

   int getMessageLength();
}
