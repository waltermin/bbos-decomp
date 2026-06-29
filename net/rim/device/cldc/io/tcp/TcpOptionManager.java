package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpDatagramProperties;

public final class TcpOptionManager implements TcpConstants {
   boolean _sackEnabled = true;
   boolean _mssEnabled = true;
   boolean _scaleEnabled = false;
   boolean _delayedAckEnabled = true;
   boolean _keepAliveEnabled;
   int _lingerTimeout = 0;
   boolean _freshnessSealed = true;
   static final boolean _headerPredictionEnabled = true;
   static final boolean _mtuDiscoveryEnabled = false;
   static final boolean _timeStampsEnabled = false;
   static final boolean _goBackToListenState = false;

   TcpOptionManager() {
      this.reset();
   }

   final void abort() {
   }

   final void reset() {
      if (!this._freshnessSealed) {
         this._sackEnabled = true;
         this._mssEnabled = true;
         this._delayedAckEnabled = true;
         this._keepAliveEnabled = false;
         this._scaleEnabled = false;
         this._lingerTimeout = 0;
      }

      this._freshnessSealed = false;
   }

   final byte[] getSynOptionsByteArray(TcpDatagramProperties props) {
      int sizeOfByteArray = 0;
      int arrayIndex = 0;
      int maxSegmentSize = props._maxSegmentSize;
      if (this._mssEnabled) {
         sizeOfByteArray += 4;
      }

      if (this._sackEnabled) {
         sizeOfByteArray += 2;
      }

      if (this._scaleEnabled) {
         sizeOfByteArray += 3;
      }

      if (sizeOfByteArray == 0) {
         return new byte[0];
      }

      int padding = sizeOfByteArray % 4 == 0 ? 0 : 4 - sizeOfByteArray % 4;
      sizeOfByteArray += padding;
      byte[] outByte = new byte[sizeOfByteArray];
      if (this._mssEnabled) {
         outByte[arrayIndex++] = 2;
         outByte[arrayIndex++] = 4;
         outByte[arrayIndex++] = (byte)(maxSegmentSize >> 8 & 0xFF);
         outByte[arrayIndex++] = (byte)(maxSegmentSize & 0xFF);
      }

      if (this._sackEnabled) {
         outByte[arrayIndex++] = 4;
         outByte[arrayIndex++] = 2;
      }

      if (this._scaleEnabled) {
         outByte[arrayIndex++] = 3;
         outByte[arrayIndex++] = 3;
         outByte[arrayIndex++] = 2;
      }

      for (int i = 0; i < padding; i++) {
         outByte[arrayIndex++] = 0;
      }

      return outByte;
   }

   final byte[] getOptionsByteArray(TcpDatagramProperties props) {
      TcpDataBlock[] sackBlocksToSend = null;
      int sizeOfByteArray = 0;
      int arrayIndex = 0;
      int numSackBlocksToSend = 0;
      if (this._sackEnabled) {
         sackBlocksToSend = props._sackBlocks;
         numSackBlocksToSend = sackBlocksToSend != null ? sackBlocksToSend.length : 0;
         int availableRoom = 40 - sizeOfByteArray;
         int availRoomCalc = (availableRoom - 2) / 4;
         numSackBlocksToSend = numSackBlocksToSend <= availRoomCalc ? numSackBlocksToSend : availRoomCalc;
         if (numSackBlocksToSend > 0) {
            sizeOfByteArray += numSackBlocksToSend * 8 + 2;
         }
      }

      int padding = sizeOfByteArray % 4 == 0 ? 0 : 4 - sizeOfByteArray % 4;
      sizeOfByteArray += padding;
      if (sizeOfByteArray == 0) {
         return new byte[0];
      }

      byte[] outByte = new byte[sizeOfByteArray];
      if (this._sackEnabled) {
         outByte[arrayIndex++] = 5;
         outByte[arrayIndex++] = (byte)(sizeOfByteArray - padding);

         for (int i = 0; i < numSackBlocksToSend; i++) {
            TcpDataBlock tempBlock = sackBlocksToSend[i];
            if (tempBlock == null) {
               throw new Object();
            }

            int leftEdge = tempBlock.getLeftEdge();
            int rightEdge = tempBlock.getRightEdge();
            outByte[arrayIndex++] = (byte)(leftEdge >> 24 & 0xFF);
            outByte[arrayIndex++] = (byte)(leftEdge >> 16 & 0xFF);
            outByte[arrayIndex++] = (byte)(leftEdge >> 8 & 0xFF);
            outByte[arrayIndex++] = (byte)(leftEdge & 0xFF);
            outByte[arrayIndex++] = (byte)(rightEdge >> 24 & 0xFF);
            outByte[arrayIndex++] = (byte)(rightEdge >> 16 & 0xFF);
            outByte[arrayIndex++] = (byte)(rightEdge >> 8 & 0xFF);
            outByte[arrayIndex++] = (byte)(rightEdge & 0xFF);
         }
      }

      for (int i = 0; i < padding; i++) {
         outByte[arrayIndex++] = 1;
      }

      return outByte;
   }
}
