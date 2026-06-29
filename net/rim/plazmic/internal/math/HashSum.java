package net.rim.plazmic.internal.math;

public class HashSum {
   private int _checksum;
   private int _lcgSeed = 7552814;
   private static final int PRIME31 = 1500450271;
   private static final int PRIME23 = 8388461;
   private static final int PRIME05 = 17;
   private static final int LCG_MULTIPLIER = 158793;
   private static final int LCG_ADDEND = 11;
   private static final int LCG_INITIAL_SEED = 7552814;

   public void update(int b) {
      this._lcgSeed = this.getLinearCongruent(158793, 11);
      this._checksum = this.getLinearCongruent(b & 0xFF, this._checksum);
   }

   public void update(byte[] b) {
      this.update(b, 0, b.length);
   }

   public void update(byte[] b, int off, int len) {
      for (int i = 0; i < len; i++) {
         this.update(b[i + off]);
      }
   }

   public int getCasualHash() {
      return (this._checksum ^ 1500450271) % 8388461 + 17;
   }

   private int getLinearCongruent(int multiplier, int addend) {
      return multiplier * this._lcgSeed + addend & 16777215;
   }
}
