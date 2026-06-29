package net.rim.device.cldc.io.utility;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.vm.Array;
import net.rim.vm.DebugSupport;
import net.rim.vm.Memory;

public final class PacketLogger {
   private Object[] _packets = new Object[20];
   private long[] _times = new long[20];
   private String[] _dests = new String[20];
   private boolean[] _sents = new boolean[20];
   private int _numEntries;
   private int _circularSize;
   private int _currentIndex;
   private long _previousTime;
   public boolean _lowLoggingEnabled;
   public boolean _highLoggingEnabled;
   private StringBuffer _tempSB = new StringBuffer();
   private static final long SINGLETON_REGISTRATION_KEY = 900432232726625421L;
   private static final long PACKET_LOG = 1638247208346682407L;
   private static final int ARRAY_SIZE_INCREMENT = 10;
   public static final int SENT_PACKETS = 1;
   public static final int RECV_PACKETS = 2;
   private static PacketLogger _instance;

   private PacketLogger() {
   }

   public static final PacketLogger getInstance() {
      return _instance;
   }

   public final synchronized int savePackets() {
      this._lowLoggingEnabled = false;
      this._highLoggingEnabled = false;
      PersistentObject store = RIMPersistentStore.getPersistentObject(1638247208346682407L);
      synchronized (store) {
         store.setContents(this._packets, 51);
         store.commit();
      }

      return Memory.objectToInt(this._packets);
   }

   public final synchronized void clearSavedPackets() {
      RIMPersistentStore.destroyPersistentObject(1638247208346682407L);
      Dialog.alert("Packet log record deleted.");
   }

   public final synchronized void startLogging(boolean lowLogging) {
      SimpleChoiceDialog dialog = new SimpleChoiceDialog("Choose Logging Mode", new String[]{"All", "Circular 10", "Circular 25"}, 0, null, 134217728);
      BackgroundDialog.show(dialog);
      switch (dialog.getSelectedIndex()) {
         case -1:
            break;
         case 0:
         default:
            this._circularSize = -1;
            break;
         case 1:
            this._circularSize = 10;
            break;
         case 2:
            this._circularSize = 25;
      }

      for (int i = 0; i < this._numEntries; i++) {
         this._packets[i] = null;
         this._dests[i] = null;
      }

      this._numEntries = 0;
      this._currentIndex = 0;
      if (lowLogging) {
         this._lowLoggingEnabled = true;
         Dialog.alert("Low-level Packet Logging Started");
      } else {
         this._highLoggingEnabled = true;
         Dialog.alert("High-level Packet Logging Started");
      }
   }

   public final synchronized String getText(int detailLevel, int logValues) {
      this._lowLoggingEnabled = false;
      this._highLoggingEnabled = false;
      this._tempSB.setLength(0);
      int currentIndex;
      if (this._circularSize != -1 && this._currentIndex < this._numEntries) {
         currentIndex = this._currentIndex;
      } else {
         currentIndex = 0;
      }

      for (int i = 0; i < this._numEntries; currentIndex++) {
         if (this._circularSize != -1) {
            currentIndex %= this._circularSize;
         }

         if (this._sents[currentIndex] ? (logValues & 1) != 0 : (logValues & 2) != 0) {
            this.printPacket(currentIndex, detailLevel);
         }

         i++;
      }

      return this._tempSB.toString();
   }

   public final synchronized void logPacket(byte[] packet, int offset, int length, String dest, boolean sent) {
      if (this._lowLoggingEnabled || this._highLoggingEnabled) {
         if (this._currentIndex >= this._packets.length) {
            Array.resize(this._packets, this._packets.length + 10);
            Array.resize(this._times, this._times.length + 10);
            Array.resize(this._dests, this._dests.length + 10);
            Array.resize(this._sents, this._sents.length + 10);
         }

         long newTime = System.currentTimeMillis();
         this._times[this._currentIndex] = newTime - this._previousTime;
         this._previousTime = newTime;
         byte[] packetCopy = new byte[length];
         System.arraycopy(packet, offset, packetCopy, 0, length);
         this._packets[this._currentIndex] = packetCopy;
         this._dests[this._currentIndex] = dest;
         this._sents[this._currentIndex] = sent;
         if (DebugSupport.isDebuggerAttached()) {
            this._tempSB.setLength(0);
            this.printPacket(this._currentIndex, 2);
            System.out.print(this._tempSB.toString());
            this._tempSB.setLength(0);
         }

         this._currentIndex++;
         if (this._circularSize != -1) {
            if (this._currentIndex >= this._circularSize) {
               this._currentIndex = 0;
               return;
            }

            if (this._numEntries < this._circularSize) {
               this._numEntries++;
               return;
            }
         } else {
            this._numEntries++;
         }
      }
   }

   public final synchronized void printPacketsToStdout() {
      this._lowLoggingEnabled = false;
      this._highLoggingEnabled = false;
      int currentIndex;
      if (this._circularSize != -1 && this._currentIndex < this._numEntries) {
         currentIndex = this._currentIndex;
      } else {
         currentIndex = 0;
      }

      for (int i = 0; i < this._numEntries; currentIndex++) {
         if (this._circularSize != -1) {
            currentIndex %= this._circularSize;
         }

         this._tempSB.setLength(0);
         this.printPacket(currentIndex, 2);
         System.out.print(this._tempSB.toString());
         i++;
      }

      this._tempSB.setLength(0);
   }

   private final void printPacket(int packet, int detailLevel) {
      this._tempSB.append("--- ");
      this._tempSB.append(this._times[packet]);
      this._tempSB.append("ms ---\n");
      if (this._sents[packet]) {
         this._tempSB.append("Sent: ");
      } else {
         this._tempSB.append("Received: ");
      }

      this._tempSB.append(((byte[])this._packets[packet]).length + " bytes\n");
      if (detailLevel >= 1) {
         this._tempSB.append(this._dests[packet]);
         this._tempSB.append("\n");
         this.convertPacket(this._tempSB, (byte[])this._packets[packet], detailLevel);
      }
   }

   private final void convertPacket(StringBuffer sb, byte[] data, int detailLevel) {
      int length = data.length;

      for (int p = 0; p < length; p += 8) {
         int i;
         for (i = 0; i < 8 && p + i < length; i++) {
            int value = (data[p + i] + 256) % 256;
            int highValue = value / 16;
            int lowValue = value % 16;
            if (highValue < 10) {
               sb.append((char)(highValue + 48));
            } else {
               sb.append((char)(highValue + 55));
            }

            if (lowValue < 10) {
               sb.append((char)(lowValue + 48));
            } else {
               sb.append((char)(lowValue + 55));
            }

            sb.append(' ');
         }

         while (i < 8) {
            sb.append("   ");
            i++;
         }

         for (int var10 = 0; var10 < 8 && p + var10 < length; var10++) {
            byte b = data[p + var10];
            if (b >= 32 && b < 128) {
               sb.append((char)b);
            } else {
               sb.append('.');
            }
         }

         sb.append('\n');
         if (detailLevel < 2 && p == 8) {
            return;
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (PacketLogger)ar.getOrWaitFor(900432232726625421L);
      if (_instance == null) {
         _instance = new PacketLogger();
         ar.put(900432232726625421L, _instance);
      }
   }
}
