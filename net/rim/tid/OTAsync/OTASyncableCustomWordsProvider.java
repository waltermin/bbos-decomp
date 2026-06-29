package net.rim.tid.OTAsync;

public interface OTASyncableCustomWordsProvider {
   int getWords(CustomWordsSyncManager var1);

   void init(byte var1);

   byte getType();

   boolean isDataStorageCreated();

   String getDataStorageName();

   byte[] getData();

   int getWordsCount();

   boolean loadLearningData(byte[] var1);

   boolean createLearningData(String var1);

   int addWord(String var1, char var2);
}
