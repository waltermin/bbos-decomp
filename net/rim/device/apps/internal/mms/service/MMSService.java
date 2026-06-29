package net.rim.device.apps.internal.mms.service;

import net.rim.device.apps.internal.mms.api.MMSMessageModel;

public final class MMSService {
   public static final void registerOnceOnSystemStart() {
      MMSNotificationManager.startListening();
      MMSServiceRadioListener.startListening();
   }

   public static final void cancelImmediateNotifications(MMSMessageModel model) {
      MMSNotificationManager.cancelImmediateNotifications(model);
   }

   public static final void testConnectionToMMSC() {
      BackgroundTaskThread.addTask(new MMSServiceTestRunnable());
   }
}
