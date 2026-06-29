package net.rim.device.cldc.io.sms;

final class Protocol$MessageSegmentQueue {
   Protocol$StoreMessage[] messages;
   private int currentSize;
   private int front;
   private int back;

   public Protocol$MessageSegmentQueue(int capacity) {
      this.messages = new Protocol$StoreMessage[capacity];
      this.currentSize = 0;
      this.front = 0;
      this.back = -1;
   }

   public final void add(Protocol$StoreMessage x) {
      if (this.currentSize == this.messages.length) {
         this.currentSize--;
         this.messages[this.front] = null;
         if (++this.front == this.messages.length) {
            this.front = 0;
         }
      }

      if (++this.back == this.messages.length) {
         this.back = 0;
      }

      this.messages[this.back] = x;
      this.currentSize++;
   }
}
