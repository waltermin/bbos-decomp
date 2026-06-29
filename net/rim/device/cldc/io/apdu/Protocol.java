package net.rim.device.cldc.io.apdu;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardAPDUListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.vm.Array;
import net.rim.vm.Process;

public class Protocol implements SIMCardAPDUListener, APDUConnection, ConnectionBaseInterface, StreamConnection {
   private byte _channelNumber;
   private byte _tag;
   private byte _error;
   private byte _pinOperation;
   private int _pinResult;
   private Object _openSemaphore = new Object();
   private Object _closeSemaphore = new Object();
   private Object _exchangeSemaphore = new Object();
   private Object _pinSemaphore = new Object();
   private boolean _isOpen;
   private boolean _exchangeSuccessful;
   private boolean _pinOperationSuccessful;
   private boolean _isSATConnection;
   private Runnable _cleanupRunnable;
   private ApplicationProcess _applicationProcess;
   private Proxy _proxy = Proxy.getInstance();
   private static final int PIN_LENGTH;
   private static final int MIN_PIN_LENGTH;
   private static final int PIN1_FOR_APPLICATION_2;
   private static final int PIN1_FOR_APPLICATION_3;
   private static final int PIN1_FOR_APPLICATION_4;
   private static final int PIN1_FOR_APPLICATION_5;
   private static final int PIN1_FOR_APPLICATION_6;
   private static final int PIN1_FOR_APPLICATION_7;
   private static final int PIN1_FOR_APPLICATION_8;
   private static final int PIN2_FOR_APPLICATION_2;
   private static final int PIN2_FOR_APPLICATION_3;
   private static final int PIN2_FOR_APPLICATION_4;
   private static final int PIN2_FOR_APPLICATION_5;
   private static final int PIN2_FOR_APPLICATION_6;
   private static final int PIN2_FOR_APPLICATION_7;
   private static final int PIN2_FOR_APPLICATION_8;
   private static final long GUID;

