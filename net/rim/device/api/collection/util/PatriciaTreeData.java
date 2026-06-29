package net.rim.device.api.collection.util;

public interface PatriciaTreeData {
   int NULL_ID = -1;

   int getBit(int var1, int var2);

   int compareBits(int var1, int var2);

   int getPrefixBit(Object var1, int var2);

   boolean prefixMatches(Object var1, int var2);

   int size();

   int getBitNumber(int var1);

   int getLeftNodes(int var1);

   int getLeaf(int var1);

   void adjustLeftNodes(int var1, int var2);

   void insert(int var1, int var2, int var3, int var4, int var5);

   void delete(int var1, int var2);

   void recordFound(Object var1, int var2, int var3);

   void dumpLeaf(int var1);
}
