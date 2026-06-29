package net.rim.blackberry.api.blackberrymessenger;

public interface SessionListener {
   void messageQueuedForSend(Session var1, Message var2);

   void messageReceived(Session var1, Message var2);

   void messageSent(Session var1, Message var2);

   void messageDelivered(Session var1, Message var2);

   void sessionClosed(Session var1);
}
