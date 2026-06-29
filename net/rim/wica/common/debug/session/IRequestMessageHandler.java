package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.protocol.messages.request.IRequestMessage;

public interface IRequestMessageHandler {
   void handleRequestMessage(int var1, int var2, IRequestMessage var3);
}
