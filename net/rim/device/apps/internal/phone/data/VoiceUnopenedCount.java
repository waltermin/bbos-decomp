package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.ribbon.indicators.NewMessageEventManager;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.phone.options.PhoneOptions;

public class VoiceUnopenedCount {
   private int _unopenedCount = 0;
   private static final long GUID;
   private static VoiceUnopenedCount _instance;

   private VoiceUnopenedCount() {
   }

   public static VoiceUnopenedCount getInstance() {
      return _instance;
   }

   private boolean showingMissedCallsInMessageList() {
      switch (PhoneOptions.getOptions().getShowCallLogsOption()) {
         case -1:
         case 2:
            return false;
         case 0:
         case 1:
         case 3:
         default:
            return true;
      }
   }

   private synchronized void modifyInternalCount(int num, boolean updateUnreadCount, boolean updateNewCount, boolean updateIndicators) {
      if (updateUnreadCount) {
         this._unopenedCount += num;
      }

      if (updateNewCount) {
         MissedCallIndicator.getInstance().modifyNewMissedCallsCount(num, updateIndicators);
      }
   }

   private synchronized void modifyExternalCount(int num, boolean updateUnreadCount, boolean updateNewCount) {
      UnreadCountManager.modifyUnreadCount(1, num, updateNewCount, updateUnreadCount);
      UnreadCountManager.modifyUnreadCount(2, num, updateNewCount, updateUnreadCount);
   }

   private synchronized void modifyCounters(int count, boolean updateUnreadCount, boolean updateNewCount) {
      this.modifyCounters(count, updateUnreadCount, updateNewCount, true);
   }

   private synchronized void modifyCounters(int count, boolean updateUnreadCount, boolean updateNewCount, boolean updateIndicators) {
      this.modifyInternalCount(count, updateUnreadCount, updateNewCount, updateIndicators);
      if (this.showingMissedCallsInMessageList()) {
         this.modifyExternalCount(count, updateUnreadCount, updateNewCount);
      }
   }

   private synchronized void modifyCounters(int count) {
      this.modifyCounters(count, true, false);
   }

   public void itemDeleted(Object item) {
      if (isUnopenedItem(item)) {
         this.modifyCounters(-1, true, isNewItem(item));
      }
   }

   public void itemMarkedOpened() {
      this.modifyCounters(-1);
   }

   public void itemMarkedUnopened() {
      this.modifyCounters(1);
   }

   public void itemMarkedOld() {
      this.modifyCounters(-1, false, true);
   }

   public void itemBulkMarkedOld() {
      this.modifyCounters(-1, false, true, false);
   }

   public void itemAdded(boolean isUnopened, boolean isNew, int addressCardUID) {
      if (isUnopened) {
         this.modifyCounters(1);
      }

      if (isNew) {
         this.modifyCounters(1, false, true);
         NewMessageEventManager.addFlag(2, addressCardUID);
      }
   }

   public void itemInitialized(Object item) {
      if (isUnopenedItem(item)) {
         this.modifyCounters(1);
      }
   }

   public void itemRestored(Object item) {
      if (isUnopenedItem(item)) {
         this.modifyCounters(1);
      }
   }

   public synchronized void databaseDeleted() {
      if (this._unopenedCount > 0 && this.showingMissedCallsInMessageList()) {
         this.modifyExternalCount(-this._unopenedCount, true, true);
      }

      this._unopenedCount = 0;
   }

   public int getUnreadCount() {
      return this._unopenedCount;
   }

   public int getNewCount() {
      return MissedCallIndicator.getInstance().getNewMissedCallsCount();
   }

   public synchronized void showMissedCallsInMessageListOptionChanged(boolean show) {
      if (this._unopenedCount > 0) {
         if (show) {
            this.modifyExternalCount(this._unopenedCount, true, false);
         } else {
            this.modifyExternalCount(-this._unopenedCount, true, false);
         }
      }

      int newCount = this.getNewCount();
      if (newCount > 0) {
         if (show) {
            this.modifyExternalCount(newCount, false, true);
            return;
         }

         this.modifyExternalCount(-newCount, false, true);
      }
   }

   static boolean isUnopenedItem(Object item) {
      if (!(item instanceof Unopened)) {
         return false;
      }

      Unopened unopened = (Unopened)item;
      return unopened.isUnopened();
   }

   static boolean isNewItem(Object item) {
      if (!(item instanceof Unopened)) {
         return false;
      }

      Unopened unopened = (Unopened)item;
      return unopened.isNew();
   }

   public void clearNewCount() {
      this.modifyCounters(-this.getNewCount(), false, true);
   }

   static {
      MissedCallIndicator.getInstance();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (VoiceUnopenedCount)ar.getOrWaitFor(4360880965294639395L);
      if (_instance == null) {
         _instance = new VoiceUnopenedCount();
         ar.put(4360880965294639395L, _instance);
      }
   }
}
