package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.collection.util.LongPatriciaTreeData;
import net.rim.device.api.collection.util.PatriciaTree;

final class AddressBookCollection$ReverseLookupPatriciaTreeData implements LongPatriciaTreeData {
   private final AddressBookCollection this$0;

   private AddressBookCollection$ReverseLookupPatriciaTreeData(AddressBookCollection _1) {
      this.this$0 = _1;
   }

   @Override
   public final int getBit(long id, int bitNum) {
      if (bitNum < 64) {
         return (id & (long)1 << 63 - bitNum) != 0 ? 1 : 0;
      } else {
         return -1;
      }
   }

   @Override
   public final int compareBits(long lookupId, long id) {
      if (lookupId == id) {
         return 0;
      }

      long compare = lookupId ^ id;
      if ((compare & -4294967296L) != 0) {
         return PatriciaTree.bitDifference(0, (int)(lookupId >> 32 & -1), (int)(id >> 32 & -1), 32);
      }

      int result = PatriciaTree.bitDifference(0, (int)(lookupId & -1), (int)(id & -1), 32);
      if (result < 0) {
         result -= 32;
      } else {
         result += 32;
      }

      return result;
   }

   @Override
   public final int getPrefixBit(Object prefix, int bitNum) {
      AddressBookCollection$ReverseLookupPrefix longPrefix = (AddressBookCollection$ReverseLookupPrefix)prefix;
      long value = longPrefix._value;
      if (bitNum < longPrefix._bits) {
         long mask = (long)1 << 63 - bitNum;
         return (value & mask) != 0 ? 1 : 0;
      } else {
         return -1;
      }
   }

   @Override
   public final boolean prefixMatches(Object prefix, long id) {
      AddressBookCollection$ReverseLookupPrefix longPrefix = (AddressBookCollection$ReverseLookupPrefix)prefix;
      long value = longPrefix._value;
      if (longPrefix._bits == 32) {
         if ((value & -4294967296L) == (id & -4294967296L)) {
            return true;
         }
      } else if (longPrefix._bits == 64) {
         if (value == id) {
            return true;
         }

         return false;
      }

      return false;
   }

   @Override
   public final int size() {
      return this.this$0._data.getReverseLookupSize();
   }

   @Override
   public final int getBitNumber(int nodeIndex) {
      return this.this$0._data.getReverseLookupBitNumber(nodeIndex);
   }

   @Override
   public final int getLeftNodes(int nodeIndex) {
      return this.this$0._data.getReverseLookupLeftNodes(nodeIndex);
   }

   @Override
   public final long getLeaf(int leafIndex) {
      return this.this$0._data.getReverseLookupLeaf(leafIndex);
   }

   @Override
   public final void adjustLeftNodes(int nodeIndex, int adjustment) {
      this.this$0._data.adjustReverseLookupLeftNodes(nodeIndex, adjustment);
   }

   @Override
   public final void insert(int leftNodes, int bitNum, int nodeIndex, long id, int leafIndex) {
      this.this$0._data.insertReverseLookup(leftNodes, bitNum, nodeIndex, id, leafIndex);
   }

   @Override
   public final void delete(int nodeIndex, int leafIndex) {
      this.this$0._data.deleteReverseLookup(nodeIndex, leafIndex);
   }

   @Override
   public final void recordFound(Object result, int offset, int length) {
      AddressBookCollection$LeafRange leafRange = (AddressBookCollection$LeafRange)result;
      leafRange._offset = offset;
      leafRange._length = length;
   }

   @Override
   public final void dumpLeaf(long id) {
   }

   AddressBookCollection$ReverseLookupPatriciaTreeData(AddressBookCollection x0, AddressBookCollection$1 x1) {
      this(x0);
   }
}
