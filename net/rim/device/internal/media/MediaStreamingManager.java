package net.rim.device.internal.media;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.util.RingBuffer;

public class MediaStreamingManager {
   static final long GUID = 8461041122205944746L;
   public static final int NO_SESSION = -1;
   public static final int CODEC_VIDEO = 1000;

   MediaStreamingManager() {
   }

   public static MediaStreamingManager getInstance() {
      if (!InternalServices.isSoftwareCapable(2)) {
         return null;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (MediaStreamingManager)ar.waitFor(8461041122205944746L);
   }

   public void addListener(MediaStreamingListener _1) {
      throw null;
   }

   public MediaStreamingManager$StreamingSession recordStream(MediaStreamingCallback _1, RingBuffer _2, int _3, int _4) {
      throw null;
   }

   public MediaStreamingManager$StreamingSession openStream(MediaStreamingCallback _1, RingBuffer _2, int _3, int _4, int _5, boolean _6) {
      throw null;
   }

   public MediaStreamingManager$StreamingSession playStream(MediaStreamingCallback _1, RingBuffer _2, int _3, int _4, int _5, int _6) {
      throw null;
   }

   public MediaStreamingManager$StreamingSession reserveSession(int _1, int _2) {
      throw null;
   }

   public void stopSingleChannelAudio() {
      throw null;
   }
}
