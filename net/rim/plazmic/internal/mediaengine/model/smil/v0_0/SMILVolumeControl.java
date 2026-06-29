package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import javax.microedition.media.control.VolumeControl;

public class SMILVolumeControl implements VolumeControl {
   private SMILPlayer _sp;
   private VolumeChangeListener _volumeListener;

   SMILVolumeControl(SMILPlayer sp) {
      this._sp = sp;
   }

   public void setVolumeChangeListener(VolumeChangeListener listener) {
      this._volumeListener = listener;
   }

   @Override
   public void setMute(boolean mute) {
      if (this.isMuted() != mute) {
         this._sp.setMute(mute);
      }
   }

   @Override
   public boolean isMuted() {
      return SMILPlayer.isMuted();
   }

   @Override
   public int setLevel(int level) {
      if (this.getLevel() == level) {
         return level;
      }

      this._sp.setVolumeLevel(level);
      if (this._volumeListener != null) {
         this._volumeListener.volumeChanged(level);
      }

      return level;
   }

   @Override
   public int getLevel() {
      return SMILPlayer.getVolumeLevel();
   }
}
