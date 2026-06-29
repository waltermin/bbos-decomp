package net.rim.plazmic.internal.contentpreview.device.apps;

import java.io.ByteArrayInputStream;
import net.rim.plazmic.internal.contentpreview.playback.io.PlaybackCommandMessageReader;

final class PlaybackCommandServer$PlaybackCommandServerTask implements Runnable {
   private final PlaybackCommandServer this$0;

   private PlaybackCommandServer$PlaybackCommandServerTask(PlaybackCommandServer _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this.this$0._connection.receive(this.this$0._packet);
         byte[] e = this.this$0._packet.getData();
         synchronized (this) {
            if (this.this$0._handler != null) {
               PlaybackCommandMessageReader.parse(new ByteArrayInputStream(e), this.this$0._handler);
            }

            var6 = false;
         }
      } finally {
         if (var6) {
            if (this.this$0._threadService.isEnabled()) {
               return;
            }

            return;
         }
      }
   }

   PlaybackCommandServer$PlaybackCommandServerTask(PlaybackCommandServer x0, PlaybackCommandServer$1 x1) {
      this(x0);
   }
}
