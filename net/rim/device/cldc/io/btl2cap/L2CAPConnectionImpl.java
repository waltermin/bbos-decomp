package net.rim.device.cldc.io.btl2cap;

import java.io.IOException;
import javax.bluetooth.L2CAPConnection;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.bluetooth.BluetoothDevice;
import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerImpl;
import net.rim.device.cldc.io.btspp.BluetoothConnection;
import net.rim.device.cldc.io.btspp.BluetoothURL;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothL2CAP;
import net.rim.device.internal.bluetooth.BluetoothL2CAPListener;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.BluetoothMEListener2;
import net.rim.vm.Array;
import net.rim.vm.Process;

public class L2CAPConnectionImpl implements L2CAPConnection, BluetoothMEListener2, BluetoothL2CAPListener, BluetoothConnection {
   private int _aclHandle;
   private int _aclResult;
   private int _psmHandle;
   private byte[] _remoteAddr;
   private Application _app = Application.getApplication();
   private int _channelID;
   private byte[][] _receivedPackets;
   private int _receiveMTU;
   private int _transmitMTU;
   private int _requestedTransmitMTU;
   private int _remotePSMID;
   private L2CAPConnectionNotifierImpl _notifier;
   private Runnable _cleanupRunnable;
   private Object _readSemaphore;
   private Object _writeSemaphore;
   private static final boolean DEBUG = false;

