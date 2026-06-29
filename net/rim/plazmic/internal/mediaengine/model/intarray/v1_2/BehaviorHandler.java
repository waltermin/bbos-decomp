package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;

class BehaviorHandler {
   protected Event _event = (Event)(new Object());
   protected MediaServices _services;
   protected EventEngine _engine;
   protected AnimationModel _data;
   protected ModelInteractor _model;
   protected static final int EVALUATE_DELAY;

   public BehaviorHandler(MediaServices services) {
      this._services = services;
      this._engine = this._services.getEngine();
      this._model = (ModelInteractor)this._services.getService("ModelInteractor");
   }

   protected void scheduleEvaluate(int behaviorHandle, long time) {
      this._event._event = 205;
      this._event._eventParam = behaviorHandle;
      this._event._time = time;
      this._event._listener = this;
      this._engine.postEvent(this._event, false);
      this._event._listener = null;
   }

   protected boolean behaviorCanBegin(int bhIdx, long startTime) {
      if (this._data._nodes[bhIdx + 18] != this._data._visualRoot
         || this._data._nodes[bhIdx + 1] != 1
         || this._data._nodes[bhIdx + 17] != 6 && this._data._nodes[bhIdx + 17] != 7) {
         if (this._data.bitsAreSet(bhIdx, 33554448)) {
            return false;
         } else {
            return this._data.bitsAreSet(bhIdx, 67108864) && this._data.bitsAreSet(bhIdx, 32) ? false : !this._data.behaviorHasStarted(bhIdx, startTime);
         }
      } else {
         return false;
      }
   }

   protected boolean behaviorCanEnd(int bhIdx, long endTime) {
      return !this._data.bitsAreSet(bhIdx, 67108864) ? false : !this._data.behaviorHasEnded(bhIdx, endTime);
   }

   public void setMedia(Object _1) {
      throw null;
   }
}
