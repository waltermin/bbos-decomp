package net.rim.device.cldc.io.sync;

import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.util.ReusableObject;

public class SyncDatagramBase extends DatagramBase implements ReusableObject {
   private String _userId;
   private long _sid;
   private int _transactionId;
   private int _protocolVersion;

   public SyncDatagramBase() {
      this.setBigEndian(true);
   }

   public void setProtocolVersion(int protocolVersion) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getProtocolVersion() {
      return this._protocolVersion;
   }

   public void setSid(long sid) {
      this._sid = sid;
   }

   public long getSid() {
      return this._sid;
   }

   public void setUserId(String userId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getUserId() {
      return this._userId;
   }

   public void setTransactionId(int value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getTransactionId() {
      return this._transactionId;
   }

   @Override
   public void reset() {
      super.reset();
      this._sid = -1;
      this._userId = null;
      this._transactionId = 0;
      this._protocolVersion = 0;
   }

   public boolean isValid() {
      throw null;
   }

   public void readFrom(DataBuffer _1) {
      throw null;
   }

   public void writeTo(DataBuffer _1) {
      throw null;
   }

   public int getAvailableBytes() {
      throw null;
   }
}
