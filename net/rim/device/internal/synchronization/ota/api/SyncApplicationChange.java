package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.device.internal.synchronization.ota.util.ReferenceGenerator;

public class SyncApplicationChange implements Persistable {
   private int _refId;
   private byte _operation;
   private Object _parameters;
   private byte _flags;
   private byte[] _retryCounts;
   private static final int FOR_SLOWSYNC;
   private static final int SHOULD_BE_FILLED;
   static final byte RETRY_COUNT_OPERATION;
   static final byte RETRY_COUNT_SUSPENSION;

   public SyncApplicationChange() {
      this.setRefId(ReferenceGenerator.getSingletonInstance().getPositiveRefID());
      this.initializeCounts();
   }

   public SyncApplicationChange(SyncApplicationChange aSyncApplicationChange) {
      this._refId = aSyncApplicationChange._refId;
      this._operation = aSyncApplicationChange._operation;
      this._parameters = aSyncApplicationChange._parameters;
      this._flags = aSyncApplicationChange._flags;
      this.initializeCounts();
   }

   public synchronized void setForSlowSync(boolean value) {
      this._flags = (byte)Helper.setFlagValue(this._flags, value, 1);
   }

   public synchronized boolean isForSlowSync() {
      return Helper.getFlagValue(this._flags, 1);
   }

   public synchronized void shouldBeFilled(boolean shouldBeFilled) {
      if (shouldBeFilled) {
         this._parameters = null;
      }

      this._flags = (byte)Helper.setFlagValue(this._flags, shouldBeFilled, 2);
   }

   public synchronized boolean shouldBeFilled() {
      return Helper.getFlagValue(this._flags, 2);
   }

   public synchronized void setRefId(int value) {
      this._refId = value;
   }

   public synchronized int getRefId() {
      return this._refId;
   }

   public synchronized void setOperation(int operation) {
      this._operation = (byte)operation;
   }

   public synchronized int getOperation() {
      return this._operation;
   }

   public synchronized void setParameters(byte[] parameters) {
      this._parameters = PersistentContent.encodeObject(parameters);
   }

   public synchronized byte[] getParameters(boolean copy) {
      byte[] xByteArray = PersistentContent.decodeByteArray(this._parameters);
      if (xByteArray != null) {
         byte[] xByteArrayCopy = new byte[xByteArray.length];
         System.arraycopy(xByteArray, 0, xByteArrayCopy, 0, xByteArray.length);
         xByteArray = xByteArrayCopy;
      }

      return xByteArray;
   }

   @Override
   public int hashCode() {
      return this.getRefId();
   }

   public synchronized void encrypt(boolean encrypt) {
      if (encrypt) {
         this.setParameters(this.getParameters(true));
      }
   }

   @Override
   public boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         return anObject instanceof SyncApplicationChange ? anObject.hashCode() == this.hashCode() : false;
      }
   }

   public short getRetryCount(byte aRetryCountType) {
      return (short)(this._retryCounts[aRetryCountType] - -128);
   }

   public synchronized void resetRetryCount(byte aRetryCountType) {
      this._retryCounts[aRetryCountType] = -128;
   }

   public synchronized void incrementRetryCount(byte aRetryCountType) {
      if (this._retryCounts[aRetryCountType] + 1 <= 127) {
         this._retryCounts[aRetryCountType]++;
      }
   }

   public int getGroup() {
      throw null;
   }

   private synchronized void initializeCounts() {
      this._retryCounts = new byte[2];
      this._retryCounts[0] = -128;
      this._retryCounts[1] = -128;
   }
}
