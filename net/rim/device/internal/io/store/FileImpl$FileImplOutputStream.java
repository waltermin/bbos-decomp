package net.rim.device.internal.io.store;

import java.io.IOException;
import java.io.OutputStream;
import net.rim.device.api.io.ReservableSize;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.vm.Array;
import net.rim.vm.Memory;

class FileImpl$FileImplOutputStream extends OutputStream implements ReservableSize {
   private int _lastFlush;
   private int _offset;
   private FileImpl$ReservableByteArrayOutputStream _baos;
   private final FileImpl this$0;

   public FileImpl$FileImplOutputStream(FileImpl _1, int offset) {
      this.this$0 = _1;
      this._baos = new FileImpl$ReservableByteArrayOutputStream();
      if (offset > _1._contentLength) {
         offset = _1._contentLength;
      }

      this._offset = offset;
   }

   @Override
   public synchronized void reserveSize(long size) {
      if (this._baos != null) {
         this._baos.reserveSize(size);
      }
   }

   @Override
   public synchronized void close() {
      if (this._baos != null) {
         this._baos.close();
         this.flush(true);
         this._baos = null;
         this.this$0.commit();
         FileSystem.addFileJournalEntry(this.this$0.getJSRPath(), 2);
      }
   }

   @Override
   public synchronized void flush() {
      this.flush(false);
   }

   private synchronized void flush(boolean onClose) throws IOException {
      if (this._baos == null) {
         throw new IOException();
      }

      int size = this._baos.size();
      this._lastFlush = 0;
      if (size != 0) {
         synchronized (this.this$0._contentLock) {
            if (this.this$0._content == null || this._offset == 0 && size > this.this$0._contentLength) {
               if (onClose) {
                  this.this$0.setContentInternal(this._baos.toByteArray());
               } else {
                  this.this$0.setContentInternal(this._baos.consumeByteArray(), size, false);
               }
            } else {
               if (Memory.isObjectInGroup(this.this$0._content)) {
                  this.this$0._content = (byte[])Memory.expandGroup(this.this$0._content);
               }

               int newLength = Math.max(this.this$0._contentLength, this._offset + size);
               if (this.this$0._content.length != newLength) {
                  Array.resize(this.this$0._content, newLength);
               }

               byte[] data = this._baos.getByteArray();
               System.arraycopy(data, 0, this.this$0._content, this._offset, size);
               this.this$0.setContentInternal(this.this$0._content);
               if (!onClose) {
                  this._baos.reset();
               }
            }

            this._offset += size;
         }
      }
   }

   @Override
   public synchronized void write(int b) throws IOException {
      if (this._baos == null) {
         throw new IOException();
      }

      FileImpl.verifyLength(this._offset + this._baos.size() + 1);
      this._baos.write(b);
      this._lastFlush++;
      if (this._lastFlush >= 262144) {
         this.flush();
      }
   }

   @Override
   public synchronized void write(byte[] b, int off, int len) throws IOException {
      if (this._baos == null) {
         throw new IOException();
      }

      FileImpl.verifyLength(this._offset + this._baos.size() + len);
      this._baos.ensureSpace(len);
      this._baos.write(b, off, len);
      this._lastFlush += len;
      if (this._lastFlush >= 262144) {
         this.flush();
      }
   }
}
