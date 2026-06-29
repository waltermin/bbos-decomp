package net.rim.device.apps.internal.qm.peer;

final class TypingTimer extends QMTimerTask {
   boolean _started;
   boolean _sent;
   private PeerConversation _conversation;
   private static final int ON_KEY_DELAY;
   private static final int DEFAULT_DELAY;
   private static QMTimer _timer = new QMTimer();

   TypingTimer(PeerConversation conversation) {
      this._conversation = conversation;
   }

   final void onKey() {
      if (!this._started) {
         this._started = true;
         _timer.schedule(this, 5000);
      } else {
         if (this._sent) {
            this.startStoppedTimer();
         }
      }
   }

   final void startStoppedTimer() {
      this.cancel();
      _timer.schedule(this, 30000);
   }

   final void onClose() {
      this.cancel();
      if (this._sent) {
         this.run();
      }
   }

   final void onSend() {
      this._sent = this._started = false;
      this.cancel();
   }

   final void cancel() {
      if (this.isScheduled()) {
         _timer.cancel(this);
      }
   }

   @Override
   public final void run() {
      if (this._sent) {
         this._started = this._sent = false;
         PeerApplication.getSession().userTyping(false, this._conversation);
      } else {
         if (this._started) {
            this._sent = true;
            PeerApplication.getSession().userTyping(true, this._conversation);
            this.startStoppedTimer();
         }
      }
   }
}
