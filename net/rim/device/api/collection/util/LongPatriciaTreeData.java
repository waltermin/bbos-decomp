package net.rim.device.api.collection.util;

public interface LongPatriciaTreeData {
   int NULL_ID;

   int getBit(long var1, int var3);

   int compareBits(long var1, long var3);

   int getPrefixBit(Object var1, int var2);

   boolean prefixMatches(Object var1, long var2);

   int size();

   int getBitNumber(int var1);

   int getLeftNodes(int var1);

   long getLeaf(int var1);

   void adjustLeftNodes(int var1, int var2);

   void insert(int var1, int var2, int var3, long var4, int var6);

   void delete(int var1, int var2);

   void recordFound(Object var1, int var2, int var3);

   void dumpLeaf(long var1);
}
