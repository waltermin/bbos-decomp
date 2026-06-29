package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpUtils;

class TcpSubDataList extends TcpDataList implements TcpDataBlock {
   private TcpDataBlock _next;
   private TcpDataBlock _prev;
   private int _rightEdge;
   private int _leftEdge;
   private int _memoryUsed;

   TcpReassembleDataNode getNextNode() {
      TcpReassembleDataNode next = (TcpReassembleDataNode)super._head;
      if (next != null) {
         this.removeUpTo(next.getRightEdge());
      }

      return next;
   }

   void appendList(TcpSubDataList newList) {
      newList.removeUpTo(this._rightEdge);
      if (super._head == null) {
         super._head = newList._head;
      } else {
         super._tail.setNext(newList._head);
         newList._head.setPrev(super._tail);
      }

      super._tail = newList._tail;
      this._rightEdge = super._tail.getRightEdge();
   }

   @Override
   public void setNext(TcpDataBlock next) {
      this._next = next;
   }

   @Override
   public void setPrev(TcpDataBlock prev) {
      this._prev = prev;
   }

   @Override
   public void append(TcpDataBlock newData) {
      if (newData instanceof TcpSubDataList) {
         this.appendList((TcpSubDataList)newData);
      } else {
         this._memoryUsed = this._memoryUsed + newData.getMemoryUsed();
         newData.removeUpTo(this.getRightEdge());
         if (super._head == null) {
            super._head = super._tail = newData;
            newData.setPrev(null);
            newData.setNext(null);
         } else {
            newData.setPrev(super._tail);
            newData.setNext(null);
            super._tail.setNext(newData);
            super._tail = newData;
         }

         this._rightEdge = super._tail.getRightEdge();
      }
   }

   @Override
   public TcpDataBlock getPrev() {
      return this._prev;
   }

   @Override
   public int size() {
      return this._rightEdge - this._leftEdge;
   }

   @Override
   public int getMemoryUsed() {
      return this._memoryUsed;
   }

   @Override
   public TcpDataBlock getNext() {
      return this._next;
   }

   @Override
   public int getRightEdge() {
      return this._rightEdge;
   }

   TcpSubDataList(TcpDataBlock startingData) {
      this.addData(startingData);
      this._leftEdge = startingData.getLeftEdge();
      this._rightEdge = startingData.getRightEdge();
   }

   @Override
   protected void remove(TcpDataBlock deadElementWalking) {
      this._memoryUsed = this._memoryUsed - deadElementWalking.getMemoryUsed();
      super.remove(deadElementWalking);
   }

   @Override
   boolean isContiguous() {
      return true;
   }

   @Override
   public int getLeftEdge() {
      return this._leftEdge;
   }

   @Override
   public void removeUpTo(int seqNo) {
      if (TcpUtils.seqLT(this._leftEdge, seqNo)) {
         super.removeUpTo(seqNo);
         this._leftEdge = seqNo;
         if (TcpUtils.seqLT(this._rightEdge, seqNo)) {
            this._rightEdge = seqNo;
         }
      }
   }

   @Override
   public void purge() {
      super.purge();
      this._next = this._prev = null;
      this._rightEdge = this._leftEdge = this._memoryUsed = 0;
   }

   public TcpSubDataList(int initialSeqNo) {
      this._leftEdge = this._rightEdge = initialSeqNo;
   }
}
