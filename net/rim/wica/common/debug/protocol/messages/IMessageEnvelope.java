package net.rim.wica.common.debug.protocol.messages;

public interface IMessageEnvelope extends ISerializableMessage, ISerializableMessageHeader {
   int MESSAGESET_REQUEST;
   int MESSAGESET_RESPONSE;
   int MESSAGESET_TARGET_EVENT;
   int MESSAGESET_ENUM_LIMIT;
   String[] MESSAGE_SET_NAMES;

   int getMessageSet();

   int getMessageId();

   void setMessageId(int var1);

   void setSessionId(int var1);

   byte[] getBody();

   int getMessageType();
}
