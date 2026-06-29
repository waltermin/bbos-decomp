package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.WeakReference;

final class MMSNotificationManager implements NotificationsEngineListener {
   private WeakReference _messageRef;
   private static final long PROFILE_OBJECT_TYPE;

   private MMSNotificationManager() {
   }

   static final void startListening() {
      NotificationsManager.registerNotificationsEngineListener(8609386677418041260L, new MMSNotificationManager());
   }

   static final void triggerNotifications(MMSMessageModel model) {
      Proxy.getInstance().invokeLater(new MMSNotificationManager$1(model));
   }

   static final void cancelImmediateNotifications(MMSMessageModel model) {
      Proxy.getInstance().invokeLater(new MMSNotificationManager$2(model));
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceID, long eventID, Object eventReference, Object context) {
      if (sourceID == 8609386677418041260L && !Phone.getInstance().isActive() && eventReference instanceof MMSMessageModel) {
         this._messageRef = (WeakReference)(new Object(eventReference));
         ContextObject contextObject = (ContextObject)(new Object(64));
         ShowMessageApp.displayMessage((MMSMessageModel)eventReference, contextObject);
      }
   }

   @Override
   public final void deferredEventWasSuperseded(long sourceID, long eventID, Object eventReference, Object context) {
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceID, long eventID, Object eventReference, Object context) {
      if (stateInt == 1 && this._messageRef != null && this._messageRef.get() == eventReference) {
         this._messageRef = null;
         ShowMessageApp.postEvent(-6275418955626563374L, 0, 0, eventReference, null);
         cancelImmediateNotifications((MMSMessageModel)eventReference);
         NotificationsManager.cancelDeferredEvent(sourceID, eventID, eventReference, 0, context);
      }
   }
}
