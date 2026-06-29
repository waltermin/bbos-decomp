package net.rim.device.internal.EScreens.sessions;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.system.UDPPacketListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.EScreens.EScreenSession;
import net.rim.device.internal.EScreens.EScreenSession$InvalidInputException;
import net.rim.device.internal.EScreens.EScreenSessionManager;
import net.rim.device.internal.system.RadioInternal;

public final class UDPBlasterSession extends EScreenSession implements UDPPacketListener {
   private int[] _packetIds;
   private byte[] _payload;
   private UDPPacketHeader _header;
   private int _windowSize;
   private int _payloadSize;
   private int _port;
   private EditField _portField = (EditField)(new Object("Destination Port: ", "56026", Integer.MAX_VALUE, 16777216));
   private NumericChoiceField _windowSizeField = (NumericChoiceField)(new Object("Window Size: ", 1, 5, 1, 4));
   private EditField _packetSizeField = (EditField)(new Object("Packet size: ", "256", Integer.MAX_VALUE, 16777216));
   private static final int COUNTER_SENT = 0;
   private static final int COUNTER_SENT_OK = 1;
   private static final int COUNTER_SENT_FAIL = 2;
   private static final String[] COUNTER_HEADERS = new String[]{"Tx Attempts: ", "Tx Succeeded: ", "Tx Failed: "};

   @Override
   public final void init(EScreenSessionManager manager, int si) {
      super.init(manager, si);
      this.setCounterHeaders(COUNTER_HEADERS);
   }

   @Override
   public final void addParameterFields(Vector v) {
      v.addElement(this._portField);
      v.addElement(this._windowSizeField);
      v.addElement(this._packetSizeField);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean verifyParameters() {
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         this._port = Integer.parseInt(this._portField.getText());
         var7 = false;
      } finally {
         if (var7) {
            Dialog.alert("Destination Port must be a number");
            return false;
         }
      }

      this._windowSize = this._windowSizeField.getSelectedValue();
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         this._payloadSize = Integer.parseInt(this._packetSizeField.getText());
         var4 = false;
      } finally {
         if (var4) {
            Dialog.alert("Packet size must be a number");
            return false;
         }
      }

      if (this._payloadSize < 1) {
         Dialog.alert("Packet must be at least 1 byte");
         return false;
      } else if (this._payloadSize > UDPPacketHeader.getMaxPacketSize()) {
         Dialog.alert("Packet is too big");
         return false;
      } else if (this._windowSize <= 0) {
         Dialog.alert("Must have a positive window size");
         return false;
      } else {
         return super.verifyParameters();
      }
   }

   @Override
   public final synchronized void stop() {
      super.stop();
      Application.getApplication().removeRadioListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void start() {
      boolean var12 = false /* VF: Semaphore variable */;

      label50: {
         try {
            var12 = true;
            this._header = (UDPPacketHeader)this.makeHeader(0);
            this._header.setDestinationPort(this._port);
            this._header.setSourcePort(this._port);
            this.setNumResults(1);
            this._payload = new byte[this._payloadSize];
            Arrays.fill(this._payload, (byte)112);
            this._packetIds = new int[this._windowSize];
            Arrays.fill(this._packetIds, -1);
            Application.getApplication().addRadioListener(this);

            for (int i = this._packetIds.length - 1; i >= 0; i--) {
               this.sendPacket(i);
            }

            var12 = false;
            break label50;
         } catch (EScreenSession$InvalidInputException e) {
            synchronized (Application.getEventLock()) {
               Status.show("Invalid Header Type!", 3000);
               var12 = false;
            }
         } finally {
            if (var12) {
               this.stop();
               this.resetStats();
            }
         }

         this.stop();
         this.resetStats();
         return;
      }

      this.stop();
      this.resetStats();
   }

   @Override
   public final String getSessionType() {
      return "UDP Blaster";
   }

   @Override
   public final void handleTimeout() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized void sendPacket(int index) {
      while (true) {
         try {
            this._packetIds[index] = RadioInternal.sendPacket(this._header, this._payload);
         } catch (Throwable var10) {
            String msg = e.getMessage();
            if (!msg.equals("Radio error") && !msg.equals("No free buffers")) {
               this.setResult(0, ((StringBuffer)(new Object("Ex: "))).append(msg).toString(), -1);
               this.stop();
               return;
            }

            try {
               Thread.sleep(50);
               continue;
            } finally {
               continue;
            }
         }

         this.incrementCounter(0);
         return;
      }
   }

   @Override
   public final synchronized void packetSent(int packetId, int networkId) {
      for (int i = this._packetIds.length - 1; i >= 0; i--) {
         if (this._packetIds[i] == packetId) {
            this.incrementCounter(1);
            this._packetIds[i] = -1;
            this.sendPacket(i);
            return;
         }
      }
   }

   @Override
   public final synchronized void packetNotSent(int packetId, int error) {
      String errorStr = null;

      for (int i = this._packetIds.length - 1; i >= 0; i--) {
         if (this._packetIds[i] == packetId) {
            switch (error) {
               case 249:
               case 253:
               case 254:
                  errorStr = "Unknown error: Aborting";
                  break;
               case 250:
               default:
                  errorStr = "Packet not sent: Bad Context: Aborting";
                  break;
               case 251:
                  this.incrementCounter(2);
                  this._packetIds[i] = -1;
                  this.sendPacket(i);
                  return;
               case 252:
                  errorStr = "Packet not sent: No Coverage: Aborting";
                  break;
               case 255:
                  errorStr = "Packet not sent: Bad Address: Aborting";
            }

            this.setResult(0, errorStr, -1);
            this.stop();
            return;
         }
      }
   }

   @Override
   public final void packetStatus(int packetId, int status) {
   }

   @Override
   public final void packetReceived(UDPPacketHeader header, byte[] data) {
   }
}
