package net.rim.wica.common.debug.protocol.messages.request;

public interface IDetachRequestMessage extends IRequestMessage {
   int FIELD_SESSION_ID;

   int getSessionId();
}
