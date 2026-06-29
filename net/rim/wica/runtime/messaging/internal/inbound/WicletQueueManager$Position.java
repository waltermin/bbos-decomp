package net.rim.wica.runtime.messaging.internal.inbound;

final class WicletQueueManager$Position {
   int _posFromHead;
   int _posFromTail;

   WicletQueueManager$Position(int posFromHead, int posFromTail) {
      this._posFromHead = posFromHead;
      this._posFromTail = posFromTail;
   }

   final void update(int posFromHead, int posFromTail) {
      this._posFromHead = posFromHead;
      this._posFromTail = posFromTail;
   }
}
