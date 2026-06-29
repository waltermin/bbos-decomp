package net.rim.device.internal.system;

final class LEDEventProcessor$EventHolder {
   private long _sourceId;
   private long _expirationDate;

   public final void fill(long sourceIdLong, long expirationDateLong) {
      this._sourceId = sourceIdLong;
      this._expirationDate = expirationDateLong;
   }

   public final long getExpirationDate() {
      return this._expirationDate;
   }

   public final long getSourceId() {
      return this._sourceId;
   }
}
