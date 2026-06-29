package net.rim.device.internal.compress;

public final class Inflater {
   private byte[] _state;
   private byte[] _window;
   private int _adler;
   private int _reserved;
   private int _dataType;
   private int _totalIn;
   private int _totalOut;
   private int _lenCodeOffset;
   private int _distCodeOffset;
   private int _nextCodeOffset;
   private boolean _lenDistFixed;
   public static final int MAX_WBITS = 15;
   public static final int DEF_WBITS = 15;
   public static final int Z_SYNC_FLUSH = 2;
   public static final int Z_FULL_FLUSH = 3;
   public static final int Z_FINISH = 4;
   public static final int Z_NO_COMPRESSION = 0;
   public static final int Z_BEST_SPEED = 1;
   public static final int Z_BEST_COMPRESSION = 9;
   public static final int Z_DEFAULT_COMPRESSION = -1;
   public static final int Z_FILTERED = 1;
   public static final int Z_HUFFMAN_ONLY = 2;
   public static final int Z_RLE = 3;
   public static final int Z_DEFAULT_STRATEGY = 0;
   public static final int Z_DEF_MEM_LEVEL = 1;
   public static final int GZIP_DEF_WBITS = 31;

   public Inflater(int windowBits) {
      this.init(windowBits);
   }

   public final native void init(int var1);

   public final native byte[] decompress(byte[] var1, int var2, int var3);

   public static final native byte[] gzipDecompress(byte[] var0, int var1, int var2);
}
