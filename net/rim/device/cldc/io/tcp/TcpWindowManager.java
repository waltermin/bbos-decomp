package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpUtils;
import net.rim.device.internal.system.TCPPacketHeader;

final class TcpWindowManager implements TcpConstants {
   protected int _initialWindow;
   protected int _lossWindow;
   protected int _restartWindow;
   protected int _peerWindow;
   protected int _flightSize;
   protected int _slowStartThreshold;
   protected int _congestionWindow;
   protected int _duplicateAcks;
   protected TcpReassemblyDataList _tcpReassemblyQueue;
   protected int _lastAckNumberReceived;
   protected TcpQueueManager _tcpQueueManager;
   protected TcpOptionManager _tcpOptionManager;
   protected Protocol _connection;
   protected int _peerMaxSegmentSize;
   protected int _senderMaxSegmentSize;
   protected int _maxSegmentSize;
   protected int _maxMTU;
   protected int _userMaxSegmentSize;
   boolean _immediateAckRequired;
   private int _localWindow;
   private int _localScale;
   private int _peerScale;
   protected boolean _freshnessSealed = true;

   TcpWindowManager(int initMTUMax) {
      this();
      this._maxMTU = initMTUMax;
   }

   TcpWindowManager() {
      this.reset();
   }

   final void abort() {
   }

   final void reset() {
      if (!this._freshnessSealed) {
         this._flightSize = 0;
         this._slowStartThreshold = 0;
         this._lastAckNumberReceived = 0;
         this._peerMaxSegmentSize = 0;
         this._senderMaxSegmentSize = 0;
         this._maxSegmentSize = 0;
         this._maxMTU = 0;
         this._userMaxSegmentSize = 0;
         this._localWindow = 0;
         this._localScale = 0;
         this._immediateAckRequired = false;
      }

      this._freshnessSealed = false;
   }

   final void setAttributes(
      TcpReassemblyDataList reassemblyQueue, TcpOptionManager tcpOptionManager, TcpQueueManager tcpQueueManager, Protocol connection, int maxRadioSeg
   ) {
      this._tcpReassemblyQueue = reassemblyQueue;
      this._tcpQueueManager = tcpQueueManager;
      this._tcpOptionManager = tcpOptionManager;
      this._connection = connection;
      this._peerScale = 0;
      this._localScale = tcpOptionManager._scaleEnabled ? 2 : 0;
      this.setPeerMaxSegmentSizeAttribute(maxRadioSeg);
   }

   final void setPeerMaxSegmentSizeAttribute(int maxRadioSeg) {
      this._peerMaxSegmentSize = maxRadioSeg;
      this.setMaxRadioSegSize(maxRadioSeg);
   }

   private final void setMaxRadioSegSize(int maxRadioSeg) {
      this._senderMaxSegmentSize = 536 >= maxRadioSeg ? 536 : maxRadioSeg;
      this._maxSegmentSize = this._peerMaxSegmentSize <= this._senderMaxSegmentSize ? this._peerMaxSegmentSize : this._senderMaxSegmentSize;
      this._tcpQueueManager._sendHotlist.setMaxSegmentSize(this._maxSegmentSize);
   }

   final int getMaxSegmentSize() {
      return this._maxSegmentSize;
   }

   protected final void setUserMaxSegmentSize(int umss) {
      this._userMaxSegmentSize = umss;
   }

   final void setPeerWindowScale(int peerScale) {
      this._peerScale = peerScale;
   }

   final void setPeerMaxSegmentSize(int peerMaxSegmentSize) {
      this._peerMaxSegmentSize = peerMaxSegmentSize;
      this._maxSegmentSize = peerMaxSegmentSize <= this._senderMaxSegmentSize ? peerMaxSegmentSize : this._senderMaxSegmentSize;
      this._tcpQueueManager._sendHotlist.setMaxSegmentSize(this._maxSegmentSize);
   }

   final void setReceiveBuffer(TcpReassemblyDataList reassemblyQueue) {
      this._tcpReassemblyQueue = reassemblyQueue;
   }

   final void setTcpQueueManager(TcpQueueManager tcpQueueManager) {
      this._tcpQueueManager = tcpQueueManager;
   }

   final void setTcpOptionManager(TcpOptionManager tcpOptionManager) {
      this._tcpOptionManager = tcpOptionManager;
   }

   final TcpQueueManager getQueueManager() {
      return this._tcpQueueManager;
   }

