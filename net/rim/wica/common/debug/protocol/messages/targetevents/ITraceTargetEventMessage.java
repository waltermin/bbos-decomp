package net.rim.wica.common.debug.protocol.messages.targetevents;

public interface ITraceTargetEventMessage extends IApplicationTargetEventMessage {
   int FIELD_TRACE_MESSAGE;

   void setMessage(String var1);
}
