package net.rim.device.internal.io.store;

import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.ReservableSize;
import net.rim.device.api.io.file.FileIOException;
import net.rim.vm.Array;

class FileImpl$ReservableByteArrayOutputStream extends NoCopyByteArrayOutputStream implements ReservableSize {
   boolean _closed;

   public byte[] consumeByteArray() {
      byte[] rc = super.buf;
      this.reset();
      super.buf = new byte[0];
      Array.setSectionSize(super.buf, 4096);
      return rc;
   }

   public void ensureSpace(int len) throws FileIOException {
      if (super.count + len >= super.buf.length) {
         try {
            Array.extend(super.buf, len);
         } finally {
            throw new FileIOException(9);
         }
      }
   }

   @Override
   public void reserveSize(long size) throws FileIOException {
      FileImpl.verifyLength(size);

      try {
         if (size > 4096) {
            Array.setSectionSize(super.buf, 4096);
         }

         Array.resize(super.buf, Math.max(super.count, (int)size));
      } finally {
         throw new FileIOException(9);
      }
   }

   FileImpl$ReservableByteArrayOutputStream() {
      Array.setSectionSize(super.buf, 4096);
   }

   @Override
   public void close() {
      if (!this._closed) {
         this._closed = true;
         Array.resize(super.buf, super.count);
      }

      super.close();
   }
}
