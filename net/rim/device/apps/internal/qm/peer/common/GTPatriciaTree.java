package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class GTPatriciaTree {
   private ContactListProvider _contactList;
   private GTPatriciaTreeData _data;
   private int[] _tmpOffsets;
   private String[] _tmpWords;
   private boolean _rebuildKeywordTree;
   private int _leafIndex;
   private int _numLeaves;

   public GTPatriciaTree(ContactListProvider contactList, GTPatriciaTreeData data) {
      if (data == null) {
         throw new Object();
      }

      this._data = data;
      this._contactList = contactList;
      this._tmpOffsets = new int[0];
      this._tmpWords = new Object[0];
   }

   public final void insert(int id) {
      synchronized (this._data) {
         this.doInsert(id);
      }
   }

   private final void doInsert(int id) {
      if (this._data.size() == 0) {
         this._data.insert(0, 0, 0, id, 0);
      } else {
         this.lookupId(id);
         int cmp = this._data.compareBits(id, this._data.getLeaf(this._leafIndex));
         if (cmp == 0) {
            throw new Object();
         }

         int insertBitNum;
         if (cmp > 0) {
            insertBitNum = cmp - 1;
         } else {
            insertBitNum = -cmp - 1;
         }

         int insertLeafIndex = this._leafIndex;
         int nodeIndex = 0;
         this._leafIndex = 0;
         this._numLeaves = this._data.size();

         while (this._numLeaves > 1) {
            int bitNum = this._data.getBitNumber(nodeIndex);
            int leftNodes = this._data.getLeftNodes(nodeIndex);
            if (bitNum > insertBitNum) {
               break;
            }

            if (insertLeafIndex <= this._leafIndex + leftNodes) {
               this._data.adjustLeftNodes(nodeIndex, 1);
               nodeIndex++;
               this._numLeaves = leftNodes + 1;
            } else {
               leftNodes++;
               this._numLeaves -= leftNodes;
               nodeIndex += leftNodes;
               this._leafIndex += leftNodes;
            }
         }

         int leftNodes;
         if (cmp > 0) {
            leftNodes = this._numLeaves - 1;
            this._leafIndex = this._leafIndex + this._numLeaves;
         } else {
            leftNodes = 0;
         }

         this._data.insert(leftNodes, insertBitNum, nodeIndex, id, this._leafIndex);
      }
   }

   public final void deleteLeaf(int deleteLeafIndex) {
      synchronized (this._data) {
         this._leafIndex = deleteLeafIndex;
         this._numLeaves = this._data.size();
         if (deleteLeafIndex < this._numLeaves) {
            int nodeIndex;
            if (this._numLeaves <= 1) {
               nodeIndex = 0;
            } else {
               nodeIndex = 0;
               this._leafIndex = 0;

               while (true) {
                  int leftNodes = this._data.getLeftNodes(nodeIndex);
                  if (deleteLeafIndex <= this._leafIndex + leftNodes) {
                     this._data.adjustLeftNodes(nodeIndex, -1);
                     this._numLeaves = leftNodes + 1;
                     if (this._numLeaves == 1) {
                        break;
                     }

                     nodeIndex++;
                  } else {
                     leftNodes++;
                     this._numLeaves -= leftNodes;
                     this._leafIndex += leftNodes;
                     if (this._numLeaves == 1) {
                        break;
                     }

                     nodeIndex += leftNodes;
                  }
               }
            }

            this._data.delete(nodeIndex, this._leafIndex);
         }
      }
   }

   public final int search(Object prefix, Object result) {
      synchronized (this._data) {
         this.lookupPrefix(prefix);
         this._data.recordFound(result, this._leafIndex, this._numLeaves);
         return this._numLeaves;
      }
   }

   private final void lookupPrefix(Object prefix) {
      int nodeIndex = 0;
      this._leafIndex = 0;
      this._numLeaves = this._data.size();
      if (this._numLeaves != 0 && prefix != null) {
         while (this._numLeaves > 1) {
            int bitNum = this._data.getBitNumber(nodeIndex);
            int leftNodes = this._data.getLeftNodes(nodeIndex);
            int bit = this._data.getPrefixBit(prefix, bitNum);
            if (bit < 0) {
               break;
            }

            if (bit == 0) {
               this._numLeaves = leftNodes + 1;
               nodeIndex++;
            } else {
               leftNodes++;
               this._numLeaves -= leftNodes;
               nodeIndex += leftNodes;
               this._leafIndex += leftNodes;
            }
         }

         if (!this._data.prefixMatches(prefix, this._data.getLeaf(this._leafIndex))) {
            this._numLeaves = 0;
         }
      }
   }

   private final void lookupId(int id) {
      int nodeIndex = 0;
      this._leafIndex = 0;
      this._numLeaves = this._data.size();
      if (this._numLeaves != 0) {
         while (this._numLeaves > 1) {
            int bitNum = this._data.getBitNumber(nodeIndex);
            int leftNodes = this._data.getLeftNodes(nodeIndex);
            int bit = this._data.getBit(id, bitNum);
            if (bit < 0) {
               throw new Object();
            }

            if (bit == 0) {
               this._numLeaves = leftNodes + 1;
               if (this._numLeaves == 1) {
                  return;
               }

               nodeIndex++;
            } else {
               leftNodes++;
               this._numLeaves -= leftNodes;
               nodeIndex += leftNodes;
               this._leafIndex += leftNodes;
            }
         }
      }
   }

   public final BitSet getMatchingContacts(String pattern) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return null;
      }

      this.rebuildKeywordTreeIfNeeded();
      synchronized (this._data) {
         int count = StringUtilities.stringToKeywords(pattern.toLowerCase(), this._tmpWords, 0);
         BitSet result = (BitSet)(new Object());
         BitSet current = result;

         for (int i = 0; i < count; i++) {
            this.search(this._tmpWords[i], current);
            if (i == 0) {
               if (count > 1) {
                  current = (BitSet)(new Object());
               }
            } else {
               result.and(current);
               current.reset();
            }
         }

         Array.resize(this._tmpWords, 0);
         return result;
      }
   }

   public final void rebuildKeywordTreeIfNeeded() {
      if (this._rebuildKeywordTree) {
         Object ticket = PersistentContent.getTicket();
         if (ticket != null) {
            this.removeAllKeywords();
            int count = this._contactList.getContactsCount();

            for (int index = 0; index < count; index++) {
               Contact contact = this._contactList.getContactAt(index);
               this.addKeywords(contact, ticket);
            }

            this._rebuildKeywordTree = false;
            this._data.optimize();
         }
      }
   }

   public final void addKeywords(Contact contact, Object ticket) {
      if (ticket == null) {
         ticket = PersistentContent.getTicket();
      }

      if (ticket == null) {
         this._rebuildKeywordTree = true;
      } else {
         int uid = contact.getUID();
         String keywords = contact.getKeywords();
         synchronized (this._data) {
            Array.resize(this._tmpOffsets, keywords.length() + 1);
            int count = StringUtilities.stringToKeywords(keywords, this._tmpOffsets, 0);

            for (int i = count - 1; i >= 0; i--) {
               int id = GTPatriciaTreeHelper.createID(uid, this._tmpOffsets[i]);
               this.insert(id);
            }

            Array.resize(this._tmpOffsets, 0);
         }
      }
   }

   public final void removeKeywords(Contact contact) {
      int uid = contact.getUID();
      synchronized (this._data) {
         int count = this._data.size();

         for (int i = count - 1; i >= 0; i--) {
            int leaf = this._data.getLeaf(i);
            if (GTPatriciaTreeHelper.getUIDFromID(leaf) == uid) {
               this.deleteLeaf(i);
            }
         }
      }
   }

   public final void updateKeywords(Contact contact) {
      synchronized (this._data) {
         this.removeKeywords(contact);
         this.addKeywords(contact, null);
      }
   }

   public final void removeAllKeywords() {
      this._data.removeAll();
      this._rebuildKeywordTree = false;
   }

   public final int addAndGetIndex(Contact contact) {
      return this._data.addAndGetIndex(contact);
   }

   public final void remove(Contact contact) {
      this._data.remove(contact);
   }

   public final boolean contains(Contact contact) {
      return this._data.contains(contact);
   }
}
