package net.rim.tid.im.spellcheck;

class SpellCheckInputMethodVariant$DelayedSpellCheckKeyedInput implements Runnable {
   private Object _lock;
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$DelayedSpellCheckKeyedInput(SpellCheckInputMethodVariant _1, Object lock) {
      this.this$0 = _1;
      this._lock = lock;
   }

   @Override
   public void run() {
      synchronized (this._lock) {
         this.this$0._delayedSpellCheckKeyedInputRunnableID = -1;
         this.this$0.spellCheckEnteredText();
      }
   }
}
