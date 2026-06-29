package net.rim.wica.common.debug.protocol.messages.request;

public interface ISessionResetRequestMessage extends IRequestMessage {
   int FIELD_NEW_SESSION_ID = 1;

   int getNewSessionId();
}
