package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player;

import javax.microedition.media.control.VolumeControl;
import net.rim.device.apps.internal.profiles.Profile;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILPlayer;

public class VideoPlayerWrapper implements Player, javax.microedition.media.PlayerListener {
   private javax.microedition.media.Player _player;
   private PlayerListener _listener;
   private VolumeControl _volumeControl;

   public VideoPlayerWrapper(javax.microedition.media.Player player) {
      this._player = player;
      this._player.realize();
      this._volumeControl = (VolumeControl)this._player.getControl("VolumeControl");
   }

   @Override
   public int getType() {
      return 3;
   }

   @Override
   public VolumeControl getVolumeControl() {
      return this._volumeControl;
   }

   @Override
   public void start() {
      try {
         if (this._volumeControl != null) {
            boolean isQuietProfile = false;
            Profile profile = Profiles.getInstance().getEnabled();
            switch (profile.getIdentifier()) {
               case 1:
               case 2:
               case 5:
               case 7:
                  isQuietProfile = true;
               default:
                  if (isQuietProfile) {
                     this._volumeControl.setMute(true);
                  } else {
                     this._volumeControl.setMute(SMILPlayer.isMuted());
                  }

                  this._volumeControl.setLevel(SMILPlayer.getVolumeLevel());
            }
         }

         if (this._listener != null) {
            this._player.addPlayerListener(this);
         }

         this._player.start();
      } finally {
         System.err.println("Didn't start");
         return;
      }
   }

   @Override
   public void stop() {
      if (this._player.getState() == 400) {
         try {
            this._player.stop();
         } finally {
            System.err.println("Didn't stop");
            return;
         }
      }
   }

   @Override
   public void close() {
      this._player.close();
   }

   @Override
   public void setTime(long time) {
      try {
         this._player.setMediaTime(time * 1000);
      } finally {
         return;
      }
   }

   @Override
   public long getTime() {
      return this._player.getMediaTime() / 1000;
   }

   @Override
   public void playerUpdate(javax.microedition.media.Player player, String event, Object eventData) {
      if ("endOfMedia".equals(event) && this._listener != null && eventData != null) {
         this._listener.notifyComplete(eventData / 1000);
      }
   }

   @Override
   public void setPlayerListener(PlayerListener listener) {
      this._listener = listener;
   }
}
