package net.rim.wica.runtime.core;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.wica.runtime.activation.ActivationService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.persistence.PersistentContentHelper;
import net.rim.wica.runtime.service.Container;
import net.rim.wica.runtime.service.DefaultContainer;

final class RuntimeFramework implements Runtime, PersistentContentListener, GlobalEventListener {
   private Container _baseContainer;
   private Container _container;
   private RuntimeFramework$RuntimeApplication _application;
   private boolean _starting;
   private boolean _stopping;
   private boolean _available;
   private boolean _scheduledForStart;
   private Object _startLock = new Object();
   private Object _stopLock = new Object();
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;
   static Class class$net$rim$wica$runtime$activation$ActivationService;
   static Class class$net$rim$wica$runtime$activation$internal$ActivationServiceImpl;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;
   static Class class$net$rim$wica$runtime$comm$internal$CommunicationServiceImpl;
   static Class class$net$rim$wica$runtime$security$SecurityService;
   static Class class$net$rim$wica$runtime$security$internal$SecurityServiceImpl;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$management$internal$ManagementServiceImpl;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$messaging$internal$MessagingServiceImpl;
   static Class class$net$rim$wica$runtime$lifecycle$internal$LifecycleServiceImpl;
   static Class class$net$rim$wica$runtime$provisioning$ProvisioningService;
   static Class class$net$rim$wica$runtime$provisioning$internal$DefaultProvisioningService;
   static Class class$net$rim$wica$runtime$authentication$internal$AuthenticationService;
   static Class class$net$rim$wica$runtime$persistence$PersistentContentHelper;
   static Class class$net$rim$wica$runtime$persistence$internal$PersistentContentHelperImpl;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$event$internal$EventServiceImpl;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$persistence$internal$PersistenceServiceImpl;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void stop() {
      synchronized (this._stopLock) {
         if (this._stopping || !this._available) {
            return;
         }

         this._stopping = true;
      }

      synchronized (this) {
         boolean var9 = false /* VF: Semaphore variable */;

         label111: {
            try {
               label86:
               try {
                  var9 = true;
                  this.registerPCListener(true);
                  this._available = false;
                  this._container.stop();
                  var9 = false;
                  break label111;
               } catch (Throwable var14) {
                  Logger.log(((StringBuffer)(new Object("Error shutting down runtime framework: "))).append(e.toString()).toString(), 2);
                  var9 = false;
                  break label86;
               }
            } finally {
               if (var9) {
                  this._stopping = false;
               }
            }

            this._stopping = false;
            return;
         }

         this._stopping = false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void start() {
      synchronized (this._startLock) {
         if (this._starting || this._available) {
            return;
         }

         if (this.disallowRuntime()) {
            return;
         }

         Object ticket = PersistentContent.getTicket();
         if (ticket == null) {
            return;
         }

         this._starting = true;
      }

      synchronized (this) {
         boolean var10 = false /* VF: Semaphore variable */;

         label181: {
            try {
               label145:
               try {
                  var10 = true;
                  this._container = new DefaultContainer(this._baseContainer);
                  this.registerServices();
                  this._container
                     .getService(
                        class$net$rim$wica$runtime$management$ManagementService == null
                           ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
                           : class$net$rim$wica$runtime$management$ManagementService
                     );
                  this._container
                     .getService(
                        class$net$rim$wica$runtime$provisioning$ProvisioningService == null
                           ? (class$net$rim$wica$runtime$provisioning$ProvisioningService = class$("net.rim.wica.runtime.provisioning.ProvisioningService"))
                           : class$net$rim$wica$runtime$provisioning$ProvisioningService
                     );
                  this._container
                     .getService(
                        class$net$rim$wica$runtime$activation$ActivationService == null
                           ? (class$net$rim$wica$runtime$activation$ActivationService = class$("net.rim.wica.runtime.activation.ActivationService"))
                           : class$net$rim$wica$runtime$activation$ActivationService
                     );
                  this._container
                     .getService(
                        class$net$rim$wica$runtime$authentication$internal$AuthenticationService == null
                           ? (
                              class$net$rim$wica$runtime$authentication$internal$AuthenticationService = class$(
                                 "net.rim.wica.runtime.authentication.internal.AuthenticationService"
                              )
                           )
                           : class$net$rim$wica$runtime$authentication$internal$AuthenticationService
                     );
                  this._available = true;
                  this.registerPCListener(false);
                  var10 = false;
                  break label181;
               } catch (Throwable var15) {
                  Logger.log(((StringBuffer)(new Object("Unable to start runtime framework\nError: "))).append(e.toString()).toString(), 2);
                  var10 = false;
                  break label145;
               }
            } finally {
               if (var10) {
                  this._starting = false;
               }
            }

            this._starting = false;
            return;
         }

         this._starting = false;
      }
   }

   final void dispatchEvents() {
      this._application.enterEventDispatcher();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final boolean loadApplication(String applicationUri) {
      if (applicationUri == null) {
         throw new Object("applicationUri");
      }

      if (this._available) {
         try {
            LifecycleService service = (LifecycleService)this._container
               .getService(
                  class$net$rim$wica$runtime$lifecycle$LifecycleService == null
                     ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
                     : class$net$rim$wica$runtime$lifecycle$LifecycleService
               );
            service.loadWiclet(applicationUri);
            return true;
         } catch (Throwable var4) {
            Logger.log(
               ((StringBuffer)(new Object("Unable to loading application: "))).append(applicationUri).append("\nError: ").append(e.toString()).toString(), 2
            );
            return false;
         }
      } else {
         return false;
      }
   }

   final void displayActivationScreen() {
      if (this._available) {
         ActivationService service = (ActivationService)this._container
            .getService(
               class$net$rim$wica$runtime$activation$ActivationService == null
                  ? (class$net$rim$wica$runtime$activation$ActivationService = class$("net.rim.wica.runtime.activation.ActivationService"))
                  : class$net$rim$wica$runtime$activation$ActivationService
            );
         service.pushActivationScreen();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean startApplication(String uri) {
      if (uri == null) {
         throw new Object("uri");
      }

      if (this._available) {
         try {
            LifecycleService service = (LifecycleService)this._container
               .getService(
                  class$net$rim$wica$runtime$lifecycle$LifecycleService == null
                     ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
                     : class$net$rim$wica$runtime$lifecycle$LifecycleService
               );
            service.startWiclet(uri);
            return true;
         } catch (Throwable var4) {
            Logger.log(((StringBuffer)(new Object("Unable to start application: "))).append(uri).append("\nError: ").append(e.toString()).toString(), 2);
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean isAvailable() {
      return this._available;
   }

   @Override
   public final Object getService(Class serviceInterface) {
      return this._available ? this._container.getService(serviceInterface) : null;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         if (this.disallowRuntime()) {
            this.stop();
            return;
         }

         this.start();
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (!this._available && !this._scheduledForStart && state == 1) {
         this._scheduledForStart = true;
         this._application.invokeLater(new RuntimeFramework$1(this));
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void baseStart() {
      try {
         this._baseContainer = new DefaultContainer();
         this._baseContainer
            .registerComponent(
               class$net$rim$wica$runtime$persistence$PersistentContentHelper == null
                  ? (class$net$rim$wica$runtime$persistence$PersistentContentHelper = class$("net.rim.wica.runtime.persistence.PersistentContentHelper"))
                  : class$net$rim$wica$runtime$persistence$PersistentContentHelper,
               class$net$rim$wica$runtime$persistence$internal$PersistentContentHelperImpl == null
                  ? (
                     class$net$rim$wica$runtime$persistence$internal$PersistentContentHelperImpl = class$(
                        "net.rim.wica.runtime.persistence.internal.PersistentContentHelperImpl"
                     )
                  )
                  : class$net$rim$wica$runtime$persistence$internal$PersistentContentHelperImpl
            );
         this.registerPCListener(true);
         this._baseContainer
            .registerComponent(
               class$net$rim$wica$runtime$event$EventService == null
                  ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
                  : class$net$rim$wica$runtime$event$EventService,
               class$net$rim$wica$runtime$event$internal$EventServiceImpl == null
                  ? (class$net$rim$wica$runtime$event$internal$EventServiceImpl = class$("net.rim.wica.runtime.event.internal.EventServiceImpl"))
                  : class$net$rim$wica$runtime$event$internal$EventServiceImpl
            );
         this._baseContainer
            .registerComponent(
               class$net$rim$wica$runtime$persistence$PersistenceService == null
                  ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
                  : class$net$rim$wica$runtime$persistence$PersistenceService,
               class$net$rim$wica$runtime$persistence$internal$PersistenceServiceImpl == null
                  ? (
                     class$net$rim$wica$runtime$persistence$internal$PersistenceServiceImpl = class$(
                        "net.rim.wica.runtime.persistence.internal.PersistenceServiceImpl"
                     )
                  )
                  : class$net$rim$wica$runtime$persistence$internal$PersistenceServiceImpl
            );
      } catch (Throwable var3) {
         Logger.log(((StringBuffer)(new Object("Unable to start runtime framework base services\nError: "))).append(e.toString()).toString(), 2);
         return;
      }
   }

   static final RuntimeFramework getFramework() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (RuntimeFramework)ar.getOrWaitFor(3899999886366589579L);
   }

   RuntimeFramework() {
      this._application = new RuntimeFramework$RuntimeApplication();
      this._application.addGlobalEventListener(this);
      this.baseStart();
      this.start();
      ApplicationRegistry.getApplicationRegistry().put(3899999886366589579L, this);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void registerServices() {
      try {
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$activation$ActivationService == null
                  ? (class$net$rim$wica$runtime$activation$ActivationService = class$("net.rim.wica.runtime.activation.ActivationService"))
                  : class$net$rim$wica$runtime$activation$ActivationService,
               class$net$rim$wica$runtime$activation$internal$ActivationServiceImpl == null
                  ? (
                     class$net$rim$wica$runtime$activation$internal$ActivationServiceImpl = class$(
                        "net.rim.wica.runtime.activation.internal.ActivationServiceImpl"
                     )
                  )
                  : class$net$rim$wica$runtime$activation$internal$ActivationServiceImpl
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$comm$CommunicationService == null
                  ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
                  : class$net$rim$wica$runtime$comm$CommunicationService,
               class$net$rim$wica$runtime$comm$internal$CommunicationServiceImpl == null
                  ? (class$net$rim$wica$runtime$comm$internal$CommunicationServiceImpl = class$("net.rim.wica.runtime.comm.internal.CommunicationServiceImpl"))
                  : class$net$rim$wica$runtime$comm$internal$CommunicationServiceImpl
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$security$SecurityService == null
                  ? (class$net$rim$wica$runtime$security$SecurityService = class$("net.rim.wica.runtime.security.SecurityService"))
                  : class$net$rim$wica$runtime$security$SecurityService,
               class$net$rim$wica$runtime$security$internal$SecurityServiceImpl == null
                  ? (class$net$rim$wica$runtime$security$internal$SecurityServiceImpl = class$("net.rim.wica.runtime.security.internal.SecurityServiceImpl"))
                  : class$net$rim$wica$runtime$security$internal$SecurityServiceImpl
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$management$ManagementService == null
                  ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
                  : class$net$rim$wica$runtime$management$ManagementService,
               class$net$rim$wica$runtime$management$internal$ManagementServiceImpl == null
                  ? (
                     class$net$rim$wica$runtime$management$internal$ManagementServiceImpl = class$(
                        "net.rim.wica.runtime.management.internal.ManagementServiceImpl"
                     )
                  )
                  : class$net$rim$wica$runtime$management$internal$ManagementServiceImpl
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$messaging$MessagingService == null
                  ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
                  : class$net$rim$wica$runtime$messaging$MessagingService,
               class$net$rim$wica$runtime$messaging$internal$MessagingServiceImpl == null
                  ? (
                     class$net$rim$wica$runtime$messaging$internal$MessagingServiceImpl = class$("net.rim.wica.runtime.messaging.internal.MessagingServiceImpl")
                  )
                  : class$net$rim$wica$runtime$messaging$internal$MessagingServiceImpl
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$lifecycle$LifecycleService == null
                  ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
                  : class$net$rim$wica$runtime$lifecycle$LifecycleService,
               class$net$rim$wica$runtime$lifecycle$internal$LifecycleServiceImpl == null
                  ? (
                     class$net$rim$wica$runtime$lifecycle$internal$LifecycleServiceImpl = class$("net.rim.wica.runtime.lifecycle.internal.LifecycleServiceImpl")
                  )
                  : class$net$rim$wica$runtime$lifecycle$internal$LifecycleServiceImpl
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$provisioning$ProvisioningService == null
                  ? (class$net$rim$wica$runtime$provisioning$ProvisioningService = class$("net.rim.wica.runtime.provisioning.ProvisioningService"))
                  : class$net$rim$wica$runtime$provisioning$ProvisioningService,
               class$net$rim$wica$runtime$provisioning$internal$DefaultProvisioningService == null
                  ? (
                     class$net$rim$wica$runtime$provisioning$internal$DefaultProvisioningService = class$(
                        "net.rim.wica.runtime.provisioning.internal.DefaultProvisioningService"
                     )
                  )
                  : class$net$rim$wica$runtime$provisioning$internal$DefaultProvisioningService
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$authentication$internal$AuthenticationService == null
                  ? (
                     class$net$rim$wica$runtime$authentication$internal$AuthenticationService = class$(
                        "net.rim.wica.runtime.authentication.internal.AuthenticationService"
                     )
                  )
                  : class$net$rim$wica$runtime$authentication$internal$AuthenticationService,
               class$net$rim$wica$runtime$authentication$internal$AuthenticationService == null
                  ? (
                     class$net$rim$wica$runtime$authentication$internal$AuthenticationService = class$(
                        "net.rim.wica.runtime.authentication.internal.AuthenticationService"
                     )
                  )
                  : class$net$rim$wica$runtime$authentication$internal$AuthenticationService
            );
      } catch (Throwable var3) {
         throw new Object(e.getMessage());
      }
   }

   private final boolean disallowRuntime() {
      return ITPolicy.getBoolean(44, 1, false);
   }

   private final void registerPCListener(boolean addListener) {
      PersistentContentHelper persistentContent = (PersistentContentHelper)this._baseContainer
         .getService(
            class$net$rim$wica$runtime$persistence$PersistentContentHelper == null
               ? (class$net$rim$wica$runtime$persistence$PersistentContentHelper = class$("net.rim.wica.runtime.persistence.PersistentContentHelper"))
               : class$net$rim$wica$runtime$persistence$PersistentContentHelper
         );
      if (addListener) {
         persistentContent.addListener(this);
      } else {
         persistentContent.removeListener(this);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
