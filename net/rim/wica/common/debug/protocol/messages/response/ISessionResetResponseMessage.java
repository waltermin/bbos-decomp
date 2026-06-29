package net.rim.wica.common.debug.protocol.messages.response;

public interface ISessionResetResponseMessage extends IResponseMessage {
   int FIELD_NEW_SESSION_ID = 1;

   void setNewSessionId(int var1);
}
