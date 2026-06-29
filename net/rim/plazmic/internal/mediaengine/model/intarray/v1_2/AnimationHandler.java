package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.module.ModelIntArray_v1_2;
import net.rim.plazmic.mediaengine.MediaListener;

class AnimationHandler extends BehaviorHandler implements MediaListener {
   private TimeBasedAnimationManager _animationManager = new ModelIntArray_v1_2();
   private int[] _relativeValues = new int[4];
   private static final int MAX_KEYVALUES_LENGTH;

   public boolean update() {
      return this._animationManager.animate();
   }

   public void reset() {
      this._animationManager.reset();
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 203:
         default:
            this.startInterpolator(eventParam, ((Event)data)._time);
            return;
         case 204:
            this.finishInterpolator(eventParam, ((Event)data)._time);
            return;
         case 205:
            this.evaluate(eventParam);
         case 202:
      }
   }

   public AnimationHandler(MediaServices services) {
      super(services);
   }

   private void startInterpolator(int interpIdx, long startTime) {
      int targetAttributeType = super._data._nodes[interpIdx + 17];
      int vnIndex = super._data._nodes[interpIdx + 18];
      int targetIndex = super._data.resolveAttributeOffset(vnIndex, targetAttributeType);
      if (this.behaviorCanBegin(interpIdx, startTime)) {
         super._data.setBehaviorHasStarted(interpIdx, startTime);
         this.initializeInterpolator(interpIdx, targetIndex, targetAttributeType, startTime);
         this._animationManager.registerAnimation(targetIndex, interpIdx);
         super._event._event = 100;
         super._event._eventParam = interpIdx;
         super._event._eventParamLong = 0;
         super._event._time = startTime;
         super._model.trigger(super._event);
         this.evaluate(interpIdx);
      }
   }

   private void initializeInterpolator(int interpIdx, int targetIndex, int targetAttributeType, long startTime) {
      throw new RuntimeException("cod2jar: array store: unknown element");
   }

   private boolean finishInterpolator(int interpIdx, long endTime) {
      int vnIndex = super._data._nodes[interpIdx + 18];
      int targetAttributeType = super._data._nodes[interpIdx + 17];
      int targetIndex = super._data.resolveAttributeOffset(vnIndex, targetAttributeType);
      if (!this.behaviorCanEnd(interpIdx, endTime)) {
         return false;
      }

      super._data.setBehaviorHasEnded(interpIdx, endTime);
      this.resolveFill(interpIdx, targetIndex, targetAttributeType, endTime);
      super._event._event = 101;
      super._event._eventParam = interpIdx;
      super._event._eventParamLong = 0;
      super._event._time = endTime;
      super._model.trigger(super._event);
      return true;
   }

   private void resolveFill(int interpIdx, int targetIndex, int targetAttributeType, long endTime) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private void evaluate(int interpIdx) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private int getEvaluateDelay(
      int interpolatorType, int keyValuesIdx, int tupleLocation, int keyTimeIdx, int interval, int numKeyValuesPerKeyTime, int timeWithinLoop
   ) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private void doConstantInterpolation(int interpIdx, int keyValuesIndex, int tupleLocation, int numKeyValuesPerKeyTime) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private void doLinearInterpolation(
      int interpIdx, int keyValuesIdx, int tupleLocation, int keyTimeIdx, int interval, int numKeyValuesPerKeyTime, int timeWithinLoop
   ) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private void accumulate(int interpIdx, int keyValuesIndex, int numKeyValuesPerKeyTime, int targetAttributeType) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   @Override
   public void setMedia(Object media) {
      this._animationManager.setMedia(media);
      super._data = (AnimationModel)media;
   }
}
