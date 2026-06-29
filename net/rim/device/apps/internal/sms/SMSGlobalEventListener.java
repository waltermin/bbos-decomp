package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.MMS;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.messaging.MessageEntryPoint;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;

final class SMSGlobalEventListener implements GlobalEventListener {
   int _unreadCountId;
   MessageEntryPoint _entry;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void updateEntry() {
      int newUnreadCountId = getUnreadCountId();
      boolean bypassUnreadCount = newUnreadCountId == this._unreadCountId;
      if (this._entry != null) {
         this._entry.unregister();
      }

      boolean mmsEnabled = MMS.isEnabled();
      if (!bypassUnreadCount && this._unreadCountId != 0) {
         int count = UnreadCountManager.getUnreadCount(4);
         if (newUnreadCountId == 1) {
            UnreadCountManager.modifyUnreadCount(1, count);
            UnreadCountManager.modifyUnreadCount(2, count);
         } else if (this._unreadCountId == 1) {
            UnreadCountManager.modifyUnreadCount(1, -1 * count);
            UnreadCountManager.modifyUnreadCount(2, -1 * count);
         }
      }

      if (newUnreadCountId == 4) {
         this._entry = MessageEntryPoint.register("sms", "service=-7118119043524803584", 180);
         UnreadCountManager.setAction(4, this._entry);
         this._entry.set(12, UnreadCountManager.getUnreadCountObject(4));
      } else if (newUnreadCountId == 11) {
         int descriptionId = mmsEnabled ? 209 : 180;
         Theme currentTheme = ThemeManager.getActiveTheme();
         boolean gotAlternate = false;
         boolean var10 = false /* VF: Semaphore variable */;

         label129:
         try {
            var10 = true;
            String any = currentTheme.getOption("sms_resource_id");
            String alternateResourceIdSMSMMS = currentTheme.getOption("sms_mms_resource_id");
            if (any != null) {
               if (alternateResourceIdSMSMMS != null) {
                  descriptionId = mmsEnabled ? Integer.parseInt(alternateResourceIdSMSMMS) : Integer.parseInt(any);
                  gotAlternate = true;
                  var10 = false;
               } else {
                  var10 = false;
               }
            } else {
               var10 = false;
            }
         } finally {
            if (var10) {
               descriptionId = mmsEnabled ? 209 : 180;
               break label129;
            }
         }

         this._entry = MessageEntryPoint.register("sms_and_mms", "service=-4696470826620059293", descriptionId, gotAlternate ? currentTheme : null);
         UnreadCountManager.setAction(11, this._entry);
         this._entry.set(12, UnreadCountManager.getUnreadCountObject(11));
      }

      if (newUnreadCountId == 4 || newUnreadCountId == 11) {
         UnreadCountManager.setUnreadCountVisible(newUnreadCountId, true);
      } else if (this._unreadCountId == 4 || this._unreadCountId == 11) {
         UnreadCountManager.setUnreadCountVisible(this._unreadCountId, false);
      }

      boolean addToMessageList = newUnreadCountId == 1;
      UnreadCountManager.mergeWithText(4, addToMessageList);
      Storage.addToMessageList(addToMessageList);
      if (!bypassUnreadCount) {
         this._unreadCountId = newUnreadCountId;
      }
   }

   final void optionsChanged() {
      MessageListOptions.getOptions().applyOptions(this._entry);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 2573494863350550132L && guid != -8639396151207124460L) {
         if (guid == 4609271590317602928L) {
            this.optionsChanged();
         }
      } else {
         this.updateEntry();
      }
   }

   SMSGlobalEventListener() {
      this.updateEntry();
      MMS.onEnabled(new SMSGlobalEventListener$1(this));
   }

   static final int getUnreadCountId() {
      return MessagingUtil.getUnreadCountId("SMSFolder");
   }

   static final void initializeUnreadCountMergeWithText() {
      int newUnreadCountId = getUnreadCountId();
      boolean addToMessageList = newUnreadCountId == 1;
      UnreadCountManager.mergeWithText(4, addToMessageList);
   }
}
