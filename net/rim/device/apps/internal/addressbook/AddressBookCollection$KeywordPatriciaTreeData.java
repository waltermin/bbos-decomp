package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.collection.util.PatriciaKeywordFilterList;
import net.rim.device.api.collection.util.PatriciaKeywordSearchResult;
import net.rim.device.api.collection.util.PatriciaTree;
import net.rim.device.api.collection.util.PatriciaTreeData;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

final class AddressBookCollection$KeywordPatriciaTreeData implements PatriciaTreeData {
   private final AddressBookCollection this$0;

   private AddressBookCollection$KeywordPatriciaTreeData(AddressBookCollection _1) {
      this.this$0 = _1;
   }

   @Override
   public final int getBit(int id, int bitNum) {
      int internalID = AddressBookCollection.getInternalIDFromKey(id);
      int offset = AddressBookCollection.getOffsetFromKey(id);
      String string = this.this$0.fetchAndCacheKeywords(internalID, null);
      int length = AddressBookCollection.getLengthFromKey(id, string.length() - offset);
      return PatriciaTree.getStringBit(string, offset, length, id, bitNum);
   }

   @Override
   public final int compareBits(int lookupId, int id) {
      int internalID = AddressBookCollection.getInternalIDFromKey(id);
      int offset = AddressBookCollection.getOffsetFromKey(id);
      Object addressCard = this.this$0._data.getElement(internalID);
      String string = this.this$0.fetchKeywords(addressCard, null);
      int length = AddressBookCollection.getLengthFromKey(id, string.length() - offset);
      int lookupInternalID = AddressBookCollection.getInternalIDFromKey(lookupId);
      int lookupOffset = AddressBookCollection.getOffsetFromKey(lookupId);
      String lookupString = this.this$0.fetchAndCacheKeywords(lookupInternalID, null);
      int lookupLength = AddressBookCollection.getLengthFromKey(lookupId, lookupString.length() - lookupOffset);
      return PatriciaTree.compareStringBits(lookupString, lookupOffset, lookupLength, lookupId, string, offset, length, id);
   }

   @Override
   public final int getPrefixBit(Object prefix, int bitNum) {
      String string = (String)prefix;
      return PatriciaTree.getStringBit(string, 0, string.length(), -1, bitNum);
   }

   @Override
   public final boolean prefixMatches(Object prefix, int id) {
      String prefixString = (String)prefix;
      int internalID = AddressBookCollection.getInternalIDFromKey(id);
      int offset = AddressBookCollection.getOffsetFromKey(id);
      String string = this.this$0.fetchAndCacheKeywords(internalID, null);
      int length = AddressBookCollection.getLengthFromKey(id, string.length() - offset);
      string = string.substring(offset, offset + length);
      return prefixString.length() > length ? false : StringUtilities.startsWithIgnoreCaseAndAccents(string, prefixString);
   }

   @Override
   public final int size() {
      return this.this$0._data.getPatriciaSize();
   }

   @Override
   public final int getBitNumber(int nodeIndex) {
      return this.this$0._data.getBitNumber(nodeIndex);
   }

   @Override
   public final int getLeftNodes(int nodeIndex) {
      return this.this$0._data.getLeftNodes(nodeIndex);
   }

   @Override
   public final int getLeaf(int leafIndex) {
      return this.this$0._data.getLeaf(leafIndex);
   }

   @Override
   public final void adjustLeftNodes(int nodeIndex, int adjustment) {
      this.this$0._data.adjustLeftNodes(nodeIndex, adjustment);
   }

   @Override
   public final void insert(int leftNodes, int bitNum, int nodeIndex, int id, int leafIndex) {
      this.this$0._data.insert(leftNodes, bitNum, nodeIndex, id, leafIndex);
   }

   @Override
   public final void delete(int nodeIndex, int leafIndex) {
      this.this$0._data.delete(nodeIndex, leafIndex);
   }

   @Override
   public final void recordFound(Object result, int offset, int length) {
      if (!(result instanceof Object)) {
         if (result instanceof AddressBookCollection$LeafRange) {
            AddressBookCollection$LeafRange leafRange = (AddressBookCollection$LeafRange)result;
            leafRange._offset = offset;
            leafRange._length = length;
         }
      } else {
         PatriciaKeywordSearchResult searchResult = (PatriciaKeywordSearchResult)result;
         PatriciaKeywordFilterList list = searchResult._list;
         int currentKeyID = this.this$0.getKeyIDFromSortOrder(this.this$0._sortOrder);
         BitSet primarySet = searchResult._primarySet;
         BitSet theSet = searchResult._theSet;
         byte[] hitCount = searchResult._hitCount;
         int i = 0;

         while (i < length) {
            int id = this.getLeaf(offset);
            int internalID = AddressBookCollection.getInternalIDFromKey(id);
            if (searchResult._wordNumber == 0) {
               int keyID = AddressBookCollection.getKeyIDFromKey(id);
               if ((keyID & currentKeyID) != 0) {
                  primarySet.fastSet(internalID);
               }
            }

            theSet.fastSet(internalID);
            if (hitCount != null) {
               if (hitCount.length <= internalID) {
                  Array.resize(hitCount, internalID + 1024);
               }

               hitCount[internalID]++;
            }

            if ((i & 15) == 0 && list._haltSearch) {
               return;
            }

            i++;
            offset++;
         }
      }
   }

   @Override
   public final void dumpLeaf(int id) {
      int internalID = AddressBookCollection.getInternalIDFromKey(id);
      int offset = AddressBookCollection.getOffsetFromKey(id);
      String string = this.this$0.fetchAndCacheKeywords(internalID, null);
      int length = AddressBookCollection.getLengthFromKey(id, string.length() - offset);
      int keyID = AddressBookCollection.getKeyIDFromKey(id);
      string = string.substring(offset, offset + length);
      System.out.println(((StringBuffer)(new Object(""))).append(keyID).append("   ").append(string).toString());
   }

   AddressBookCollection$KeywordPatriciaTreeData(AddressBookCollection x0, AddressBookCollection$1 x1) {
      this(x0);
   }
}
