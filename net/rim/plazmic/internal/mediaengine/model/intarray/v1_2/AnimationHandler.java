package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.module.ModelIntArray_v1_2;
import net.rim.plazmic.mediaengine.MediaListener;

class AnimationHandler extends BehaviorHandler implements MediaListener {
   private TimeBasedAnimationManager _animationManager = new ModelIntArray_v1_2();
   private int[] _relativeValues = new int[4];
   private static final int MAX_KEYVALUES_LENGTH = 4;

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
      if (super._data.bitsAreSet(interpIdx, 67108864)) {
         super._engine.cancelEvent(205, interpIdx);
         this._animationManager.cancelAnimation(targetIndex, interpIdx, false);
      } else if (super._data.bitsAreSet(interpIdx, 134217728)) {
         this._animationManager.cancelAnimation(targetIndex, interpIdx, false);
      }

      if (super._data.bitsAreSet(interpIdx, 67108864)) {
         super._event._event = 101;
         super._event._eventParam = interpIdx;
         super._event._eventParamLong = 0;
         super._event._time = startTime;
         super._model.trigger(super._event);
      }

      super._data.setBits(interpIdx, 100663296);
      super._data.unsetBits(interpIdx, 134217728);
      super._data._nodes[interpIdx + 14] = (int)(startTime >>> 32);
      super._data._nodes[interpIdx + 15] = (int)startTime;
      int keyValuesLength = this._animationManager.getKeyValueLength(targetAttributeType);

      for (int i = 0; i < keyValuesLength; i++) {
         super._data._nodes[interpIdx + 21 + i] = 0;
      }

      super._data._nodes[interpIdx + 27] = 1;
      super._data._nodes[interpIdx + 28] = 0;
      if (super._data.bitsAreSet(interpIdx, 32768) && !super._data.bitsAreSet(interpIdx, 4096)) {
         int keyValuesIndex = super._data._nodes[interpIdx + 20];
         this._animationManager.getRelativeValues(interpIdx, targetIndex, targetAttributeType, this._relativeValues);

         for (int i = 0; i < keyValuesLength; i++) {
            super._data._keyValues[keyValuesIndex][i] = this._relativeValues[i];
         }
      }
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
      if (super._data.bitsAreSet(interpIdx, 131072)) {
         super._data.setBits(interpIdx, 134217728);
      }

