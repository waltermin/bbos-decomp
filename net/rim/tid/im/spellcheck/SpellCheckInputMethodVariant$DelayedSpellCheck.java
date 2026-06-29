package net.rim.tid.im.spellcheck;

class SpellCheckInputMethodVariant$DelayedSpellCheck implements Runnable {
   private Object _lock;
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$DelayedSpellCheck(SpellCheckInputMethodVariant _1, Object lock) {
      this.this$0 = _1;
      this._lock = lock;
   }

   @Override
   public void run() {
      synchronized (this._lock) {
         switch (this.this$0._state) {
            case 2:
            case 4:
               break;
            case 3:
            default:
               if (!this.this$0._cancelDelay) {
                  this.this$0.hideWindows();
                  this.this$0.ignoreToken(false);
                  this.this$0.runSpellCheck();
               }
               break;
            case 5:
            case 6:
               if (!this.this$0._cancelDelay) {
                  this.this$0.scanFromCurrentPosition(true);
               }
         }

         this.this$0._delaying = false;
      }
   }
}
