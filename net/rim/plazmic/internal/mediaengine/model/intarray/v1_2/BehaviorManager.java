package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.service.EventResolver;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.mediaengine.MediaListener;

public class BehaviorManager implements MediaService, MediaListener {
   private MediaServices _services;
   private AnimationModel _media;
   private ModelInteractorImpl _model;
   private EventEngine _engine;
   private BehaviorManager$LogicBuilder _logicBuilder = new BehaviorManager$LogicBuilder();
   private long _lastTime;
   private boolean _updateRequested;
   private AnimationHandler _animationHandler;
   private MediaObjectHandler _mediaObjectHandler;
   public static final String ID = "Behaviors";

   @Override
   public void setServices(MediaServices s) {
      if (this._services != null) {
         throw new Object("setServices has to be called only once");
      }

      if (s == null) {
         throw new Object("Provided MediaServices cannot be null");
      }

      this._services = s;
      this._services.registerService("Behaviors", this);
      this._engine = this._services.getEngine();
      EventResolver resolver = (EventResolver)this._services.getService("EventResolver");
      if (resolver != null) {
         resolver.setEngine(this._services.getEngine());
         resolver.removeAllEventListeners();
         this._animationHandler = new AnimationHandler(this._services);
         resolver.setEventListener(203, this._animationHandler);
         resolver.setEventListener(204, this._animationHandler);
         this._mediaObjectHandler = new MediaObjectHandler(this._services);
         resolver.setEventListener(207, this._mediaObjectHandler);
         resolver.setEventListener(208, this._mediaObjectHandler);
         ActionHandler actionHandler = new ActionHandler(this._services);
         resolver.setEventListener(206, actionHandler);
         resolver.setEventListener(209, actionHandler);
      }

      this._engine.addListener(this);
   }

   @Override
   public void setMedia(Object media) {
      this._lastTime = 0;
      this._model = (ModelInteractorImpl)this._services.getService("ModelInteractor");
      if (media != null) {
         this._media = (AnimationModel)media;
         this._animationHandler.setMedia(media);
         this._mediaObjectHandler.setMedia(media);
      }

      if (this._model != null && this._media != null) {
         EventLogic logic = this._media._logic;
         if (logic == null) {
            logic = this._logicBuilder.buildLogic(this._media);
         }

         EventResolver resolver = (EventResolver)this._services.getService("EventResolver");
         if (resolver != null) {
            resolver.setEventLogic(logic);
         }

         this.triggerBeginEvent();
      } else {
         if (this._services.hasService("EventResolver")) {
            EventResolver resolver = (EventResolver)this._services.getService("EventResolver");
            if (resolver != null) {
               resolver.setEventLogic(null);
            }
         }
      }
   }

   @Override
   public Object getMedia() {
      return this._media;
   }

   @Override
   public void dispose() {
      this._engine.removeListener(this);
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (this._media != null) {
         switch (event) {
            case -1717674305:
               this.onStop();
               return;
            case -1391809431:
               this.onNewTime();
               return;
            case -1206114632:
               if (eventParam != 239239) {
                  this.update();
                  return;
               }

               if (this._mediaObjectHandler != null) {
                  this._mediaObjectHandler.update();
                  return;
               }
               break;
            case 24:
               this._updateRequested = true;
               break;
            case 868433339:
               this.onStart();
               return;
         }
      }
   }

   private void onStart() {
      this._lastTime = this._engine.getTime();
      this._mediaObjectHandler.resumeMediaObjects();
   }

   private void onStop() {
      this._lastTime = this._engine.getTime();
      this._mediaObjectHandler.pauseMediaObjects();
   }

   private void onNewTime() {
      this._mediaObjectHandler.stopMediaObjects();
      if (this._lastTime > this._engine.getTime()) {
         this._engine.cancelAllEvents();

         label27:
         try {
            FocusHandler f = (FocusHandler)this._services.getService("FocusInteractor");
            f.setItemInFocus(-1);
            f.moveFocus(1, 0, false);
            this._media.reset();
            this._animationHandler.reset();
            AnimationViewport viewport = (AnimationViewport)this._services.getService("Viewport");
            if (viewport != null) {
               viewport.resetContextData();
            }
         } finally {
            break label27;
         }

         this.triggerBeginEvent();
      }

      this._engine.dispatchEvents();
   }

   private void update() {
      if (this._animationHandler != null) {
         if (this._animationHandler.update() || this._updateRequested) {
            this._model.notify(20, -1, this._model);
            this._updateRequested = false;
         }

         if (this._engine.isEmpty() && this._mediaObjectHandler.isComplete()) {
            this._model.notify(3, -1, this._media);
         }
      }

      if (this._mediaObjectHandler != null) {
         this._mediaObjectHandler.update();
      }
   }

   private void triggerBeginEvent() {
      Event ev = this._engine.getEventInstance();
      ev._event = 100;
      ev._time = 0;
      ev._eventParam = this._media._behaviorsRoot;
      this._model.trigger(ev);
      this._engine.releaseEventInstance(ev);
   }
}
