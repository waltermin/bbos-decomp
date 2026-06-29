package net.rim.blackberry.api.sms;

import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageListener;

public interface OutboundMessageListener extends MessageListener {
   void notifyOutgoingMessage(Message var1);
}
