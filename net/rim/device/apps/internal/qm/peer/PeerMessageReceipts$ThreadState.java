package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.DataBuffer;

final class PeerMessageReceipts$ThreadState extends QMTimerTask {
   PeerConversation _conversation;
   int _lastReceived;
   int _lastRead;
   int[] _cookies;
   private final PeerMessageReceipts this$0;

   PeerMessageReceipts$ThreadState(PeerMessageReceipts _1, PeerConversation conversation) {
      this.this$0 = _1;
      this._lastReceived = -1;
      this._lastRead = -1;
      this._cookies = new int[0];
      this._conversation = conversation;
   }

   public final void onReceive(int id) {
      this._lastReceived = id;
   }

   public final void onRead() {
      if (this._lastReceived != -1) {
         if (this._lastRead == -1) {
            this.this$0._timers.schedule(this, 5000);
         }

         this._lastRead = this._lastReceived;
         this._lastReceived = -1;
      }
   }

   public final void hitch(DataBuffer db) {
      if (this._lastRead != -1) {
         label22:
         try {
            new ReadReceiptDataBlob(this._conversation.getId(), this._lastRead).pickle(db);
         } finally {
            break label22;
         }

         this._lastRead = -1;
         this.this$0._timers.cancel(this);
      }
   }

   @Override
   public final void run() {
      this.this$0._session.sendBlob(this._conversation.getParticipants(), new ReadReceiptDataBlob(this._conversation.getId(), this._lastRead));
      this._lastRead = -1;
   }

   final int find(int cookie) {
      int count = this._cookies.length;

      for (int i = 0; i < count; i++) {
         if (this._cookies[i] == cookie) {
            return i;
         }
      }

      return -1;
   }

   final void onSend(int cookie) {
      Utils.add(this._cookies, cookie);
   }

   final void onReadReceipt(int cookie) {
      int index = this.find(cookie);
      if (index >= 0) {
         for (int i = 0; i <= index; i++) {
            this.this$0._application._messageFieldLookup.messageStateChange(this._cookies[0], 11);
            this.this$0._application._messageFieldLookup.remove(this._cookies[0]);
            Utils.removeAt(this._cookies, 0);
         }
      }
   }
}
