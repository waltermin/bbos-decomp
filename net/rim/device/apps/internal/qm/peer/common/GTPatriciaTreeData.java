package net.rim.device.apps.internal.qm.peer.common;

public interface GTPatriciaTreeData {
   int NULL_ID;

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

   void optimize();

   void removeAll();

   int addAndGetIndex(Object var1);

   void remove(Object var1);

   boolean contains(Object var1);
}
