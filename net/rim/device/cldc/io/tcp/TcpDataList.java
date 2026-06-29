package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpUtils;

class TcpDataList {
   protected TcpDataBlock _head;
   protected TcpDataBlock _tail;
   protected TcpDataBlock _lastModified;
   private Object lockObj = new Object();

   boolean isEmpty() {
      return this.getRightEdge() == this.getLeftEdge();
   }

   void addData(TcpDataBlock newData) {
      int leftEdge = newData.getLeftEdge();
      synchronized (this.lockObj) {
         if (this._head == null) {
            this._lastModified = this._head = this._tail = newData;
            return;
         }

         if (TcpUtils.seqLT(leftEdge, this._head.getLeftEdge())) {
            newData.setNext(this._head);
            newData.setPrev(null);
            this._head.setPrev(newData);
            this._lastModified = this._head = newData;
         } else {
            this._lastModified = this._tail;

            while (TcpUtils.seqLT(leftEdge, this._lastModified.getLeftEdge())) {
               this._lastModified = this._lastModified.getPrev();
            }

            if (TcpUtils.seqLT(this._lastModified.getRightEdge(), leftEdge)) {
               this.insertAfter(this._lastModified, newData);
            } else {
               if (TcpUtils.seqGEQ(this._lastModified.getRightEdge(), newData.getRightEdge())) {
                  return;
               }

               this._lastModified.append(newData);
            }
         }
      }

      int rightEdge = this._lastModified.getRightEdge();

      while (this._lastModified.getNext() != null && TcpUtils.seqGEQ(rightEdge, this._lastModified.getNext().getRightEdge())) {
         this.remove(this._lastModified.getNext());
      }

      if (this._lastModified.getNext() != null && TcpUtils.seqGEQ(rightEdge, this._lastModified.getNext().getLeftEdge())) {
         this.concatenate(this._lastModified, this._lastModified.getNext());
      }
   }

   protected void insertAfter(TcpDataBlock cur, TcpDataBlock newData) {
      newData.setNext(cur.getNext());
      newData.setPrev(cur);
      cur.setNext(newData);
      if (newData.getNext() != null) {
         newData.getNext().setPrev(newData);
      }

      this._lastModified = newData;
      synchronized (this.lockObj) {
         if (cur == this._tail) {
            this._tail = newData;
         }
      }
   }

   protected void concatenate(TcpDataBlock left, TcpDataBlock right) {
      left.append(right);
      this.remove(right);
   }

   protected void remove(TcpDataBlock deadElementWalking) {
      TcpDataBlock prev = deadElementWalking.getPrev();
      TcpDataBlock next = deadElementWalking.getNext();
      synchronized (this.lockObj) {
         if (deadElementWalking == this._tail) {
            this._tail = prev;
         }
      }

      if (next != null) {
         next.setPrev(prev);
      }

      if (prev != null) {
         prev.setNext(next);
      }

      synchronized (this.lockObj) {
         if (deadElementWalking == this._head) {
            this._head = next;
         }
      }
   }

   void removeUpTo(int seqNo) {
      synchronized (this.lockObj) {
         while (this._head != null && TcpUtils.seqGEQ(seqNo, this._head.getRightEdge())) {
            this.remove(this._head);
         }

         if (this._head != null && TcpUtils.seqLT(this._head.getLeftEdge(), seqNo)) {
            this._head.removeUpTo(seqNo);
         }
      }
   }

   void purge() {
      synchronized (this.lockObj) {
         this._head = this._tail = this._lastModified = null;
      }
   }

   boolean isContiguous() {
      synchronized (this.lockObj) {
         return this._head == this._tail || this.isEmpty();
      }
   }

   int getLeftEdge() {
      synchronized (this.lockObj) {
         return this._head == null ? 0 : this._head.getLeftEdge();
      }
   }

   int getRightEdge() {
      synchronized (this.lockObj) {
         return this._tail == null ? 0 : this._tail.getRightEdge();
      }
   }

   TcpDataBlock dequeueHead() {
      synchronized (this.lockObj) {
         if (this._head == null) {
            throw new Object("List is empty");
         }

         TcpDataBlock ret = this._head;
         this.remove(this._head);
         return ret;
      }
   }
}
