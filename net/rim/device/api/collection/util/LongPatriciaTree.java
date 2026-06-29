package net.rim.device.api.collection.util;

public class LongPatriciaTree {
   private LongPatriciaTreeData _data;
   private int _leafIndex;
   private int _numLeaves;

   public LongPatriciaTree(LongPatriciaTreeData data) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      this._data = data;
   }

   public void insert(long id) {
      synchronized (this._data) {
         this.doInsert(id);
      }
   }

   private void doInsert(long id) {
      if (this._data.size() == 0) {
         this._data.insert(0, 0, 0, id, 0);
      } else {
         this.lookupId(id);
         int cmp = this._data.compareBits(id, this._data.getLeaf(this._leafIndex));
         if (cmp == 0) {
            throw new IllegalArgumentException();
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

   public void delete(long id) {
      synchronized (this._data) {
         this.doDelete(id);
      }
   }

   private void doDelete(long id) {
      this.lookupId(id);
      if (this._numLeaves != 0) {
         if (this._data.getLeaf(this._leafIndex) == id) {
            this.deleteLeaf(this._leafIndex);
         }
      }
   }

   public void deleteLeaf(int deleteLeafIndex) {
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

   public int search(Object prefix, Object result) {
      synchronized (this._data) {
         this.lookupPrefix(prefix);
         this._data.recordFound(result, this._leafIndex, this._numLeaves);
         return this._numLeaves;
      }
   }

   private void lookupPrefix(Object prefix) {
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

   private void lookupId(long id) {
      int nodeIndex = 0;
      this._leafIndex = 0;
      this._numLeaves = this._data.size();
      if (this._numLeaves != 0) {
         while (this._numLeaves > 1) {
            int bitNum = this._data.getBitNumber(nodeIndex);
            int leftNodes = this._data.getLeftNodes(nodeIndex);
            int bit = this._data.getBit(id, bitNum);
            if (bit < 0) {
               throw new IllegalArgumentException();
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

   public void dump() {
      this.dump(0, 0, 0, this._data.size());
   }

   private void dump(int indent, int nodeIndex, int leafIndex, int numLeaves) {
      if (numLeaves != 0) {
         if (numLeaves == 1) {
            for (int i = 0; i < indent; i++) {
               System.out.print("  ");
            }

            this._data.dumpLeaf(this._data.getLeaf(leafIndex));
         } else {
            int leftNodes = this._data.getLeftNodes(nodeIndex);
            int bitNum = this._data.getBitNumber(nodeIndex);

            for (int i = 0; i < indent; i++) {
               System.out.print("  ");
            }

            System.out.println("" + nodeIndex + "[" + bitNum + ":" + leftNodes + "]");
            this.dump(indent + 1, nodeIndex + 1, leafIndex, leftNodes + 1);
            this.dump(indent + 1, nodeIndex + leftNodes + 1, leafIndex + leftNodes + 1, numLeaves - leftNodes - 1);
         }
      }
   }

   public boolean validate() {
      boolean valid = true;
      int numLeaves = this._data.size();
      return numLeaves < 2 ? true : this.validateNode(0, numLeaves, -1);
   }

   private boolean validateNode(int nodeIndex, int numLeaves, int parentBitNum) {
      boolean valid = true;
      if (nodeIndex >= this._data.size() - 1) {
         System.out.println("Invalid tree: Node " + nodeIndex + " does not exist");
         return false;
      }

      int leftNodes = this._data.getLeftNodes(nodeIndex);
      int bitNum = this._data.getBitNumber(nodeIndex);
      if (bitNum <= parentBitNum) {
         System.out.println("Invalid tree: Node " + nodeIndex + " has bit number " + bitNum + " <= parent bit number " + parentBitNum);
         valid = false;
      }

      if (leftNodes >= numLeaves) {
         System.out.println("Invalid tree: Node " + nodeIndex + " has left node count " + leftNodes + " >= " + numLeaves);
         return false;
      }

      if (leftNodes > 0 && !this.validateNode(nodeIndex + 1, leftNodes + 1, bitNum)) {
         valid = false;
      }

      int rightNodes = numLeaves - 2 - leftNodes;
      if (rightNodes > 0 && !this.validateNode(nodeIndex + leftNodes + 1, rightNodes + 1, bitNum)) {
         valid = false;
      }

      return valid;
   }
}
