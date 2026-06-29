package net.rim.tid.awt.im.repository;

public interface CustomWordsRepository {
   int ADDRESS_BOOK_REPOSITORY;
   int EUROPEAN_READERS_REPOSITORY_DATA;
   int MAIL_EXTRACTOR_REPOSITORY;
   int FREQUENCY_LEARNING_REPOSITORY;
   int CHINESE_REPOSITORY_DATA;
   int YOMI_ADDRESS_BOOK_REPOSITORY;
   byte LOW_PRIORITY;
   byte NORMAL_PRIORITY;
   int NO;
   int YES;
   int YES_WORD;
   int YES_PREFIX;

   void init(int var1);

   void addWords(Object var1);

   void removeWords(Object var1);

   void addWords(StringBuffer var1);

   void removeWords(StringBuffer var1);

   int addWord(char[] var1, int var2);

   int addWord(char[] var1, int var2, byte var3);

   int addWord(char[] var1, int var2, byte var3, int var4);

   int removeWord(char[] var1, int var2);

   boolean containsCharInAlphabet(char var1);

   int containsWord(char[] var1, int var2);

   void clear();

   void commit();

   int getType();
}
