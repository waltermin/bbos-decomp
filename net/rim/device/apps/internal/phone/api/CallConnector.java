package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;

public class CallConnector implements PhoneEventListener, Runnable {
   private long _timeout;
   protected UiApplication _app = (UiApplication)VoiceServices.getVoiceApplication();
   private int _timerId;
   protected int _eventToWaitFor;
   protected boolean _conditionsSatisfied;
   private int _originalLine;
   protected static final long DEFAULT_TIMEOUT = 5000L;
   protected static final long MAX_EVENTS = 3L;

   protected void setPreferredLine(int line) {
      if (this._originalLine != line) {
         PhoneUtilities.setCurrentLine(line);
         new CallConnector$LineSwitcher(this._originalLine);
      }
   }

   public void connect() {
      this.connect(true);
   }

   public void connect(boolean requestForeground) {
      if (requestForeground) {
         VoiceServices.broadcastEvent(2006);
      }

      this.startConnection();
   }

   protected void completeConnection() {
      this.stopListening();
   }

   protected void stopListening() {
      VoiceServices.removePhoneEventListener(this);
   }

   protected void startListening() {
      VoiceServices.addPhoneEventListener(this);
   }

   protected void startTimer() {
      if (this._app != null) {
         this._timerId = this._app.invokeLater(this, this._timeout, false);
      }
   }

   protected void stopTimer() {
      this._app.cancelInvokeLater(this._timerId);
   }

   protected void startConnection() {
      this.startListening();
   }

   protected void onTimeout() {
      this.stopListening();
   }

   protected void waitForEvent(int eventId) {
      System.out.println("Call connect waiting for " + eventId);
      this._eventToWaitFor = eventId;
      this.startTimer();
   }

   protected boolean conditionsSatisfied(int lastEvent, int callId, Object context) {
      return lastEvent == this._eventToWaitFor;
   }

   @Override
   public void phoneEventNotify(int eventId, int callId, Object context) {
      if (!this._conditionsSatisfied && this.conditionsSatisfied(eventId, callId, context)) {
         this._conditionsSatisfied = true;
         this.stopTimer();
         this.completeConnection();
      }
   }

   @Override
   public void run() {
      this.onTimeout();
   }

   public CallConnector(long timeout) {
      this._timeout = timeout;
      this._originalLine = PhoneUtilities.getCurrentLineId();
   }
}
