package net.rim.device.api.browser.field;

public final class ContentReadEvent extends Event {
   private int _numRead = -1;
   private int _numToRead;
   private boolean _inBytes;

   public ContentReadEvent(Object src) {
      super(10011, src);
   }

   public final int getItemsRead() {
      return this._numRead;
   }

   public final void setItemsRead(int amountRead) {
      this._numRead = amountRead;
   }

   public final int getItemsToRead() {
      return this._numToRead;
   }

   public final void setItemsToRead(int amountToRead) {
      this._numToRead = amountToRead;
   }

   public final void setItemsToReadInBytes(boolean value) {
      this._inBytes = value;
   }

   public final boolean getItemsToReadInBytes() {
      return this._inBytes;
   }
}
