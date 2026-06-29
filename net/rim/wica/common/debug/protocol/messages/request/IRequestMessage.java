package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.protocol.messages.IBodyMessage;

public interface IRequestMessage extends IBodyMessage {
   int DEBUGGER_HANDSHAKE;
   int DEBUGGER_SESSION_RESET;
   int DEBUGGER_DETACH;
   int START_APPLICATION;
   int STOP_APPLICATION;
   int APPLICATION_STATUS;
}
