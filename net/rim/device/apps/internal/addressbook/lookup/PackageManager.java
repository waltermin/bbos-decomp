package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.i18n.UnicodeServiceUtilities;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      ServiceBook.getSB().registerCIDAsSingleton("ALP");
      ALPTransmissionService alpts = new ALPTransmissionService();
      TransmissionServiceManager.register(alpts);
      alpts.setDefaultTransmissionStatusListener(ALPConfiguration.getManager());
      ServiceRecord serviceRec = alpts.getOutgoingServiceRecord();
      if (serviceRec != null) {
         String uid = serviceRec.getUid();
         ALPConfiguration.setServiceUID(uid);
      }

      try {
         ConverterDescriptor descriptor = new ALPConverterDescriptor();
         SerializationManager.registerConverter("net.rim.AddressLookupProtocol.Request", descriptor);
         SerializationManager.registerConverter("net.rim.AddressLookupProtocol.Result", descriptor);
         EventLogger.register(-4453883819751179668L, "net.rim.addressbook.lookup", 2);
         OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
         if (oac != null) {
            oac.addDeviceCapabilities((byte)10, UnicodeServiceUtilities.getSupportedEncodings());
         }
      } finally {
         throw new Object();
      }
   }
}
