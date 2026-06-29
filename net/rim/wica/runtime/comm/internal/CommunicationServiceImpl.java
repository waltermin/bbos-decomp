package net.rim.wica.runtime.comm.internal;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.cldc.io.utility.MalformedURLException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.comm.IncomingRequestListener;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.runtime.util.ProgressMonitor;

public final class CommunicationServiceImpl implements CommunicationService, Serviceable, Startable, RadioStatusListener, ServiceRoutingListener {
   private EventService _eventService;
   private IncomingRequestProcessor _incomingRequestProcessor;
   private ManagementService _managementService;
   private OutgoingRequestProcessor _outgoingRequestProcessor;
   private boolean _running;
   private boolean _inDataCoverage;
   private CommunicationServiceImpl$CommPhoneListener _phoneListener;
   private boolean _serialBypassOn;
   private static String DEFAULT_PUSH_REQUEST_LISTENING_PORT = "http://:1942;DeviceSide=false";
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   final synchronized void waitForNetwork(long timeout) {
      long waitTime = timeout;
      long start = System.currentTimeMillis();

      while (!this.isInCoverage() && this.isRunning()) {
         try {
            this.wait(waitTime);
            waitTime = timeout - (System.currentTimeMillis() - start);
            if (waitTime <= 0) {
               return;
            }
         } finally {
            continue;
         }
      }
   }

   final synchronized void waitForNetwork() {
      while (!this.isInCoverage() && this.isRunning()) {
         try {
            this.wait();
         } finally {
            continue;
         }
      }
   }

   final void logException(String message, Throwable t) {
      Logger.log(this.toString(), message + t.toString(), 3);
   }

   final boolean isRunning() {
      return this._running;
   }

   final String getIPPPUid() {
      return this._managementService.getIPPPUid();
   }

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   final void fireServerStatusChangeEvent(URL serverURL, boolean disabled) {
      if (disabled) {
         this._eventService.dispatchEvent(this, 301, serverURL);
      } else {
         this._eventService.dispatchEvent(this, 303, serverURL);
      }
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.handleNetworkServiceNotification(service);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.handleNetworkServiceNotification(service);
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void radioTurnedOff() {
      synchronized (this) {
         this._inDataCoverage = false;
         this.notifyAll();
      }

      this.fireCoverageEvent(this.isInCoverage());
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      synchronized (this) {
         String transportUid = this.getIPPPUid();
         if (transportUid != null && service.equalsIgnoreCase(transportUid)) {
            this._serialBypassOn = ServiceRouting.getInstance().isServiceRoutable(service, -1);
         }

         this._inDataCoverage = (RadioInfo.getNetworkService() & 4) != 0;
         this.notifyAll();
      }

      this.fireCoverageEvent(this.isInCoverage());
   }

   @Override
   public final void registerIncomingRequestListener(IncomingRequestListener listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      this._incomingRequestProcessor.setIncomingRequestListener(listener);
   }

   @Override
   public final void resendDeferredRequests() {
      this._outgoingRequestProcessor.attemptDeferred();
   }

   @Override
   public final void sendRequest(OutgoingRequest request) {
      if (request == null) {
         throw new NullPointerException();
      }

      this._outgoingRequestProcessor.sendRequest((OutgoingRequestImpl)request);
   }

   @Override
   public final void sendRequest(OutgoingRequest request, ProgressMonitor pm) {
      if (request != null && pm != null) {
         OutgoingRequestImpl outRequest = (OutgoingRequestImpl)request;
         outRequest.setProgressMonitor(pm);
         this._outgoingRequestProcessor.sendRequest(outRequest);
      } else {
         throw new NullPointerException();
      }
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      this._eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      this._managementService = (ManagementService)provider.getService(
         class$net$rim$wica$runtime$management$ManagementService == null
            ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
            : class$net$rim$wica$runtime$management$ManagementService
      );
   }

   @Override
   public final void signalLevel(int level) {
      if (level == -256) {
         synchronized (this) {
            this._inDataCoverage = false;
            this.notifyAll();
         }

         this.fireCoverageEvent(this.isInCoverage());
      }
   }

   @Override
   public final void start() {
      Application.getApplication().addRadioListener(this);
      ServiceRouting.getInstance().addListener(this);
      this._phoneListener = new CommunicationServiceImpl$CommPhoneListener(this, null);
      Phone.addPhoneListener(this._phoneListener);
      this._running = true;
      this._inDataCoverage = (RadioInfo.getNetworkService() & 4) != 0;
      this._eventService.addListener(302, this._outgoingRequestProcessor);
      this._eventService.addListener(100, this._outgoingRequestProcessor);
      Thread outgoingRequestProcessorThread = new Thread(this._outgoingRequestProcessor, "CommOutgoingRequestProcessor");
      outgoingRequestProcessorThread.start();
      Thread incomingRequestProcessorThread = new Thread(this._incomingRequestProcessor, "CommIncomingRequestProcessor");
      incomingRequestProcessorThread.start();
   }

   @Override
   public final void stop() {
      this._running = false;
      this._outgoingRequestProcessor.stop();
      this._incomingRequestProcessor.stop();
      this._eventService.removeListener(302, this._outgoingRequestProcessor);
      this._eventService.removeListener(100, this._outgoingRequestProcessor);
      Application.getApplication().removeRadioListener(this);
      ServiceRouting.getInstance().removeListener(this);
      Phone.removePhoneListener(this._phoneListener);
      synchronized (this) {
         this.notifyAll();
      }
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final synchronized boolean isInCoverage() {
      return this._inDataCoverage || this._serialBypassOn;
   }

   @Override
   public final boolean isDestinationServerPolled(URL url) {
      return this._outgoingRequestProcessor.isDestinationServerPolled(url);
   }

   @Override
   public final OutgoingRequest createOutgoingRequestInstance(URL url) throws MalformedURLException {
      if (url.getHost() == null) {
         throw new MalformedURLException();
      } else {
         return new OutgoingRequestImpl(url);
      }
   }

   @Override
   public final OutgoingRequest createOutgoingRequestInstance(String url) {
      return this.createOutgoingRequestInstance(new URL(url));
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final String toString() {
      return "CommunicationService";
   }

   public CommunicationServiceImpl() {
      this._incomingRequestProcessor = new IncomingRequestProcessor(this, DEFAULT_PUSH_REQUEST_LISTENING_PORT);
      this._outgoingRequestProcessor = new OutgoingRequestProcessor(this);
   }

   private final void handleNetworkServiceNotification(int service) {
      synchronized (this) {
         this._inDataCoverage = (service & 4) != 0;
         this.notifyAll();
      }

      this.fireCoverageEvent(this.isInCoverage());
   }

   private final void fireCoverageEvent(boolean inCoverage) {
      if (inCoverage) {
         this._eventService.dispatchEvent(this, 302, null);
      } else {
         this._eventService.dispatchEvent(this, 300, null);
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
