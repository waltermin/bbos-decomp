package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager$Instance;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAFMConfigurationManagerImpl;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.vm.Array;

public final class GhostMessageData implements Persistable {
   private int[] _ghostMessageTags = new int[0];
   private int[] _ghostMessageInfo = new int[0];
   private int _messageListId;
   public static final int MAXIMUM_GHOST_MESSAGE_COUNT;

   GhostMessageData() {
   }

   public final Object getLock() {
      return this;
   }

   public final void setMessageListId(int messageListId) {
      this._messageListId = messageListId;
      PersistentObject.commit(this);
   }

   public final int getMessageListId() {
      return this._messageListId;
   }

   final synchronized void add(int referenceTag, int info, int folder, ServiceRecord sr) {
      int loc = this._ghostMessageTags.length;
      if (loc > 500) {
         OTAFMConfigurationManagerImpl manager = (OTAFMConfigurationManagerImpl)OTAFMConfigurationManager$Instance.getInstance();
         OTAFMConfiguration config = manager.getConfiguration(sr);
         if (config.isPurgedMessageListSupported()) {
            OTAMessageSync.getInstance()
               .sendPurgedMessageList(Arrays.copy(this._ghostMessageTags), Arrays.copy(this._ghostMessageInfo), this._messageListId, sr);
         }

         loc = 0;
      }

      Array.resize(this._ghostMessageTags, loc + 1);
      Array.resize(this._ghostMessageInfo, loc + 1);
      this._ghostMessageTags[loc] = referenceTag;
      this._ghostMessageInfo[loc] = info;
      PersistentObject.commit(this);
   }

   public final int size() {
      return this._ghostMessageTags.length;
   }

   final int getTagAt(int index) {
      return this._ghostMessageTags[index];
   }

   public final int getInfoAt(int index) {
      return this._ghostMessageInfo[index];
   }

   final synchronized void remove(int referenceTag) {
      boolean found = false;

      for (int i = this._ghostMessageTags.length - 1; i >= 0; i--) {
         if (this._ghostMessageTags[i] == referenceTag) {
            this._ghostMessageTags[i] = 0;
            this._ghostMessageInfo[i] = 0;
            found = true;
         }
      }

      if (found) {
         PersistentObject.commit(this);
      }
   }

   final synchronized void removeAll() {
      Array.resize(this._ghostMessageTags, 0);
      Array.resize(this._ghostMessageInfo, 0);
      PersistentObject.commit(this);
   }

   private final void removeAt(int[] array, int index) {
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   public final synchronized void removeDeletedEntries() {
      int i = 0;
      int length = this._ghostMessageTags.length;

      while (i < length) {
         if (this._ghostMessageTags[i] == 0) {
            this.removeAt(this._ghostMessageTags, i);
            this.removeAt(this._ghostMessageInfo, i);
            length--;
         } else {
            i++;
         }
      }

      PersistentObject.commit(this);
   }
}
