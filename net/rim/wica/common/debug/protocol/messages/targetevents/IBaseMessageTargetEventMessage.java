package net.rim.wica.common.debug.protocol.messages.targetevents;

public interface IBaseMessageTargetEventMessage extends IApplicationTargetEventMessage {
   int FIELD_MESSAGE_NAME = 2;
   int FIELD_MESSAGE_CORRELATION_ID = 3;

   void setType(String var1);

   void setCorrelationId(long var1);
}
