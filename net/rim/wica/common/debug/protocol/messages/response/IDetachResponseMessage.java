package net.rim.wica.common.debug.protocol.messages.response;

public interface IDetachResponseMessage extends IResponseMessage {
   int FIELD_SESSION_ID = 1;

   void setSessionId(int var1);
}
