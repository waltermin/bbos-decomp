package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player;

import javax.microedition.media.control.VolumeControl;

public interface Player {
   int TYPE_AUDIO = 1;
   int TYPE_ANIMATED_BITMAP = 2;
   int TYPE_VIDEO = 3;

   VolumeControl getVolumeControl();

   int getType();

   void start();

   void stop();

   void close();

   void setTime(long var1);

   long getTime();

   void setPlayerListener(PlayerListener var1);
}
