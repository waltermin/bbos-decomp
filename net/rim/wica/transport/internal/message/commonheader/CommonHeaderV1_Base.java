package net.rim.wica.transport.internal.message.commonheader;

public class CommonHeaderV1_Base {
   protected int _version;
   protected int _messageCount;
   protected long _senderId;
   protected long _wicletId;
   protected boolean _more;

   public CommonHeaderV1_Base() {
      this._messageCount = 1;
   }

   public CommonHeaderV1_Base(CommonHeaderV1_Base clone) {
      this._version = clone.getVersion();
      this._messageCount = clone.getMessageCount();
      this._senderId = clone.getSenderId();
      this._wicletId = clone.getWicletId();
      this._more = clone.hasMore();
   }

   public int getVersion() {
      return this._version;
   }

   public void setVersion(int version) {
      if (version < 0) {
         throw new Object();
      }

      this._version = version;
   }

   public long getSenderId() {
      return this._senderId;
   }

   public void setSenderId(long senderId) {
      this._senderId = senderId;
   }

   public long getWicletId() {
      return this._wicletId;
   }

   public void setWicletId(long wicletId) {
      this._wicletId = wicletId;
   }

   public boolean isBundle() {
      return this._messageCount > 1;
   }

   public int getMaxBundleSize() {
      return 256;
   }

   public int getMessageCount() {
      return this._messageCount;
   }

   public void setMessageCount(int count) {
      this._messageCount = count;
   }

   public boolean hasMore() {
      return this._more;
   }

   public void haveMore(boolean haveMore) {
      this._more = haveMore;
   }
}
