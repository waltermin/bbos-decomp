package net.rim.device.internal.synchronization.ota.session;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Vector;
import net.rim.device.cldc.io.sync.SyncCommandsPool;
import net.rim.device.cldc.io.sync.SyncDatagram;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;

public class Session extends Thread {
   private int _datagramTransactionId;
   private Object _datagramStatusLock = new Object();
   protected int _type;
   protected int _state = 1;
   protected int _sessionId;
   protected int _changeListId;
   protected int _startOfSendingWindow;
   protected long _initialTimeout;
   protected long _timeout;
   protected boolean _operationSuspensionHappened;
   protected boolean _operationReTryRequested;
   protected ReusableObjectPool _syncDatagramsPool;
   protected SyncCommandsPool _syncCommandsPool;
   protected SessionManager _sessionManager;
   public static final int DEVICE_GET_CONFIGURATION_SESSION = 1;
   public static final int DEVICE_INITIALIZATION_SESSION = 2;
   public static final int DEVICE_CHANGES_SESSION = 3;
   public static final int DEVICE_RESUME_COMMANDS_SESSION = 4;
   public static final int DEVICE_SUSPEND_COMMANDS_SESSION = 5;
   public static final int DEVICE_CONFIGURATION_CHANGES_SESSION = 6;
   public static final int DEVICE_LOG_SESSION = 7;
   public static final int SERVER_CONFIGURATION_SESSION = 17;
   public static final int SERVER_INITIALIZATION_SESSION = 18;
   public static final int SERVER_CHANGES_SESSION = 19;
   public static final byte UNKNOWN = 1;
   public static final byte WAITING = 2;
   public static final byte RUNNING = 3;
   public static final byte PROCESSING = 4;
   public static final byte SUCCEEDED = 5;
   public static final byte TIMEDOUT = 6;
   public static final byte RESETSYNCSTATE = 7;
   public static final byte INVALIDSYNCSTATE = 8;
   public static final byte ABORTED = 9;

   public Session(SessionManager aSessionManager, int aSessionId, int aChangeListId) {
      this._sessionManager = aSessionManager;
      this._sessionId = aSessionId;
      this._changeListId = aChangeListId;
      this._syncDatagramsPool = ReusableObjectPool.getSingletonInstance(7926551755126522851L);
      this._syncCommandsPool = SyncCommandsPool.getSingletonInstance(aSessionManager.getSid());
   }

   public int getState() {
      return this._state;
   }

   public int getSessionId() {
      return this._sessionId;
   }

   public int getChangeListId() {
      return this._changeListId;
   }

   public synchronized void setState(int state) {
      this._state = state;
      synchronized (this._datagramStatusLock) {
         this._datagramStatusLock.notifyAll();
      }

      this.notifyAll();
   }

   public void setTimeout(long value) {
      this._timeout = value;
   }

   public long getTimeout() {
      return this._timeout;
   }

   public void setInitialTimeout(long value) {
      this._initialTimeout = value;
      this.goIntoRunningState();
   }

   public long getInitialTimeout() {
      return this._initialTimeout;
   }

   public boolean isServerSession() {
      return (this.getType() & 16) == 16;
   }

   public int getType() {
      return this._type;
   }

   public void setType(int aType) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean isOperationReTryRequested() {
      return this._operationReTryRequested;
   }

   public boolean isOperationSuspensionHappened() {
      return this._operationSuspensionHappened;
   }

   public synchronized void goIntoRunningState() {
      this.setTimeout(this._initialTimeout);
      this.setState(3);
   }

   protected synchronized void goIntoProcessingState() {
      this.setTimeout(0);
      this.setState(4);
   }

   protected void timeout() {
      this.setState(6);
   }

   protected void abort() {
      this.setState(9);
   }

   protected void succeed() {
      this.setState(5);
   }

   protected void resetSyncState() {
      this.setState(7);
   }

   protected void invalidSyncState() {
      this.setState(8);
   }

   protected SyncDatagram checkOutSyncDatagram() {
      SyncDatagram xSyncDatagram = (SyncDatagram)this._syncDatagramsPool.checkOut();
      if (xSyncDatagram == null) {
         xSyncDatagram = new SyncDatagram();
      }

      xSyncDatagram.setProtocolVersion(32);
      return xSyncDatagram;
   }

