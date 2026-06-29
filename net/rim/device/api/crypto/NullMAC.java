package net.rim.device.api.crypto;

public final class NullMAC extends AbstractMAC implements MAC {
   private boolean _checkMAC;

   public NullMAC() {
      this(true);
   }

   public NullMAC(boolean checkMAC) {
      this._checkMAC = checkMAC;
   }

   @Override
   public final String getAlgorithm() {
      return "Null";
   }

   @Override
   public final void reset() {
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
   }

   @Override
   public final int getLength() {
      return 0;
   }

   @Override
   public final int getMAC(byte[] buffer, int offset, boolean reset) {
      return 0;
   }

   @Override
   public final boolean checkMAC(byte[] mac, int offset) {
      return this._checkMAC;
   }
}
