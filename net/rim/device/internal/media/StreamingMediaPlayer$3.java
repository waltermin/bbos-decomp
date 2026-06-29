package net.rim.device.internal.media;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

class StreamingMediaPlayer$3 implements Runnable {
   private final PlayerListener val$pl;
   private final Player val$player;
   private final StreamingMediaPlayer this$0;

   StreamingMediaPlayer$3(StreamingMediaPlayer _1, PlayerListener _2, Player _3) {
      this.this$0 = _1;
      this.val$pl = _2;
      this.val$player = _3;
   }

   @Override
   public void run() {
      this.this$0._timeEventRunnableID = -1;
      this.val$pl.playerUpdate(this.val$player, "com.rim.timeUpdate", new Object(this.this$0._mediaTime));
   }
}
