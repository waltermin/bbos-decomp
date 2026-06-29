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
   public static final int MAX_WBITS;
   public static final int DEF_WBITS;
   public static final int Z_SYNC_FLUSH;
   public static final int Z_FULL_FLUSH;
   public static final int Z_FINISH;
   public static final int Z_NO_COMPRESSION;
   public static final int Z_BEST_SPEED;
   public static final int Z_BEST_COMPRESSION;
   public static final int Z_DEFAULT_COMPRESSION;
   public static final int Z_FILTERED;
   public static final int Z_HUFFMAN_ONLY;
   public static final int Z_RLE;
   public static final int Z_DEFAULT_STRATEGY;
   public static final int Z_DEF_MEM_LEVEL;
   public static final int GZIP_DEF_WBITS;

   public Inflater(int windowBits) {
      this.init(windowBits);
   }

   public final native void init(int var1);

   public final native byte[] decompress(byte[] var1, int var2, int var3);

   public static final native byte[] gzipDecompress(byte[] var0, int var1, int var2);
}
