package net.rim.wica.common.debug.protocol.messages;

public interface IMessageEnvelope extends ISerializableMessage, ISerializableMessageHeader {
   int MESSAGESET_REQUEST = 0;
   int MESSAGESET_RESPONSE = 1;
   int MESSAGESET_TARGET_EVENT = 2;
   int MESSAGESET_ENUM_LIMIT = 3;
   String[] MESSAGE_SET_NAMES;

   int getMessageSet();

   int getMessageId();

   void setMessageId(int var1);

   void setSessionId(int var1);

   byte[] getBody();

   int getMessageType();
}
