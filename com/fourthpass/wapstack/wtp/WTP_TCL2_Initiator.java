package com.fourthpass.wapstack.wtp;

import com.fourthpass.wapstack.wtp.pdu.WTP_AbortPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_AckPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_NegAckPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_ResultPDU;
import com.fourthpass.wapstack.wtp.pdu.WTP_SegResultPDU;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import net.rim.device.api.util.BitSet;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipeOutputStream;
import net.rim.device.internal.browser.util.TimeLogger;

final class WTP_TCL2_Initiator extends WTP_Transaction_Initiator {
   private Pipe _pipe = (Pipe)(new Object());
   private PipeOutputStream _pipeOutputResult;
   private int _maxSeqNumber = -1;
   private int _startGroupSeqNum;
   private int _numOfMissingPackets = -1;
   private int _lastMissingPacketSeqNum = -1;
   private Pipe _sendPipe;
   private BitSet _psnSentAcks = (BitSet)(new Object(256));
   private int _numSendPackets;
   private int _lastSendPacketNumber;
   private static final int SEND_PACKET_GROUP_SIZE;
   private static final int MAX_PACKET_SIZE;

   public WTP_TCL2_Initiator(WTPLayer wtpLayer, int TID, boolean TIDnew) {
      super(wtpLayer, TID, TIDnew, (byte)2);
      this.setState((byte)20);
   }

   @Override
   public final boolean sendInvokeRequest(byte[] userData) {
      if (TimeLogger._loggingEnabled) {
         TimeLogger.getInstance().startTimer(0, this.hashCode());
      }

      this._numSendPackets = 1;
      if (userData != null) {
         int length = userData.length;
         if (length > 1400) {
            this._numSendPackets = 0;
            this._sendPipe = (Pipe)(new Object());

            for (int offset = 0; offset < length; offset += 1400) {
               this._sendPipe.write(userData, offset, Math.min(1400, length - offset), this._numSendPackets++);
            }

            this._sendPipe.closeWrite();
            userData = this._sendPipe.readPacket(0);
         }
      }

      super._wtpLayer.transmitInvokePDU(this, userData, this._numSendPackets > 1, false);
      this.transmitNextGroup(true);
      this.addTimerTask((byte)1, (byte)1, true);
      this.setState((byte)1);
      return this.isTRInvokeCnfGenerated();
   }

   public final boolean isTRInvokeCnfGenerated() {
      long timeoutPoint = (long)super._transactionTimeOut * 1000 + System.currentTimeMillis();

      while (super._trState != 22) {
         long systime = System.currentTimeMillis();
         if (systime >= timeoutPoint) {
            return false;
         }

         synchronized (super._transactionStateSync) {
            try {
               super._transactionStateSync.wait(timeoutPoint - systime);
            } finally {
               continue;
            }
         }
      }

      return true;
   }

   @Override
   public final InputStream getResultIndication(int timeout) {
      this._pipe.setTimeout(timeout);
      return this._pipe.getInputStream();
   }

