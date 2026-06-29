package com.fourthpass.wapstack.wtp;

import com.fourthpass.wapstack.WAPManagementEntity;
import com.fourthpass.wapstack.util.ITimerScheduler;
import com.fourthpass.wapstack.wtp.pdu.WTP_PDU;

public class WTP_Transaction implements ITimerScheduler {
   protected byte _trState;
   protected byte _trClassId;
   protected byte _abortType;
   protected byte _abortReason;
   protected short _retransmissionCounter;
   protected int _TID;
   protected boolean _TIDnew;
   protected int _transactionTimeOut;
   protected boolean _userAckEnabled;
   protected boolean _holdOnTransmission;
   protected boolean _ackPDUSent;
   protected WTP_PDU _lastPDUTransmitted;
   protected WTPLayer _wtpLayer;
   public Object _transactionStateSync = new Object();

   @Override
   public void timerExpired(byte _1) {
      throw null;
   }

   public boolean getTIDnew() {
      return this._TIDnew;
   }

   public byte getClassType() {
      return this._trClassId;
   }

   public void setLastTransmitted(WTP_PDU wtpPDU) {
      this._lastPDUTransmitted = wtpPDU;
   }

   public WTP_PDU getLastTransmitted() {
      return this._lastPDUTransmitted;
   }

   public WTPLayer getWTPLayerInstance() {
      return this._wtpLayer;
   }

   public boolean isActive() {
      return this._trState != 21;
   }

   public boolean isAborted() {
      return this._trState == 21;
   }

   public boolean isComplete() {
      return this._trState == 22;
   }

   public void setState(byte state) {
      synchronized (this._transactionStateSync) {
         this._trState = state;
         this._transactionStateSync.notifyAll();
      }

      if (this._trState >= 20) {
         this._holdOnTransmission = false;
         this._ackPDUSent = false;
      }

      this._wtpLayer.doTransactionNotification(this);
   }

   public byte getState() {
      return this._trState;
   }

   public byte getAbortReason() {
      return this._abortReason;
   }

   public void requestAbort(byte abortType, byte abortReason) {
      if (this.isActive()) {
         this._wtpLayer.transmitAbortPDU(this, abortType, abortReason);
      }

      this.removeTimerTasks();
      this.setState((byte)21);
      this._wtpLayer.unregisterTransaction(this);
   }

   public void abortTransaction() {
      if (this.isActive()) {
         this.removeTimerTasks();
         this.setState((byte)21);
         this._wtpLayer.unregisterTransaction(this);
      }
   }

   public void reTransmitPDU(byte taskId) {
      if (this._retransmissionCounter <= 0) {
         this.requestAbort((byte)1, (byte)8);
      } else {
         this._wtpLayer.reTransmitPDU(this);
         this._wtpLayer.removeTimerTask(this, taskId);
         this.addTimerTask((byte)1, (byte)1, false);
      }
   }

   public boolean receivedPDU(WTP_PDU _1) {
      throw null;
   }

   public int getTID() {
      return this._TID;
   }

   protected void addTimerTask(byte msgType, byte taskId, boolean reset) {
      byte msgTypeId = 7;
      byte counterId = -1;
      switch (msgType) {
         case 0:
            break;
         case 1:
         default:
            counterId = 0;
            if (this._trClassId == 2) {
               msgTypeId = 3;
            } else {
               msgTypeId = 4;
            }
            break;
         case 2:
            msgTypeId = 8;
            break;
         case 3:
            counterId = 0;
            msgTypeId = 5;
            break;
         case 4:
            counterId = 1;
            if (this._trClassId == 2) {
               msgTypeId = 3;
            } else {
               msgTypeId = 1;
            }
            break;
         case 5:
            counterId = 0;
            msgTypeId = 2;
      }

      if (counterId != -1) {
         if (reset) {
            this._retransmissionCounter = WAPManagementEntity.getCounterSetting(counterId, this._userAckEnabled);
         } else {
            this._retransmissionCounter--;
         }
      }

      this._wtpLayer.addTimerTask(this, WAPManagementEntity.getTimerSetting(msgTypeId, this._userAckEnabled), taskId);
   }

   protected void removeTimerTasks() {
      this._wtpLayer.removeTimerTask(this);
   }

   protected void removeTimerTask(byte taskId) {
      this._wtpLayer.removeTimerTask(this, taskId);
   }

   public WTP_Transaction(WTPLayer wtpLayer, int TID, boolean TIDnew, byte cID) {
      this(wtpLayer, TID, TIDnew, cID, false);
   }

   public WTP_Transaction(WTPLayer wtpLayer, int TID, boolean TIDnew, byte cID, boolean userAckEnabled) {
      this._wtpLayer = wtpLayer;
      this._TID = TID;
      this._TIDnew = TIDnew;
      this._trClassId = cID;
      this._userAckEnabled = userAckEnabled;
      this._transactionTimeOut = 1;
      wtpLayer.registerTransaction(this);
      wtpLayer.addTimerTask(this, WAPManagementEntity.getTimerSetting((byte)7, true), (byte)20);
   }
}
