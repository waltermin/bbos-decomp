package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.protocol.messages.IBodyMessage;

public interface ITargetEventMessage extends IBodyMessage {
   int TARGETEVENT_APPLICATION_INSTALLED;
   int TARGETEVENT_APPLICATION_UNINSTALLED;
   int TARGETEVENT_APPLICATION_UPGRADED;
   int TARGETEVENT_APPLICATION_STARTED;
   int TARGETEVENT_APPLICATION_STOPPED;
   int TARGETEVENT_TRACE;
   int TARGETEVENT_MESSAGE_SENT;
   int TARGETEVENT_MESSAGE_RECEIVED;
   String[] MESSAGE_TYPE_NAMES;
}
