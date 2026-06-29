package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.Message;
import net.rim.blackberry.api.blackberrymessenger.SessionListener;
import net.rim.device.api.system.ApplicationDescriptor;

final class SessionImpl$SessionListenerRunnable extends ApplicationStartupRunnable {
   private SessionListener _listener;
   private int _event;
   private Message _message;
   private SessionImpl _session;
   public static final int EVENT_MESSAGE_SENT = 1;
   public static final int EVENT_MESSAGE_QUEUED = 2;
   public static final int EVENT_MESSAGE_RECEIVED = 3;
   public static final int EVENT_MESSAGE_DELIVERED = 4;
   public static final int EVENT_SESSION_CLOSED = 5;

   public SessionImpl$SessionListenerRunnable(SessionListener listener, ApplicationDescriptor application) {
      super(application);
      this._listener = listener;
   }

   public final void invokeEvent(int event, SessionImpl session, Message message) {
      this._event = event;
      this._session = session;
      this._message = message;
      this.invokeLater();
   }

   public final SessionListener getListener() {
      return this._listener;
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof SessionImpl$SessionListenerRunnable) {
         SessionImpl$SessionListenerRunnable slr = (SessionImpl$SessionListenerRunnable)obj;
         if (slr.getListener() == this._listener) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final void run() {
      switch (this._event) {
         case 1:
         default:
            this._listener.messageSent(this._session, this._message);
            return;
         case 2:
            this._listener.messageQueuedForSend(this._session, this._message);
            return;
         case 3:
            this._listener.messageReceived(this._session, this._message);
            return;
         case 4:
            this._listener.messageDelivered(this._session, this._message);
            return;
         case 5:
            this._listener.sessionClosed(this._session);
         case 0:
      }
   }
}
