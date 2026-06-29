package net.rim.tid.im.conv.repository.OTAReaders;

import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.OTAsync.OTASyncableCustomWordsProvider;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.europe.repository.WordLearningReader;

public class FastEuropeanWordLearningOTAReader extends WordLearningReader implements OTASyncableCustomWordsProvider {
   private boolean _isDataStorageCreated;

   public FastEuropeanWordLearningOTAReader() {
      this.enableOTAListener(false);
   }

   @Override
   public int getWordsCount() {
      return this.size();
   }

   @Override
   public String getDataStorageName() {
      return super._currentLearnName;
   }

   @Override
   public byte getType() {
      return super._type;
   }

   @Override
   public byte[] getData() {
      return super.getData();
   }

   @Override
   public void init(byte type) {
      super.init(type);
      this.enableOTAListener(false);
   }

   @Override
   public boolean isDataStorageCreated() {
      return this._isDataStorageCreated;
   }

   @Override
   public int getWords(CustomWordsSyncManager manager) {
      if (manager != null && super._sizeLimit > 0) {
         int count = manager.size();

         try {
            super._complexPrefixTables[0].getEntries(manager, super._wordBuffer, super._header.getLocaleStr());
         } finally {
            return manager.size() - count;
         }

         return manager.size() - count;
      } else {
         return 0;
      }
   }

   @Override
   public boolean loadLearningData(byte[] aData) {
      if (aData == null) {
         return false;
      }

      super._complexPrefixTables = null;
      if (aData.length > super._sizeLimit) {
         super._sizeLimit = aData.length;
      }

      try {
         super._header.reset(null);
         super.loadLearningWordlist(aData);
      } finally {
         ;
      }

      this.resetPrivateMembers();
      return true;
   }

   @Override
   protected synchronized void modifyLearningData(boolean dataChanged) {
      super._header.update();
      super._alphabet.update();
      this.loadLearningWordlist(super._data);
   }

   @Override
   public boolean createLearningData(String key) {
      super._currentLearnName = key;
      super._sizeLimit = LearningDataManager.getType(key) == 3 ? 7168 : 5120;

      try {
         byte[] data = super.createLearningWordlist();
         super.loadLearningWordlist(data);
      } finally {
         ;
      }

      this._isDataStorageCreated = true;
      return true;
   }

   @Override
   public int trim(int desiredSize) {
      super._internalEvent = (byte)(super._internalEvent | 2);
      CustomWordsSyncManager sm = new CustomWordsSyncManager(2);
      int res = this.trim(this.getTrimControllerForTrim(super._type == 3 ? 7 : 3, sm));
      if (super._listener != null && sm.size() > 0 && res >= desiredSize) {
         super._listener.remove(sm);
      }

      return res;
   }

   @Override
   public int addWord(String word, char freq) {
      return super.addWord(word, freq, false);
   }
}
