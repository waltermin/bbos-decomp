package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.util.MediaQueue;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.mediaengine.MediaListener;

class MediaObjectHandler extends BehaviorHandler implements MediaListener {
   private MediaQueue _mediaQ;
   private boolean _resumeMedia = false;
   private MediaQueue _activeMediaQ;
   private Platform _platform;

   void pauseMediaObjects() {
      for (int i = this._activeMediaQ.getSize(); i > 0; i--) {
         int mediaClipIndex = this._activeMediaQ.dequeue();
         int mediaIndex = super._data._nodes[mediaClipIndex + 17];
         this._activeMediaQ.enqueue(mediaClipIndex);
         this._platform.pausePlayer(super._data._media[mediaIndex], this);
      }
   }

   public boolean isComplete() {
      return this._activeMediaQ.getSize() == 0 && this._mediaQ.getSize() == 0;
   }

   void update() {
      if (super._engine.isRunning()) {
         long currentTime = super._engine.getTime();
         if (this._resumeMedia) {
            this._resumeMedia = false;

            for (int i = this._activeMediaQ.getSize(); i > 0; i--) {
               int mediaClipIndex = this._activeMediaQ.dequeue();
               this._activeMediaQ.enqueue(mediaClipIndex);
               int timeOffset = this.getTimeSinceStarted(mediaClipIndex, currentTime);
               this.startPlayback(mediaClipIndex, timeOffset);
            }
         }

         while (this._mediaQ.hasMoreElements()) {
            int mediaClipIndex = this._mediaQ.dequeue();
            this._activeMediaQ.enqueue(mediaClipIndex);
            int timeOffset = this.getTimeSinceStarted(mediaClipIndex, currentTime);
            this.startPlayback(mediaClipIndex, timeOffset);
         }
      }
   }

   void stopMediaObjects() {
      long currentTime = super._engine.getTime();

      while (this._activeMediaQ.hasMoreElements()) {
         this.stopMediaObject(this._activeMediaQ.dequeue(), currentTime, false);
      }

      this._mediaQ.reset();
   }

   void resumeMediaObjects() {
      this._resumeMedia = true;
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      long time;
      if (!(data instanceof Event)) {
         time = super._engine.getTime();
      } else {
         time = ((Event)data)._time;
      }

      switch (event) {
         case 3:
            if (sender == super._engine) {
               this.onMediaComplete(((Event)data)._data);
               return;
            }

            super._event._event = 3;
            super._event._data = data;
            super._event._listener = this;
            super._event._time = time;
            super._engine.postEvent(super._event, true);
            super._event.clear();
            super._event._time = time;
            return;
         case 205:
            this.evaluate(eventParam, false);
            break;
         case 207:
            this.startMediaObject(eventParam, time);
            return;
         case 208:
            this.stopMediaObject(eventParam, time, false);
            if (super._data.bitsAreSet(eventParam, 1024)) {
               super._engine.cancelEvent(205, eventParam);
               return;
            }
      }
   }

   private void stopMediaObject(int nodeIndex, long time, boolean justCompleted) {
      if (nodeIndex != -1 && this.behaviorCanEnd(nodeIndex, time)) {
         super._data.setBehaviorHasEnded(nodeIndex, time);
         super._data.unsetBits(nodeIndex, 67108864);
         super._data._nodes[nodeIndex + 18] = 0;
         if (!justCompleted) {
            int mediaIndex = super._data._nodes[nodeIndex + 17];
            int currentNodePos = -1;
            int lastSameMediaNodePos = -1;
            int queueSize = this._activeMediaQ.getSize();

            for (int i = 0; i < queueSize; i++) {
               int nodeMediaIdx = this._activeMediaQ.dequeue();
               if (nodeMediaIdx == nodeIndex) {
                  currentNodePos = i;
               } else {
                  this._activeMediaQ.enqueue(nodeMediaIdx);
                  if (super._data._nodes[nodeMediaIdx + 17] == mediaIndex) {
                     lastSameMediaNodePos = i;
                  }
               }
            }

            if (currentNodePos > lastSameMediaNodePos || lastSameMediaNodePos == -1) {
               this._platform.stopPlayer(super._data._media[mediaIndex], this);
            }
         }

         super._event._event = 101;
         super._event._eventParam = nodeIndex;
         super._event._eventParamLong = 0;
         super._event._time = time;
         super._model.trigger(super._event);
      }
   }

