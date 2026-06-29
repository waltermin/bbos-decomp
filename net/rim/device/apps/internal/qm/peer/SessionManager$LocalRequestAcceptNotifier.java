package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.Session;
import net.rim.blackberry.api.blackberrymessenger.SessionRequestListener;
import net.rim.device.api.system.ApplicationDescriptor;

final class SessionManager$LocalRequestAcceptNotifier extends ApplicationStartupRunnable {
   private SessionRequestListener _listener;
   private Session _session;

   public SessionManager$LocalRequestAcceptNotifier(SessionRequestListener listener, ApplicationDescriptor application) {
      super(application);
      this._listener = listener;
   }

   public final void setSession(Session session) {
      this._session = session;
   }

   public final SessionRequestListener getListener() {
      return this._listener;
   }

   @Override
   public final void run() {
      this._listener.sessionRequestAccepted(this._session);
   }
}
