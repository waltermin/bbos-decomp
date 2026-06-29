package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.SimpleTcpDataBlock;
import net.rim.device.internal.io.tcp.TcpDataBlock;
import net.rim.device.internal.io.tcp.TcpUtils;

final class SendHotlist {
   protected int _numberOfTimesAckTransmitted;
   protected int _ackNumberToSend;
   protected int _initialSendSequenceNumber;
   protected int _initialReceiveSequenceNumber;
   protected int _highestSentSequenceNumber;
   protected int _highestAckNumberReceived;
   protected int _lastSentDataByteSequenceNumber;
   protected boolean _receiverFastRetransmitOn;
   protected boolean _localFinSequenceNumberValid;
   boolean _peerFinWasAcked;
   protected int _senderRetransmitCounter;
   protected int _maxSegmentSize;
   private int _localFinSequenceNumber;
   private int _peerFinSequenceNumber;
   boolean _mustSendSyn;
   boolean _ourSynWasAcked;
   boolean _mustSendFinRightAway;
   boolean _haveSentFin;
   boolean _haveEverSentFin;
   boolean _haveReceivedFin;
   boolean _ourFinWasAcked;
   boolean _immediateAckRequired;
   boolean _delayedAckRequired;
   boolean _keepAlivePacketRequired;
   protected boolean _freshnessSealed = true;
   TcpDataList _sendRequestsList;
   Protocol _tcpConnection;
   boolean _mustFlushData;
   int _flushSequenceNumber;

   SendHotlist() {
      this.init();
   }

   final void init() {
      if (!this._freshnessSealed) {
         this._numberOfTimesAckTransmitted = 0;
         this._receiverFastRetransmitOn = false;
         this._ourSynWasAcked = false;
         this._ourFinWasAcked = false;
         this._peerFinWasAcked = false;
         this._haveReceivedFin = false;
         this._haveSentFin = false;
         this._haveEverSentFin = false;
         this._mustSendFinRightAway = false;
         this._mustSendSyn = false;
         this._immediateAckRequired = false;
         this._localFinSequenceNumberValid = false;
         this._ackNumberToSend = 0;
         this._localFinSequenceNumber = 0;
         this._peerFinSequenceNumber = 0;
         if (this._sendRequestsList == null) {
            this._sendRequestsList = new TcpDataList();
         } else {
            this._sendRequestsList.purge();
         }
      }

      this._freshnessSealed = false;
   }

   final void setAttributes(Protocol tcpConnection) {
      this._tcpConnection = tcpConnection;
   }

   final void abort() {
      this.init();
   }

   final void setMaxSegmentSize(int maxSegmentSize) {
      this._maxSegmentSize = maxSegmentSize;
   }

   final int getSendBufferOffset() {
      return this._sendRequestsList.getLeftEdge() - this._highestAckNumberReceived;
   }

   final void setIss(int iss) {
      this._initialSendSequenceNumber = iss;
      this._highestSentSequenceNumber = iss;
   }

   final int getIss() {
      return this._initialSendSequenceNumber;
   }

   final void setIrs(int irs) {
      this._initialReceiveSequenceNumber = irs;
      this._ackNumberToSend = irs + 1;
   }

   final int getIrs() {
      return this._initialReceiveSequenceNumber;
   }

   final int getLocalFinSequenceNumber() {
      return this._localFinSequenceNumber;
   }

   final int getSendNext() {
      if (this._mustSendSyn) {
         return this._initialSendSequenceNumber;
      }

      synchronized (this._sendRequestsList) {
         return this._sendRequestsList.isEmpty() ? this._highestSentSequenceNumber : this._sendRequestsList.getLeftEdge();
      }
   }

   final void peerFinReceived(int peerFinSeqNum) {
      this._haveReceivedFin = true;
      this._peerFinSequenceNumber = peerFinSeqNum;
      this._immediateAckRequired = true;
   }

   final int getPeerFinSequenceNumber() {
      return this._peerFinSequenceNumber;
   }

   final void localFinWasSent() {
      this._haveSentFin = true;
      this._haveEverSentFin = true;
      this._mustSendFinRightAway = false;
   }

   final void setLocalFinSequenceNumber(int localFinSeqNum) {
      if (!this._localFinSequenceNumberValid) {
         this._localFinSequenceNumberValid = true;
         this._localFinSequenceNumber = localFinSeqNum;
      }
   }

   final void localFinWasAcked() {
      if (!this._ourFinWasAcked) {
         this._ourFinWasAcked = true;
         this._mustSendFinRightAway = false;
         this._sendRequestsList.purge();
      }
   }

   final void ackReceivedInSynSent(int segAck) {
      this._highestAckNumberReceived = segAck;
      this._sendRequestsList.removeUpTo(this._highestAckNumberReceived);
      this._highestSentSequenceNumber = this._initialSendSequenceNumber + 1;
      if (this._highestAckNumberReceived == this._initialSendSequenceNumber + 1) {
         this._ourSynWasAcked = true;
      }
   }

   final void endSendTransmission() {
      this._ourFinWasAcked = false;
   }

