package net.rim.device.apps.internal.addressbook;

import java.util.Enumeration;
import net.rim.device.api.collection.util.BigIntVector;
import net.rim.device.api.collection.util.BigLongVector;
import net.rim.device.api.collection.util.SparseList;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class AddressBookData implements Persistable {
   private SparseList _cards;
   private BigLongVector _uidToIDMap;
   private BigIntVector _nodes;
   private BigIntVector _leaves;
   private BigIntVector _reverseLookupNodes;
   private BigLongVector _reverseLookupLeaves;
   private boolean _tablesValid;
   private Object[] _contentProtectionQueue;
   private static final int BIT_NUMBER_MASK = 65535;
   private static final int BIT_NUMBER_SHIFT = 16;
   private static final int LEFT_SIZE_MASK = 65535;

   AddressBookData() {
      this.reset();
   }

   final void commit(boolean force) {
      if (force) {
         PersistentObject.forceCommit(this);
      } else {
         PersistentObject.commit(this);
      }
   }

   final void reset() {
      this._cards = new SparseList();
      this._uidToIDMap = new BigLongVector();
      this.resetTables();
      this._tablesValid = true;
      this._contentProtectionQueue = null;
   }

   final void resetTables() {
      this._nodes = new BigIntVector();
      this._leaves = new BigIntVector();
      this._reverseLookupNodes = new BigIntVector();
      this._reverseLookupLeaves = new BigLongVector();
   }

   final void optimize() {
      this._uidToIDMap.optimize();
      this._nodes.optimize();
      this._leaves.optimize();
      this._reverseLookupNodes.optimize();
      this._reverseLookupLeaves.optimize();
   }

   final void setTablesValid(boolean valid) {
      this._tablesValid = valid;
   }

   final boolean getTablesValid() {
      return this._tablesValid;
   }

   final Object[] getContentProtectionQueue() {
      return this._contentProtectionQueue;
   }

   final void clearContentProtectionQueue() {
      this._contentProtectionQueue = null;
   }

   final void queueContentProtectionRecord(Object removeObject, Object addObject) {
      if (this._contentProtectionQueue == null) {
         this._contentProtectionQueue = new Object[0];
      }

      Arrays.add(this._contentProtectionQueue, removeObject);
      Arrays.add(this._contentProtectionQueue, addObject);
   }

   final int size() {
      return this._cards.size();
   }

   final Enumeration elements() {
      return this._cards.elements();
   }

   final SparseList getAddressCards() {
      return this._cards;
   }

   final int getInternalID(int uid) {
      long key = (long)uid << 32;
      int index = this._uidToIDMap.binarySearch(key);
      if (index < 0) {
         index = -(index + 1);
      }

      if (index < this._uidToIDMap.size()) {
         long element = this._uidToIDMap.elementAt(index);
         if ((element & -4294967296L) == key) {
            return (int)(element & 4294967295L);
         }
      }

      return -1;
   }

   final Object getElement(int internalID) {
      return this._cards.get(internalID);
   }

   final synchronized int add(int uid, Object element) {
      int internalID = this._cards.addAndGetIndex(element);
      long key = (long)uid << 32 | internalID & 4294967295L;
      int index = this._uidToIDMap.binarySearch(key);
      if (index < 0) {
         index = -(index + 1);
      }

      this._uidToIDMap.insertElementAt(key, index);
      return internalID;
   }

   final synchronized void remove(int uid, int internalID) {
      this._cards.removeAt(internalID);
      long key = (long)uid << 32 | internalID & 4294967295L;
      int index = this._uidToIDMap.binarySearch(key);
      if (index >= 0) {
         this._uidToIDMap.removeElementAt(index);
      }
   }

   final synchronized void update(int internalID, Object element) {
      this._cards.insertAt(internalID, element);
   }

   final int getPatriciaSize() {
      return this._leaves.size();
   }

   final int getBitNumber(int nodeIndex) {
      return this._nodes.elementAt(nodeIndex) >> 16 & 65535;
   }

   final int getLeftNodes(int nodeIndex) {
      return this._nodes.elementAt(nodeIndex) & 65535;
   }

   final int getLeaf(int leafIndex) {
      return this._leaves.elementAt(leafIndex);
   }

   final BigIntVector getLeaves() {
      return this._leaves;
   }

   final void adjustLeftNodes(int nodeIndex, int adjustment) {
      int node = this._nodes.elementAt(nodeIndex);
      this._nodes.setElementAt(node + adjustment, nodeIndex);
   }

   final void insert(int leftNodes, int bitNum, int nodeIndex, int id, int leafIndex) {
      this._nodes.insertElementAt(bitNum << 16 | leftNodes, nodeIndex);
      this._leaves.insertElementAt(id, leafIndex);
   }

   final void delete(int nodeIndex, int leafIndex) {
      this._nodes.removeElementAt(nodeIndex);
      this._leaves.removeElementAt(leafIndex);
   }

   final int getReverseLookupSize() {
      return this._reverseLookupLeaves.size();
   }

   final int getReverseLookupBitNumber(int nodeIndex) {
      return this._reverseLookupNodes.elementAt(nodeIndex) >> 16 & 65535;
   }

   final int getReverseLookupLeftNodes(int nodeIndex) {
      return this._reverseLookupNodes.elementAt(nodeIndex) & 65535;
   }

   final long getReverseLookupLeaf(int leafIndex) {
      return this._reverseLookupLeaves.elementAt(leafIndex);
   }

   final void adjustReverseLookupLeftNodes(int nodeIndex, int adjustment) {
      int node = this._reverseLookupNodes.elementAt(nodeIndex);
      this._reverseLookupNodes.setElementAt(node + adjustment, nodeIndex);
   }

   final void insertReverseLookup(int leftNodes, int bitNum, int nodeIndex, long id, int leafIndex) {
      this._reverseLookupNodes.insertElementAt(bitNum << 16 | leftNodes, nodeIndex);
      this._reverseLookupLeaves.insertElementAt(id, leafIndex);
   }

   final void deleteReverseLookup(int nodeIndex, int leafIndex) {
      this._reverseLookupNodes.removeElementAt(nodeIndex);
      this._reverseLookupLeaves.removeElementAt(leafIndex);
   }
}
