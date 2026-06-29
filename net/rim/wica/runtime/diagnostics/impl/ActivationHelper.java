package net.rim.wica.runtime.diagnostics.impl;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.wica.runtime.core.RuntimeServices;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.management.RuntimeInfo;

final class ActivationHelper implements EventListener {
   private EventService _eventService;
   private ManagementService _managementService;
   private boolean _activated = false;
   private static final String IPPP_SERVICE_CID = "IPPP";
   private static final String IPPP_SIM_SB_NAME = "MDS Transport (Sim)";
   private static final int[] EVENTS = new int[]{100, -804651006, 901, 900};
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   ActivationHelper() {
      this._eventService = (EventService)RuntimeServices.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      this._managementService = (ManagementService)RuntimeServices.getService(
         class$net$rim$wica$runtime$management$ManagementService == null
            ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
            : class$net$rim$wica$runtime$management$ManagementService
      );
      this._eventService.addListener(EVENTS, this);
   }

   final boolean activate(String activationUrl) {
      if (this.checkActivation()) {
         this._activated = true;
      } else {
         ServiceRecord ipppRecord = this.getServiceRecord();
         if (ipppRecord != null) {
            AGInfo serverInfo = new AGInfo();
            serverInfo.setAGCompactMsgURL(AGInfo.createAGCompactMsgURL(activationUrl));
            serverInfo.setAGRegURL(activationUrl);
            serverInfo.setIPPP_UID(ipppRecord.getUid());
            this._managementService.register(serverInfo);
            synchronized (this) {
               label35:
               try {
                  this.wait();
               } finally {
                  break label35;
               }
            }

            this._eventService.removeListener(EVENTS, this);
         }
      }

      return this._activated;
   }

   private final boolean checkActivation() {
      RuntimeInfo runtimeInfo = this._managementService.getRuntimeInfo();
      return runtimeInfo != null && runtimeInfo.isRegistered();
   }

   private final ServiceRecord getServiceRecord() {
      ServiceRecord record = null;
      if (DeviceInfo.isSimulator()) {
         ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("IPPP");
         if (records.length > 0) {
            record = records[0];

            for (int i = 0; i < records.length; i++) {
               if ("MDS Transport (Sim)".equals(records[i].getName())) {
                  record = records[i];
               }
            }
         }
      }

      return record;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 100:
            if (eventParam == 1) {
               synchronized (this) {
                  this._activated = true;
                  this.notifyAll();
                  return;
               }
            } else if (eventParam == 11) {
               synchronized (this) {
                  this._activated = false;
                  this.notifyAll();
                  return;
               }
            }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
