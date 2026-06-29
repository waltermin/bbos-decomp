package net.rim.device.apps.internal.qm.peer;

import java.lang.ref.WeakReference;
import net.rim.blackberry.api.blackberrymessenger.Message;
import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Session;
import net.rim.blackberry.api.blackberrymessenger.SessionListener;
import net.rim.blackberry.api.blackberrymessenger.SessionSetupListener;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ListenerUtilities;

final class SessionImpl implements Session {
   private final PeerContact _contact;
   private int _state;
   private int _id;
   private Object[] _listeners;
   private SessionImpl$SessionSetupRunnable _setupListener;
   private String _appName;
   private IntHashtable _messages;

   final SessionImpl$SessionSetupRunnable getSetupListener() {
      return this._setupListener;
   }

   final void messageReceived(Message message) {
      if (this._state == 3) {
         for (int i = 0; i < this._listeners.length; i++) {
            ((SessionImpl$SessionListenerRunnable)this._listeners[i]).invokeEvent(3, this, message);
         }
      }
   }

   final void messageStateChange(int id, int state) {
      WeakReference ref = (WeakReference)this._messages.get(id);
      if (ref != null) {
         Message message = (Message)ref.get();
         switch (state) {
            case 6:
            case 8:
               break;
            case 7:
            default:
               for (int i = 0; i < this._listeners.length; i++) {
                  ((SessionImpl$SessionListenerRunnable)this._listeners[i]).invokeEvent(2, this, message);
               }
               break;
            case 9:
               for (int i = 0; i < this._listeners.length; i++) {
                  ((SessionImpl$SessionListenerRunnable)this._listeners[i]).invokeEvent(1, this, message);
               }
               break;
            case 10:
               for (int i = 0; i < this._listeners.length; i++) {
                  ((SessionImpl$SessionListenerRunnable)this._listeners[i]).invokeEvent(4, this, message);
               }

               this._messages.remove(id);
         }
      }
   }

   final void onEnd(boolean remote) {
      this._state = 1;
      SessionInfoField field = new SessionEndedField(this._contact, this._appName);
      PeerApplication.getInstance().newObject(this._contact, field);
      if (remote) {
         if (this._listeners != null) {
            for (int i = 0; i < this._listeners.length; i++) {
               ((SessionImpl$SessionListenerRunnable)this._listeners[i]).invokeEvent(5, this, null);
            }
         }
      } else {
         SessionBlob blob = new SessionBlob(3, this._appName, null);
         blob.setId(this._id);
         PeerApplication.getInstance();
         PeerApplication.getSession().sendBlob(this._contact, blob);
      }
   }

   final void onRefuse(boolean remote) {
      this._state = 1;
      if (remote) {
         this._setupListener.invokeEvent(4, this);
      } else {
         SessionBlob blob = new SessionBlob(2, this._appName, null);
         blob.setId(this._id);
         PeerApplication.getInstance();
         PeerApplication.getSession().sendBlob(this._contact, blob);
      }

      PeerApplication.getInstance().newObject(this._contact, new SessionRequestDeniedField(this._contact));
   }

   final void onAccept(boolean remote) {
      this._state = 3;
      SessionInfoField field = new SessionRequestAcceptedField(this._id);
      PeerApplication.getInstance().newObject(this._contact, field);
      if (remote) {
         this._setupListener.invokeEvent(1, this);
      } else {
         SessionBlob blob = new SessionBlob(1, this._appName, null);
         blob.setId(this._id);
         PeerApplication.getInstance();
         PeerApplication.getSession().sendBlob(this._contact, blob);
      }
   }

   final void onRequest() {
      SessionInfoField field = new SessionRequestReceivedField(this._contact, this._appName, this);
      PeerApplication.getInstance().newObject(this._contact, field);
   }

   final String getApplicationName() {
      return this._appName;
   }

   final void setApplicationName(String applicationName) {
      this._appName = applicationName;
   }

   final int getId() {
      return this._id;
   }

   @Override
   public final void send(Message message) {
      if (this._state != 3) {
         throw new IllegalStateException("Session is closed");
      }

      FileTransferBlob blob = new FileTransferBlob(
         message.getContentType(), message.getName(), message.getData(), message.getInteger(), message.getURL(), this._appName
      );
      blob.setSessionId(this._id);
      PeerApplication.getInstance();
      PeerApplication.getSession().sendBlob(this._contact, blob);
      this._messages.put(blob.getId(), new WeakReference(message));
   }

   @Override
   public final boolean isOpen() {
      return this._state == 3;
   }

   @Override
   public final int getState() {
      return this._state;
   }

   @Override
   public final void close() {
      this._state = 1;
      SessionManager.getInstance().sessionEnded(this._id, false);
   }

   @Override
   public final void sendRequest(SessionSetupListener listener, ApplicationDescriptor application, String url) {
      if (listener == null) {
         throw new IllegalArgumentException("SessionSetupListener cannot be null");
      }

      if (this._state == 2) {
         throw new IllegalStateException("Session request already in progress");
      }

      if (application != null) {
         if (application.getModuleHandle() != ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle()) {
            throw new IllegalArgumentException("ApplicationDescriptor must describe the registering application");
         }

         this._setupListener = new SessionImpl$SessionSetupRunnable(listener, application);
         this._appName = application.getName();
         if (this._appName != null && this._appName.length() != 0) {
            SessionRequestSentField request = new SessionRequestSentField(this._contact, this._appName, this);
            PeerApplication.getInstance().newObject(this._contact, request);
            SessionBlob blob = new SessionBlob(0, this._appName, url);
            this._id = blob.getId();
            PeerApplication.getInstance();
            PeerApplication.getSession().sendBlob(this._contact, blob);
            SessionManager.getInstance().sessionRequestSent(this);
            this._state = 2;
         } else {
            throw new IllegalArgumentException("Application must have a name");
         }
      } else {
         throw new IllegalArgumentException("applicaiton cannot be null");
      }
   }

   @Override
   public final void display(String text, Field field) {
      if (this._state != 3) {
         throw new IllegalStateException("Session closed");
      }

      ExternalMessageModelWrapper message = new ExternalMessageModelWrapper(text, field, this._appName);
      PeerApplication.getInstance().newObject(this._contact, message);
   }

   @Override
   public final void display(String text) {
      this.display(text, new RichTextField(text));
   }

   @Override
   public final void removeListener(SessionListener listener) {
      for (int i = 0; i < this._listeners.length; i++) {
         if (listener == ((SessionImpl$SessionListenerRunnable)this._listeners[i]).getListener()) {
            Arrays.removeAt(this._listeners, i);
            return;
         }
      }
   }

   @Override
   public final void addListener(SessionListener listener, ApplicationDescriptor application) {
      if (application != null) {
         if (application.getModuleHandle() != ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle()) {
            throw new IllegalArgumentException("ApplicationDescriptor must describe the registering application");
         }

         if (listener != null) {
            this._listeners = ListenerUtilities.addListener(this._listeners, new SessionImpl$SessionListenerRunnable(listener, application));
         } else {
            throw new IllegalArgumentException("listener cannot be null");
         }
      } else {
         throw new IllegalArgumentException("applicaiton cannot be null");
      }
   }

   @Override
   public final MessengerContact getContact() {
      return this._contact;
   }

   SessionImpl(PeerContact contact, int id) {
      this(contact);
      this._id = id;
   }

   SessionImpl(PeerContact contact) {
      this._contact = contact;
      this._state = 1;
      this._messages = new IntHashtable(1);
   }
}
