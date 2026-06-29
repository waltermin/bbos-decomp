package net.rim.device.internal.io.store;

import java.io.OutputStream;
import java.util.Vector;
import net.rim.device.api.io.ReservableSize;
import net.rim.device.api.system.ApplicationRegistry;

final class AutoCloseOutputStream extends OutputStream implements ReservableSize {
   private OutputStream _underlying;
   private AutoCloseOutputStream$Helper _helper;
   private static long GUID = 4177602302191527710L;
   private static Vector _table = ApplicationRegistry.getApplicationRegistry().getVector(GUID);

   AutoCloseOutputStream(OutputStream outputStream) {
      this._underlying = outputStream;
      this._helper = new AutoCloseOutputStream$Helper(this, outputStream);
      synchronized (_table) {
         _table.addElement(this._helper);
      }
   }

   static final void cleanup() {
      synchronized (_table) {
         for (int lv = _table.size() - 1; lv >= 0; lv--) {
            AutoCloseOutputStream$Helper helper = (AutoCloseOutputStream$Helper)_table.elementAt(lv);
            if (helper.isDiscarded()) {
               _table.removeElementAt(lv);
            }
         }
      }
   }

   @Override
   public final void close() {
      if (this._helper != null) {
         this._underlying = null;
         this._helper.close();
         synchronized (_table) {
            _table.removeElement(this._helper);
            this._helper = null;
         }
      }
   }

   @Override
   public final void flush() {
      if (this._underlying == null) {
         throw new Object(1000);
      }

      this._underlying.flush();
   }

   @Override
   public final void reserveSize(long size) {
      if (this._underlying == null) {
         throw new Object(1000);
      }

      ((ReservableSize)this._underlying).reserveSize(size);
   }

   @Override
   public final void write(int b) {
      if (this._underlying == null) {
         throw new Object(1000);
      }

      this._underlying.write(b);
   }

   @Override
   public final void write(byte[] b, int off, int len) {
      if (this._underlying == null) {
         throw new Object(1000);
      }

      this._underlying.write(b, off, len);
   }

   @Override
   public final void write(byte[] b) {
      if (this._underlying == null) {
         throw new Object(1000);
      }

      this._underlying.write(b);
   }
}
