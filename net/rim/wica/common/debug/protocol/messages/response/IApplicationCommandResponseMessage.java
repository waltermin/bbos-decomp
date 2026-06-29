package net.rim.wica.common.debug.protocol.messages.response;

public interface IApplicationCommandResponseMessage extends IResponseMessage {
   int FIELD_APPLICATION_ID = 1;

   void setApplicationId(long var1);
}
