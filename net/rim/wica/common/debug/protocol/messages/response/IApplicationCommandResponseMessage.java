package net.rim.wica.common.debug.protocol.messages.response;

public interface IApplicationCommandResponseMessage extends IResponseMessage {
   int FIELD_APPLICATION_ID;

   void setApplicationId(long var1);
}
