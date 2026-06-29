package net.rim.device.api.io;

import java.io.IOException;
import java.io.OutputStream;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.internal.io.file.FileSystemInfo;
import net.rim.vm.Process;

public final class FileOutputStream extends OutputStream {
   private FileEventDispatcher _dispatcher;
   private int _handle;
   private byte[] _buffer;
   private int _offset;
   private int _available;
   private Runnable _cleanupRunnable;

   public FileOutputStream(int fs, String fileName) {
      if (fs != 0) {
         throw new IllegalArgumentException("Wrong API, use javax.microedition.io.file");
      }

      this._dispatcher = File.getEventDispatcher();
      FileSystemInfo fsInfo = File.getFileSystemInfo(fs);
      this._buffer = new byte[fsInfo.getMaxWriteLength()];
      this._offset = 0;
      this._available = this._buffer.length;
      this._handle = File.open(fs, fileName, 6);
      this._cleanupRunnable = new FileOutputStream$FileOutputStreamCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
   }

   @Override
   public final synchronized void write(int b) {
      if (this._available == 0) {
         this.flush();
      }

      this._available--;
      this._buffer[this._offset++] = (byte)b;
   }

   @Override
   public final synchronized void write(byte[] b, int off, int len) {
      if (off >= 0 && len >= 0 && b.length - len >= off) {
         int bytesCopied = 0;

         while (bytesCopied < len) {
            if (this._available == 0) {
               this.flush();
            }

            int bytesToCopy = Math.min(this._available, len - bytesCopied);
            System.arraycopy(b, off + bytesCopied, this._buffer, this._offset, bytesToCopy);
            bytesCopied += bytesToCopy;
            this._available -= bytesToCopy;
            this._offset += bytesToCopy;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public final synchronized void flush() {
      synchronized (this._dispatcher) {
         if (this._handle == -1) {
            throw new IOException("File not open");
         }

         int bytesToWrite = this._buffer.length - this._available;
         if (bytesToWrite != 0) {
            int status = File.write(this._handle, this._buffer, bytesToWrite);
            if (!File.checkStatus(status)) {
               status = this._dispatcher.waitForCompletion(3);
               File.checkStatus(status);
            }

            this._offset = 0;
            this._available = this._buffer.length;
         }
      }
   }

   @Override
   public final synchronized void close() {
      this.flush();
      File.close(this._handle);
      this._handle = -1;
      ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this._cleanupRunnable);
   }
}
