package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.protocol.messages.IBodyMessage;

public interface ITargetEventMessage extends IBodyMessage {
   int TARGETEVENT_APPLICATION_INSTALLED = 0;
   int TARGETEVENT_APPLICATION_UNINSTALLED = 1;
   int TARGETEVENT_APPLICATION_UPGRADED = 2;
   int TARGETEVENT_APPLICATION_STARTED = 3;
   int TARGETEVENT_APPLICATION_STOPPED = 4;
   int TARGETEVENT_TRACE = 5;
   int TARGETEVENT_MESSAGE_SENT = 6;
   int TARGETEVENT_MESSAGE_RECEIVED = 7;
   String[] MESSAGE_TYPE_NAMES;
}
