package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.apps.api.ribbon.RibbonLauncher;

class MediaLayout$3 implements Runnable {
   private final String val$url;
   private final MediaLayout this$0;

   MediaLayout$3(MediaLayout _1, String _2) {
      this.this$0 = _1;
      this.val$url = _2;
   }

   @Override
   public void run() {
      String finalUrl = this.val$url.substring(9);
      if (!this.this$0._skinManager.load(finalUrl)) {
         if (this.this$0._runnableResolver != null) {
            Runnable r = this.this$0._runnableResolver.get(finalUrl);
            if (r != null) {
               r.run();
               return;
            }
         }

         try {
            RibbonLauncher.getInstance().launch(finalUrl);
         } finally {
            System.err.println(((StringBuffer)(new Object("Could not launch "))).append(finalUrl).toString());
            return;
         }
      }
   }
}
