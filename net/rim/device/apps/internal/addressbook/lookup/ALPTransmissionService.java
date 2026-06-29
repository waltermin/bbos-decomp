package net.rim.device.apps.internal.addressbook.lookup;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.PacketReceiver;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;

public class ALPTransmissionService extends AbstractTransmissionService implements PacketReceiver {
   ALPTransmissionService() {
      super(-8892319056465090102L, -4453883819751179668L);
      ALPConfiguration.activate(super._serviceRecord);
   }

   public static ALPTransmissionService getInstance() {
      TransmissionService transmissionService = TransmissionServiceManager.get(-8892319056465090102L);
      return !(transmissionService instanceof ALPTransmissionService) ? null : (ALPTransmissionService)transmissionService;
   }

   @Override
   protected ServiceRecord initServiceRecord() {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("ALP");
      return records.length > 0 ? records[0] : null;
   }

   @Override
   protected void serviceRecordChanged(ServiceRecord targetSR, ServiceRecord oldServiceRecord) {
      if (targetSR != null) {
         String cid = targetSR.getCid();
         if (StringUtilities.strEqualIgnoreCase(cid, "ALP")) {
            super._serviceRecord = this.initServiceRecord();
            ALPConfiguration.activate(super._serviceRecord);
            if (super._serviceRecord != null) {
               String uid = super._serviceRecord.getUid();
               ALPConfiguration.setServiceUID(uid);
            }
         }
      }
   }

   @Override
   protected DatagramConnection createConnection(ServiceRecord serviceRecord) {
      if (serviceRecord == null) {
         throw new IllegalArgumentException("Null Service Record");
      } else {
         return (DatagramConnection)Connector.open("gme:ALP/" + serviceRecord.getUid());
      }
   }

   @Override
   public int getStatus() {
      return super._serviceRecord != null ? 3 : 2;
   }

   void cancel(Datagram dg) {
      DatagramConnection dc = this.getConnection();
      if (dc instanceof DatagramConnectionBase) {
         DatagramConnectionBase dcb = (DatagramConnectionBase)dc;
         dcb.cancel(dg);
      }
   }

   @Override
   public void receivePacket(DataBuffer packetDataBuffer, Object contextObject) {
      if (this.verifyGMEDatagram(packetDataBuffer, true) != null) {
         Object convertContext = this.getContext();
         Converter converter = SerializationManager.getConverter("net.rim.AddressLookupProtocol.Result", convertContext);
         if (converter != null) {
            try {
               Result result = (Result)converter.convert(packetDataBuffer, convertContext);
               ALPManager manager = ALPConfiguration.getManager();
               manager.incomingResult(result);
               if (super._tLogger != null) {
                  super._tLogger.bytesReceived(this, 1, null, packetDataBuffer.getLength(), packetDataBuffer.getArray());
                  return;
               }
            } finally {
               return;
            }
         }
      }
   }
}
