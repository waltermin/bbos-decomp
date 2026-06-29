package net.rim.wica.runtime.security.internal;

final class SequenceProviderImpl$IncomingSequence {
   private int _windowStart;
   private long _window;
   private int _index;
   private static final int WINDOW_SIZE = 64;

   SequenceProviderImpl$IncomingSequence(int index, int windowStart, long window) {
      this._index = index;
      this._windowStart = windowStart;
      this._window = window;
   }

   final void start(int windowStart, long window) {
      this._windowStart = windowStart;
      this._window = window;
   }

   final int getIndex() {
      return this._index;
   }

   final void setIndex(int index) {
      this._index = index;
   }

   final int getWindowStart() {
      return this._windowStart;
   }

   final long getWindow() {
      return this._window;
   }

   final boolean verify(int sequenceId) {
      boolean verified = false;
      if (sequenceId >= this._windowStart + 64) {
         int windowEnd = this._windowStart + 64 - 1;
         int shiftDistance = sequenceId - windowEnd;
         this._window <<= shiftDistance;
         this._window |= 1;
         this._windowStart += shiftDistance;
         return true;
      }

      if (sequenceId >= this._windowStart) {
         int windowEnd = this._windowStart + 64 - 1;
         int shiftDistance = windowEnd - sequenceId;
         long mark = (long)1 << shiftDistance;
         verified = (this._window & mark) == 0;
         if (verified) {
            this._window |= mark;
         }
      }

      return verified;
   }
}
