package net.rim.tid.awt.im.repository;

public interface CustomWordsRepository {
   int ADDRESS_BOOK_REPOSITORY = 1;
   int EUROPEAN_READERS_REPOSITORY_DATA = 2;
   int MAIL_EXTRACTOR_REPOSITORY = 3;
   int FREQUENCY_LEARNING_REPOSITORY = 4;
   int CHINESE_REPOSITORY_DATA = 5;
   int YOMI_ADDRESS_BOOK_REPOSITORY = 6;
   byte LOW_PRIORITY = 1;
   byte NORMAL_PRIORITY = 2;
   int NO = 0;
   int YES = 1;
   int YES_WORD = 2;
   int YES_PREFIX = 3;

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
