package net.rim.device.apps.internal.browser.html;

final class HTMLBrowserContent$ShutdownScriptEngine implements Runnable {
   private boolean _pause;
   private final HTMLBrowserContent this$0;

   public HTMLBrowserContent$ShutdownScriptEngine(HTMLBrowserContent _1, boolean pause) {
      this.this$0 = _1;
      this._pause = pause;
   }

   public final void runIt() {
      if (HTMLBrowserContent.access$000(this.this$0) != null) {
         HTMLBrowserContent.access$100(this.this$0).invokeRunnable(this);
      } else {
         new Thread(this).start();
      }
   }

   @Override
   public final void run() {
      if (this.this$0._scriptEngine != null) {
         if (this._pause) {
            this.this$0._scriptEngine.documentHidden(this.this$0._document);
            return;
         }

         this.this$0._scriptEngine.documentClosed(this.this$0._document);
         this.this$0._scriptEngine = null;
      }
   }
}
