package net.rim.device.cldc.io.proxyhttp.compression.coders;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.util.StringUtilitiesInternal;

public class EscapedTextCoder implements Coder {
   private StringMatch _kmpMatcher;
   private String[] _table;
   private int[] _cacheHash;
   private String[] _cacheKey;
   private byte[][] _cacheValue;
   private MemoryCleanerListener _memoryCleanerListener;
   private static final int CACHE_SIZE = 4;

   public void clearCacheData() {
      this.clearCacheData2();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public String decode(InputStream ins) {
      StringBuffer scratch = StringUtilitiesInternal.getScratchBuffer();
      synchronized (scratch) {
         boolean var10 = false /* VF: Semaphore variable */;

         String var5;
         try {
            var10 = true;
            scratch.setLength(0);

            int currentByte;
            while ((currentByte = ins.read()) != 0) {
               if (currentByte == -1) {
                  throw new Object();
               }

               if (currentByte <= 31) {
                  scratch.append(this._table[currentByte - 1]);
               } else if (currentByte != 127) {
                  scratch.append((char)currentByte);
               }
            }

            var5 = scratch.toString();
            var10 = false;
         } finally {
            if (var10) {
               scratch.setLength(0);
            }
         }

         scratch.setLength(0);
         return var5;
      }
   }

   @Override
   public void encode(String decoded, OutputStream outs) {
      synchronized (this._cacheHash) {
         int hashCode = decoded.hashCode();

         for (int i = 0; i < 4; i++) {
            if (this._cacheHash[i] == hashCode && StringUtilities.strEqual(this._cacheKey[i], decoded)) {
               outs.write(this._cacheValue[i]);
               this.moveToFront(i);
               return;
            }
         }

         ByteArrayOutputStream tempOut = (ByteArrayOutputStream)(new Object());
         int offset = 0;
         int length = decoded.length();
         byte[] data = decoded.getBytes();

         while (offset < length) {
            int pos = this._kmpMatcher.indexOf(decoded, offset);
            if (pos == -1) {
               break;
            }

            tempOut.write(data, offset, pos - offset);
            tempOut.write(this._kmpMatcher.getLastMatchedPattern() + 1);
            offset = pos + this._table[this._kmpMatcher.getLastMatchedPattern()].length();
         }

         tempOut.write(data, offset, length - offset);
         tempOut.write(0);
         tempOut.close();
         byte[] compressedData = tempOut.toByteArray();
         outs.write(compressedData);
         this._cacheHash[3] = hashCode;
         this._cacheValue[3] = compressedData;
         this._cacheKey[3] = decoded;
         this.moveToFront(3);
      }
   }

   private void moveToFront(int index) {
      if (index != 0) {
         int hash = this._cacheHash[index];
         String key = this._cacheKey[index];
         byte[] value = this._cacheValue[index];

         for (int i = index; i > 0; i--) {
            this._cacheHash[i] = this._cacheHash[i - 1];
            this._cacheKey[i] = this._cacheKey[i - 1];
            this._cacheValue[i] = this._cacheValue[i - 1];
         }

         this._cacheHash[0] = hash;
         this._cacheKey[0] = key;
         this._cacheValue[0] = value;
      }
   }

   public EscapedTextCoder(String[] table, boolean isCaseSensitive) {
      this._table = table;
      this._kmpMatcher = (StringMatch)(new Object(table, isCaseSensitive, false));
      this._cacheHash = new int[4];
      this._cacheValue = new byte[4][];
      this._cacheKey = new Object[4];
      this._memoryCleanerListener = new EscapedTextCoder$MyMemoryCleanerListener(this, null);
      MemoryCleanerDaemon.addWeakListener(this._memoryCleanerListener, false);
   }

   private boolean clearCacheData2() {
      synchronized (this._cacheHash) {
         boolean gc = false;
         Arrays.fill(this._cacheHash, 0);

         for (int i = 0; i < 4; i++) {
            gc |= this._cacheKey[i] != null;
            this._cacheKey[i] = null;
            this._cacheValue[i] = null;
         }

         return gc;
      }
   }
}
