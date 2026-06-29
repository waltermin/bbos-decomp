package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.protocol.messages.IBodyMessage;

public interface IRequestMessage extends IBodyMessage {
   int DEBUGGER_HANDSHAKE = 0;
   int DEBUGGER_SESSION_RESET = 1;
   int DEBUGGER_DETACH = 2;
   int START_APPLICATION = 3;
   int STOP_APPLICATION = 4;
   int APPLICATION_STATUS = 5;
}
