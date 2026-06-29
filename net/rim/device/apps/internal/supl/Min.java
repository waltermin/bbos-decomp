package net.rim.device.apps.internal.supl;

final class Min implements SetId {
   private long min;
   static final byte MIN_BIT_SIZE;

   @Override
   public final void decode(Nibbler nib) {
      this.min = nib.getBitsLarge(32);
      this.min <<= 2;
      this.min = this.min | nib.getBitsLarge(2);
   }

   @Override
   public final void encode(Stuffer stuff) {
      byte SHIFT_AMT = 34;
      stuff.putBits(2, 3);
      stuff.putBits(this.min, 34);
      stuff.putBits(this.min >> 34, 0);
   }
}
