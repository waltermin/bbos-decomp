package net.rim.tid.im.conv.europe.repository;

import net.rim.tid.data.LearningData;
import net.rim.tid.data.LearningDataManager;

public class AddressBookReader extends WordLearningReader {
   private static final int MIN_INITIAL_SIZE;
   private static final int MAX_BUFFER_SIZE;

   public AddressBookReader() {
      super.init((byte)3);
      super._currentLearnName = "AddrBook";
      super._header.setLocaleStr(super._currentLearnName);
      LearningData data = LearningDataManager.getLearningData(super._currentLearnName);
      if (data != null && data.getData() != null) {
         super._data = data.getData();
         super._sizeLimit = super._data.length;
      } else {
         super._sizeLimit = 20480;
         super._data = this.createLearningWordlist();
         data = (LearningData)(new Object(super._data));
         LearningDataManager.setLearningData(super._currentLearnName, data, true);
      }

      this.loadLearningWordlist(super._data);
      super._ignoreTimeStamps = true;
   }

   public void commit() {
      LearningData data = LearningDataManager.getLearningData(super._currentLearnName);
      if (data != null) {
         LearningDataManager.commit();
      }
   }

   @Override
   public void clearAll() {
      super._data = null;
      LearningData data = LearningDataManager.getLearningData(super._currentLearnName);
      if (data != null) {
         data.setData(null);
         LearningDataManager.setLearningData(super._currentLearnName, data, true);
      }
   }

   public void init(int aSizeLimit) {
      if (aSizeLimit > super._sizeLimit) {
         super._sizeLimit = aSizeLimit;
         super._data = this.createLearningWordlist();
         LearningData data = LearningDataManager.getLearningData(super._currentLearnName);
         if (data != null) {
            data.setData(super._data);
         }

         this.loadLearningWordlist(super._data);
      }
   }

   @Override
   protected synchronized void updateLearningData() {
      LearningData data = LearningDataManager.getLearningData(super._currentLearnName);
      if (data != null && data.getData() != null) {
         super._data = data.getData();
         super._sizeLimit = super._data.length;
         this.loadLearningWordlist(super._data);
      }
   }

   public void setOverrideListenersMode(boolean isEnabled) {
      super._alphabet.setOverrideListenersMode(isEnabled);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean ensureSize(int aPotentialAdd) {
      int fullSize = this.getFileSize();
      if (fullSize + aPotentialAdd <= super._sizeLimit) {
         return super.ensureSize(aPotentialAdd);
      }

      if (super._sizeLimit >= 51200) {
         return false;
      }

      try {
         this.setSizeLimit(51200);
         return super.ensureSize(aPotentialAdd);
      } catch (Throwable var5) {
         e.printStackTrace();
         return super.ensureSize(aPotentialAdd);
      }
   }
}