   final void init(int apnId) {
      int tempSMss = 4 * this._senderMaxSegmentSize;
      this._initialWindow = tempSMss <= 4380 ? tempSMss : 4380;
      this._lossWindow = TCPPacketHeader.getMaxPacketSize(apnId);
      this._restartWindow = this._initialWindow;
      this._duplicateAcks = 0;
      this._congestionWindow = this._initialWindow;
      this.recalculateSlowStartThreshold();
      this._peerWindow = this._initialWindow;
      this._peerScale = 0;
   }

   final int getLocalScale() {
      return this._localScale;
   }

   final int getLocalWindow() {
      int availableSpace = this._tcpReassemblyQueue.getAvailableSpace();
      int maxData = this._tcpReassemblyQueue.getMaxDataLimit() >> 1;
      int diff = availableSpace - this._localWindow;
      if (diff < 1 || diff >= (maxData < this._maxSegmentSize ? maxData : this._maxSegmentSize)) {
         this._localWindow = availableSpace;
         if (this._localWindow > 65535) {
            this._localWindow = 65535;
         }
      }

      return this._localWindow;
   }

   final void retransmissionTimerExpiredWithSegmentLoss() {
      this.recalculateFlightSize();
      this.recalculateSlowStartThreshold();
      this._congestionWindow = this._lossWindow;
   }

   final void nonDuplicateAckReceived() {
      if (this._congestionWindow < this._slowStartThreshold) {
         this._congestionWindow = this._congestionWindow + this._senderMaxSegmentSize;
      } else {
         int tempWindow = this._senderMaxSegmentSize * this._senderMaxSegmentSize / this._congestionWindow;
         this._congestionWindow += tempWindow >= 1 ? tempWindow : 1;
      }
   }

   protected final void recalculateSlowStartThreshold() {
      int a = this._flightSize >> 1;
      int b = 2 * this._senderMaxSegmentSize;
      this._slowStartThreshold = a >= b ? a : b;
   }

   protected final void threeDuplicateAcksReceived() {
      this.recalculateSlowStartThreshold();
   }

   final void resentLostSegment() {
      this._congestionWindow = this._slowStartThreshold + 3 * this._senderMaxSegmentSize;
   }

   protected final void newDataAckedDuringFastRecovery() {
      this._congestionWindow = this._slowStartThreshold;
   }

   final void ackReceived(int newAckNumber, int newPeerWindow) {
      if (this.seqGEQ(newAckNumber, this._tcpQueueManager._sendHotlist._highestAckNumberReceived)) {
         this._peerWindow = newPeerWindow;
         if (this._peerWindow == 0) {
            this._connection.checkPersistTimer();
         }
      }

      if (newAckNumber != this._lastAckNumberReceived) {
         if (this._duplicateAcks >= 3 && TcpUtils.seqGT(newAckNumber, this._lastAckNumberReceived)) {
            this.newDataAckedDuringFastRecovery();
         }

         this._duplicateAcks = 0;
      } else if (newAckNumber == this._lastAckNumberReceived
         && this.seqLT(this._lastAckNumberReceived, this._tcpQueueManager._sendHotlist._highestSentSequenceNumber)) {
         this._duplicateAcks++;
         if (this._duplicateAcks == 3) {
            this.threeDuplicateAcksReceived();
            return;
         }

         if (this._duplicateAcks > 3) {
            this._congestionWindow = this._congestionWindow + this._senderMaxSegmentSize;
         }

         return;
      }

      this._lastAckNumberReceived = newAckNumber;
      this.nonDuplicateAckReceived();
      this.recalculateFlightSize();
   }

   protected final int getCurrentSendWindow() {
      int scaledPeerWindow = this._peerWindow << this._peerScale;
      return this._congestionWindow <= scaledPeerWindow ? this._congestionWindow : scaledPeerWindow;
   }

   final int getAvailableWindow() {
      return this.getCurrentSendWindow() - this._flightSize;
   }

   protected final void recalculateFlightSize() {
      this._flightSize = this._tcpQueueManager._sendHotlist.getSendNext() - this._lastAckNumberReceived;
   }

   final boolean shouldWeSendAPacket() {
      return this._immediateAckRequired;
   }

   final boolean isInCongestionControlMode() {
      return this._congestionWindow >= this._slowStartThreshold;
   }

   final boolean isInSlowStartMode() {
      return this._congestionWindow < this._slowStartThreshold;
   }

   final boolean isInPersistMode() {
      return this.getCurrentSendWindow() == 0;
   }

   private final boolean seqGEQ(int a, int b) {
      if (a == b) {
         return true;
      } else if (a > 0) {
         return b < a && b > a - 2147483648L;
      } else {
         return b < a ? true : b > a && b > a + 2147483648L;
      }
   }

   private final boolean seqLT(int a, int b) {
      return !this.seqGEQ(a, b);
   }
}
