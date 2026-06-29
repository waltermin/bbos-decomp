package net.rim.wica.common.debug.protocol.messages.request;

public interface IApplicationCommandRequestMessage extends IRequestMessage {
   int FIELD_APPLICATION_ID;

   long getApplicationId();
}
