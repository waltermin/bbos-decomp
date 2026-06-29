package net.rim.tid.im.spellcheck;

class SpellCheckInputMethodVariant$DelayedRunSpellCheck implements Runnable {
   private Object _lock;
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$DelayedRunSpellCheck(SpellCheckInputMethodVariant _1, Object lock) {
      this.this$0 = _1;
      this._lock = lock;
   }

   @Override
   public void run() {
      synchronized (this._lock) {
         switch (this.this$0._state) {
            case 5:
            case 6:
            default:
               this.this$0._bounds.end = this.this$0._bounds.start;
               this.this$0.runSpellCheck();
            case 4:
         }
      }
   }
}
