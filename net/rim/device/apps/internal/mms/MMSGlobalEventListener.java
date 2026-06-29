package net.rim.device.apps.internal.mms;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.MMS;
import net.rim.device.apps.api.messaging.MessageEntryPoint;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;

final class MMSGlobalEventListener implements GlobalEventListener {
   int _unreadCountId;
   MessageEntryPoint _entry;

   MMSGlobalEventListener() {
      this.updateStatus();
      this.updateEntry();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L || guid == -4220058463650496006L) {
         this.updateStatus();
      } else if (guid == -2734094174038131697L) {
         MMSClientServiceBook.setMMSCVersion(18);
         MMSClientServiceBook.setRestrictedSendMode(1);
         MMSClientServiceBook.enableImageReductionBeforeSend(false);
      } else {
         if (guid == 2573494863350550132L || guid == -8639396151207124460L) {
            this.updateEntry();
         }
      }
   }

   private final void updateStatus() {
      MMS.setServiceBookStatus(MMSTransportServiceBook.hasMMSServiceRecord());
   }

   private final void updateEntry() {
      int newUnreadCountId = MMSUtilities.getUnreadCountId();
      boolean bypassUnreadCount = newUnreadCountId == this._unreadCountId;
      if (!bypassUnreadCount && this._unreadCountId != 0) {
         int count = UnreadCountManager.getUnreadCount(5);
         if (newUnreadCountId == 1) {
            UnreadCountManager.modifyUnreadCount(1, count);
            UnreadCountManager.modifyUnreadCount(2, count);
         } else if (this._unreadCountId == 1) {
            UnreadCountManager.modifyUnreadCount(1, -1 * count);
            UnreadCountManager.modifyUnreadCount(2, -1 * count);
         }
      }

      if (this._entry != null) {
         this._entry.unregister();
      }

      if (newUnreadCountId == 5) {
         this._entry = MessageEntryPoint.register("mms", "service=-942103673428357213", 181);
         UnreadCountManager.setAction(5, this._entry);
         this._entry.set(12, UnreadCountManager.getUnreadCountObject(5));
      }

      if (newUnreadCountId == 5 || newUnreadCountId == 11) {
         UnreadCountManager.setUnreadCountVisible(newUnreadCountId, true);
      } else if (this._unreadCountId == 5 || this._unreadCountId == 11) {
         UnreadCountManager.setUnreadCountVisible(this._unreadCountId, false);
      }

      boolean addToMessageList = MMSUtilities.addToMessageList();
      UnreadCountManager.mergeWithText(5, addToMessageList);
      MMSStorage.addToMessageList(addToMessageList);
      if (!bypassUnreadCount) {
         this._unreadCountId = newUnreadCountId;
      }
   }

   static final void initializeUnreadCountMergeWithText() {
      UnreadCountManager.mergeWithText(5, MMSUtilities.addToMessageList());
   }
}
