package net.rim.device.api.io;

import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.internal.io.file.FileSystemInfo;
import net.rim.vm.Process;

public final class FileInputStream extends InputStream {
   private FileEventDispatcher _dispatcher;
   private int _handle;
   private byte[] _buffer;
   private int _offset;
   private int _available;
   private boolean _eof;
   private Runnable _cleanupRunnable;

   public FileInputStream(int fs, String fileName) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      this._dispatcher = File.getEventDispatcher();
      FileSystemInfo fsInfo = File.getFileSystemInfo(fs);
      this._buffer = new byte[fsInfo.getMaxReadLength()];
      this._offset = 0;
      this._available = 0;
      this._handle = File.open(fs, fileName, 1);
      this._cleanupRunnable = new FileInputStream$FileInputStreamCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
   }

   @Override
   public final synchronized int read() {
      if (this._available == 0) {
         this.doRead();
      }

      if (this._available == 0) {
         return -1;
      }

      this._available--;
      return this._buffer[this._offset++] & 0xFF;
   }

   @Override
   public final synchronized int read(byte[] b, int off, int len) {
      if (off >= 0 && len >= 0 && b.length - len >= off) {
         int bytesCopied = 0;

         while (bytesCopied < len) {
            if (this._available == 0) {
               this.doRead();
               if (this._available == 0) {
                  if (bytesCopied == 0) {
                     return -1;
                  }

                  return bytesCopied;
               }
            }

            int bytesToCopy = Math.min(this._available, len - bytesCopied);
            System.arraycopy(this._buffer, this._offset, b, off + bytesCopied, bytesToCopy);
            bytesCopied += bytesToCopy;
            this._available -= bytesToCopy;
            this._offset += bytesToCopy;
         }

         return bytesCopied;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public final synchronized void close() {
      File.close(this._handle);
      this._handle = -1;
      ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this._cleanupRunnable);
   }

   private final void doRead() {
      synchronized (this._dispatcher) {
         if (this._handle == -1) {
            throw new IOException("File not open");
         }

         if (!this._eof) {
            while (true) {
               long result = File.read(this._handle, this._buffer);
               int status = (int)result;
               if (File.checkStatus(status)) {
                  this._offset = 0;
                  this._available = (int)(result >> 32);
                  if (this._available != this._buffer.length) {
                     this._eof = true;
                  }

                  return;
               }

               status = this._dispatcher.waitForCompletion(4);
               File.checkStatus(status);
            }
         }
      }
   }
}
