package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.util.MediaQueue;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.internal.mediaengine.util.TransformationMatrix;

public class TimeBasedAnimationManager {
   private int[] _targetList;
   private int _numActiveTargets;
   private MediaQueue[] _interpolatorList;
   private int[] _tempMatrix;
   private int[] _prevValues = new int[6];
   private TransformationMatrix _matrixDecomp;
   private Platform _platform = MediaFactory.getPlatform();
   private AnimationModel _data;
   private boolean _updateRequired;
   private static final int INIT_TARGET_LIST_SIZE = 20;
   private static final int TARGET_LIST_GROWTH_RATE = 10;
   private static final int INIT_INTERPOLATOR_QUEUE_SIZE = 5;
   private static final int INTERPOLATOR_QUEUE_GROWTH_RATE = 5;
   private static final int SPACE_AVAILABLE = -1;

   public TimeBasedAnimationManager() {
      this._tempMatrix = new int[9];
      this._targetList = new int[20];

      for (int i = 0; i < this._targetList.length; i++) {
         this._targetList[i] = -1;
      }

      this._interpolatorList = new MediaQueue[20];

      for (int i = 0; i < this._interpolatorList.length; i++) {
         this._interpolatorList[i] = new MediaQueue(5, 5);
      }
   }

   public void registerAnimation(int targetIdx, int interpIdx) {
      int insertIndex = this.findInList(this._targetList, targetIdx);
      switch (insertIndex) {
         case -1:
            if (this._numActiveTargets == this._targetList.length) {
               this.growTargetList(10);
            }

            insertIndex = this.findInList(this._targetList, -1);
            this._targetList[insertIndex] = targetIdx;
            this._numActiveTargets++;
         default:
            this._interpolatorList[insertIndex].enqueue(interpIdx);
      }
   }

   public void getRelativeValues(int interpIdx, int targetIndex, int targetAttributeType, int[] returnValues) {
      int vnIndex = this._data._nodes[interpIdx + 18];
      int numKeyValuesPerKeyTime = this.getKeyValueLength(targetAttributeType);
      int queueIndex = this.findInList(this._targetList, targetIndex);
      int queueLength = -1;
      if (queueIndex >= 0) {
         queueLength = this._interpolatorList[queueIndex].getSize();
      }

      if (queueLength > 0) {
         MediaQueue mq = this._interpolatorList[queueIndex];
         int hpIndex = mq.peekLast();
         if (!this._data.bitsAreSet(hpIndex, 4096)) {
            for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
               returnValues[i] = this._data._nodes[hpIndex + 21 + i];
            }
         } else {
            int hpVnIndex = this._data._nodes[hpIndex + 18];
            int hpTargetAttributeType = this._data._nodes[hpIndex + 17];
            int hpOmValuesIdx = this._data._nodes[hpIndex + 20];

            for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
               returnValues[i] = this._data._omValues[hpOmValuesIdx][i];
            }

            while (returnValues[0] == Integer.MAX_VALUE) {
               int parentNode = this._data._nodes[hpVnIndex + 3];
               if (hpVnIndex == -1) {
                  this.getDefaultAttributeValues(hpTargetAttributeType, returnValues);
               } else {
                  int parentTargetIndex = this._data.resolveAttributeOffset(parentNode, hpTargetAttributeType);
                  if (parentTargetIndex != -1) {
                     for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
                        returnValues[i] = this._data._nodes[parentTargetIndex + i];
                     }
                  }
               }
            }