   @Override
   public final boolean receivedPDU(WTP_PDU wtpPDU) {
      super._ackPDUSent = false;
      switch (wtpPDU.getPDUType()) {
         case 1:
         case 5:
            this.removeTimerTasks();
            super._wtpLayer.unregisterTransaction(this);
            super._wtpLayer.transmitAbortPDU(this, (byte)0, (byte)1);
            this.setState((byte)21);
            return true;
         case 2:
            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().stopTimer(0, this.hashCode());
            }

            WTP_ResultPDU resPdu = (WTP_ResultPDU)wtpPDU;
            if (!resPdu.isSegmentationPacket()) {
               byte[] data = wtpPDU.getUserData();
               this._pipe.write(data, 0, data.length, 0);
               this._pipe.closeWrite();
               super._wtpLayer.transmitAckPDU(this, false);
               super._ackPDUSent = true;
               this.removeTimerTasks();
               this.setState((byte)22);
               super._wtpLayer.unregisterTransaction(this);
               return true;
            } else {
               this.removeTimerTasks();
               if (TimeLogger._loggingEnabled) {
                  TimeLogger.getInstance().startTimer(1, this.hashCode());
               }

               if (this._pipeOutputResult == null) {
                  this._pipeOutputResult = this._pipe.getOutputStream();
                  this._startGroupSeqNum = 0;
                  this._maxSeqNumber = 0;
                  this._pipeOutputResult.setPacketNo(0);

                  label448:
                  try {
                     this._pipeOutputResult.write(resPdu.getUserData());
                  } finally {
                     break label448;
                  }

                  if (resPdu.isEndOfGroup()) {
                     super._wtpLayer.transmitSARAckPDU(this, this._maxSeqNumber, this._psnSentAcks.isSet(this._maxSeqNumber));
                     this._psnSentAcks.set(this._maxSeqNumber);
                  }

                  this.setState((byte)2);
                  return true;
               } else if (resPdu.isRetransmittedPacket()) {
                  this.handleRetransmittedPacket(resPdu, 0);
                  return true;
               } else {
                  if (!this._pipeOutputResult.isPacketIncluded(0)) {
                     this._maxSeqNumber = Math.max(this._maxSeqNumber, 0);
                     this._pipeOutputResult.setPacketNo(0);

                     try {
                        this._pipeOutputResult.write(resPdu.getUserData());
                        return true;
                     } finally {
                        return true;
                     }
                  }

                  return true;
               }
            }
         case 3:
         default:
            if (super._trState == 1) {
               WTP_AckPDU ackpdu = (WTP_AckPDU)wtpPDU;
               byte[] tpi = ackpdu.getTPI(3);
               if (ackpdu.isTveTokSet()) {
                  this.removeTimerTask((byte)1);
                  super._wtpLayer.transmitAckPDU(this, true);
                  super._ackPDUSent = true;
                  this.addTimerTask((byte)1, (byte)1, false);
                  if (tpi == null) {
                     return true;
                  }
               }

               if (tpi != null) {
                  if (tpi.length == 1 && (tpi[0] & 255) == this._lastSendPacketNumber) {
                     if (this._lastSendPacketNumber == this._numSendPackets - 1) {
                        this.removeTimerTasks();
                        super._holdOnTransmission = true;
                        this.addTimerTask((byte)2, (byte)1, true);
                        return true;
                     }

                     this.removeTimerTask((byte)1);
                     this._pipe.kickReadTimer();
                     this.transmitNextGroup(false);
                     this.addTimerTask((byte)1, (byte)1, true);
                  }

                  return true;
               }

               this.removeTimerTasks();
               super._holdOnTransmission = true;
               this.addTimerTask((byte)2, (byte)1, true);
               return true;
            } else {
               if (super._trState != 22) {
                  return false;
               }

               return true;
            }
         case 4:
            WTP_AbortPDU abortp = (WTP_AbortPDU)wtpPDU;
            super._abortReason = abortp.getAbortReason();
            super._abortType = abortp.getAbortType();
            this.removeTimerTasks();
            this._pipe.closeWrite();
            this.setState((byte)21);
            super._wtpLayer.unregisterTransaction(this);
            return true;
         case 6:
            WTP_SegResultPDU segPdu = (WTP_SegResultPDU)wtpPDU;
            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().stopTimer(1, this.hashCode());
            }

            if (this._pipeOutputResult == null) {
               this._pipeOutputResult = this._pipe.getOutputStream();
            }

            if (segPdu.isEndOfMessage()) {
               if (this._maxSeqNumber <= segPdu.getPacketSequenceNumber()) {
                  if (this.handleEndGroup(segPdu)) {
                     this._pipeOutputResult.close();
                     this.setState((byte)22);
                     super._wtpLayer.unregisterTransaction(this);
                     return true;
                  }

                  if (TimeLogger._loggingEnabled) {
                     TimeLogger.getInstance().startTimer(1, this.hashCode());
                  }

                  this.setState((byte)8);
               }

               return true;
            } else {
               if (TimeLogger._loggingEnabled) {
                  TimeLogger.getInstance().startTimer(1, this.hashCode());
               }

               if (segPdu.isEndOfGroup()) {
                  if (this._maxSeqNumber <= segPdu.getPacketSequenceNumber() && this.handleEndGroup(segPdu)) {
                     this._startGroupSeqNum = this._maxSeqNumber + 1;
                  }

                  return true;
               } else if (segPdu.isRetransmittedPacket()) {
                  this.handleRetransmittedPacket(segPdu, segPdu.getPacketSequenceNumber());
                  return true;
               } else {
                  if (!this._pipeOutputResult.isPacketIncluded(segPdu.getPacketSequenceNumber())) {
                     this._maxSeqNumber = Math.max(this._maxSeqNumber, segPdu.getPacketSequenceNumber());
                     this._pipeOutputResult.setPacketNo(segPdu.getPacketSequenceNumber());
                     this._pipe.setEstimatedSize(this._pipe.getLength() + 1500);

                     try {
                        this._pipeOutputResult.write(segPdu.getUserData());
                        return true;
                     } finally {
                        return true;
                     }
                  }

                  return true;
               }
            }
         case 7:
            WTP_NegAckPDU nackp = (WTP_NegAckPDU)wtpPDU;
            this.removeTimerTask((byte)1);
            int numMissed = nackp.getMissingPktCount();
            if (numMissed == 0) {
               super._wtpLayer
                  .transmitSegmentedInvokePDU(
                     this,
                     this._sendPipe.readPacket(this._lastSendPacketNumber),
                     this._lastSendPacketNumber,
                     true,
                     this._lastSendPacketNumber == this._numSendPackets - 1,
                     true
                  );
            } else {
               this._pipe.kickReadTimer();

               for (int i = 0; i < numMissed; i++) {
                  int packetId = nackp.getMissingPktSequenceAt(i);
                  if (packetId > this._lastSendPacketNumber - 5 && packetId <= this._lastSendPacketNumber) {
                     if (packetId == 0) {
                        super._wtpLayer.transmitInvokePDU(this, this._sendPipe.readPacket(packetId), this._numSendPackets > 1, true);
                     } else {
                        super._wtpLayer
                           .transmitSegmentedInvokePDU(
                              this,
                              this._sendPipe.readPacket(packetId),
                              packetId,
                              packetId == this._lastSendPacketNumber,
                              packetId == this._numSendPackets - 1,
                              true
                           );
                     }
                  }
               }
            }

