package net.rim.plazmic.internal.mediaengine.util;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.mediaengine.MediaListener;

public class MEUtilities {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void fireMediaEvent(Object sender, SafeArray listeners, int event, int eventParam, Object data) {
      if (listeners.isSafe()) {
         listeners.acquireSafeArray();
      }

      for (int i = listeners.count - 1; i >= 0; i--) {
         try {
            if (listeners.array[i] != null) {
               ((MediaListener)listeners.array[i]).mediaEvent(sender, event, eventParam, data);
            }
         } catch (Throwable var8) {
            MediaFactory.getPlatform().logDebug(sender, 22, -1, e);
            continue;
         }
      }

      if (listeners.isSafe()) {
         listeners.releaseSafeArray();
      }
   }

   public static MediaModel getMediaModel(Object media) {
      if (media instanceof MediaServices) {
         media = ((MediaServices)media).getMedia();
      }

      return !(media instanceof MediaModel) ? null : (MediaModel)media;
   }
}