            for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
               if (hpTargetAttributeType != 3 && hpTargetAttributeType != 5) {
                  returnValues[i] += this._data._nodes[hpIndex + 21 + i];
               } else {
                  int oldModelValue = returnValues[i];
                  int newModelValue = 0;

                  for (int j = 0; j < 3; j++) {
                     int currentColor = (oldModelValue >> 8 * j & 0xFF) + (this._data._nodes[hpIndex + 21 + i] >> 8 * j & 0xFF);
                     if (currentColor > 255) {
                        currentColor = 255;
                     }

                     newModelValue |= currentColor << 8 * j;
                  }

                  returnValues[i] = newModelValue;
               }
            }
         }
      } else {
         int omValuesIdx = this._data._nodes[interpIdx + 20];
         if (this.isMatrixTarget(targetAttributeType)) {
            this.getMatrixValues(targetAttributeType, this._data._omValues[omValuesIdx], 0, returnValues);
            return;
         }

         if (this.isMaskTarget(interpIdx)) {
            returnValues[0] = this._data._omValues[omValuesIdx][0];
            int mask = this._data._nodes[interpIdx + 29];
            int shift = this._data._nodes[interpIdx + 30];
            returnValues[0] = (returnValues[0] & mask) >>> shift;
            return;
         }

         for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
            returnValues[i] = this._data._omValues[omValuesIdx][i];
         }

         while (returnValues[0] == Integer.MAX_VALUE) {
            vnIndex = this._data._nodes[vnIndex + 3];
            if (vnIndex == -1) {
               this.getDefaultAttributeValues(targetAttributeType, returnValues);
            } else {
               int parentTargetIndex = this._data.resolveAttributeOffset(vnIndex, targetAttributeType);
               if (parentTargetIndex != -1) {
                  for (int i = 0; i < numKeyValuesPerKeyTime; i++) {
                     returnValues[i] = this._data._nodes[parentTargetIndex + i];
                  }
               }
            }
         }
      }
   }

   public void cancelAnimation(int targetIdx, int interpIdx, boolean resetOMValue) {
      int listIndex = this.findInList(this._targetList, targetIdx);
      boolean bUpdateRequired = false;
      if (listIndex >= 0 && listIndex < this._targetList.length) {
         MediaQueue mq = this._interpolatorList[listIndex];
         mq.remove(interpIdx);
         int targetAttributeType = this._data._nodes[interpIdx + 17];
         if (mq.getSize() != 0) {
            if (this.isMaskTarget(interpIdx) && resetOMValue) {
               boolean bPendingInterp = false;

               for (int i = mq.getSize() - 1; i > -1; i--) {
                  int iInterp = mq.peek(i);
                  if (targetAttributeType == this._data._nodes[iInterp + 17]) {
                     bPendingInterp = true;
                     break;
                  }
               }

               if (!bPendingInterp) {
                  int targetPrevValue = this._data._nodes[targetIdx];
                  int mask = this._data._nodes[interpIdx + 29];
                  this.resetOMMaskTarget(targetIdx, mask, interpIdx);
                  if (mask == 128) {
                     this.resetOMMaskTarget(targetIdx, 64, interpIdx);
                  }

                  if (targetPrevValue != this._data._nodes[targetIdx]) {
                     bUpdateRequired = true;
                  }
               }
            }
         } else {
            if (resetOMValue) {
               if (this.isMaskTarget(interpIdx)) {
                  int targetPrevValue = this._data._nodes[targetIdx];
                  int mask = this._data._nodes[interpIdx + 29];
                  this.resetOMMaskTarget(targetIdx, mask, interpIdx);
                  if (mask == 128) {
                     this.resetOMMaskTarget(targetIdx, 64, interpIdx);
                  }

                  if (targetPrevValue != this._data._nodes[targetIdx]) {
                     bUpdateRequired = true;
                  }
               } else if (this.isMatrixTarget(targetAttributeType)) {
                  System.arraycopy(this._data._nodes, targetIdx, this._prevValues, 0, 6);
                  this.resetOMMatrixTarget(targetIdx, interpIdx);

                  for (int i = 0; i < 6; i++) {
                     if (this._prevValues[i] != this._data._nodes[targetIdx + i]) {
                        bUpdateRequired = true;
                        break;
                     }
                  }
               } else {
                  int length = this.getKeyValueLength(targetAttributeType);
                  System.arraycopy(this._data._nodes, targetIdx, this._prevValues, 0, length);
                  this.resetOMScalarTarget(targetIdx, interpIdx);

                  for (int i = 0; i < length; i++) {
                     if (this._prevValues[i] != this._data._nodes[targetIdx + i]) {
                        bUpdateRequired = true;
                        break;
                     }
                  }
               }
            }

            this._targetList[listIndex] = -1;
            this._numActiveTargets--;
         }

         if (bUpdateRequired) {
            int vnodeIdx = this._data._nodes[interpIdx + 18];
            this.setDirty(vnodeIdx, targetAttributeType);
            this._updateRequired = true;
         }
      }
   }

   public boolean animate() {
      boolean bUpdateRequired = false;
      int numTargetsBeforeLoop = this._numActiveTargets;
      int index = 0;

      for (int targetsFound = 0; targetsFound < numTargetsBeforeLoop; index++) {
         if (this._targetList[index] != -1) {
            bUpdateRequired |= this.resolveInterpolators(this._targetList[index], index);
            targetsFound++;
            if (this.animationIsFinished(index)) {
               this._targetList[index] = -1;
               this._numActiveTargets--;
            }
         }
      }

      if (this._updateRequired) {
         bUpdateRequired = true;
         this._updateRequired = false;
      }

      return bUpdateRequired;
   }

   private boolean animationIsFinished(int listIndex) {
      return this._interpolatorList[listIndex].getSize() == 0;
   }

   private boolean resolveInterpolators(int targetIdx, int interpolatorQueueIndex) {
      boolean bUpdateRequired = false;
      MediaQueue mq = this._interpolatorList[interpolatorQueueIndex];
      if (!mq.hasMoreElements()) {
         return false;
      }

      int interpIdx = -1;
      int targetAttributeType = -1;
      int valuesLength = -1;
      boolean valuesSaved = false;
      int mqSize = mq.getSize();

      for (int i = 0; i < mqSize; i++) {
         interpIdx = mq.dequeue();
         targetAttributeType = this._data._nodes[interpIdx + 17];
         if (this.isMatrixTarget(targetAttributeType)) {
            if (!valuesSaved) {
               valuesLength = 6;
               System.arraycopy(this._data._nodes, targetIdx, this._prevValues, 0, valuesLength);
               valuesSaved = true;
            }

            this.setMatrixTarget(targetIdx, interpIdx, i == 0);
         } else if (this.isMaskTarget(interpIdx)) {
            if (!valuesSaved) {
               valuesLength = 1;
               this._prevValues[0] = this._data._nodes[targetIdx];
               valuesSaved = true;
            }

            this.setMaskTarget(targetIdx, interpIdx, i == 0);
            if (targetAttributeType == 1) {
               int ii = targetIdx;
               this._data._nodes[ii] = this._data._nodes[ii] | 64;
            }
         } else {
            if (!valuesSaved) {
               valuesLength = this.getKeyValueLength(targetAttributeType);
               System.arraycopy(this._data._nodes, targetIdx, this._prevValues, 0, valuesLength);
               valuesSaved = true;
            }

            this.setScalarTarget(targetIdx, interpIdx, targetAttributeType, i == 0);
         }

         mq.enqueue(interpIdx);
      }

      for (int i = 0; i < valuesLength; i++) {
         if (this._prevValues[i] != this._data._nodes[targetIdx + i]) {
            bUpdateRequired = true;
            break;
         }
      }

      if (bUpdateRequired) {
         this.setDirty(this._data._nodes[interpIdx + 18], targetAttributeType);
      }

      return bUpdateRequired;
   }

   private void resetOMMaskTarget(int targetIndex, int mask, int interpIdx) {
      int omValuesIdx = this._data._nodes[interpIdx + 20];
      int value = this._data._omValues[omValuesIdx][0] & mask;
      this._data._nodes[targetIndex] = this._data._nodes[targetIndex] & ~mask;
      this._data._nodes[targetIndex] = this._data._nodes[targetIndex] | value;
   }

   private void setMaskTarget(int targetIndex, int interpIdx, boolean firstPass) {
      int value = this._data._nodes[interpIdx + 21];
      int mask = this._data._nodes[interpIdx + 29];
      int shift = this._data._nodes[interpIdx + 30];
      if (firstPass) {
         this.resetOMMaskTarget(targetIndex, mask, interpIdx);
         if (mask == 128) {
            this.resetOMMaskTarget(targetIndex, 64, interpIdx);
         }
      }

      this._data._nodes[targetIndex] = this._data._nodes[targetIndex] & ~mask;
      this._data._nodes[targetIndex] = this._data._nodes[targetIndex] | value << shift;
   }

   private void resetOMScalarTarget(int targetIdx, int interpIdx) {
      int omValuesIdx = this._data._nodes[interpIdx + 20];
      int size = this._data._omValues[omValuesIdx].length;
      if (size > 1) {
         System.arraycopy(this._data._omValues[omValuesIdx], 0, this._data._nodes, targetIdx, size);
      } else {
         this._data._nodes[targetIdx] = this._data._omValues[omValuesIdx][0];
      }
   }

   private void setScalarTarget(int targetIndex, int interpIdx, int targetAttributeType, boolean firstPass) {
      int length = this.getKeyValueLength(targetAttributeType);
      if (firstPass) {
         this.resetOMScalarTarget(targetIndex, interpIdx);
      }

      if (!this._data.bitsAreSet(interpIdx, 4096)) {
         for (int i = 0; i < length; i++) {
            int currentValue = this._data._nodes[interpIdx + 21 + i];
            this._data._nodes[targetIndex + i] = currentValue;
         }
      } else {
         if (this._data._nodes[targetIndex] == Integer.MAX_VALUE) {
            int[] returnValues = new int[length];
            this.getRelativeValues(interpIdx, targetIndex, targetAttributeType, returnValues);

            for (int i = 0; i < length; i++) {
               this._data._nodes[targetIndex + i] = returnValues[i];
            }
         }

         for (int i = 0; i < length; i++) {
            int currentValue = this._data._nodes[interpIdx + 21 + i];
            if (targetAttributeType != 3 && targetAttributeType != 5) {
               this._data._nodes[targetIndex + i] = this._data._nodes[targetIndex + i] + currentValue;
            } else {
               int oldModelValue = this._data._nodes[targetIndex + i];
               int newModelValue;
               if (currentValue == 0) {
                  newModelValue = oldModelValue;
               } else {
                  newModelValue = 0;

                  for (int j = 0; j < 3; j++) {
                     int currentColor = (oldModelValue >> 8 * j & 0xFF) + (currentValue >> 8 * j & 0xFF);
                     if (currentColor > 255) {
                        currentColor = 255;
                     }

                     newModelValue |= currentColor << 8 * j;
                  }
               }

               this._data._nodes[targetIndex + i] = newModelValue;
            }
         }
      }
   }

   private void resetOMMatrixTarget(int targetIndex, int interpIdx) {
      int omValuesIdx = this._data._nodes[interpIdx + 20];
      System.arraycopy(this._data._omValues[omValuesIdx], 0, this._data._nodes, targetIndex, 9);
   }

   private void setMatrixTarget(int targetIndex, int interpIdx, boolean firstPass) {
      int targetAttributeType = this._data._nodes[interpIdx + 17];
      if (firstPass) {
         this.resetOMMatrixTarget(targetIndex, interpIdx);
      }

      int[] tempResult = this.generateMatrix(targetAttributeType, this._data._nodes, interpIdx + 21);
      if (this._data.bitsAreSet(interpIdx, 4096)) {
         this._platform.matrixMultiply(this._data._nodes, targetIndex, tempResult, 0, this._tempMatrix, 0);
         tempResult = this._tempMatrix;
      }

      System.arraycopy(tempResult, 0, this._data._nodes, targetIndex, 9);
   }

   private int[] generateMatrix(int targetAttributeType, int[] values, int offset) {
      this._platform.setIdentity(this._tempMatrix, 0);
      switch (targetAttributeType) {
         case 13:
         case 21:
            break;
         case 14:
         default:
            this._platform.setTranslateMatrix(values[offset], 0, this._tempMatrix);
            break;
         case 15:
            this._platform.setTranslateMatrix(0, values[offset], this._tempMatrix);
            break;
         case 16:
            this._platform.setTranslateMatrix(values[offset], values[offset + 1], this._tempMatrix);
            break;
         case 17:
            this._platform.setSkewMatrix(values[offset], 0, this._tempMatrix);
            break;
         case 18:
            this._platform.setSkewMatrix(0, values[offset], this._tempMatrix);
            break;
         case 19:
            this._platform.setSkewMatrix(values[offset], values[offset + 1], this._tempMatrix);
            break;
         case 20:
            this._platform.setScaleMatrix(values[offset], values[offset], this._tempMatrix);
            break;
         case 22:
            this._platform.setScaleMatrix(values[offset], values[offset + 1], this._tempMatrix);
            break;
         case 23:
            this._platform.setRotateMatrix(values[offset], 0, 0, this._tempMatrix);
            break;
         case 24:
            this._platform.setRotateMatrix(values[offset], values[offset + 1], values[offset + 2], this._tempMatrix);
      }

      this._tempMatrix[8] = 65536;
      return this._tempMatrix;
   }

   private boolean setDirty(int vnodeIdx, int attribute) {
      boolean affectTSpans = false;
      switch (attribute) {
         case -1:
         case 13:
         case 21:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
            this._data._nodes[vnodeIdx + 8] = this._data._nodes[vnodeIdx + 8] | 0xFF000000;
            affectTSpans = true;
            break;
         case 0:
            this._data.setBits(vnodeIdx, 1073741824);
            break;
         case 1:
         case 42:
         default:
            this._data.setBits(vnodeIdx, 16777216);
            break;
         case 2:
            this._data.setBits(vnodeIdx, 1610612736);
            break;
         case 3:
            this._data.setBits(vnodeIdx, 536870912);
            break;
         case 4:
            this._data.setBits(vnodeIdx, 1342177280);
            break;
         case 5:
            this._data.setBits(vnodeIdx, 268435456);
            break;
         case 6:
         case 7:
            this._data.setBits(vnodeIdx, 67108864);
            affectTSpans = true;
            break;
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 22:
         case 23:
         case 24:
            this._data.setBits(vnodeIdx, 134217728);
            affectTSpans = true;
            this._data._nodes[vnodeIdx + 15]++;
            break;
         case 25:
         case 26:
         case 29:
            this._data.setBits(vnodeIdx, 134217728);
            this._data._nodes[vnodeIdx + 15]++;
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 27:
         case 28:
         case 30:
            this._data.setBits(vnodeIdx, 33554432);
      }

      this._data._nodes[vnodeIdx + 16]++;
      if (this._data._nodes[vnodeIdx + 1] == 32 && affectTSpans) {
         for (int nextIdx = this._data._nodes[vnodeIdx + 4]; nextIdx != -1 && this._data._nodes[nextIdx + 1] == 32; nextIdx = this._data._nodes[nextIdx + 4]) {
            this._data._nodes[nextIdx + 16]++;
            this._data._nodes[nextIdx + 8] = this._data._nodes[nextIdx + 8] | 0xFF000000;
         }
      }

      return true;
   }

   private void growTargetList(int growth) {
      int[] newTargetList = new int[this._targetList.length + growth];

      for (int i = 0; i < this._targetList.length; i++) {
         newTargetList[i] = this._targetList[i];
      }

      for (int i = this._targetList.length; i < newTargetList.length; i++) {
         newTargetList[i] = -1;
      }

      MediaQueue[] newInterpolatorList = new MediaQueue[this._interpolatorList.length + growth];

      for (int i = 0; i < this._interpolatorList.length; i++) {
         newInterpolatorList[i] = this._interpolatorList[i];
      }

      for (int i = this._interpolatorList.length; i < newInterpolatorList.length; i++) {
         newInterpolatorList[i] = new MediaQueue(5, 5);
      }

      this._targetList = newTargetList;
      this._interpolatorList = newInterpolatorList;
   }

   private int findInList(int[] list, int num) {
      int result = -1;

      for (int i = 0; i < list.length; i++) {
         if (list[i] == num) {
            return i;
         }
      }

      return result;
   }

   public void reset() {
      int numTargetsBeforeLoop = this._numActiveTargets;
      int index = 0;

      for (int targetsFound = 0; targetsFound < numTargetsBeforeLoop; index++) {
         if (this._targetList[index] != -1) {
            this._interpolatorList[index].reset();
            this._targetList[index] = -1;
            targetsFound++;
         }
      }

      this._numActiveTargets = 0;
   }

   public void setMedia(Object media) {
      this._data = (AnimationModel)media;
      this.reset();
   }

   private boolean isMaskTarget(int interpIdx) {
      int targetAttributeType = this._data._nodes[interpIdx + 17];
      return this.isMaskAttributeType(targetAttributeType);
   }

   private boolean isMaskAttributeType(int targetAttributeType) {
      switch (targetAttributeType) {
         case 1:
         case 31:
         case 32:
         case 33:
         case 35:
         case 36:
         case 42:
            return true;
         default:
            return false;
      }
   }

   private boolean isMatrixTarget(int targetAttributeType) {
      switch (targetAttributeType) {
         case 13:
         case 21:
            return false;
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 22:
         case 23:
         case 24:
         default:
            return true;
      }
   }

   private void getDefaultAttributeValues(int targetAttributeType, int[] returnValues) {
      int length = this.getKeyValueLength(targetAttributeType);
      switch (targetAttributeType) {
         case 2:
            returnValues[0] = 255;
            return;
         case 3:
            for (int i = 0; i < length; i++) {
               returnValues[i] = 0;
            }
            break;
         case 4:
            returnValues[0] = 255;
            return;
         case 5:
            for (int i = 0; i < length; i++) {
               returnValues[i] = Integer.MIN_VALUE;
            }
            break;
         case 30:
            returnValues[0] = Fixed32.toFP(1);
            return;
         case 35:
            returnValues[0] = 16;
            return;
         case 36:
            returnValues[0] = 1;
            return;
         case 37:
            returnValues[0] = -1;
            return;
         case 38:
            returnValues[0] = Fixed32.toFP(12);
            return;
         case 39:
            returnValues[0] = 1;
            return;
         case 40:
            returnValues[0] = 400;
            return;
         case 41:
            returnValues[0] = 1;
      }
   }

   int getKeyValueLength(int targetAttributeType) {
      switch (targetAttributeType) {
         case 16:
         case 19:
         case 22:
            return 2;
         case 24:
            return 3;
         case 29:
            return 4;
         default:
            return 1;
      }
   }

   private void getMatrixValues(int targetAttributeType, int[] matrixArray, int matrixIndex, int[] values) {
      this._matrixDecomp = this._platform.createTransformationMatrix(matrixArray, matrixIndex);
      if (this._matrixDecomp == null) {
         throw new Object("Failed to decompose transformation matrix");
      }

      switch (targetAttributeType) {
         case 14:
         default:
            values[0] = this._matrixDecomp.getTranslateX();
            return;
         case 15:
            values[0] = this._matrixDecomp.getTranslateY();
            return;
         case 16:
            values[0] = this._matrixDecomp.getTranslateX();
            values[1] = this._matrixDecomp.getTranslateY();
            return;
         case 17:
            values[0] = this._matrixDecomp.getSkewX();
            return;
         case 18:
            values[0] = this._matrixDecomp.getSkewY();
            return;
         case 20:
            values[0] = this._matrixDecomp.getScaleX();
            return;
         case 22:
            values[0] = this._matrixDecomp.getScaleX();
            values[1] = this._matrixDecomp.getScaleY();
            return;
         case 23:
            values[0] = this._matrixDecomp.getTheta();
            return;
         case 24:
            values[0] = this._matrixDecomp.getTheta();
            values[1] = this._matrixDecomp.getTranslateX();
            values[2] = this._matrixDecomp.getTranslateY();
         case 13:
         case 19:
         case 21:
      }
   }
}
