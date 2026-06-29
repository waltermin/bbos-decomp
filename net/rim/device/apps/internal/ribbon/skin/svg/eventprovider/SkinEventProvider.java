package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.mediaengine.MediaListener;

public class SkinEventProvider implements MediaService, MediaListener {
   private EventQueue _eventQueue = new EventQueue();
   private MessagesEventProvider _messagesEventProvider = new MessagesEventProvider(this);
   private PhoneEventProvider _phoneEventProvider = new PhoneEventProvider(this);
   private WlanEventProvider _wlanEventProvider = new WlanEventProvider(this);
   private ModelInteractorImpl _model;
   private EventEngine _engine;
   public static final String ID = "SkinEventProvider";

   void dispatchEvent(String eventId) {
      if (this._engine != null && this._engine.isRunning()) {
         int handle = this._model.getHandle(eventId);
         if (handle != -1) {
            this._model.trigger(107, handle, null);
            return;
         }
      } else {
         this._eventQueue.queueEvent(eventId);
      }
   }

   public void registerEventPair(String eventId1, String eventId2) {
      this._eventQueue.registerEventPair(eventId1, eventId2);
   }

   public void onExposed() {
      this._messagesEventProvider.onExposed();
      this._phoneEventProvider.onExposed();
   }

   public void onObscured() {
      this._messagesEventProvider.onObscured();
      this._phoneEventProvider.onObscured();
   }

   public void onVisibilityChange(boolean visible) {
      this._messagesEventProvider.onVisibilityChange(visible);
      this._phoneEventProvider.onVisibilityChange(visible);
   }

   @Override
   public void dispose() {
   }

   @Override
   public Object getMedia() {
      return this._model;
   }

   @Override
   public void setMedia(Object media) {
      if (media instanceof ModelInteractorImpl) {
         this._model = (ModelInteractorImpl)media;
         String[] eventIds = this._model.getCustomEventIds();

         for (int i = 0; i < eventIds.length; i++) {
            this.provideEvent(eventIds[i]);
         }

         this.dispatchInitEvents();
      }
   }

   @Override
   public void setServices(MediaServices services) {
      if (services != null) {
         this._engine = services.getEngine();
         if (this._engine != null) {
            this._engine.addListener(868433339, this);
         }
      }
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (this._model != null) {
         this._eventQueue.triggerEvents(this._model);
      }
   }

   private void provideEvent(String eventId) {
      if (this._wlanEventProvider.isEventSupported(eventId)) {
         this._wlanEventProvider.provideEvent(eventId);
      } else if (this._messagesEventProvider.isEventSupported(eventId)) {
         this._messagesEventProvider.provideEvent(eventId);
      } else {
         if (this._phoneEventProvider.isEventSupported(eventId)) {
            this._phoneEventProvider.provideEvent(eventId);
         }
      }
   }

   static void initialize() {
      MessagesEventProvider.initialize();
      PhoneEventProvider.initialize();
      WlanEventProvider.initialize();
   }

   SkinEventProvider() {
   }

   private void dispatchInitEvents() {
      this._wlanEventProvider.dispatchInitEvents();
   }
}