   @Override
   public void close() {
      if (this._isOpen) {
         synchronized (this._closeSemaphore) {
            SIMCard.closeAPDUConnection(this._channelNumber, this._tag);

            label40:
            try {
               this._closeSemaphore.wait();
            } finally {
               break label40;
            }

            if (!this._isOpen) {
               SIMCard.removeListener(this._proxy, this);
               this._applicationProcess.removeCleanupRunnable(this._cleanupRunnable);
            } else {
               throw new Object();
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) {
      if (!SIMCard.isJSR177Supported()) {
         throw new Object("JSR 177 is not supported on this platform");
      }

      byte[] aid = new byte[16];
      byte aidLength = 0;
      int tempIndex = name.indexOf(59);
      int slot = 0;
      if (tempIndex == -1) {
         throw new Object();
      }

      if (tempIndex != 0) {
         boolean var16 = false /* VF: Semaphore variable */;

         try {
            var16 = true;
            slot = (byte)Integer.parseInt(name.substring(0, tempIndex), 16);
            var16 = false;
         } finally {
            if (var16) {
               throw new Object();
            }
         }
      }

      if (slot != 0) {
         throw new Object();
      }

      name = name.substring(tempIndex + 1);
      if (!name.startsWith("target=")) {
         throw new Object();
      }

      name = name.substring(7);
      this._isSATConnection = name.indexOf("SAT") >= 0;
      if (this._isSATConnection) {
         if (name.length() != 3) {
            throw new Object();
         }
      } else {
         for (StringTokenizer st = (StringTokenizer)(new Object(name, '.')); st.hasMoreTokens(); aidLength++) {
            if (aidLength >= 16) {
               throw new Object("Invalid AID; Too Long");
            }

            String temp = st.nextToken();
            if (temp.length() > 2) {
               throw new Object("Invalid AID");
            }

            boolean var13 = false /* VF: Semaphore variable */;

            try {
               var13 = true;
               aid[aidLength] = (byte)Integer.parseInt(temp, 16);
               var13 = false;
            } finally {
               if (var13) {
                  throw new Object("Invalid AID");
               }
            }
         }

         if (aidLength < 5 || aidLength > 16) {
            throw new Object("Invalid AID; Too short or too long");
         }
      }

      return this.connect(aid, aidLength, (byte)0);
   }

   @Override
   public int getProperties(String name) {
      return 32;
   }

   public String getSIMCode(boolean puk, String prompt) {
      Protocol$SIMCodeDialog d = new Protocol$SIMCodeDialog(prompt);
      d.setRevealPassword(false);
      if (puk) {
         d.setMinLength(8);
      } else {
         d.setMinLength(4);
      }

      BackgroundDialog.show(d);
      return d.getCloseReason() == -1 ? null : d.getText();
   }

   public byte[] getNewPin(int pinID) {
      String newPin = this.getSIMCode(false, CommonResource.getString(10137));
      if (newPin == null) {
         return null;
      } else {
         String verifiedPIN = this.getSIMCode(false, CommonResource.getString(10143));
         if (verifiedPIN == null) {
            return null;
         } else if (newPin.compareTo(verifiedPIN) != 0) {
            BackgroundDialog.showMessage(CommonResource.getString(10159));
            return null;
         } else {
            return this.ensurePINSize(newPin.getBytes());
         }
      }
   }

   public byte[] ensurePINSize(byte[] pin) {
      int pinBytesLength = pin.length;
      if (pinBytesLength < 8) {
         Array.resize(pin, 8);
         Arrays.fill(pin, (byte)-1, pinBytesLength, 8 - pinBytesLength);
      }

      return pin;
   }

   public byte[] getPIN(int id) {
      String pin = this.getSIMCode(false, CommonResource.getString(10139));
      return pin == null ? null : this.ensurePINSize(pin.getBytes());
   }

   public byte getChannelNumber() {
      return this._channelNumber;
   }

   public boolean isOpen() {
      return this._isOpen;
   }

   public byte[] openRMIConnection(byte[] aid, int aidLength) {
      this.connect(aid, aidLength, (byte)2);
      return SIMCard.getAPDUResponse(this._channelNumber, this._tag);
   }

   @Override
   public byte[] getATR() {
      return !this._isOpen ? null : SIMCard.getATR();
   }

   @Override
   public byte[] exchangeAPDU(byte[] commandAPDU) {
      if (commandAPDU == null) {
         throw new Object();
      }

      if (!this._isOpen) {
         throw new Object();
      }

      synchronized (this._exchangeSemaphore) {
         SIMCard.exchangeAPDU(commandAPDU, this._channelNumber, this._tag);

         label57:
         try {
            this._exchangeSemaphore.wait();
         } finally {
            break label57;
         }

         if (this._exchangeSuccessful) {
            this._exchangeSuccessful = false;
            byte[] responseAPDU = SIMCard.getAPDUResponse(this._channelNumber, this._tag);
            if (responseAPDU == null) {
               throw new Object();
            } else {
               return responseAPDU;
            }
         } else {
            switch (this._error) {
               case -1:
                  throw new Object();
               case 0:
               default:
                  throw new Object();
               case 1:
               case 7:
                  throw new Object();
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
                  throw new Object();
            }
         }
      }
   }

   @Override
   public byte[] changePin(int pinID) {
      if (this._isSATConnection) {
         throw new UnsupportedOperationException();
      }

      if (!this._isOpen) {
         throw new Object();
      }

      byte[] currentPin = this.getPIN(pinID);
      if (currentPin == null) {
         return null;
      }

      byte[] newPin = this.getNewPin(pinID);
      return newPin == null ? null : this.doPinOperation(this.ensurePinID(pinID), currentPin, newPin, 1);
   }

   @Override
   public byte[] disablePin(int pinID) {
      if (this._isSATConnection) {
         throw new UnsupportedOperationException();
      }

      if (!this._isOpen) {
         throw new Object();
      }

      byte[] currentPin = this.getPIN(pinID);
      return currentPin == null ? null : this.doPinOperation(this.ensurePinID(pinID), currentPin, null, 2);
   }

   @Override
   public byte[] enablePin(int pinID) {
      if (this._isSATConnection) {
         throw new UnsupportedOperationException();
      }

      if (!this._isOpen) {
         throw new Object();
      }

      byte[] currentPin = this.getPIN(pinID);
      return currentPin == null ? null : this.doPinOperation(this.ensurePinID(pinID), currentPin, null, 3);
   }

   @Override
   public byte[] enterPin(int pinID) {
      if (this._isSATConnection) {
         throw new UnsupportedOperationException();
      }

      if (!this._isOpen) {
         throw new Object();
      }

      byte[] currentPin = this.getPIN(pinID);
      return currentPin == null ? null : this.doPinOperation(this.ensurePinID(pinID), currentPin, null, 0);
   }

   @Override
   public byte[] unblockPin(int blockedPinID, int unblockingPinID) {
      if (this._isSATConnection) {
         throw new UnsupportedOperationException();
      } else if (!this._isOpen) {
         throw new Object();
      } else {
         String pin = this.getSIMCode(false, CommonResource.getString(10139));
         if (pin != null && pin.length() != 0) {
            byte[] blockedPin = this.getNewPin(blockedPinID);
            return blockedPin == null ? null : this.doPinOperation(this.ensurePinID(blockedPinID), this.ensurePINSize(pin.getBytes()), blockedPin, 4);
         } else {
            return null;
         }
      }
   }

   @Override
   public InputStream openInputStream() {
      throw new Object("Not supported");
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      throw new Object("Not supported");
   }

   @Override
   public OutputStream openOutputStream() {
      throw new Object("Not supported");
   }

   @Override
   public DataInputStream openDataInputStream() {
      throw new Object("Not supported");
   }

   @Override
   public void openSuccessful(byte channelNumber, byte tag) {
      if (this._tag == tag) {
         synchronized (this._openSemaphore) {
            this._channelNumber = channelNumber;
            this._isOpen = true;
            this._openSemaphore.notify();
         }
      }
   }

   @Override
   public void openError(byte error, byte tag) {
      if (this._tag == tag) {
         synchronized (this._openSemaphore) {
            this._error = error;
            this._isOpen = false;
            this._openSemaphore.notify();
         }
      }
   }

   @Override
   public void closeSuccessful(byte channelNumber, byte tag) {
      if (this._tag == tag && channelNumber == this._channelNumber) {
         synchronized (this._closeSemaphore) {
            this._error = 0;
            this._isOpen = false;
            this._closeSemaphore.notify();
         }

         synchronized (this._exchangeSemaphore) {
            this._exchangeSemaphore.notify();
            this._exchangeSuccessful = false;
         }

         synchronized (this._pinSemaphore) {
            this._pinSemaphore.notify();
            this._pinOperationSuccessful = false;
         }
      }
   }

   @Override
   public void closeError(byte channelNumber, byte error, byte tag) {
      if (this._tag == tag && channelNumber == this._channelNumber) {
         synchronized (this._closeSemaphore) {
            this._error = error;
            this._closeSemaphore.notify();
         }
      }
   }

   @Override
   public void exchangeAPDUSuccessful(byte channelNumber, byte tag) {
      if (this._tag == tag && channelNumber == this._channelNumber && this._isOpen) {
         synchronized (this._exchangeSemaphore) {
            this._exchangeSuccessful = true;
            this._exchangeSemaphore.notify();
         }
      }
   }

   @Override
   public void exchangeAPDUError(byte channelNumber, byte error, byte tag) {
      if (this._tag == tag && channelNumber == this._channelNumber && this._isOpen) {
         synchronized (this._exchangeSemaphore) {
            this._exchangeSuccessful = false;
            this._error = error;
            this._exchangeSemaphore.notify();
         }
      }
   }

   @Override
   public void pinOpeartionSuccessful(byte channelNumber, int result, byte operation) {
      if (channelNumber == this._channelNumber && this._isOpen) {
         synchronized (this._pinSemaphore) {
            this._pinOperationSuccessful = true;
            this._error = 0;
            this._pinOperation = operation;
            this._pinResult = result;
            this._pinSemaphore.notify();
         }
      }
   }

   @Override
   public void pinOperationUnSuccessful(byte channelNumber, int result, byte error, byte operation) {
      if (channelNumber == this._channelNumber && this._isOpen) {
         synchronized (this._pinSemaphore) {
            this._pinOperationSuccessful = false;
            this._error = error;
            this._pinOperation = operation;
            this._pinResult = result;
            this._pinSemaphore.notify();
         }
      }
   }

   private byte[] doPinOperation(int pinID, byte[] currentPin, byte[] newPin, int pinOperation) {
      if (currentPin.length == 8 && (newPin == null || newPin.length == 8)) {
         synchronized (this._pinSemaphore) {
            SIMCard.doAPDUPinOperation((byte)pinOperation, this._channelNumber, (byte)pinID, currentPin, newPin);

            label67:
            try {
               this._pinSemaphore.wait();
            } finally {
               break label67;
            }

            if (this._pinOperation != pinOperation || !this._pinOperationSuccessful && this._pinResult == 0) {
               if (this._error == 0) {
                  throw new Object();
               } else {
                  throw new Object();
               }
            } else {
               byte[] result = new byte[]{(byte)(this._pinResult >> 8 & 0xFF), (byte)(this._pinResult & 0xFF)};
               this._pinOperationSuccessful = false;
               return result;
            }
         }
      } else {
         throw new Object();
      }
   }

   private Protocol connect(byte[] aid, int aidLength, byte fcpResponse) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         Byte tempTag = (Byte)ar.get(487410666346702508L);
         if (tempTag == null) {
            tempTag = (Byte)(new Object((byte)0));
         } else {
            tempTag = (Byte)(new Object((byte)(tempTag + 1)));
         }

         ar.replace(487410666346702508L, tempTag);
         this._tag = tempTag;
      }

      SIMCard.addListener(this._proxy, this);
      synchronized (this._openSemaphore) {
         if (this._isSATConnection) {
            SIMCard.openAPDUConnection(null, (byte)0, (byte)0, this._tag, fcpResponse);
         } else {
            SIMCard.openAPDUConnection(aid, (byte)0, (byte)aidLength, this._tag, fcpResponse);
         }

         label66:
         try {
            this._openSemaphore.wait();
         } finally {
            break label66;
         }

         if (this._isOpen) {
            this._cleanupRunnable = new Protocol$JSR177CleanupRunnable(this);
            this._applicationProcess = (ApplicationProcess)Process.currentProcess();
            this._applicationProcess.addCleanupRunnable(this._cleanupRunnable);
            return this;
         }

         SIMCard.removeListener(this._proxy, this);
         switch (this._error) {
            case 0:
               throw new Object();
            case 1:
            default:
               throw new Object("Unable to open; Open failed");
            case 2:
               throw new UnsupportedOperationException("Unable to open; JSR177 is not supported on this platform");
            case 3:
               throw new Object("Unable to open; Bad AID");
            case 4:
               throw new Object("Unable to open; No free channels");
         }
      }
   }

   private int ensurePinID(int pinId) {
      switch (pinId) {
         case 0:
            return 2;
         case 1:
            return 130;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
            return pinId;
         default:
            throw new Object("Error - Invalid Pin");
      }
   }
}
