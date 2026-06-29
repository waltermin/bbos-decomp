package net.rim.tid.im.spellcheck;

class SpellCheckInputMethodVariant$PushScreenRunnable implements Runnable {
   String _message;

   SpellCheckInputMethodVariant$PushScreenRunnable(String aMessage) {
      this._message = aMessage;
   }

   void setMessage(String aMessage) {
      this._message = aMessage;
   }

   @Override
   public void run() {
      SpellCheckInputMethodVariant$CompositionPreserveStatus.show(this._message, 1250);
   }
}