   protected void checkInSyncDatagram(SyncDatagram aSyncDatagram) {
      this._syncDatagramsPool.checkIn(aSyncDatagram);
   }

   protected void checkInSyncDatagrams(Vector SyncDatagrams) {
      for (int xIndex = SyncDatagrams.size() - 1; xIndex > -1; xIndex--) {
         this._syncDatagramsPool.checkIn((SyncDatagram)SyncDatagrams.elementAt(xIndex));
      }
   }

   @Override
   public synchronized void run() {
      while (this.getState() == 3 || this.getState() == 4) {
         try {
            this.setState(2);
            this.wait(this.getTimeout());
            if (this.getState() == 2) {
               this.timeout();
            }
         } finally {
            continue;
         }
      }
   }

   public synchronized void waitOnState(int aSessionState) {
      while (this.getState() != aSessionState) {
         try {
            this.wait();
         } finally {
            continue;
         }
      }
   }

   private boolean isSessionActive() {
      switch (this.getState()) {
         case 1:
            return false;
         case 2:
         case 3:
         case 4:
         default:
            return true;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void sendDatagram(SyncDatagram aSyncDatagram, boolean checkIn, boolean block) {
      synchronized (this._datagramStatusLock) {
         this._datagramTransactionId = 0;
         long sid = this._sessionManager.getSid();
         Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
         if (xConfiguration.extendedHeadersEnabled()) {
            long xCurrentSessionTimeout = this.getInitialTimeout();
            if (xCurrentSessionTimeout != xConfiguration.getSessionTimeout()) {
               aSyncDatagram.setSessionTimeout((int)(xCurrentSessionTimeout / 60000));
            }
         }

         int xDatagramTransactionId = this._sessionManager.send(aSyncDatagram, checkIn);
         this.setTimeout(this._sessionManager.getMaxSessionTimeoutFactor() * this._initialTimeout);
         if (block) {
            this._startOfSendingWindow++;

            while (xDatagramTransactionId != this._datagramTransactionId) {
               boolean var13 = false /* VF: Semaphore variable */;

               try {
                  var13 = true;
                  if (!this.isSessionActive()) {
                     var13 = false;
                     break;
                  }

                  this._datagramStatusLock.wait();
                  var13 = false;
               } finally {
                  if (var13) {
                     throw new InterruptedIOException();
                  }
               }
            }

            if (this.getState() == 9) {
               throw new IOException();
            }
         }

         this.setTimeout(this._initialTimeout);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void sendDatagrams(Vector listOfDatagramsToSend, boolean block) {
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         int xLastDatagramIndex = listOfDatagramsToSend.size();
         int xLastSeq = xLastDatagramIndex - 1;
         xLastSeq += this._startOfSendingWindow;
         int xDatagramIndex = 0;

         while (true) {
            if (xDatagramIndex < xLastDatagramIndex) {
               if (this.isSessionActive()) {
                  SyncDatagram xSyncDatagram = (SyncDatagram)listOfDatagramsToSend.elementAt(xDatagramIndex);
                  xSyncDatagram.setLastSequenceNumber(xLastSeq);
                  this.sendDatagram(xSyncDatagram, false, block);
                  xDatagramIndex++;
                  continue;
               }

               var9 = false;
               break;
            }

            var9 = false;
            break;
         }
      } finally {
         if (var9) {
            this.checkInSyncDatagrams(listOfDatagramsToSend);
            listOfDatagramsToSend.setSize(0);
         }
      }

      this.checkInSyncDatagrams(listOfDatagramsToSend);
      listOfDatagramsToSend.setSize(0);
   }

   public synchronized void onDatagramSent(int transactionId) {
      synchronized (this._datagramStatusLock) {
         this._datagramTransactionId = transactionId;
         this._datagramStatusLock.notifyAll();
      }
   }

   public synchronized void onDatagramDropped(int datagramTransactionId) {
      synchronized (this._datagramStatusLock) {
         if (this._datagramTransactionId == 0 || this._datagramTransactionId == datagramTransactionId) {
            this.abort();
         }
      }
   }

   public void onDatagramReceived(SyncDatagram _1) {
      throw null;
   }
}
