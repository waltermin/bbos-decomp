package net.rim.device.apps.internal.qm.peer;

import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.blackberry.api.blackberrymessenger.Message;
import net.rim.blackberry.api.blackberrymessenger.SessionRequestListener;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;

final class SessionManager {
   private IntHashtable _pendingSessionRequests = new IntHashtable(1);
   private IntHashtable _currentSessions = new IntHashtable(1);
   private Hashtable _localRequestListeners = new Hashtable(1);
   private static final long GUID = -7605572761419464897L;
   private static SessionManager _instance;

   private SessionManager() {
   }

   static final SessionManager getInstance() {
      _instance = (SessionManager)ApplicationRegistry.getApplicationRegistry().get(-7605572761419464897L);
      if (_instance == null) {
         _instance = new SessionManager();
         ApplicationRegistry.getApplicationRegistry().replace(-7605572761419464897L, _instance);
      }

      return _instance;
   }

   final void sessionRequestSent(SessionImpl session) {
      this._pendingSessionRequests.put(session.getId(), new WeakReference(session));
   }

   final void messageReceived(int id, Message message) {
      WeakReference ref = (WeakReference)this._currentSessions.get(id);
      if (ref != null) {
         SessionImpl session = (SessionImpl)ref.get();
         session.messageReceived(message);
      }
   }

   final void sessionRequested(PeerContact contact, int id, String application, String url) {
      if (this._localRequestListeners.containsKey(application)) {
         SessionImpl session = new SessionImpl(contact, id);
         session.setApplicationName(application);
         session.onRequest();
         this._pendingSessionRequests.put(id, new WeakReference(session));
      } else {
         String message;
         if (url != null && url.length() > 0) {
            message = "You've received an invitation that has no handler associated with it. Please visit " + url + " to download the necessary applications.";
         } else {
            message = MessageFormat.format(PeerResources.getString(2027), new String[]{application});
         }

         PeerApplication.getInstance().newObject(contact, new SessionManager$NoApplicationMessage(message));
         SessionBlob blob = new SessionBlob(2, application, null);
         blob.setId(id);
         PeerApplication.getInstance();
         PeerApplication.getSession().sendBlob(contact, blob);
      }
   }

   final void sessionAccepted(PeerContact contact, String application, int id) {
      WeakReference ref = (WeakReference)this._pendingSessionRequests.get(id);
      if (ref != null) {
         SessionImpl session = (SessionImpl)ref.get();
         if (session.getState() == 2) {
            session.onAccept(true);
         } else if (this._localRequestListeners.containsKey(session.getApplicationName())) {
            session.onAccept(false);
            SessionManager$LocalRequestAcceptNotifier notifier = (SessionManager$LocalRequestAcceptNotifier)this._localRequestListeners
               .get(session.getApplicationName());
            notifier.setSession(session);
            notifier.invokeLater();
         }

         this._pendingSessionRequests.remove(id);
         this._currentSessions.put(id, new WeakReference(session));
      }
   }

   final void sessionRefused(int id, int reason) {
      WeakReference ref = (WeakReference)this._pendingSessionRequests.get(id);
      if (ref != null) {
         SessionImpl session = (SessionImpl)ref.get();
         session.onRefuse(session.getState() == 2);
         this._pendingSessionRequests.remove(id);
      }
   }

   final void sessionEnded(int id, boolean remotely) {
      WeakReference ref = (WeakReference)this._currentSessions.get(id);
      if (ref != null) {
         SessionImpl session = (SessionImpl)ref.get();
         session.onEnd(remotely);
         this._currentSessions.remove(id);
      }
   }

   final void messageStateChange(int id, int state) {
      WeakReference ref = (WeakReference)this._pendingSessionRequests.get(id);
      if (ref != null) {
         if (state == 10) {
            SessionImpl session = (SessionImpl)ref.get();
            session.getSetupListener().invokeEvent(2, session);
         }
      } else {
         IntEnumeration e = this._currentSessions.keys();

         while (e.hasMoreElements()) {
            ref = (WeakReference)this._currentSessions.get(e.nextElement());
            SessionImpl session = (SessionImpl)ref.get();
            session.messageStateChange(id, state);
         }
      }
   }

   final void addRequestListener(SessionRequestListener listener, ApplicationDescriptor application) {
      String name = application.getName();
      if (name == null || name.length() == 0) {
         throw new IllegalArgumentException("Invalid application name");
      }

      if (this._localRequestListeners.containsKey(name)) {
         throw new IllegalArgumentException("An application with the same name is already registered for incoming Sessions");
      }

      this._localRequestListeners.put(name, new SessionManager$LocalRequestAcceptNotifier(listener, application));
   }

   final void removeRequestListener(SessionRequestListener listener) {
      Enumeration e = this._localRequestListeners.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         if (((SessionManager$LocalRequestAcceptNotifier)this._localRequestListeners.get(key)).getListener() == listener) {
            this._localRequestListeners.remove(key);
         }
      }
   }
}
