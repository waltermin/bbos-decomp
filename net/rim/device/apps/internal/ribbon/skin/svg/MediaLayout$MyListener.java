package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.plazmic.mediaengine.MediaListener;

final class MediaLayout$MyListener implements MediaListener {
   private final MediaLayout this$0;

   MediaLayout$MyListener(MediaLayout _1) {
      this.this$0 = _1;
   }

   @Override
   public final void mediaEvent(Object sender, int event, int id, Object data) {
      switch (event) {
         case 7:
            this.this$0.load((String)data);
      }
   }
}
