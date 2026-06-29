package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.collection.util.SparseList;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringUtilities;

public final class ContactKeywordsData implements GTPatriciaTreeData {
   private IntVector _nodes = (IntVector)(new Object());
   private IntVector _leaves = (IntVector)(new Object());
   private SparseList _contactMap = (SparseList)(new Object());
   private static final int BIT_NUMBER_MASK;
   private static final int BIT_NUMBER_SHIFT;
   private static final int LEFT_SIZE_MASK;

   @Override
   public final void removeAll() {
      this._nodes.removeAllElements();
      this._leaves.removeAllElements();
   }

   @Override
   public final void optimize() {
   }

   @Override
   public final int getBit(int id, int bitNum) {
      Contact contact = this.getByID(id);
      int offset = GTPatriciaTreeHelper.getOffsetFromID(id);
      String keywords = contact.getKeywords();
      return GTPatriciaTreeHelper.getStringBit(keywords, offset, keywords.length() - offset, id, bitNum);
   }

   @Override
   public final int compareBits(int lookupId, int id) {
      Contact contact = this.getByID(id);
      int offset = GTPatriciaTreeHelper.getOffsetFromID(id);
      String keywords = contact.getKeywords();
      Contact lookupContact = this.getByID(lookupId);
      int lookupOffset = GTPatriciaTreeHelper.getOffsetFromID(lookupId);
      String lookupKeywords = lookupContact.getKeywords();
      return GTPatriciaTreeHelper.compareStringBits(
         lookupKeywords, lookupOffset, lookupKeywords.length() - lookupOffset, lookupId, keywords, offset, keywords.length() - offset, id
      );
   }

   @Override
   public final int getPrefixBit(Object prefix, int bitNum) {
      String string = (String)prefix;
      return GTPatriciaTreeHelper.getStringBit(string, 0, string.length(), -1, bitNum);
   }

   @Override
   public final boolean prefixMatches(Object prefix, int id) {
      String prefixString = (String)prefix;
      Contact contact = this.getByID(id);
      int offset = GTPatriciaTreeHelper.getOffsetFromID(id);
      String keywords = contact.getKeywords();
      keywords = keywords.substring(offset);
      return prefixString.length() > keywords.length() ? false : StringUtilities.startsWithIgnoreCaseAndAccents(keywords, prefixString);
   }

   @Override
   public final int size() {
      return this._leaves.size();
   }

   @Override
   public final int getBitNumber(int nodeIndex) {
      return this._nodes.elementAt(nodeIndex) >> 16 & 65535;
   }

   @Override
   public final int getLeftNodes(int nodeIndex) {
      return this._nodes.elementAt(nodeIndex) & 65535;
   }

   @Override
   public final int getLeaf(int leafIndex) {
      return this._leaves.elementAt(leafIndex);
   }

   @Override
   public final void adjustLeftNodes(int nodeIndex, int adjustment) {
      int node = this._nodes.elementAt(nodeIndex);
      this._nodes.setElementAt(node + adjustment, nodeIndex);
   }

   @Override
   public final void insert(int leftNodes, int bitNum, int nodeIndex, int id, int leafIndex) {
      this._nodes.insertElementAt(bitNum << 16 | leftNodes, nodeIndex);
      this._leaves.insertElementAt(id, leafIndex);
   }

   @Override
   public final void delete(int nodeIndex, int leafIndex) {
      this._nodes.removeElementAt(nodeIndex);
      this._leaves.removeElementAt(leafIndex);
   }

   @Override
   public final void recordFound(Object result, int offset, int length) {
      BitSet bitset = (BitSet)result;
      int i = 0;

      while (i < length) {
         int uid = GTPatriciaTreeHelper.getUIDFromID(this._leaves.elementAt(offset));
         bitset.fastSet(uid);
         i++;
         offset++;
      }
   }

   @Override
   public final int addAndGetIndex(Object contact) {
      return this._contactMap.addAndGetIndex(contact);
   }

   @Override
   public final void remove(Object contact) {
      this._contactMap.removeAt(((Contact)contact).getUID());
   }

   public final Contact getByID(int id) {
      return (Contact)this._contactMap.get(GTPatriciaTreeHelper.getUIDFromID(id));
   }

   @Override
   public final boolean contains(Object contact) {
      return this._contactMap.contains(((Contact)contact).getUID());
   }
}
