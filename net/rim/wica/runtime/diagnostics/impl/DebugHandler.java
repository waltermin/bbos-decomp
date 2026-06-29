package net.rim.wica.runtime.diagnostics.impl;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.wica.common.debug.protocol.messages.request.IApplicationCommandRequestMessage;
import net.rim.wica.common.debug.protocol.messages.request.IRequestMessage;
import net.rim.wica.common.debug.protocol.messages.response.IResponseMessage;
import net.rim.wica.common.debug.protocol.messages.response.ResponseMessageFactory;
import net.rim.wica.common.debug.protocol.messages.targetevents.ITargetEventMessage;
import net.rim.wica.common.debug.protocol.messages.targetevents.TargetEventMessageFactory;
import net.rim.wica.common.debug.session.IRequestMessageHandler;
import net.rim.wica.common.debug.session.TargetSessionManager;
import net.rim.wica.runtime.core.RuntimeServices;
import net.rim.wica.runtime.diagnostics.Debugger;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.lifecycle.WicletUninstalledEvent;
import net.rim.wica.runtime.lifecycle.WicletUpgradeEvent;
import net.rim.wica.runtime.metadata.WicletContext;

final class DebugHandler implements Debugger, EventListener, IRequestMessageHandler {
   private EventService _eventService;
   private LifecycleService _lifecycleService;
   private TargetSessionManager _manager;
   private boolean _attached;
   private LongHashtable _eventListeners;
   private static final int[] SYSTEM_EVENTS = new int[]{
      200,
      201,
      202,
      802,
      803,
      100664320,
      526977907,
      1929773292,
      -333485813,
      15098345,
      319291400,
      671613046,
      594288411,
      34171223,
      1091043569,
      1850041454,
      1969583473,
      16534885,
      1886404872,
      1738588877
   };
   private static final int[] APPLICATION_EVENTS = new int[]{901, 900, -804651003, 200, 201, 202, 802, 803};
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;

   DebugHandler() {
      this._eventService = (EventService)RuntimeServices.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      this._lifecycleService = (LifecycleService)RuntimeServices.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
      ApplicationRegistry.getApplicationRegistry().put(-8317686990539473415L, this);
      this._eventListeners = new LongHashtable(5);
   }

   final synchronized void setAttached(boolean attached, TargetSessionManager manager) {
      if (attached && !this._attached) {
         this._attached = true;
         this._manager = manager;
         this._eventService.addListener(SYSTEM_EVENTS, this);
      } else {
         if (!attached && this._attached) {
            this._attached = false;
            this._eventService.removeListener(this);
            this.clearAppEventListeners();
         }
      }
   }

   @Override
   public final synchronized boolean isAttached() {
      return this._attached;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      ITargetEventMessage messageBody = null;
      long applicationId = -1;
      switch (event) {
         case 200:
            messageBody = TargetEventMessageFactory.createApplicationTargetEventMessage(((WicletContext)data).getId());
            this._manager.sendTargetEventMessage(0, messageBody);
            return;
         case 201:
            WicletUpgradeEvent upgradeEvent = (WicletUpgradeEvent)data;
            messageBody = TargetEventMessageFactory.createApplicationUpgradedTargetEventMessage(
               upgradeEvent.getInstalledWicletId(), upgradeEvent.getWiclet().getId()
            );
            this._manager.sendTargetEventMessage(2, messageBody);
            return;
         case 202:
            messageBody = TargetEventMessageFactory.createApplicationTargetEventMessage(((WicletUninstalledEvent)data).getWicletId());
            this._manager.sendTargetEventMessage(1, messageBody);
            return;
         case 802:
            applicationId = ((Wiclet)data).getId();
            this.addAppEventListener(applicationId);
            messageBody = TargetEventMessageFactory.createApplicationTargetEventMessage(applicationId);
            this._manager.sendTargetEventMessage(3, messageBody);
            return;
         case 803:
            applicationId = ((Wiclet)data).getId();
            this.removeAppEventListener(applicationId);
            messageBody = TargetEventMessageFactory.createApplicationTargetEventMessage(applicationId);
            this._manager.sendTargetEventMessage(4, messageBody);
      }
   }

   @Override
   public final synchronized void logConsole(long generatorId, String message) {
      if (this._attached) {
         ITargetEventMessage messageBody = TargetEventMessageFactory.createTraceTargetEventMessage(generatorId, message);
         this._manager.sendTargetEventMessage(5, messageBody);
      }
   }

   @Override
   public final void handleRequestMessage(int messageType, int messageId, IRequestMessage request) {
      switch (messageType) {
         case 3:
         default:
            this.handleStartApplicationRequest(messageId, (IApplicationCommandRequestMessage)request);
            return;
         case 4:
            this.handleStopApplicationRequest(messageId, (IApplicationCommandRequestMessage)request);
            return;
         case 5:
            this.handleApplicationStatusRequest(messageId, (IApplicationCommandRequestMessage)request);
         case 2:
      }
   }

   private final void addAppEventListener(long applicationId) {
      DebugHandler$AppEventListener listener = new DebugHandler$AppEventListener(this, applicationId);
      this._eventListeners.put(applicationId, listener);
      EventService eventService = this._lifecycleService.getWiclet(applicationId).getEventService();
      eventService.addListener(APPLICATION_EVENTS, listener);
   }

   private final void clearAppEventListeners() {
      Enumeration e = this._eventListeners.elements();

      while (e.hasMoreElements()) {
         DebugHandler$AppEventListener listener = (DebugHandler$AppEventListener)e.nextElement();
         EventService eventService = this._lifecycleService.getWiclet(listener._applicationId).getEventService();
         eventService.removeListener(listener);
      }

      this._eventListeners.clear();
   }

   private final void removeAppEventListener(long applicationId) {
      DebugHandler$AppEventListener listener = (DebugHandler$AppEventListener)this._eventListeners.get(applicationId);
      if (listener != null) {
         EventService eventService = this._lifecycleService.getWiclet(applicationId).getEventService();
         eventService.removeListener(listener);
      }

      this._eventListeners.remove(applicationId);
   }

   private final void handleStartApplicationRequest(int messageId, IApplicationCommandRequestMessage message) {
      long applicationId = message.getApplicationId();

      label20:
      try {
         this._lifecycleService.startWiclet(message.getApplicationId());
      } finally {
         break label20;
      }

      IResponseMessage response = ResponseMessageFactory.createApplicationCommandResponseMessage(applicationId);
      this._manager.sendResponseMessage(3, messageId, response);
   }

   private final void handleStopApplicationRequest(int messageId, IApplicationCommandRequestMessage message) {
      long applicationId = message.getApplicationId();

      label20:
      try {
         this._lifecycleService.stopApplication(message.getApplicationId());
      } finally {
         break label20;
      }

      IResponseMessage response = ResponseMessageFactory.createApplicationCommandResponseMessage(applicationId);
      this._manager.sendResponseMessage(4, messageId, response);
   }

   private final void handleApplicationStatusRequest(int messageId, IApplicationCommandRequestMessage message) {
      long applicationId = message.getApplicationId();
      boolean running = false;

      label20:
      try {
         Wiclet application = this._lifecycleService.getWiclet(applicationId);
         running = application.isRunning();
      } finally {
         break label20;
      }

      IResponseMessage response = ResponseMessageFactory.createApplicationStatusResponseMessage(applicationId, running);
      this._manager.sendResponseMessage(5, messageId, response);
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
