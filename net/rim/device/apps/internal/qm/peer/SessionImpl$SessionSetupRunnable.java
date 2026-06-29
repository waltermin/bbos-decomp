package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.SessionSetupListener;
import net.rim.device.api.system.ApplicationDescriptor;

final class SessionImpl$SessionSetupRunnable extends ApplicationStartupRunnable {
   private SessionSetupListener _listener;
   private int _event;
   private SessionImpl _session;
   public static final int EVENT_ACCEPTED;
   public static final int EVENT_DELIVERED;
   public static final int EVENT_FAILED;
   public static final int EVENT_REFUSED;

   public SessionImpl$SessionSetupRunnable(SessionSetupListener listener, ApplicationDescriptor application) {
      super(application);
      this._listener = listener;
   }

   public final void invokeEvent(int event, SessionImpl session) {
      this._event = event;
      this._session = session;
      this.invokeLater();
   }

   @Override
   public final void run() {
      switch (this._event) {
         case 1:
         default:
            this._listener.sessionRequestAccepted(this._session);
            return;
         case 2:
            this._listener.sessionRequestDelivered(this._session);
            return;
         case 3:
            this._listener.sessionRequestFailed(this._session, 0);
            return;
         case 4:
            this._listener.sessionRequestRefused(this._session);
         case 0:
      }
   }
}
