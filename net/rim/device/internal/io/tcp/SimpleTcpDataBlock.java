package net.rim.device.internal.io.tcp;

public final class SimpleTcpDataBlock implements TcpDataBlock, TcpConstants {
   private int _leftEdge;
   private int _rightEdge;
   private TcpDataBlock _next;
   private TcpDataBlock _prev;

   SimpleTcpDataBlock() {
   }

   public SimpleTcpDataBlock(int leftEdge, int rightEdge) {
      this._leftEdge = leftEdge;
      this._rightEdge = rightEdge;
   }

   @Override
   public final String toString() {
      return "(" + this._leftEdge + ", " + this._rightEdge + ")";
   }

   @Override
   public final int getLeftEdge() {
      return this._leftEdge;
   }

   @Override
   public final int getRightEdge() {
      return this._rightEdge;
   }

   @Override
   public final int size() {
      return this._rightEdge - this._leftEdge;
   }

   @Override
   public final TcpDataBlock getNext() {
      return this._next;
   }

   @Override
   public final TcpDataBlock getPrev() {
      return this._prev;
   }

   @Override
   public final void setNext(TcpDataBlock next) {
      this._next = next;
   }

   @Override
   public final void setPrev(TcpDataBlock prev) {
      this._prev = prev;
   }

   @Override
   public final void removeUpTo(int newLeftEdge) {
      if (TcpUtils.seqGT(newLeftEdge, this._leftEdge) && TcpUtils.seqLT(newLeftEdge, this._rightEdge)) {
         this._leftEdge = newLeftEdge;
      }
   }

   @Override
   public final void append(TcpDataBlock newData) {
      if (TcpUtils.seqLT(this._rightEdge, newData.getRightEdge())) {
         this._rightEdge = newData.getRightEdge();
      }
   }

   @Override
   public final int getMemoryUsed() {
      return 0;
   }
}
