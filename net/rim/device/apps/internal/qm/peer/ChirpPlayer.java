package net.rim.device.apps.internal.qm.peer;

import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import net.rim.device.resources.Resource;

final class ChirpPlayer extends Thread implements PlayerListener {
   private Player _player;
   private ChirpPlayer$Listener _listener;

   ChirpPlayer(ChirpPlayer$Listener listener) {
      this._listener = listener;
   }

   @Override
   public final void run() {
      try {
         byte[] buffer = Resource.getResourceClass().getResource("Floor_Control.wav");
         InputStream is = (InputStream)(new Object(buffer));
         this._player = Manager.createPlayer(is, "audio/x-wav");
         this._player.addPlayerListener(this);
         this._player.start();
      } finally {
         this._player.removePlayerListener(this);
         return;
      }
   }

   @Override
   public final void playerUpdate(Player player, String event, Object eventData) {
      if (event.equals("endOfMedia") || event.equals("error") || event.equals("closed")) {
         this._player.removePlayerListener(this);
         this._listener.onEndOfChirp();
      }
   }
}
