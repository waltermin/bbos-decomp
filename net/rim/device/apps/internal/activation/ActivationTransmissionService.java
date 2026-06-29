package net.rim.device.apps.internal.activation;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.Packet;
import net.rim.device.apps.api.transmission.PacketReceiver;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.internal.firewall.Firewall;

final class ActivationTransmissionService extends AbstractTransmissionService implements PacketReceiver {
   private boolean _debugMode = true;
   private OTAKeyGenProtocolConverter _converter;
   private ActivationServiceImpl _activationService;

   ActivationTransmissionService(ActivationServiceImpl activationService) {
      super(-7467774798685319400L, -5915434835955743234L, false);
      this._activationService = activationService;
      this.init();
   }

   private final void init() {
      this._converter = new OTAKeyGenProtocolConverter();

      try {
         SerializationManager.registerConverter("net.rim.OTAKeyGenProtocol", this._converter);
      } finally {
         return;
      }
   }

   @Override
   protected final ServiceRecord initServiceRecord() {
      return null;
   }

   @Override
   protected final DatagramConnection createConnection(ServiceRecord serviceRecord) {
      return (DatagramConnection)Connector.open("gme:OTAKEYGEN");
   }

   protected final void setNewSenderConnection(DatagramConnection connection) {
      this.setSendingConnection(connection);
   }

   @Override
   public final void receivePacket(DataBuffer packetDataBuffer, Object contextObject) {
      GMEDatagram gmeDatagram = null;
      if (!(packetDataBuffer instanceof GMEDatagram)) {
         Firewall.getInstance().incrementBlockedCount((byte)-4);
         this.logEvent(1380144725, 2, packetDataBuffer.getArray());
      } else {
         gmeDatagram = (GMEDatagram)packetDataBuffer;
         if (gmeDatagram.isFromPeer() && !this._debugMode) {
            Firewall.getInstance().incrementBlockedCount((byte)-4);
            EventLogger.logEvent(-5915434835955743234L, 1112558420, 2);
         } else {
            this.processPacket(gmeDatagram);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processPacket(GMEDatagram gmeDatagram) {
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         OTAKeyGenEvent e = null;
         DataBuffer packetDataBuffer = gmeDatagram;
         int position = packetDataBuffer.getArrayPosition();
         int length = packetDataBuffer.getArrayLength();
         byte[] packet = new byte[length - position];
         packetDataBuffer.read(packet);
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            var13 = true;
            e = (OTAKeyGenEvent)this._converter.convert(packet, null);
            this._activationService.processEvent(e);
            if (super._tLogger != null) {
               super._tLogger.bytesReceived(this, 1, null, packet.length, packet);
               var10 = false;
               var13 = false;
            } else {
               var10 = false;
               var13 = false;
            }
         } finally {
            if (var13) {
               this.logEvent(1112558420, 2, packetDataBuffer.getArray());
               if (e != null) {
                  this._activationService.abortTransaction(e._transactionId, e._abortReason, true, null);
                  return;
               }

               var10 = false;
               return;
            }
         }
      } finally {
         if (var10) {
            this.logEvent(1112558420, 2, null);
            return;
         }
      }
   }

   @Override
   public final int getStatus() {
      return 3;
   }

   @Override
   protected final void transmitPacket(Packet packet, Object contextUsedToFindConverterAndSend) {
      this.logEvent(1397050948, 4, packet.getPayload());
      super.transmitPacket(packet, contextUsedToFindConverterAndSend);
   }

   private final void logEvent(int eventId, int level, byte[] packet) {
      EventLogger.logEvent(-5915434835955743234L, eventId, level);
      if (packet != null) {
         EventLogger.logEvent(1200380696048604626L, packet, level == 2 ? level : 5);
      }
   }
}
