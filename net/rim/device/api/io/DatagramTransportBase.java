package net.rim.device.api.io;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class DatagramTransportBase extends TransportBase implements GlobalEventListener {
   public long GUID;
   protected String STR;
   protected DatagramConnectionBase _subConnection;
   protected SendPacketThread _sendThread;
   protected WeakReference[] _superConnections = new WeakReference[0];
   private int _superConnectionsPurge;
   private WeakReference[] _dgsls = new WeakReference[16];
   private int[] _datagramIds = new int[16];
   private int[] _subIds = new int[16];
   private int _nextIdIndex;
   private Datagram _sendCurrentDatagram;
   private Thread _sendCurrentThread;
   private Datagram[] _sendDatagramList = new Datagram[4];
   private Thread[] _sendThreadList = new Thread[4];
   private int _sendListCount;
   private static final int MAX_DATAGRAM_IDS;
   private static final int INITIAL_SEND_CAPACITY;

   @Override
   public void init() {
      throw null;
   }

   public void init(DatagramConnection subConnection) {
      if (subConnection != null) {
         if (!(subConnection instanceof DatagramConnectionBase)) {
            throw new RuntimeException();
         }

         DatagramConnectionBase subConnectionBase = (DatagramConnectionBase)subConnection;
         this._subConnection = subConnectionBase;
         this.addSubConnection(subConnectionBase);
      }

      Application.getApplication().addGlobalEventListener(this);
   }

   public void addConnection(DatagramConnection connection) {
      synchronized (this._superConnections) {
         this._superConnectionsPurge = WeakReferenceUtilities.incrementalWRArrayPurge(this._superConnectionsPurge, this._superConnections);
         ((DatagramConnectionBase)connection).setTrafficLogger(super._tLogger);
         Arrays.add(this._superConnections, new WeakReference(connection));
      }
   }

   public void addSubConnection(DatagramConnectionBase connection) {
      DatagramReceiveThread.getInstance().addConnection(connection, this);
   }

   public void close(DatagramConnection connection) {
      synchronized (this._superConnections) {
         for (int i = this._superConnections.length - 1; i >= 0; i--) {
            WeakReference w = this._superConnections[i];
            DatagramConnection c;
            if (w == null || (c = (DatagramConnection)w.get()) == null || c == connection) {
               Arrays.removeAt(this._superConnections, i);
            }
         }
      }
   }

   public int getMaximumLength() {
      throw null;
   }

   public int getNominalLength() {
      return this.getMaximumLength();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void superSend(Datagram datagram) {
      DatagramBase dgram = null;
      if (datagram instanceof DatagramBase) {
         dgram = (DatagramBase)datagram;
      }

      DatagramStatusListener listener = null;
      int dgramId = 0;
      if (dgram != null) {
         listener = dgram.getDatagramStatusListener();
         dgramId = dgram.getDatagramId();
      }

      this.xmitDgslEvent(listener, dgramId, 1, null);
      boolean cancelled = false;
      synchronized (this._sendDatagramList) {
         Thread thread = Thread.currentThread();
         if (this._sendCurrentDatagram != null || this._sendListCount > 0) {
            this.addDatagramForSend(datagram, thread);

            try {
               this._sendDatagramList.wait();
            } catch (InterruptedException var22) {
            }

            int index = this.findDatagramForSend(datagram, thread);
            if (index >= 0) {
               this.removeDatagramForSend(index);
            } else {
               cancelled = true;
            }
         }

         this._sendCurrentDatagram = datagram;
         this._sendCurrentThread = thread;
      }

      boolean var19 = false /* VF: Semaphore variable */;

      try {
         var19 = true;
         if (cancelled) {
            this.xmitDgslEvent(listener, dgramId, 129, null);
            throw new IOCancelledException();
         }

         if (dgram != null) {
            this.send(datagram, dgram.getAddressBase(), dgram, listener, dgramId);
            var19 = false;
         } else {
            this.send(datagram);
            var19 = false;
         }
      } finally {
         if (var19) {
            synchronized (this._sendDatagramList) {
               this._sendCurrentDatagram = null;
               this._sendCurrentThread = null;
               this._sendDatagramList.notify();
            }
         }
      }

      synchronized (this._sendDatagramList) {
         this._sendCurrentDatagram = null;
         this._sendCurrentThread = null;
         this._sendDatagramList.notify();
      }
   }

   private void addDatagramForSend(Datagram datagram, Thread thread) {
      int length = this._sendDatagramList.length;
      if (this._sendListCount >= length) {
         length *= 2;
         Array.resize(this._sendDatagramList, length);
         Array.resize(this._sendThreadList, length);
      }

      this._sendDatagramList[this._sendListCount] = datagram;
      this._sendThreadList[this._sendListCount] = thread;
      this._sendListCount++;
   }

   private int findDatagramForSend(Datagram datagram, Thread thread) {
      for (int i = this._sendListCount - 1; i >= 0; i--) {
         if (this._sendThreadList[i] == thread && this._sendDatagramList[i] == datagram) {
            return i;
         }
      }

      return -1;
   }

   private void removeDatagramForSend(int index) {
      this._sendListCount--;
      System.arraycopy(this._sendDatagramList, index + 1, this._sendDatagramList, index, this._sendListCount - index);
      System.arraycopy(this._sendThreadList, index + 1, this._sendThreadList, index, this._sendListCount - index);
      this._sendDatagramList[this._sendListCount] = null;
      this._sendThreadList[this._sendListCount] = null;
      if (this._sendListCount <= 0 && this._sendDatagramList.length > 4) {
         Array.resize(this._sendDatagramList, 4);
         Array.resize(this._sendThreadList, 4);
         this._sendListCount = 0;
      }
   }

   protected final void kickSend() {
      synchronized (this._sendDatagramList) {
         for (int i = this._sendListCount - 1; i >= 0; i--) {
            if (!this._sendThreadList[i].isAlive()) {
               this.removeDatagramForSend(i);
            }
         }

         if (this._sendCurrentDatagram != null && !this._sendCurrentThread.isAlive()) {
            this._sendCurrentDatagram = null;
            this._sendCurrentThread = null;
            this._sendDatagramList.notify();
         }
      }
   }

   protected void send(Datagram datagram, DatagramAddressBase addressBase, IOProperties properties, DatagramStatusListener listener, int dgramId) {
      this.send(datagram);
   }

   public void send(Datagram _1) {
      throw null;
   }

   protected void subSend(Datagram datagram, DatagramStatusListener listener, int datagramId, Datagram superDatagram) {
      if (datagramId != 0) {
         int subId = this._subConnection.allocateDatagramId(datagram);
         if (subId != 0) {
            this.addDgramId(listener, datagramId, subId);
         }
      }

      if (superDatagram instanceof DatagramBase && datagram instanceof DatagramBase) {
         ((DatagramBase)superDatagram).copyFlagsInto((DatagramBase)datagram);
      }

      this._subConnection.send(datagram);
   }

   public void superCancel(Datagram datagram) {
      boolean cancelFlag;
      synchronized (this._sendDatagramList) {
         for (int i = this._sendListCount - 1; i >= 0; i--) {
            if (this._sendDatagramList[i] == datagram) {
               this.removeDatagramForSend(i);
            }
         }

         cancelFlag = this._sendCurrentDatagram == datagram;
      }

      if (cancelFlag) {
         this.cancel(datagram);
      }
   }

   public void cancel(Datagram datagram) {
   }

   protected void addDgramId(DatagramStatusListener listener, int datagramId, int subId) {
      if (listener != null) {
         synchronized (this._dgsls) {
            this._dgsls[this._nextIdIndex] = new WeakReference(listener);
            this._datagramIds[this._nextIdIndex] = datagramId;
            this._subIds[this._nextIdIndex] = subId;
            this._nextIdIndex++;
            this._nextIdIndex &= 15;
         }
      }
   }

   public Datagram newDatagram(byte[] buffer, int offset, int length, String address) {
      return new DatagramBase(buffer, offset, length, address);
   }

   public DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      DatagramAddressBase ret = new DatagramAddressBase(address);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   public DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      DatagramAddressBase ret = new DatagramAddressBase(addressBase);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   protected boolean passUpDatagram(Datagram datagram) {
      DatagramAddressBase addressBase = null;
      String address = null;
      if (!(datagram instanceof DatagramBase)) {
         address = datagram.getAddress();
      } else {
         addressBase = ((DatagramBase)datagram).getAddressBase();
      }

      int datagramReceived = 0;
      synchronized (this._superConnections) {
         for (int i = this._superConnections.length - 1; i >= 0; i--) {
            WeakReference w = this._superConnections[i];
            DatagramConnectionBase c;
            if (w != null && (c = (DatagramConnectionBase)w.get()) != null) {
               boolean ret;
               if (addressBase != null) {
                  ret = c.isAddressed(addressBase);
               } else {
                  ret = c.isAddressed(address);
               }

               if (ret) {
                  c.processReceivedDatagram(datagram);
                  datagramReceived++;
               }
            } else {
               Arrays.removeAt(this._superConnections, i);
            }
         }
      }

      return datagramReceived > 0;
   }

   protected void processReceivedDatagram(Datagram _1) {
      throw null;
   }

   public void superProcessReceivedDatagram(Datagram datagram) {
      if (super._tLogger != null) {
         super._tLogger.bytesReceived(this, 1, datagram.getAddress(), datagram.getLength(), datagram.getData());
      }

      this.processReceivedDatagram(datagram);
   }

   protected int getNextDatagramId(DatagramBase dgram) {
      return 0;
   }

   public int allocateDatagramId(Datagram datagram) {
      int datagramId = 0;
      if (datagram instanceof DatagramBase) {
         DatagramBase dgram = (DatagramBase)datagram;
         datagramId = this.getNextDatagramId(dgram);
         dgram.setDatagramId(datagramId);
      }

      return datagramId;
   }

   protected void forwardDgslEvent(int index, int event, Object context) {
      DatagramStatusListener listener = null;
      int datagramId;
      synchronized (this._dgsls) {
         if (this._dgsls[index] != null) {
            listener = (DatagramStatusListener)this._dgsls[index].get();
         }

         datagramId = this._datagramIds[index];
      }

      this.xmitDgslEvent(listener, datagramId, event, context);
   }

   public void passDgslEvent(int subId, int event, Object context) {
      DatagramStatusListener listener = null;
      int datagramId;
      synchronized (this._dgsls) {
         int index = this.lookupDgramIndexFromSubId(subId);
         if (index < 0) {
            return;
         }

         if (this._dgsls[index] != null) {
            listener = (DatagramStatusListener)this._dgsls[index].get();
         }

         datagramId = this._datagramIds[index];
      }

      this.xmitDgslEvent(listener, datagramId, event, context);
   }

   public void xmitDgslEvent(DatagramStatusListener listener, int datagramId, int event, Object context) {
      if (datagramId != 0) {
         try {
            if (listener != null) {
               listener.updateDatagramStatus(datagramId, event, context);
            } else {
               for (int i = this._superConnections.length - 1; i >= 0; i--) {
                  WeakReference w = this._superConnections[i];
                  DatagramConnectionBase c;
                  if (w != null && (c = (DatagramConnectionBase)w.get()) != null) {
                     c.handleDatagramStatus(datagramId, event, context);
                  } else {
                     Arrays.removeAt(this._superConnections, i);
                  }
               }
            }
         } catch (Throwable var8) {
         }
      }
   }

   protected int lookupDgramIndexFromSubId(int subId) {
      synchronized (this._dgsls) {
         int index = this._nextIdIndex - 1 & 15;

         for (int i = 0; i < 16; i++) {
            if (this._subIds[index] == subId) {
               return index;
            }

            index = --index & 15;
         }

         return -1;
      }
   }

   protected int lookupDgramIndexFromDgramId(int datagramId) {
      synchronized (this._dgsls) {
         int index = this._nextIdIndex - 1 & 15;

         for (int i = 0; i < 16; i++) {
            if (this._datagramIds[index] == datagramId) {
               return index;
            }

            index = --index & 15;
         }

         return -1;
      }
   }

   protected int getDgramIdByIndex(int index) {
      synchronized (this._dgsls) {
         return this._datagramIds[index];
      }
   }

   protected void addSendRequest(Object sendObj, Datagram datagram) {
      if (this._sendThread == null) {
         this._sendThread = new SendPacketThread();
         ProtocolDaemon.getInstance().startThread(this._sendThread);
      }

      this._sendThread.addRequest(sendObj, datagram);
   }

   protected byte[] setup(int callType, Object context) {
      return this._subConnection != null ? this._subConnection.setup(callType, context) : null;
   }

   public void datagramProcessed(int datagramId) {
      if (this._subConnection != null) {
         this._subConnection.datagramProcessed(datagramId);
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -1270659756336956134L) {
         this.kickSend();
      }
   }

   @Override
   public void setTrafficLogger(TrafficLogger tLogger) {
      super.setTrafficLogger(tLogger);
      synchronized (this._superConnections) {
         for (int i = this._superConnections.length - 1; i >= 0; i--) {
            WeakReference w = this._superConnections[i];
            DatagramConnectionBase c;
            if (w != null && (c = (DatagramConnectionBase)w.get()) != null) {
               c.setTrafficLogger(tLogger);
            } else {
               Arrays.removeAt(this._superConnections, i);
            }
         }
      }
   }
}
