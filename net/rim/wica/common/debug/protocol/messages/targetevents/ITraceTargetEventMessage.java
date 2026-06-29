package net.rim.wica.common.debug.protocol.messages.targetevents;

public interface ITraceTargetEventMessage extends IApplicationTargetEventMessage {
   int FIELD_TRACE_MESSAGE = 2;

   void setMessage(String var1);
}