   final void prependSendRequest(TcpDataBlock sendRequest) {
      this._sendRequestsList.addData(sendRequest);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final TcpDataBlock getNextSendBlock(int maxBlockSize) {
      int blockToSendRightEdge;
      SimpleTcpDataBlock blockToSend;
      synchronized (this._sendRequestsList) {
         if (this._sendRequestsList.isEmpty() || maxBlockSize <= 0) {
            return null;
         }

         boolean var11 = false /* VF: Semaphore variable */;

         TcpDataBlock nextSendBlock;
         try {
            var11 = true;
            nextSendBlock = this._sendRequestsList.dequeueHead();
            var11 = false;
         } finally {
            if (var11) {
               throw new RuntimeException();
            }
         }

         int rightEdge = nextSendBlock.getRightEdge();
         if (rightEdge - nextSendBlock.getLeftEdge() <= maxBlockSize) {
            if (TcpUtils.seqGT(rightEdge, this._highestSentSequenceNumber)) {
               this._highestSentSequenceNumber = rightEdge;
               if (this._mustFlushData && TcpUtils.seqGEQ(this._highestSentSequenceNumber, this._flushSequenceNumber)) {
                  this._mustFlushData = false;
               }
            }

            return nextSendBlock;
         }

         int blockToSendLeftEdge = nextSendBlock.getLeftEdge();
         blockToSendRightEdge = blockToSendLeftEdge + maxBlockSize;
         blockToSend = new SimpleTcpDataBlock(blockToSendLeftEdge, blockToSendRightEdge);
         nextSendBlock.removeUpTo(blockToSendRightEdge);
         this._sendRequestsList.addData(nextSendBlock);
      }

      if (TcpUtils.seqGT(blockToSendRightEdge, this._highestSentSequenceNumber)) {
         this._highestSentSequenceNumber = blockToSendRightEdge;
         if (this._mustFlushData && TcpUtils.seqGEQ(this._highestSentSequenceNumber, this._flushSequenceNumber)) {
            this._mustFlushData = false;
         }
      }

      return blockToSend;
   }

   final void setNextAckNumberToSend(int nextAck, boolean holeDetected) {
      if (this._haveReceivedFin && this._peerFinSequenceNumber == nextAck + 1) {
         this._ackNumberToSend = nextAck + 1;
         this._immediateAckRequired = true;
      } else {
         if (TcpUtils.seqGEQ(nextAck, this._ackNumberToSend)) {
            this._ackNumberToSend = nextAck;
            if (this._delayedAckRequired || !this._tcpConnection._tcpOptionManager._delayedAckEnabled || holeDetected) {
               this._immediateAckRequired = true;
               this._delayedAckRequired = false;
               return;
            }

            this._delayedAckRequired = true;
            this._tcpConnection.startDelayedAckTimer();
         }
      }
   }

   final void enqueueFastRetransmitBlock(int sendBufferSize) {
      int packetSize = sendBufferSize <= this._maxSegmentSize ? sendBufferSize : this._maxSegmentSize;
      SimpleTcpDataBlock sendRequest = new SimpleTcpDataBlock(this._highestAckNumberReceived, this._highestAckNumberReceived + packetSize);
      this._sendRequestsList.addData(sendRequest);
   }

   final void ackReceived(int segAck) {
      this._highestAckNumberReceived = segAck;
      this._sendRequestsList.removeUpTo(segAck);
   }

   final void turnOnReceiverFastRetransmit() {
      this._receiverFastRetransmitOn = true;
   }

   final void retransmissionTimeoutOccurred() {
      if (this._haveSentFin) {
         this._haveSentFin = false;
         this._mustSendFinRightAway = true;
      }

      synchronized (this._sendRequestsList) {
         TcpDataBlock block = null;
         int newRightEdge;
         if (!this._sendRequestsList.isEmpty()) {
            newRightEdge = this._sendRequestsList.getRightEdge();
         } else if (this._haveEverSentFin) {
            newRightEdge = this._localFinSequenceNumber;
         } else {
            newRightEdge = this._highestSentSequenceNumber;
         }

         this._sendRequestsList.purge();
         if (this._ourSynWasAcked
            && (
               !this._localFinSequenceNumberValid
                  || newRightEdge != this._localFinSequenceNumber
                  || this._highestAckNumberReceived != this._localFinSequenceNumber
            )) {
            block = new SimpleTcpDataBlock(this._highestAckNumberReceived, newRightEdge);
            if (this._highestAckNumberReceived != newRightEdge) {
               this._sendRequestsList.addData(block);
            }
         }
      }
   }

   final int getAckNumberToSend() {
      return this._ackNumberToSend;
   }

   final void sendAckForPeerFin() {
      if (!this._peerFinWasAcked) {
         this._peerFinWasAcked = true;
         this._ackNumberToSend++;
      }
   }

   final boolean shouldWeSendAPacket(int currentMaxMTU) {
      if (this._immediateAckRequired) {
         return true;
      }

      if (currentMaxMTU > 0) {
         synchronized (this._sendRequestsList) {
            if (!this._sendRequestsList.isContiguous()) {
               return true;
            }

            int sendSize = this._sendRequestsList.getRightEdge() - this._sendRequestsList.getLeftEdge();
            if (sendSize > currentMaxMTU) {
               sendSize = currentMaxMTU;
            }

            if (sendSize >= this._maxSegmentSize) {
               return true;
            }

            if (sendSize > 0 && (this._mustFlushData || this._highestAckNumberReceived == this._highestSentSequenceNumber)) {
               return true;
            }
         }
      }

      return this._mustSendFinRightAway && !this._haveSentFin && this._tcpConnection._noMoreData ? true : this._mustSendSyn;
   }

   final int getHighestAckNumberReceived() {
      return this._highestAckNumberReceived;
   }

   final void newDataToSend(int offset, int size) {
      int leftEdge = offset + this._initialSendSequenceNumber + 1;
      int rightEdge = leftEdge + size;
      SimpleTcpDataBlock newBlock = new SimpleTcpDataBlock(leftEdge, rightEdge);
      this._sendRequestsList.addData(newBlock);
   }

   final void flushAllData() {
      synchronized (this._sendRequestsList) {
         this._flushSequenceNumber = this._sendRequestsList.getRightEdge();
         this._mustFlushData = true;
      }
   }
}
