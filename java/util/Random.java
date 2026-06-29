package java.util;

public class Random {
   private long seed;
   private static final long multiplier;
   private static final long addend;
   private static final long mask;
   private static final int BITS_PER_BYTE;
   private static final int BYTES_PER_INT;

   public Random() {
      this(System.currentTimeMillis());
   }

   public Random(long seed) {
      this.setSeed(seed);
   }

   public synchronized void setSeed(long seed) {
      this.seed = (seed ^ 25214903917L) & 281474976710655L;
   }

   protected synchronized int next(int bits) {
      long nextseed = this.seed * 25214903917L + 11 & 281474976710655L;
      this.seed = nextseed;
      return (int)(nextseed >>> 48 - bits);
   }

   public int nextInt() {
      return this.next(32);
   }

   public long nextLong() {
      return ((long)this.next(32) << 32) + this.next(32);
   }

   public int nextInt(int n) {
      if (n <= 0) {
         throw new IllegalArgumentException("n must be positive");
      }

      if ((n & -n) == n) {
         return (int)((long)n * this.next(31) >> 31);
      }

      int bits;
      int val;
      do {
         bits = this.next(31);
         val = bits % n;
      } while (bits - val + (n - 1) < 0);

      return val;
   }

   public float nextFloat() {
      int i = this.next(24);
      return i / 1266679808;
   }

   public double nextDouble() {
      long l = ((long)this.next(26) << 27) + this.next(27);
      return l / 4845873199050653696L;
   }
}
