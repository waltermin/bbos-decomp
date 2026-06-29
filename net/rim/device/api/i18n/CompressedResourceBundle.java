package net.rim.device.api.i18n;

import java.io.IOException;
import java.util.Hashtable;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.compress.Inflater;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.WeakReference;

public class CompressedResourceBundle extends ResourceBundle {
   private int _moduleNumber;
   private String _name;
   private WeakReference _bundleReference = new WeakReference(null);
   private static final boolean DEBUG;
   private static final int HEADER_SIZE;
   private static final int MIN_SIZE;
   private static final int MAX_SIZE;
   private static final int MIN_RESOURCE_ENTRIES;
   private static final int MAX_RESOURCE_ENTRIES;
   private static Hashtable _bundleHash;
   static final int COMPRESSED_RESOURCES;
   static final int UNCOMPRESSED_RESOURCES;

   private CompressedResourceBundle(String name, int moduleNumber, Locale loc) {
      super(loc);
      this._name = name;
      if (_bundleHash.containsKey(this._name)) {
         throw new RuntimeException("CRB exists" + this._name);
      }

      this._moduleNumber = moduleNumber;
      _bundleHash.put(this._name, this);
   }

   public static CompressedResourceBundle getInstance(String name, int modNumber, Locale loc) {
      return _bundleHash.containsKey(name) ? (CompressedResourceBundle)_bundleHash.get(name) : new CompressedResourceBundle(name, modNumber, loc);
   }

   @Override
   long getId() {
      try {
         return this.getResourceBundle().getId();
      } catch (MissingResourceException var2) {
         return super.getId();
      }
   }

   @Override
   protected Object handleGetObject(int key) {
      ResourceBundle rb = this.getResourceBundle();

      try {
         return rb.getObject(key);
      } catch (MissingResourceException mre) {
         return null;
      }
   }

   private ResourceBundle getResourceBundle() {
      ResourceBundle rb = (ResourceBundle)this._bundleReference.get();
      if (rb == null) {
         if (this._moduleNumber == -1) {
            throw new MissingResourceException();
         }

         byte[] data = Resource$Internal.getResource(this._name, this._moduleNumber);
         rb = getResourceBundle(data);
         byte[] var3 = null;
         if (rb == null) {
            throw new MissingResourceException();
         }

         this._bundleReference.set(rb);
      }

      return rb;
   }

   public static ResourceBundle getResourceBundle(byte[] compressed) {
      if (compressed == null) {
         return null;
      }

      int offset = 0;
      byte[] header = Arrays.copy(compressed, offset, 10);
      int info = getInt(header, 6);

      try {
         compressed = Arrays.copy(compressed, header.length, compressed.length - header.length);
         if ((info & 1) == 1) {
            compressed = Inflater.gzipDecompress(compressed, 0, compressed.length);
         }
      } catch (IOException ioe) {
         throw new MissingResourceException(ioe.toString());
      }

      int var15 = 0;
      int code = getInt(compressed, var15);
      var15 += 4;
      String variant = null;
      if ((info & 1) == 1 || (info & 2) == 2) {
         byte vlen = compressed[var15];
         var15++;
         if (vlen != 0) {
            byte[] vbytes = new byte[vlen];
            vbytes = Arrays.copy(compressed, var15, vlen);
            variant = new String(vbytes);
            vbytes = null;
            var15 += vlen;
         }
      }

      long id = getLong(compressed, var15);
      var15 += 8;
      int ids = getShort(compressed, var15);
      if (ids >= 0 && ids <= 2048) {
         var15 += 2;
         int[] resourceIds = new int[ids];
         var15 = copy(compressed, var15, resourceIds);
         short[] resourceOffsets = new short[ids + 1];
         var15 = copy(compressed, var15, resourceOffsets);
         int dataSize = compressed.length - var15;
         byte[] data = null;
         if (dataSize > 0) {
            data = Arrays.copy(compressed, var15, dataSize);
         }

         return new CompiledResourceBundle(Locale.get(code, variant), id, resourceIds, resourceOffsets, data);
      } else {
         throw new MissingResourceException();
      }
   }

   static void intToFourBytes(int i, byte[] b, int index) {
      b[index] = (byte)(i >> 24);
      b[index + 1] = (byte)(i >> 16);
      b[index + 2] = (byte)(i >> 8);
      b[index + 3] = (byte)i;
   }

   static short twoBytesToShort(byte b1, byte b2) {
      return (short)(b1 << 8 | b2 & 0xFF);
   }

   static int getInt(byte[] src, int offset) {
      return fourBytesToInt(src[offset], src[++offset], src[++offset], src[++offset]);
   }

   static long getLong(byte[] src, int offset) {
      return eightBytesToLong(src[offset], src[++offset], src[++offset], src[++offset], src[++offset], src[++offset], src[++offset], src[++offset]);
   }

   static int fourBytesToInt(byte b1, byte b2, byte b3, byte b4) {
      return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | b4 & 0xFF;
   }

   static long eightBytesToLong(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
      int hi = fourBytesToInt(b1, b2, b3, b4);
      int lo = fourBytesToInt(b5, b6, b7, b8);
      return twoIntsToLong(lo, hi);
   }

   static long twoIntsToLong(int lo, int hi) {
      return lo & 4294967295L | (long)hi << 32;
   }

   static short getShort(byte[] src, int offset) {
      return twoBytesToShort(src[offset], src[++offset]);
   }

   static int copy(byte[] src, int offset, int[] dest) {
      for (int i = 0; i < dest.length; i++) {
         dest[i] = getInt(src, offset);
         offset += 4;
      }

      return offset;
   }

   static int copy(byte[] src, int offset, short[] dest) {
      for (int i = 0; i < dest.length; i++) {
         dest[i] = getShort(src, offset);
         offset += 2;
      }

      return offset;
   }

   static void log(Object o) {
      System.out.println(o);
   }

   static {
      if (_bundleHash == null) {
         _bundleHash = new Hashtable();
      }
   }
}