   private void startPlayback(int mediaIndex, long mediaTime) {
      int objIndex = super._data._nodes[mediaIndex + 17];
      Object media = super._data._media[objIndex];
      if (media != null) {
         for (int i = this._activeMediaQ.getSize(); i > 0; i--) {
            int queuedMediaIndex = this._activeMediaQ.dequeue();
            this._activeMediaQ.enqueue(queuedMediaIndex);
            int queuedObjIndex = super._data._nodes[queuedMediaIndex + 17];
            Object queuedMedia = super._data._media[queuedObjIndex];
            this._platform.stopPlayer(queuedMedia, this);
         }

         this._platform.startPlayer(media, this, mediaTime, 1);
      }
   }

   @Override
   public void setMedia(Object media) {
      if (media != super._data) {
         super._data = (AnimationModel)media;
         this._activeMediaQ.reset();
         this._mediaQ.reset();
      }
   }

   private void evaluate(int mediaIdx, boolean justCompleted) {
      if (super._data.bitsAreSet(mediaIdx, 67108864)) {
         long currentTime = super._engine.getTime();
         long startTime = (long)super._data._nodes[mediaIdx + 14] << 32 | super._data._nodes[mediaIdx + 15];
         int repeatCount = super._data._nodes[mediaIdx + 11];
         int loopsCompleted = super._data._nodes[mediaIdx + 18];
         int dur;
         if (super._data.bitsAreSet(mediaIdx, 1024)) {
            dur = super._data._nodes[mediaIdx + 12];
         } else {
            dur = -1;
         }

         boolean isFinished;
         if (repeatCount <= 0) {
            isFinished = false;
         } else {
            isFinished = dur > 0 ? currentTime >= dur * repeatCount + startTime : loopsCompleted >= repeatCount;
         }

         if (isFinished) {
            long endTime = dur > 0 ? dur * repeatCount + startTime : currentTime;
            this.stopMediaObject(mediaIdx, endTime, justCompleted);
            return;
         }

         if (justCompleted) {
            this._activeMediaQ.enqueue(mediaIdx);
         }

         super._data._nodes[mediaIdx + 18]++;
         this.startPlayback(mediaIdx, 0);
         super._event._event = 102;
         super._event._eventParam = mediaIdx;
         super._event._eventParamLong = loopsCompleted;
         super._model.trigger(super._event);
         if (dur > 0) {
            this.scheduleEvaluate(mediaIdx, startTime + dur * (loopsCompleted + 1));
         }
      }
   }

   private int getTimeSinceStarted(int mediaIndex, long currentTime) {
      long startTime = (long)super._data._nodes[mediaIndex + 14] << 32 | super._data._nodes[mediaIndex + 15];
      int result = (int)(currentTime - startTime);
      int currentLoop = super._data._nodes[mediaIndex + 18];
      if (currentLoop > 1 && super._data.bitsAreSet(mediaIndex, 1024)) {
         int dur = super._data._nodes[mediaIndex + 12];
         result %= dur;
      }

      return result;
   }

   private void startMediaObject(int mediaIdx, long startTime) {
      if (this.behaviorCanBegin(mediaIdx, startTime)) {
         super._data.setBehaviorHasStarted(mediaIdx, startTime);
         if (super._data.bitsAreSet(mediaIdx, 67108864)) {
            this.stopMediaObject(mediaIdx, startTime, false);
         }

         super._data.setBits(mediaIdx, 100663296);
         this._mediaQ.enqueue(mediaIdx);
         super._data._nodes[mediaIdx + 18] = 1;
         super._data._nodes[mediaIdx + 14] = (int)(startTime & -4294967296L);
         super._data._nodes[mediaIdx + 15] = (int)startTime;
         super._event._event = 100;
         super._event._eventParam = mediaIdx;
         super._event._eventParamLong = 0;
         super._event._time = startTime;
         super._model.trigger(super._event);
         if (super._data.bitsAreSet(mediaIdx, 1024)) {
            int dur = super._data._nodes[mediaIdx + 12];
            this.scheduleEvaluate(mediaIdx, startTime + dur);
         }
      }
   }

   private void onMediaComplete(Object completedMedia) {
      for (int i = this._activeMediaQ.getSize(); i > 0; i--) {
         int mediaIdx = this._activeMediaQ.dequeue();
         Object media = super._data._media[super._data._nodes[mediaIdx + 17]];
         if (media != completedMedia) {
            this._activeMediaQ.enqueue(mediaIdx);
         } else {
            this.evaluate(mediaIdx, true);
         }
      }
   }

   public MediaObjectHandler(MediaServices s) {
      super(s);
      this._mediaQ = new MediaQueue();
      this._activeMediaQ = new MediaQueue();
      this._platform = MediaFactory.getPlatform();
   }
}
