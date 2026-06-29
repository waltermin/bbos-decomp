package net.rim.tid.im.conv.repository.OTAReaders;

import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.OTAsync.OTASyncableCustomWordsProvider;
import net.rim.tid.im.conv.europe.spellcheck.PairLearningReader;

public class FastEuropeanPairLearningOTAReader extends PairLearningReader implements OTASyncableCustomWordsProvider {
   private boolean _isDataStorageCreated;

   @Override
   public void init(byte type) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public int getWordsCount() {
      return this.getEntryNo();
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
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
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
   public boolean loadLearningData(byte[] data) {
      if (data == null) {
         return false;
      }

      super._complexPrefixTables = null;
      if (data.length > super._sizeLimit) {
         super._sizeLimit = data.length;
      }

      try {
         super._header.reset(null);
         super.loadLearningWordlist(data);
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
   public int addWord(String word, char freq) {
      return this.addPair(word, (byte)freq);
   }
}
