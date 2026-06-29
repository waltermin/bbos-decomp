package net.rim.device.internal.io.store;

import java.io.DataOutputStream;
import java.io.OutputStream;

final class ContentStoreOutputStream extends DataOutputStream {
   private ContentStoreConnection _connection;

   ContentStoreOutputStream(ContentStoreConnection connection, OutputStream out) {
      super(out);
      this._connection = connection;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         super.close();
         var3 = false;
      } finally {
         if (var3) {
            this._connection.outputClosed();
         }
      }

      this._connection.outputClosed();
   }
}
