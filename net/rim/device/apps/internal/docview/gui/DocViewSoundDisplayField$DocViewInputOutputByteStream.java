package net.rim.device.apps.internal.docview.gui;

import java.io.InputStream;
import net.rim.device.api.util.Arrays;

final class DocViewSoundDisplayField$DocViewInputOutputByteStream extends InputStream {
   private boolean _streamingRequestSent;
   private Runnable _sendRunnable;
   private int _rawDataOffset;
   private int _rawDataLength;
   private int _mark;
   private final int _totalLength;
   private Object[] _rawDataChunks;
   private final DocViewSoundDisplayField this$0;
   private static final int THRESHOLD_SIZE = 65536;
   private static final long WAIT_TIMEOUT = 120000L;

   DocViewSoundDisplayField$DocViewInputOutputByteStream(DocViewSoundDisplayField _1, byte[] initialData, int totalLength) {
      this.this$0 = _1;
      this._rawDataChunks = new Object[0];
      this._totalLength = totalLength;
      this.appendData(initialData);
   }

   @Override
   public final synchronized int read() {
      if (this._rawDataOffset >= this._rawDataLength) {
         if (this._rawDataOffset >= this._totalLength) {
            return -1;
         }

         this.waitForData();
         if (this._rawDataOffset >= this._rawDataLength) {
            return -1;
         }
      }

      byte[] b = new byte[1];
      this.read(b);
      int retVal = b[0] & 255;
      byte[] var3 = null;
      return retVal;
   }

   @Override
   public final synchronized int read(byte[] b, int off, int len) {
      if (b == null) {
         throw new Object();
      }

      if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (this._rawDataOffset >= this._rawDataLength) {
            if (this._rawDataOffset >= this._totalLength) {
               return -1;
            }

            this.waitForData();
            if (this._rawDataOffset >= this._rawDataLength) {
               return -1;
            }
         }

         if (this._rawDataOffset + len > this._rawDataLength) {
            len = this._rawDataLength - this._rawDataOffset;
         }

         this.getData(b, off, len);
         this._rawDataOffset += len;
         this.checkForMore();
         return len;
      } else {
         throw new Object();
      }
   }

   @Override
   public final synchronized long skip(long n) {
      if (n <= 0) {
         return 0;
      }

      long skipBytes = 0;
      if (this._rawDataOffset < this._rawDataLength) {
         skipBytes = Math.min(this._rawDataLength - this._rawDataOffset, n);
      }

      if (skipBytes > 0) {
         this._rawDataOffset = (int)(this._rawDataOffset + skipBytes);
      }

      this.checkForMore();
      return skipBytes;
   }

   @Override
   public final synchronized int available() {
      return this._rawDataLength - this._rawDataOffset;
   }

   @Override
   public final boolean markSupported() {
      return true;
   }

   @Override
   public final void mark(int readAheadLimit) {
      this._mark = this._rawDataOffset;
   }

   @Override
   public final synchronized void reset() {
      this._rawDataOffset = this._mark;
   }

   @Override
   public final synchronized void close() {
      this.notifyWaitingThreads();
   }

   final void appendData(byte[] newData) {
      if (newData != null && newData.length > 0) {
         Arrays.add(this._rawDataChunks, newData);
         this._rawDataLength += newData.length;
      }

      this._streamingRequestSent = false;
      this.notifyWaitingThreads();
   }

   private final void checkForMore() {
      if (!this._streamingRequestSent && this.available() <= 65536 && this.this$0.isMoreAvailable()) {
         this._streamingRequestSent = true;
         if (this._sendRunnable == null) {
            this._sendRunnable = new DocViewSoundDisplayField$DocViewInputOutputByteStream$1(this);
         }

         ((Thread)(new Object(this._sendRunnable))).start();
      }
   }

   private final void getData(byte[] buffer, int offset, int length) {
      int partialOffset = 0;
      int i = 0;

      for (i = 0;
         i < this._rawDataChunks.length
            && (this._rawDataOffset < partialOffset || this._rawDataOffset >= ((byte[])this._rawDataChunks[i]).length + partialOffset);
         i++
      ) {
         partialOffset += ((byte[])this._rawDataChunks[i]).length;
      }

      int regionOffset = this._rawDataOffset - partialOffset;
      int totalLength = length;
      int destOffset = offset;

      while (totalLength > 0) {
         byte[] dataChunk = (byte[])this._rawDataChunks[i];
         int lengthToWrite = Math.min(dataChunk.length - regionOffset, totalLength);
         System.arraycopy(dataChunk, regionOffset, buffer, destOffset, lengthToWrite);
         totalLength -= lengthToWrite;
         regionOffset = 0;
         destOffset += lengthToWrite;
         i++;
      }
   }

   private final synchronized void notifyWaitingThreads() {
      this.notifyAll();
   }

   private final synchronized void waitForData() {
      try {
         this.wait(120000);
      } finally {
         return;
      }
   }

   final synchronized byte[] getAudioData() {
      if (this._rawDataLength <= 0) {
         return null;
      }

      byte[] audioData = new byte[0];

      for (int i = 0; i < this._rawDataChunks.length; i++) {
         Arrays.append(audioData, (byte[])this._rawDataChunks[i]);
      }

      return audioData;
   }
}
