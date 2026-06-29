package net.rim.device.apps.internal.blackberryemail;

import net.rim.device.api.synchronization.SummaryParameterListener;
import net.rim.device.api.util.DataBuffer;

public class EmailSyncState implements SummaryParameterListener {
   private boolean _summaryParameterSet;
   private int _summaryServiceNameHash;
   private int _summaryServiceUIDHash;
   public static final int MS_READ_FLAG = 1;
   public static final int MS_MOVED_FLAG = 2;
   public static final int MS_USE_FLAG = 4;
   public static final int MS_DELETE_FLAG = 8;
   public static final int MS_FOLDER_ATTR_LOCAL_FOLDER = 1;
   public static final int MS_FOLDER_ATTR_NO_MESSAGES = 2;
   private static final int SUMMARY_VERSION = 1;
   private static final int SUMMARY_PARAMETER_COMMAND = 1;

   public boolean getUseFlag(ServiceRecordProvider srp) {
      return this._summaryParameterSet && (srp.getServiceUidHash() == this._summaryServiceUIDHash || srp.getServiceNameHash() == this._summaryServiceNameHash);
   }

   @Override
   public void clearSummaryParameter() {
      this._summaryParameterSet = false;
   }

   @Override
   public void setSummaryParameter(DataBuffer db) {
      try {
         if (db.readByte() == 1 && db.readByte() == 1) {
            if (db.readShort() != 8) {
            }

            this._summaryServiceNameHash = db.readInt();
            this._summaryServiceUIDHash = db.readInt();
            this._summaryParameterSet = true;
            return;
         }
      } finally {
         return;
      }
   }

   public static int getFolderId(int state) {
      return state >> 16;
   }

   public static int getFlags(int state) {
      return state & 65535;
   }

   public static boolean isMoved(int state) {
      return (state & 2) != 0;
   }

   public static boolean isRead(int state) {
      return (state & 1) != 0;
   }

   public static boolean isDeleted(int state) {
      return (state & 8) != 0;
   }

   public static int makeStateInfo(int folderId, int flags) {
      return folderId << 16 | flags;
   }

   public static int makeStateInfo(int folderId, boolean read, boolean moved, boolean use, boolean delete) {
      int state = folderId << 16;
      if (read) {
         state |= 1;
      }

      if (moved) {
         state |= 2;
      }

      if (use) {
         state |= 4;
      }

      if (delete) {
         state |= 8;
      }

      return state;
   }
}