            this.addTimerTask((byte)1, (byte)1, true);
            return false;
      }
   }

   @Override
   public final void setState(byte state) {
      super.setState(state);
      if (state == 21) {
         this._pipe.closeWrite();
      }
   }

   @Override
   public final synchronized void timerExpired(byte eventId) {
      if (this.isActive()) {
         switch (eventId) {
            case 1:
               if (super._trState == 1) {
                  if (!super._holdOnTransmission) {
                     this.reTransmitPDU(eventId);
                     return;
                  }

                  super._abortType = 1;
                  super._abortReason = 8;
                  this.requestAbort((byte)1, (byte)8);
                  return;
               }
               break;
            case 22:
               this.removeTimerTasks();
               super._wtpLayer.unregisterTransaction(this);
               if (super._trState != eventId) {
                  this.setState((byte)21);
               }
         }
      }
   }

   private final byte[] getMissingPackets(int start, int end) {
      boolean missing = false;
      if (start >= end) {
         return null;
      }

      byte[] result = null;
      ByteArrayOutputStream bos = (ByteArrayOutputStream)(new Object(end - start));
      DataOutputStream dos = (DataOutputStream)(new Object(bos));

      try {
         for (int i = start; i <= end; i++) {
            if (!this._pipeOutputResult.isPacketIncluded(i)) {
               dos.writeByte(i);
               missing = true;
            }
         }
      } finally {
         ;
      }

      if (missing) {
         result = bos.toByteArray();
      }

      return result;
   }

   private final boolean handleEndGroup(WTP_SegResultPDU segPdu) {
      int packetSeqNumber = segPdu.getPacketSequenceNumber();
      this._maxSeqNumber = Math.max(this._maxSeqNumber, packetSeqNumber);
      if (!this._pipeOutputResult.isPacketIncluded(packetSeqNumber)) {
         this._pipeOutputResult.setPacketNo(packetSeqNumber);

         try {
            this._pipeOutputResult.write(segPdu.getUserData());
         } finally {
            return this.checkMissingSegments();
         }
      }

      return this.checkMissingSegments();
   }

   final boolean checkMissingSegments() {
      byte[] missSeg = this.getMissingPackets(this._startGroupSeqNum, this._maxSeqNumber);
      if (missSeg != null) {
         this._numOfMissingPackets = missSeg.length;
         this._lastMissingPacketSeqNum = missSeg[missSeg.length - 1] & 255;
         super._wtpLayer.transmitNackPDU(this, missSeg);
         return false;
      }

      super._wtpLayer.transmitSARAckPDU(this, this._maxSeqNumber, this._psnSentAcks.isSet(this._maxSeqNumber));
      this._psnSentAcks.set(this._maxSeqNumber);
      if (super._trState == 8) {
         this._pipeOutputResult.close();
         this.setState((byte)22);
      }

      return true;
   }

   private final void handleRetransmittedPacket(WTP_PDU segPdu, int seq) {
      if (!this._pipeOutputResult.isPacketIncluded(seq)) {
         this._pipeOutputResult.setPacketNo(seq);

         label38:
         try {
            this._pipeOutputResult.write(segPdu.getUserData());
         } finally {
            break label38;
         }

         this._numOfMissingPackets--;
      }

      if (this._numOfMissingPackets == 0) {
         super._wtpLayer.transmitSARAckPDU(this, this._maxSeqNumber, this._psnSentAcks.isSet(this._maxSeqNumber));
         this._psnSentAcks.set(this._maxSeqNumber);
         this._numOfMissingPackets = -1;
         this._startGroupSeqNum = this._maxSeqNumber + 1;
         if (super._trState == 8) {
            this._pipeOutputResult.close();
            this.setState((byte)22);
            return;
         }
      } else if (seq == this._lastMissingPacketSeqNum) {
         this.checkMissingSegments();
      }
   }

   @Override
   public final void abortTransaction() {
      this._pipe.closeWrite();
      super.abortTransaction();
   }

   private final void transmitNextGroup(boolean firstGroup) {
      int packetNo = this._lastSendPacketNumber + 1;

      for (int i = firstGroup ? 1 : 0; packetNo < this._numSendPackets && i < 5; i++) {
         super._wtpLayer.transmitSegmentedInvokePDU(this, this._sendPipe.readPacket(packetNo), packetNo, i == 4, packetNo == this._numSendPackets - 1, false);
         this._lastSendPacketNumber = packetNo++;
      }
   }
}
