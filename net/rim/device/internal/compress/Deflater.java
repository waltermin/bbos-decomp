package net.rim.device.internal.compress;

public final class Deflater {
   private byte[] _state;
   private byte[] _window;
   private int _adler;
   private int _reserved;
   private int _dataType;
   private int _totalIn;
   private int _totalOut;
   private byte[] _overlay;
   private byte[] _head;
   private byte[] _prev;
   public static final int MAX_WINDOW_BITS = 15;
   public static final int DEFAULT_WINDOW_BITS = 15;
   public static final int Z_SYNC_FLUSH = 2;
   public static final int Z_FULL_FLUSH = 3;
   public static final int Z_FINISH = 4;
   public static final int COMPRESSION_NONE = 0;
   public static final int COMPRESSION_BEST_SPEED = 1;
   public static final int COMPRESSION_BEST = 9;
   public static final int COMPRESSION_DEFAULT = -1;
   public static final int STRATEGY_FILTERED = 1;
   public static final int STRATEGY_HUFFMAN_ONLY = 2;
   public static final int STRATEGY_RLE = 3;
   public static final int STRATEGY_DEFAULT = 0;
   public static final int Z_DEF_MEM_LEVEL = 1;

   public Deflater(int level, int strategy, int windowbits) {
      this.init(level, windowbits, 1, strategy);
   }

   private final native void init(int var1, int var2, int var3, int var4);

   public final native byte[] compress(byte[] var1, int var2, int var3, int var4);
}
