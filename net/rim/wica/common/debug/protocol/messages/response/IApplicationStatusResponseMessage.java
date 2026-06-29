package net.rim.wica.common.debug.protocol.messages.response;

public interface IApplicationStatusResponseMessage extends IApplicationCommandResponseMessage {
   int FIELD_IS_RUNNING = 2;

   void setIsRunning(boolean var1);
}
