package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.mediaengine.MediaListener;

public class AnimationSampler implements MediaService, MediaListener {
   private long SAMPLE_DELAY = 20;
   protected EventEngine _engine;
   protected AnimationModel _media;
   protected MediaServices _services;
   public static final String ID;
   protected static final int SAMPLE_EVENT;

   protected boolean sampleAndSchedule() {
      boolean uiUpdate = this.sample();
      AnimationModel model = this._media;
      if (model != null && model.tasks.queueSize() > 0) {
         int time = -1;
         int diffQueueSizeTimerCount = model.tasks.queueSize() - model.timerCount;
         if (diffQueueSizeTimerCount < 0) {
            throw new Object("Timer Count Assertion Failed");
         }

         if (model.tasks.heapHasElements() && diffQueueSizeTimerCount == 0) {
            time = model.tasks.peekInHeap();
         }

         this.scheduleSample(time == -1 ? this._engine.getTime() + this.SAMPLE_DELAY : time);
      }

      return uiUpdate;
   }

   protected boolean sample() {
      boolean uiUpdate = false;
      AnimationModel model = this._media;
      if (model != null) {
         uiUpdate = model.sample();
      }

      return uiUpdate;
   }

   protected int postEvent(int event, int param) {
      AnimationModel model = this._media;
      if (model == null) {
         return 1;
      }

      model.tasks.enqueueEvent(event, param);
      int unconsumed = 0;
      switch (event) {
         case 2:
            break;
         case 3:
            unconsumed = (param & 1) == 1 ? 0 : model.getUnconsumed(param >> 1);
            break;
         case 4:
         default:
            unconsumed = 1;
            if (model.hotspotFocus >= 0 && model.getItemCount() > model.hotspotFocus) {
               int hs = model.hotspotList[model.hotspotFocus];
               if (model.nodes[hs + 5] > 0) {
                  unconsumed = 0;
               }
            }
      }

      this.scheduleSample(this._engine.getTime());
      return unconsumed;
   }

   protected void scheduleSample(long time) {
      Event e = this._engine.getEventInstance();
      e._time = time;
      e._listener = this;
      e._event = 112035382;
      this._engine.postEvent(e, false);
      this._engine.releaseEventInstance(e);
   }

   @Override
   public void setServices(MediaServices s) {
      if (this._services != s) {
         if (this._engine != null) {
            this._engine.removeListener(this);
         }

         this._services = s;
         if (this._services != null) {
            this._engine = this._services.getEngine();
            this._engine.addListener(this);
         }
      }
   }

   @Override
   public void dispose() {
      this._engine.removeListener(this);
   }

   @Override
   public Object getMedia() {
      return this._media;
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case -1717674305:
            this._media.onStop();
            this.sample();
            return;
         case -1391809431:
            this._media.onNewTime();
            this._engine.cancelAllEvents();
            this.sample();
            return;
         case 868433339:
            this._media.onStart();
         case 112035382:
            this.sampleAndSchedule();
      }
   }

   @Override
   public void setMedia(Object media) {
      AnimationModel model = this._media;
      if (model != media) {
         if (media != null) {
            ((AnimationModel)media)._engine = this._engine;
            if (this._engine.isRunning()) {
               throw new Object();
            }
         }

         this._media = (AnimationModel)media;
         if (media != null) {
            this.sample();
         }
      }
   }
}
