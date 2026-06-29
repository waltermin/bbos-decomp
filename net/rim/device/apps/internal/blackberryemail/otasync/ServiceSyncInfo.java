package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.vm.Array;

final class ServiceSyncInfo implements Persistable {
   OTAFMConfiguration _otafmConfiguration;
   private int[] _deletedMessages = new int[0];
   private int[] _readStatusChanges = new int[0];
   private boolean[] _readStatus = new boolean[0];
   private int[] _movedMessages = new int[0];
   private int[] _fromFolderIds = new int[0];
   private int[] _toFolderIds = new int[0];
   private boolean _abortMessageList;
   private boolean _restoreMessageList;
   private long _lastConfigurationSent;
   private static final int DELETE_MESSAGE_COMMAND_SIZE = 8;
   private static final int MOVE_MESSAGE_COMMAND_SIZE = 20;
   private static final int READ_MESSAGE_COMMAND_SIZE = 11;

   final void setConfigurationSentTimestamp(long timestamp) {
      this._lastConfigurationSent = timestamp;
   }

   final long getConfigurationSentTimestamp() {
      return this._lastConfigurationSent;
   }

   final void setRestoreMessageList(boolean state) {
      if (this._restoreMessageList != state) {
         this._restoreMessageList = state;
      }
   }

   final boolean getRestoreMessageList() {
      return this._restoreMessageList;
   }

   final void abortMessageList() {
      this._abortMessageList = true;
   }

   final boolean getAbortMessageList() {
      return this._abortMessageList || this.size() != 0;
   }

   final void clearAbortMessageList() {
      this._abortMessageList = false;
   }

   private final void add(int[] array, int value) {
      int count = array.length;
      Array.resize(array, count + 1);
      array[count] = value;
   }

   final synchronized void deleteMessage(int refId) {
      this.add(this._deletedMessages, refId);
   }

   final synchronized void readStatusChange(int refId, boolean read) {
      int index = Arrays.binarySearch(this._readStatusChanges, refId);
      if (index >= 0) {
         this._readStatus[index] = read;
      } else {
         int length = this._readStatusChanges.length;
         index = -index - 1;
         Array.resize(this._readStatusChanges, length + 1);
         Array.resize(this._readStatus, length + 1);
         System.arraycopy(this._readStatusChanges, index, this._readStatusChanges, index + 1, length - index);
         System.arraycopy(this._readStatus, index, this._readStatus, index + 1, length - index);
         this._readStatusChanges[index] = refId;
         this._readStatus[index] = read;
      }
   }

   final synchronized void moveMessage(int refId, int fromFolderId, int toFolderId) {
      this.add(this._movedMessages, refId);
      int count = this._movedMessages.length;
      Array.resize(this._fromFolderIds, count);
      Array.resize(this._toFolderIds, count);
      this._fromFolderIds[count - 1] = fromFolderId;
      this._toFolderIds[count - 1] = toFolderId;
   }

   private final void deleteViaMoveFromEnd(int[] array, int index) {
      int lastIndex = array.length - 1;
      array[index] = array[lastIndex];
      Array.resize(array, lastIndex);
   }

   private final void deleteViaShift(int[] array, int index) {
      int count = array.length - 1;
      System.arraycopy(array, index + 1, array, index, count - index);
      Array.resize(array, count);
   }

   private final void deleteViaShift(boolean[] array, int index) {
      int count = array.length - 1;
      System.arraycopy(array, index + 1, array, index, count - index);
      Array.resize(array, count);
   }

   final synchronized void messageDeleted(int refId) {
      int deletedCount = this._deletedMessages.length;

      for (int i = deletedCount - 1; i >= 0; i--) {
         if (this._deletedMessages[i] == refId) {
            this.deleteViaMoveFromEnd(this._deletedMessages, i);
            break;
         }
      }

      int readIndex = Arrays.binarySearch(this._readStatusChanges, refId);
      if (readIndex >= 0) {
         this.deleteViaShift(this._readStatusChanges, readIndex);
         this.deleteViaShift(this._readStatus, readIndex);
      }

      int movedCount = this._movedMessages.length;

      for (int i = movedCount - 1; i >= 0; i--) {
         if (this._movedMessages[i] == refId) {
            this.deleteViaShift(this._movedMessages, i);
            this.deleteViaShift(this._fromFolderIds, i);
            this.deleteViaShift(this._toFolderIds, i);
         }
      }
   }

   final synchronized int size() {
      return 8 * this._deletedMessages.length + 20 * this._movedMessages.length + 11 * this._readStatusChanges.length;
   }

   private final void resetQueuedCommands() {
      Array.resize(this._deletedMessages, 0);
      Array.resize(this._readStatusChanges, 0);
      Array.resize(this._readStatus, 0);
      Array.resize(this._movedMessages, 0);
      Array.resize(this._fromFolderIds, 0);
      Array.resize(this._toFolderIds, 0);
   }

   final synchronized RIMMessagingFolderManagement compileQueuedCommands() {
      if (this.size() == 0) {
         return null;
      }

      RIMMessagingFolderManagement packet = (RIMMessagingFolderManagement)(new Object(this.size()));
      boolean[] processed = new boolean[this._readStatusChanges.length];
      int deletedCount = this._deletedMessages.length;

      for (int i = 0; i < deletedCount; i++) {
         int uid = this._deletedMessages[i];
         int readIndex = Arrays.binarySearch(this._readStatusChanges, uid);
         if (readIndex < 0) {
            packet.addDeleteMessage(uid, 0);
         } else {
            packet.addDeleteMessage(uid, 0, true, this._readStatus[readIndex]);
            processed[readIndex] = true;
         }
      }

      Arrays.sort(this._deletedMessages, 0, deletedCount);
      int movedCount = this._movedMessages.length;

      for (int i = 0; i < movedCount; i++) {
         int fromFolderId = this._fromFolderIds[i];
         int toFolderId = this._toFolderIds[i];
         if (fromFolderId != 0 && toFolderId != 0) {
            int uid = this._movedMessages[i];
            int readIndex = Arrays.binarySearch(this._readStatusChanges, uid);

            for (int j = i + 1; j < movedCount; j++) {
               if (this._movedMessages[j] == uid && this._toFolderIds[j] != 0) {
                  toFolderId = this._toFolderIds[j];
                  this._fromFolderIds[j] = 0;
                  this._toFolderIds[j] = 0;
               }
            }

            if (readIndex >= 0) {
               if (!processed[readIndex]) {
                  packet.addMoveMessage(uid, fromFolderId, toFolderId, true, this._readStatus[readIndex]);
                  processed[readIndex] = true;
               }
            } else if (Arrays.binarySearch(this._deletedMessages, uid) < 0) {
               packet.addMoveMessage(uid, fromFolderId, toFolderId);
            }
         }
      }

      int readCount = this._readStatusChanges.length;

      for (int i = 0; i < readCount; i++) {
         if (!processed[i]) {
            packet.addMessageStatus(this._readStatusChanges[i], this._readStatus[i] ? 1 : 0);
         }
      }

      this.resetQueuedCommands();
      PersistentObject.commit(this);
      return packet;
   }
}
