package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpUtils;

final class TcpReassembleDataNode implements TcpDataBlock {
   protected int _length;
   protected int _offset;
   byte[] _data;
   private int _leftEdge;
   private int _rightEdge;
   private TcpDataBlock _next;
   private TcpDataBlock _prev;
   private static final int TCP_DEBUG_LEVEL;
   protected static final boolean TCP_SHOW_RTE_MESSAGES;

   TcpReassembleDataNode(int seqNum, int length, int offset, byte[] data) {
      this._length = length;
      this._offset = offset;
      this._data = data;
      this._leftEdge = seqNum;
      this._rightEdge = seqNum + length;
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
   public final void append(TcpDataBlock overlappingBlock) {
      int overhang = overlappingBlock.getRightEdge() - this._rightEdge;
      int myDataLength = this._length;
      this._length = myDataLength + overhang;
      byte[] tempData = new byte[this._length];
      System.arraycopy(this._data, this._offset, tempData, 0, myDataLength);
      if (this._rightEdge - overlappingBlock.getLeftEdge() >= 0) {
         System.arraycopy(
            ((TcpReassembleDataNode)overlappingBlock)._data,
            ((TcpReassembleDataNode)overlappingBlock)._offset + this._rightEdge - overlappingBlock.getLeftEdge(),
            tempData,
            myDataLength,
            overhang
         );
         this._data = tempData;
         this._rightEdge = overlappingBlock.getRightEdge();
         this._offset = 0;
      } else {
         System.out.println("ERROR: RTE - TcpReassembleDataNode::setRightEdge - This should always be positive.");
         throw new Object("setRightEdge should always be positive.");
      }
   }

   @Override
   public final void removeUpTo(int seqNo) {
      if (!TcpUtils.seqGEQ(this._leftEdge, seqNo)) {
         int amountTrimmed = seqNo - this._leftEdge;
         if (TcpUtils.seqLT(seqNo, this._rightEdge)) {
            this._offset += amountTrimmed;
            this._length -= amountTrimmed;
            this._leftEdge = seqNo;
         } else {
            System.out.println("ERROR: RTE - TcpReassembleDataNode::trimLeftEdge() Bad LeftEdge trim attempt");
            throw new Object("Bad LeftEdge trim attempt");
         }
      }
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
   public final int size() {
      return this._rightEdge - this._leftEdge;
   }

   @Override
   public final int getMemoryUsed() {
      return this._length;
   }
}
