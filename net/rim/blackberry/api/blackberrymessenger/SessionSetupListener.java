package net.rim.blackberry.api.blackberrymessenger;

public interface SessionSetupListener {
   void sessionRequestAccepted(Session var1);

   void sessionRequestDelivered(Session var1);

   void sessionRequestFailed(Session var1, int var2);

   void sessionRequestRefused(Session var1);
}
