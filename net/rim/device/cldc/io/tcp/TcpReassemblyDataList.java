package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.SimpleTcpDataBlock;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpUtils;

class TcpReassemblyDataList extends TcpDataList implements TcpConstants {
   private int _maxMemoryLimit = 65536;
   private int _maxDataLimit = 65536;
   private TcpDataBlock[] _sackQueue = new TcpDataBlock[3];
   private SendHotlist _sendHotlist;

   TcpReassemblyDataList(SendHotlist sendHotlist) {
      this._sendHotlist = sendHotlist;
   }

   @Override
   void purge() {
      this._maxMemoryLimit = 65536;
      this._maxDataLimit = 65536;
      this._sackQueue = new TcpDataBlock[3];
      super.purge();
   }

   int getAvailableSpace() {
      int avail = super._head == null ? this._maxDataLimit : this._maxDataLimit - super._head.size();
      return avail > 0 ? avail : 0;
   }

   int getAvailableData() {
      return super._head.size();
   }

   void setMaxDataLimit(int maxDataLimit) {
      this._maxDataLimit = maxDataLimit;
   }

   int getMaxDataLimit() {
      return this._maxDataLimit;
   }

   void setMaxMemoryLimit(int maxMemoryLimit) {
      this._maxMemoryLimit = maxMemoryLimit;
   }

   SimpleTcpDataBlock[] getSackBlocks() {
      if (this.isContiguous()) {
         return null;
      }

      int count = 0;

      for (int i = 0; i < 3 && this._sackQueue[i] != null; i++) {
         count++;
      }

      if (count == 0) {
         return null;
      }

      SimpleTcpDataBlock[] ret = new SimpleTcpDataBlock[count];

      for (int var4 = 0; var4 < 3 && this._sackQueue[var4] != null; var4++) {
         ret[var4] = new SimpleTcpDataBlock(this._sackQueue[var4].getLeftEdge(), this._sackQueue[var4].getRightEdge());
      }

      return ret;
   }

   private void addSackBlock(TcpDataBlock newBlock) {
      this.removeSackBlock(newBlock);
      this._sackQueue[2] = this._sackQueue[1];
      this._sackQueue[1] = this._sackQueue[0];
      this._sackQueue[0] = newBlock;
   }

   private void removeSackBlock(TcpDataBlock deadBlockWalking) {
      for (int i = 0; i < 3; i++) {
         if (this._sackQueue[i] == deadBlockWalking) {
            for (int j = i; j < 2; j++) {
               this._sackQueue[j] = this._sackQueue[j + 1];
            }

            this._sackQueue[2] = null;
            return;
         }
      }
   }

   @Override
   void addData(TcpDataBlock newData) {
      int memoryUsed = 0;

      for (TcpDataBlock cur = super._head; cur != null; cur = cur.getNext()) {
         memoryUsed += cur.getMemoryUsed();
      }

      if (memoryUsed + newData.getMemoryUsed() <= this._maxMemoryLimit) {
         if (super._head != null) {
            int currentAck = super._head.getRightEdge();
            if (TcpUtils.seqGEQ(currentAck, newData.getRightEdge())) {
               return;
            }

            if (TcpUtils.seqLT(newData.getLeftEdge(), currentAck)) {
               newData.removeUpTo(currentAck);
            }
         }

         super.addData(newData);
         if (super._lastModified != super._head) {
            this.addSackBlock(super._lastModified);
         }

         if (this._sendHotlist != null) {
            this._sendHotlist.setNextAckNumberToSend(super._head.getRightEdge(), !this.isContiguous());
         }
      }
   }

   void setInitialAck(int initialAck) {
      if (super._head == null) {
         super.addData(new TcpSubDataList(initialAck));
      }
   }

   @Override
   protected void insertAfter(TcpDataBlock cur, TcpDataBlock newData) {
      super.insertAfter(cur, new TcpSubDataList(newData));
   }

   @Override
   protected void remove(TcpDataBlock deadElementWalking) {
      super.remove(deadElementWalking);
      this.removeSackBlock(deadElementWalking);
   }

   @Override
   void removeUpTo(int seqNo) {
      if (super._head != null) {
         if (super._head.getNext() != null && TcpUtils.seqLT(seqNo, super._head.getNext().getLeftEdge())) {
            super._head.removeUpTo(seqNo);
         } else {
            super.removeUpTo(seqNo);
         }
      }
   }

   TcpReassembleDataNode getNextNode() {
      if (super._head.size() == 0 && super._head.getNext() != null) {
         this.removeUpTo(super._head.getRightEdge());
      }

      return ((TcpSubDataList)super._head).getNextNode();
   }
}
