package net.rim.plazmic.internal.contentpreview.playback;

public interface PlaybackCommandHandler {
   String rcsid;

   void play();

   void pause();

   void changeRate(float var1);

   void seek(long var1);

   void seekFast(long var1, long var3);

   void skip(long var1);

   void requestSceneInfo();
}
