package net.rim.device.apps.internal.mms.service;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.internal.proxy.Proxy;

final class MMSServiceRadioListener implements RadioStatusListener, PersistentContentListener {
   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final synchronized void networkStateChange(int state) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      if (canLoadDeferredMessages(service)) {
         loadDeferredMessages();
      }
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final synchronized void signalLevel(int level) {
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 1) {
         int service = RadioInfo.getNetworkService();
         if (canLoadDeferredMessages(service)) {
            loadDeferredMessages();
         }
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }

   private static final void loadDeferredMessages() {
      Enumeration inboxMessages = MMSStorage.getMessages(8244211460627721111L);
      Enumeration orphanSavedMessages = MMSStorage.getMessages(-7297051376619864492L);
      if (inboxMessages != null || orphanSavedMessages != null) {
         new DeferredMessageLoader(inboxMessages, orphanSavedMessages).run();
      }
   }

   static final void startListening() {
      MMSServiceRadioListener listener = new MMSServiceRadioListener();
      Proxy.getInstance().addRadioListener(listener);
      PersistentContent.addListener(listener);
   }

   private static final boolean canLoadDeferredMessages(int service) {
      if (!MMSTransportServiceBook.hasMMSServiceRecord()) {
         return false;
      }

      if (!MMSUtilities.hasDataCoverage()) {
         return false;
      }

      switch (MMSOptions.getInstance().getAutomaticRetrievalMode()) {
         case 1:
         default:
            return false;
         case 2:
            if ((service & 8) != 0) {
               return false;
            }
         case 0:
            return true;
      }
   }
}
