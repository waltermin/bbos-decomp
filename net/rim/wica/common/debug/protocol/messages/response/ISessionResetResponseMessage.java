package net.rim.wica.common.debug.protocol.messages.response;

public interface ISessionResetResponseMessage extends IResponseMessage {
   int FIELD_NEW_SESSION_ID;

   void setNewSessionId(int var1);
}
