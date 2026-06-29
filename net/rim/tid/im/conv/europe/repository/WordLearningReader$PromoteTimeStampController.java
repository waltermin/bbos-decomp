package net.rim.tid.im.conv.europe.repository;

class WordLearningReader$PromoteTimeStampController extends TrimController {
   @Override
   public boolean isTrimmingFinished() {
      return false;
   }

   @Override
   public int getNewLeastTimeStamp() {
      return super._outdatedIntervalEnd;
   }

   @Override
   public void complexTableFinished(int aComplexTableId, int aLastPos) {
      super._leastTimeStamp = super._outdatedIntervalEnd;
   }

   @Override
   public boolean allowSecondPass() {
      return false;
   }

   @Override
   public boolean leastTimeStampChanged() {
      return true;
   }

   @Override
   public byte getAction() {
      return 2;
   }
}
