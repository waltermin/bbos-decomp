package net.rim.wica.runtime.messaging.internal;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.transport.VersionProvider;
import net.rim.wica.transport.VersionProviderException;
import net.rim.wica.transport.message.TransportMessageFactory;

public class VersionProviderImpl implements VersionProvider, EventListener {
   private TransportMessageFactory _factory;
   private ManagementService _management;
   private TransportMsgHelper _helper = new TransportMsgHelper();
   private long _defaultAgId;
   private int _transportVersion;
   private int _securityVersion;
   static Class class$net$rim$wica$transport$message$TransportMessageFactory;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$event$EventService;

   void initialize(ServiceProvider provider) {
      this._factory = (TransportMessageFactory)provider.getService(
         class$net$rim$wica$transport$message$TransportMessageFactory == null
            ? (class$net$rim$wica$transport$message$TransportMessageFactory = class$("net.rim.wica.transport.message.TransportMessageFactory"))
            : class$net$rim$wica$transport$message$TransportMessageFactory
      );
      this._management = (ManagementService)provider.getService(
         class$net$rim$wica$runtime$management$ManagementService == null
            ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
            : class$net$rim$wica$runtime$management$ManagementService
      );
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      eventService.addListener(new int[]{105, 100, -804651006, 106, 102, -804651004, 129, 133}, this);
      this.setAgVersions();
   }

   public synchronized boolean hasVersions(long agId) {
      return agId != 0 && agId == this._defaultAgId && this._transportVersion > 0 && this._securityVersion > 0;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   synchronized Message createMessage() {
      try {
         this._factory.handleMessage(this._defaultAgId, this._helper);
      } catch (Throwable var3) {
         InternalLogger.logError("MDS Runtime Messaging", null, t, null);
         throw new RuntimeException("Message creation failed for MDS Services " + this._defaultAgId);
      }

      return this._helper.getMessage();
   }

   @Override
   public synchronized int getSecurityVersion(long id) throws VersionProviderException {
      if (id == this._defaultAgId && id != 0) {
         return this._securityVersion;
      } else {
         throw new VersionProviderException("MDS Services " + id + " not found");
      }
   }

   @Override
   public synchronized int getTransportMessageVersion(long id) throws VersionProviderException {
      if (id == this._defaultAgId && id != 0) {
         return this._transportVersion;
      } else {
         throw new VersionProviderException("MDS Services " + id + " not found");
      }
   }

   @Override
   public void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 100:
            if (eventParam == 2 || eventParam == 11) {
               this.resetAgVersions();
               return;
            }
            break;
         case 105:
            this.setAgVersions();
            return;
         default:
            InternalLogger.logUnkownEvent(this, event);
      }
   }

   private synchronized void setAgVersions() {
      AGInfo ag = this._management.getRuntimeInfo().getDefaultAGInfo();
      if (ag != null) {
         this._defaultAgId = ag.getAgID();
         this._securityVersion = ag.getSecurityVersion();
         this._transportVersion = ag.getTransportVersion();
      }
   }

   private synchronized void resetAgVersions() {
      this._defaultAgId = 0;
      this._securityVersion = 0;
      this._transportVersion = 0;
   }

   VersionProviderImpl() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
