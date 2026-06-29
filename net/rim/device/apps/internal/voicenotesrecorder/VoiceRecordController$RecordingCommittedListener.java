package net.rim.device.apps.internal.voicenotesrecorder;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

final class VoiceRecordController$RecordingCommittedListener implements PlayerListener {
   private Player _player;
   private boolean _committed;

   public final synchronized boolean waitUntilCommitted() {
      if (this._committed) {
         return true;
      }

      try {
         this.wait(5000);
      } finally {
         return this._committed;
      }

      return this._committed;
   }

   @Override
   public final synchronized void playerUpdate(Player player, String event, Object eventData) {
      if (player == this._player) {
         if (event.equals("net.rim.device.internal.media.recordCommitted")) {
            this._committed = true;
            this.notifyAll();
            return;
         }

         if (event.equals("recordError") || event.equals("error")) {
            this.notifyAll();
         }
      }
   }

   public VoiceRecordController$RecordingCommittedListener(Player player) {
      this._player = player;
      this._player.addPlayerListener(this);
      this._committed = false;
   }
}
