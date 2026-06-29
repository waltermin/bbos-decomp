package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.util.MathUtilities;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.vm.Array;

final class AccumulatorInputStream$AccumulatorThread extends Thread {
   private SessionStats _stats;
   private final AccumulatorInputStream this$0;
   private static final int MAX_DESIRED_ARRAY_CHUNK = 262144;

   public AccumulatorInputStream$AccumulatorThread(AccumulatorInputStream _1, SessionStats stats) {
      this.this$0 = _1;
      this._stats = stats;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var9 = false /* VF: Semaphore variable */;

      label89: {
         try {
            label87:
            try {
               var9 = true;
               int e = 262144;
               int dataBuffSize = MathUtilities.clamp(1024, this.this$0._subIn.available(), e);
               byte[] data = new byte[dataBuffSize];
               int packetCount = 0;

               while (true) {
                  int count = this.this$0._subIn.read(data, 0, dataBuffSize);
                  if (count <= 0) {
                     if (count == -1) {
                        var9 = false;
                        break label89;
                     }
                  } else {
                     if (count != dataBuffSize) {
                        Array.resize(data, count);
                     }

                     this.this$0._pipe.write(data, 0, count, packetCount++);
                     if (this._stats != null) {
                        this._stats.addToReceived(count);
                     }

                     dataBuffSize = MathUtilities.clamp(1024, this.this$0._subIn.available(), e);
                     data = new byte[dataBuffSize];
                  }
               }
            } catch (Throwable var12) {
               this.this$0._ioException = e;
               var9 = false;
               break label87;
            }
         } finally {
            if (var9) {
               this.this$0._pipe.closeWrite();
            }
         }

         this.this$0._pipe.closeWrite();
         return;
      }

      this.this$0._pipe.closeWrite();
   }
}