   @Override
   public void close() {
      this.cleanup(false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void connect() {
      synchronized (this._readSemaphore) {
         BluetoothME.addListener(this._app, this);
         int retries = 5;

         while (true) {
            int[] handle = new int[1];
            BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
            BluetoothDevice device = btManager.getDevice(this._remoteAddr, 0);
            int rc = BluetoothME.connectDevice(this._remoteAddr, device.getPageScanInfo(), handle);
            switch (rc) {
               case 0:
                  this._aclHandle = handle[0];
                  break;
               case 2:
                  this._aclHandle = handle[0];

                  try {
                     this._readSemaphore.wait(60000);
                     break;
                  } finally {
                     break;
                  }
               default:
                  this.cleanup(true);
                  throw new IOException("connectDevice failed: " + rc);
            }

            if (this._aclHandle != -1) {
               BluetoothME.removeListener(this._app, this);
               boolean var17 = false /* VF: Semaphore variable */;

               try {
                  var17 = true;
                  this._channelID = BluetoothL2CAP.connectRequest(this._aclHandle, this._psmHandle, this._remotePSMID);
                  var17 = false;
               } finally {
                  if (var17) {
                     this.cleanup(true);
                  }
               }

               label129:
               try {
                  this._readSemaphore.wait(60000);
               } finally {
                  break label129;
               }

               if (this._channelID == -1) {
                  this.cleanup(true);
                  throw new IOException("Unable to connect");
               }

               return;
            }

            if (this._aclResult != 4 || retries <= 0) {
               this.cleanup(true);
               throw new IOException("Unable to connect");
            }

            retries--;
         }
      }
   }

   int getPSM() {
      if (this._psmHandle != -1) {
         try {
            return BluetoothL2CAP.getPSM(this._psmHandle);
         } finally {
            return -1;
         }
      } else {
         return -1;
      }
   }

   void cleanup(boolean forceDeregister) {
      synchronized (this._readSemaphore) {
         this.disconnect();
         if (this._notifier == null || forceDeregister) {
            this.deregister();
         }

         this._readSemaphore.notify();
      }

      synchronized (this._writeSemaphore) {
         this._writeSemaphore.notify();
      }
   }

   int getPSMHandle() {
      return this._psmHandle;
   }

   @Override
   public byte[] getRemoteAddress() {
      synchronized (this._readSemaphore) {
         return this._channelID == -1 ? null : this._remoteAddr;
      }
   }

   @Override
   public boolean isConnected() {
      return this._channelID != -1;
   }

   @Override
   public int getReceiveMTU() throws IOException {
      if (this._channelID == -1) {
         throw new IOException("Connection closed");
      } else {
         return this._receiveMTU;
      }
   }

   @Override
   public int getTransmitMTU() throws IOException {
      if (this._channelID == -1) {
         throw new IOException("Connection closed");
      }

      if (this._transmitMTU == -1) {
         if (this._requestedTransmitMTU != -1) {
            this._transmitMTU = this._requestedTransmitMTU;
         } else {
            this._transmitMTU = BluetoothL2CAP.getTransmitMTU(this._channelID);
         }
      }

      return this._transmitMTU;
   }

   @Override
   public boolean ready() {
      synchronized (this._readSemaphore) {
         if (this._channelID == -1) {
            throw new IOException("Connection closed");
         } else {
            return this._receivedPackets.length != 0;
         }
      }
   }

   @Override
   public int receive(byte[] buf) {
      if (buf == null) {
         throw new NullPointerException();
      }

      synchronized (this._readSemaphore) {
         if (this._channelID == -1) {
            throw new IOException("Connection closed");
         }

         if (this._receivedPackets.length == 0) {
            label50:
            try {
               this._readSemaphore.wait();
            } finally {
               break label50;
            }
         }

         if (this._receivedPackets.length == 0) {
            throw new IOException("Connection closed");
         }

         byte[] packet = this._receivedPackets[0];
         int length = buf.length;
         if (length > packet.length) {
            length = packet.length;
         }

         System.arraycopy(packet, 0, buf, 0, length);
         Arrays.removeAt(this._receivedPackets, 0);
         return length;
      }
   }

   @Override
   public void send(byte[] data) {
      synchronized (this._writeSemaphore) {
         if (this._channelID == -1) {
            throw new IOException("Connection closed");
         }

         BluetoothL2CAP.sendData(this._psmHandle, this._channelID, data);

         try {
            this._writeSemaphore.wait(60000);
         } finally {
            return;
         }
      }
   }

   @Override
   public void powerOnComplete(boolean success) {
   }

   @Override
   public void powerOffComplete() {
   }

   @Override
   public void inquiryComplete() {
   }

   @Override
   public void inquiryCancelled() {
   }

   @Override
   public void inquiryResult(byte[] address, int rssi, int deviceClass, int pageScanInfo) {
   }

   @Override
   public void deviceNameRetrieved(byte[] address, byte[] name) {
   }

   @Override
   public void pairingComplete(byte[] address, int result) {
   }

   @Override
   public void pinCodeRequired(byte[] address, int deviceClass) {
   }

   @Override
   public void authorizationRequired(byte[] address, int securityRecordID) {
   }

   @Override
   public void serviceDiscoveryComplete(byte[] address, int result, byte[] data) {
   }

   @Override
   public void linkModeChanged(byte[] address, int result, int mode) {
   }

   @Override
   public void connectionAccepted(byte[] address) {
   }

   @Override
   public void linkKeyChangeComplete(byte[] address, int result) {
   }

   @Override
   public void authenticationComplete(byte[] address, int result) {
   }

   @Override
   public void encryptionComplete(byte[] address, int result) {
   }

   @Override
   public void hciFatalError(byte[] address, int result) {
   }

   @Override
   public void deviceConnected(byte[] address, int deviceClass, int result) {
      if (Arrays.equals(address, this._remoteAddr)) {
         synchronized (this._readSemaphore) {
            if (result != 0) {
               this._aclHandle = -1;
               this._aclResult = result;
            }

            this._readSemaphore.notify();
         }
      }
   }

   @Override
   public void deviceDisconnected(byte[] address, int reason) {
   }

   @Override
   public void l2capIncomingConnection(int psmHandle, int cid, byte[] address) {
      if (psmHandle == this._psmHandle) {
         this._remoteAddr = address;
         this._channelID = cid;

         label30:
         try {
            BluetoothL2CAP.connectResponse(cid, 0);
         } finally {
            break label30;
         }

         if (this._aclHandle == -1) {
            int[] handle = new int[1];
            if (BluetoothME.connectDevice(this._remoteAddr, 0, handle) == 0) {
               this._aclHandle = handle[0];
            }
         }
      }
   }

   @Override
   public void l2capConnected(int cid) {
      if (cid == this._channelID) {
         synchronized (this._readSemaphore) {
            this._readSemaphore.notify();
         }

         if (this._notifier != null) {
            this._notifier.incomingConnection();
         }
      }
   }

   @Override
   public void l2capDisconnected(int cid) {
      if (cid == this._channelID) {
         synchronized (this._readSemaphore) {
            this._channelID = -1;
            this._readSemaphore.notify();

            label38:
            try {
               this.close();
            } finally {
               break label38;
            }

            if (this._notifier != null) {
               this._notifier.connectionClosed();
            }
         }
      }
   }

   @Override
   public void l2capDataReceived(int cid, byte[] data) {
      if (cid == this._channelID) {
         synchronized (this._readSemaphore) {
            Arrays.add(this._receivedPackets, data);
            this._readSemaphore.notify();
         }
      }
   }

   @Override
   public void l2capDataSent(int cid) {
      if (cid == this._channelID) {
         synchronized (this._writeSemaphore) {
            this._writeSemaphore.notify();
         }
      }
   }

   L2CAPConnectionImpl(L2CAPConnectionNotifierImpl notifier, BluetoothURL url) {
      this(url);
      this._notifier = notifier;
   }

   private void disconnect() {
      Array.resize(this._receivedPackets, 0);
      if (this._channelID != -1) {
         label34:
         try {
            BluetoothL2CAP.disconnectRequest(this._channelID);
         } finally {
            break label34;
         }

         this._channelID = -1;
      }

      if (this._aclHandle != -1) {
         BluetoothME.disconnectDevice(this._aclHandle);
         this._aclHandle = -1;
      }

      if (this._notifier != null) {
         this._notifier.connectionClosed();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void deregister() {
      if (this._psmHandle != -1) {
         for (int i = 0; i < 5; i++) {
            boolean var13 = false /* VF: Semaphore variable */;

            try {
               var13 = true;
               BluetoothL2CAP.deregisterPSM(this._psmHandle);
               var13 = false;
               break;
            } finally {
               if (var13) {
                  try {
                     Thread.sleep(500);
                     continue;
                  } finally {
                     continue;
                  }
               }
            }
         }

         this._psmHandle = -1;
      }

      BluetoothL2CAP.removeListener(this._app, this);
      BluetoothME.removeListener(this._app, this);

      try {
         ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this._cleanupRunnable);
      } finally {
         return;
      }
   }

   L2CAPConnectionImpl(BluetoothURL url) {
      this();
      this._remoteAddr = url.getAddress();
      this._receiveMTU = url.getReceiveMTU();
      this._transmitMTU = -1;
      this._requestedTransmitMTU = url.wasTransmitMTURequested() ? url.getTransmitMTU() : -1;
      this._remotePSMID = url.getPSM();
      this._psmHandle = BluetoothL2CAP.registerPSM(
         this._receiveMTU, url.getTransmitMTU(), this._requestedTransmitMTU, this._remoteAddr == null, this._remotePSMID
      );
      this._cleanupRunnable = new L2CAPConnectionImpl$L2CAPCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
      BluetoothL2CAP.addListener(this._app, this);
   }

   private L2CAPConnectionImpl() {
      this._aclHandle = -1;
      this._psmHandle = -1;
      this._channelID = -1;
      this._readSemaphore = new Object();
      this._writeSemaphore = new Object();
      this._receivedPackets = new byte[0][];
   }
}
