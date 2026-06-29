package net.rim.plazmic.internal.contentpreview.playback;

public interface PlaybackCommandHandler {
   String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/playback/PlaybackCommandHandler.java#1 $";

   void play();

   void pause();

   void changeRate(float var1);

   void seek(long var1);

   void seekFast(long var1, long var3);

   void skip(long var1);

   void requestSceneInfo();
}
