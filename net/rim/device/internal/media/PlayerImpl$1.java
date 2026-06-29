package net.rim.device.internal.media;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

class PlayerImpl$1 implements Runnable {
   private final PlayerListener val$pl;
   private final Player val$player;
   private final String val$event;
   private final Object val$eventData;
   private final PlayerImpl this$0;

   PlayerImpl$1(PlayerImpl _1, PlayerListener _2, Player _3, String _4, Object _5) {
      this.this$0 = _1;
      this.val$pl = _2;
      this.val$player = _3;
      this.val$event = _4;
      this.val$eventData = _5;
   }

   @Override
   public void run() {
      this.val$pl.playerUpdate(this.val$player, this.val$event, this.val$eventData);
   }
}