      super._data.unsetBits(interpIdx, 67108864);
      if (super._data.bitsAreSet(interpIdx, 131072) && super._data.bitsAreSet(interpIdx, 1024)) {
         int keyValuesIndex;
         int keyTimesIndex = keyValuesIndex = super._data._nodes[interpIdx + 20];
         int numKeyValuesPerKeyTime = this._animationManager.getKeyValueLength(targetAttributeType);
         int numKeys = super._data._keyTimes[keyTimesIndex].length;
         int interval = super._data._nodes[interpIdx + 27];
         int dur = super._data._keyTimes[keyTimesIndex][numKeys - 1];
         long interpST = (long)super._data._nodes[interpIdx + 14] << 32 | super._data._nodes[interpIdx + 15] & 4294967295L;
         int timeWithinLoop = (int)(endTime - interpST);
         if (timeWithinLoop != 0 && timeWithinLoop % dur == 0) {
            timeWithinLoop = dur;
         } else {
            timeWithinLoop %= dur;
         }

         int type = super._data._nodes[interpIdx + 19];
         switch (type) {
            case 10:
               int nextKeyTime = super._data._keyTimes[keyTimesIndex][interval];
               int tupleLocation;
               if (timeWithinLoop < nextKeyTime) {
                  tupleLocation = (interval - 1) * numKeyValuesPerKeyTime;
               } else {
                  tupleLocation = interval * numKeyValuesPerKeyTime;
               }

               this.doConstantInterpolation(interpIdx, keyValuesIndex, tupleLocation, numKeyValuesPerKeyTime);
               return;
            case 20:
               this.doLinearInterpolation(
                  interpIdx, keyValuesIndex, (interval - 1) * numKeyValuesPerKeyTime, keyTimesIndex, interval, numKeyValuesPerKeyTime, timeWithinLoop
               );
               return;
         }
      } else if (!super._data.bitsAreSet(interpIdx, 131072)) {
         this._animationManager.cancelAnimation(targetIndex, interpIdx, true);
      }
   }

   private void evaluate(int interpIdx) {
      if (super._data.bitsAreSet(interpIdx, 67108864)) {
         int keyTimesIndex;
         int keyValuesIndex = keyTimesIndex = super._data._nodes[interpIdx + 20];
         int targetAttributeType = super._data._nodes[interpIdx + 17];
         int numKeyValuesPerKeyTime = this._animationManager.getKeyValueLength(targetAttributeType);
         if (!super._data.bitsAreSet(interpIdx, 1024)) {
            this.doConstantInterpolation(interpIdx, keyValuesIndex, 0, numKeyValuesPerKeyTime);
         } else {
            long currentTime = super._engine.getTime();
            int type = super._data._nodes[interpIdx + 19];
            int numKeys = super._data._keyTimes[keyTimesIndex].length;
            int dur = super._data._keyTimes[keyTimesIndex][numKeys - 1];
            int repeatCount = super._data._nodes[interpIdx + 11];
            long interpST = (long)super._data._nodes[interpIdx + 14] << 32 | super._data._nodes[interpIdx + 15] & 4294967295L;
            int prevEvaluatedTime = super._data._nodes[interpIdx + 28];
            int evaluateTime = (int)(currentTime - interpST);
            int timeWithinLoop = evaluateTime % dur;
            int prevTimeWithinLoop = prevEvaluatedTime % dur;
            if (timeWithinLoop < prevTimeWithinLoop || evaluateTime - prevEvaluatedTime > dur) {
               int prevIteration = prevEvaluatedTime / dur + 1;
               int curIteration = evaluateTime / dur + 1;

               for (int skippedIter = prevIteration;
                  skippedIter < curIteration && skippedIter < 5 && (repeatCount == 0 || skippedIter < repeatCount);
                  skippedIter++
               ) {
                  if (super._data.bitsAreSet(interpIdx, 8192)) {
                     this.accumulate(interpIdx, keyValuesIndex, numKeyValuesPerKeyTime, targetAttributeType);
                  }

                  super._event._event = 102;
                  super._event._eventParam = interpIdx;
                  super._event._eventParamLong = skippedIter;
                  super._event._time = interpST + skippedIter * dur;
                  super._model.trigger(super._event);
               }
            }

            boolean isFinished = repeatCount > 0 && currentTime >= dur * repeatCount + interpST;
            if (isFinished) {
               super._data._nodes[interpIdx + 27] = numKeys - 1;
               isFinished = this.finishInterpolator(interpIdx, dur * repeatCount + interpST);
            }

            if (!isFinished) {
               super._data._nodes[interpIdx + 28] = evaluateTime;
               int interval = super._data._nodes[interpIdx + 27];
               if (timeWithinLoop < super._data._keyTimes[keyTimesIndex][interval - 1]) {
                  super._data._nodes[interpIdx + 27] = 1;
                  interval = 1;
               }

               while (timeWithinLoop >= super._data._keyTimes[keyTimesIndex][interval]) {
                  super._data._nodes[interpIdx + 27] = ++interval;
               }

               int tupleLocation = (interval - 1) * numKeyValuesPerKeyTime;
               switch (type) {
                  case 10:
                     this.doConstantInterpolation(interpIdx, keyValuesIndex, tupleLocation, numKeyValuesPerKeyTime);
                     break;
                  case 20:
                     this.doLinearInterpolation(interpIdx, keyValuesIndex, tupleLocation, keyTimesIndex, interval, numKeyValuesPerKeyTime, timeWithinLoop);
               }

               this.scheduleEvaluate(
                  interpIdx,
                  currentTime + this.getEvaluateDelay(type, keyValuesIndex, tupleLocation, keyTimesIndex, interval, numKeyValuesPerKeyTime, timeWithinLoop)
               );
            }
         }
      }
   }

   private int getEvaluateDelay(
      int interpolatorType, int keyValuesIdx, int tupleLocation, int keyTimeIdx, int interval, int numKeyValuesPerKeyTime, int timeWithinLoop
   ) {
      int numEqualTuples = 0;
      boolean tuplesEqual = true;
      int[] kvArray = super._data._keyValues[keyValuesIdx];

      while (tuplesEqual && tupleLocation + (numEqualTuples + 1) * numKeyValuesPerKeyTime < kvArray.length) {
         int i = 0;

         while (true) {
            if (i < numKeyValuesPerKeyTime) {
               if (kvArray[tupleLocation + numEqualTuples * numKeyValuesPerKeyTime + i]
                  == kvArray[tupleLocation + (numEqualTuples + 1) * numKeyValuesPerKeyTime + i]) {
                  i++;
                  continue;
               }

               tuplesEqual = false;
            }

            if (tuplesEqual) {
               numEqualTuples++;
            }
            break;
         }
      }

      switch (interpolatorType) {
         case 10:
            int nextInterval = interval + numEqualTuples;
            if (nextInterval >= super._data._keyTimes[keyTimeIdx].length) {
               nextInterval = super._data._keyTimes[keyTimeIdx].length - 1;
            }

            return super._data._keyTimes[keyTimeIdx][nextInterval] - timeWithinLoop;
         default:
            return numEqualTuples == 0 ? 20 : Math.max(super._data._keyTimes[keyTimeIdx][interval + numEqualTuples - 1] - timeWithinLoop, 20);
      }
   }

   private void doConstantInterpolation(int interpIdx, int keyValuesIndex, int tupleLocation, int numKeyValuesPerKeyTime) {
      boolean accumulative = super._data.bitsAreSet(interpIdx, 8192);

      for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
         super._data._nodes[interpIdx + 21 + i] = super._data._keyValues[keyValuesIndex][tupleLocation + i];
         if (accumulative) {
            super._data._nodes[interpIdx + 21 + i] = super._data._nodes[interpIdx + 21 + i] + super._data._nodes[interpIdx + 24 + i];
         }
      }
   }

   private void doLinearInterpolation(
      int interpIdx, int keyValuesIdx, int tupleLocation, int keyTimeIdx, int interval, int numKeyValuesPerKeyTime, int timeWithinLoop
   ) {
      int prevKeyTime = super._data._keyTimes[keyTimeIdx][interval - 1];
      int nextKeyTime = super._data._keyTimes[keyTimeIdx][interval];
      int targetAttributeType = super._data._nodes[interpIdx + 17];
      boolean accumulative = super._data.bitsAreSet(interpIdx, 8192);
      int currentValue = 0;
      int nextValue = 0;

      for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
         int prevValue = super._data._keyValues[keyValuesIdx][tupleLocation + i];
         nextValue = super._data._keyValues[keyValuesIdx][tupleLocation + numKeyValuesPerKeyTime + i];
         if (prevValue != Integer.MIN_VALUE && nextValue != Integer.MIN_VALUE) {
            if (targetAttributeType != 3 && targetAttributeType != 5) {
               currentValue = (int)(prevValue + (long)(nextValue - prevValue) * (timeWithinLoop - prevKeyTime) / (nextKeyTime - prevKeyTime));
               if (accumulative) {
                  currentValue += super._data._nodes[interpIdx + 24 + i];
               }
            } else {
               currentValue = 0;

               for (int j = 0; j < 3; j++) {
                  int prevColor = prevValue >> 8 * j & 0xFF;
                  int nextColor = nextValue >> 8 * j & 0xFF;
                  int currentColor = prevColor + (nextColor - prevColor) * (timeWithinLoop - prevKeyTime) / (nextKeyTime - prevKeyTime);
                  if (accumulative) {
                     int accumulatedColor = super._data._nodes[interpIdx + 24 + i] >> 8 * j & 0xFF;
                     currentColor += accumulatedColor;
                  }

                  if (currentColor > 255) {
                     currentColor = 255;
                  }

                  currentValue |= currentColor << 8 * j;
               }
            }
         } else if (timeWithinLoop - prevKeyTime >= nextKeyTime - timeWithinLoop) {
            currentValue = nextValue;
         } else {
            currentValue = prevValue;
         }

         super._data._nodes[interpIdx + 21 + i] = currentValue;
      }
   }

   private void accumulate(int interpIdx, int keyValuesIndex, int numKeyValuesPerKeyTime, int targetAttributeType) {
      int firstKeyValueSetIndex = 0;
      int lastKeyValueSetIndex = super._data._keyValues[keyValuesIndex].length - numKeyValuesPerKeyTime;
      if (targetAttributeType != 3 && targetAttributeType != 5) {
         for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
            super._data._nodes[interpIdx + 24 + i] = super._data._nodes[interpIdx + 24 + i]
               + (super._data._keyValues[keyValuesIndex][lastKeyValueSetIndex + i] - super._data._keyValues[keyValuesIndex][firstKeyValueSetIndex + i]);
         }
      } else {
         int curAccumulatedColor = super._data._nodes[interpIdx + 24];
         int firstKeyValue = super._data._keyValues[keyValuesIndex][firstKeyValueSetIndex];
         int lastKeyValue = super._data._keyValues[keyValuesIndex][lastKeyValueSetIndex];
         int newAccumulatedColor = 0;

         for (int j = 0; j < 3; j++) {
            int newComponent = (curAccumulatedColor >> 8 * j & 0xFF) + (lastKeyValue >> 8 * j & 0xFF) - (firstKeyValue >> 8 * j & 0xFF);
            if (newComponent > 255) {
               newComponent = 255;
            } else if (newComponent < 0) {
               newComponent = 0;
            }

            newAccumulatedColor |= newComponent << 8 * j;
         }

         super._data._nodes[interpIdx + 24] = newAccumulatedColor;
      }
   }

   @Override
   public void setMedia(Object media) {
      this._animationManager.setMedia(media);
      super._data = (AnimationModel)media;
   }
}
