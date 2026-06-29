package net.rim.device.internal.ui;

import net.rim.vm.Array;

public final class Tree {
   private int _nodeCount;
   private int _visibleCount;
   private int _firstFreeNode;
   private byte _defaultInfo;
   private short[] _parent;
   private short[] _firstChild;
   private short[] _nextSibling;
   private short[] _previousSibling;
   private Object[] _cookie;
   private byte[] _info;
   private static final int START_SIZE = 8;
   private static final int SUPERROOT = 0;
   private static final int INFO_EXPANDED = 1;
   private static final int INFO_VISIBLE = 2;
   private static final int INFO_OCCUPIED = 4;
   private static final int INFO_DEFAULT = 6;

   public Tree() {
      this.init(true);
   }

   private final void init(boolean defaultExpanded) {
      this._parent = new short[8];
      this._firstChild = new short[8];
      this._nextSibling = new short[8];
      this._previousSibling = new short[8];
      this._cookie = new Object[8];
      this._info = new byte[8];
      this._nodeCount = 1;
      this._visibleCount = 0;
      this.setDefaultExpansion(defaultExpanded);
      this._parent[0] = -1;
      this._firstChild[0] = -1;
      this._nextSibling[0] = -1;
      this._previousSibling[0] = -1;
      this._info[0] = 7;
      this.initFreeListPointers(1);
   }

   public final void deleteAll() {
      int len = this._cookie.length;
      if (len > 8) {
         this.init((this._defaultInfo & 1) != 0);
      } else {
         for (int i = 1; i < len; i++) {
            this._cookie[i] = null;
            this._info[i] = 0;
         }

         this._nodeCount = 1;
         this._visibleCount = 0;
         this._firstChild[0] = -1;
         this.initFreeListPointers(1);
      }
   }

   private final void initFreeListPointers(int start) {
      this._firstFreeNode = start;
      short[] ns = this._nextSibling;
      int n = ns.length;

      while (start < n) {
         int next = start + 1;
         ns[start] = (short)next;
         start = next;
      }

      ns[start - 1] = -1;
   }

   private final void validateNodeIdSuperOK(int node) {
      try {
         if ((this._info[node] & 4) != 0) {
            return;
         }
      } catch (ArrayIndexOutOfBoundsException e) {
      }

      throw new IllegalArgumentException();
   }

   private final void validateNodeId(int node) {
      try {
         if (node != 0 && (this._info[node] & 4) != 0) {
            return;
         }
      } catch (ArrayIndexOutOfBoundsException e) {
      }

      throw new IllegalArgumentException();
   }

   public final void setDefaultExpansion(boolean expanded) {
      this._defaultInfo = (byte)(expanded ? 7 : 6);
   }

   public final void deleteSubtree(int node) {
      this.validateNodeId(node);
      int parent = this._parent[node];
      if (this._firstChild[parent] == node) {
         this._firstChild[parent] = this._nextSibling[node];
      } else {
         this._nextSibling[this._previousSibling[node]] = this._nextSibling[node];
      }

      if (this._nextSibling[node] != -1) {
         this._previousSibling[this._nextSibling[node]] = this._previousSibling[node];
      }

      int childCount = 0;
      int visibleChildCount = 0;
      int step = node;

      while (true) {
         int next = this._firstChild[step];
         if (next == -1) {
            while (true) {
               childCount++;
               if ((this._info[step] & 2) != 0) {
                  visibleChildCount++;
               }

               int sibling = this._nextSibling[step];
               this._info[step] = 0;
               this._cookie[step] = null;
               this._nextSibling[step] = (short)this._firstFreeNode;
               this._firstFreeNode = step;
               if (step == node) {
                  this._nodeCount -= childCount;
                  this._visibleCount -= visibleChildCount;
                  return;
               }

               next = sibling;
               if (next == -1) {
                  step = this._parent[step];
               } else {
                  step = next;

                  while (true) {
                     int var9 = this._firstChild[step];
                     if (var9 == -1) {
                        break;
                     }

                     step = var9;
                  }
               }
            }
         }

         step = next;
      }
   }

   public final int addChildNode(int parent, Object cookie) {
      this.validateNodeIdSuperOK(parent);
      short id = this.allocateNode();
      this._parent[id] = (short)parent;
      this._previousSibling[id] = -1;
      short previousFirstChild = this._firstChild[parent];
      this._firstChild[parent] = id;
      this._nextSibling[id] = previousFirstChild;
      if (previousFirstChild != -1) {
         this._previousSibling[previousFirstChild] = id;
      }

      this._cookie[id] = cookie;
      if ((this._info[parent] & 3) != 3) {
         this._info[id] = (byte)(this._info[id] & -3);
         return id;
      } else {
         this._visibleCount++;
         return id;
      }
   }

   public final int addSiblingNode(int previousSibling, Object cookie) {
      this.validateNodeId(previousSibling);
      short id = this.allocateNode();
      this._parent[id] = this._parent[previousSibling];
      short next = this._nextSibling[previousSibling];
      this._nextSibling[id] = next;
      this._previousSibling[id] = (short)previousSibling;
      this._nextSibling[previousSibling] = id;
      if (next != -1) {
         this._previousSibling[next] = id;
      }

      this._cookie[id] = cookie;
      if ((this._info[previousSibling] & 2) == 0) {
         this._info[id] = (byte)(this._info[id] & -3);
         return id;
      } else {
         this._visibleCount++;
         return id;
      }
   }

   public final int getNodeCount() {
      return this._nodeCount - 1;
   }

   public final int getFirstRoot() {
      return this._firstChild[0];
   }

   public final int getFirstChild(int node) {
      this.validateNodeIdSuperOK(node);
      return this._firstChild[node];
   }

   public final int getNextSibling(int node) {
      this.validateNodeId(node);
      return this._nextSibling[node];
   }

   public final int getPreviousSibling(int node) {
      this.validateNodeId(node);
      return this._previousSibling[node];
   }

   public final int getParent(int node) {
      this.validateNodeId(node);
      return this._parent[node];
   }

   public final void setNodeExpansion(int node, boolean expanded) {
      this.validateNodeId(node);
      if (expanded) {
         if (!this.getNodeExpansion(node)) {
            this._info[node] = (byte)(this._info[node] | 1);

            for (int child = this.nextSubtreeNode(node, node, false); child != -1; child = this.nextSubtreeNode(child, node, false)) {
               this._info[child] = (byte)(this._info[child] | 2);
               this._visibleCount++;
            }
         }
      } else if (this.getNodeExpansion(node)) {
         for (int child = this.nextSubtreeNode(node, node, false); child != -1; child = this.nextSubtreeNode(child, node, false)) {
            this._info[child] = (byte)(this._info[child] & -3);
            this._visibleCount--;
         }

         this._info[node] = (byte)(this._info[node] & -2);
      }
   }

   public final int getVisibleCount() {
      return this._visibleCount;
   }

   public final boolean getNodeExpansion(int node) {
      this.validateNodeId(node);
      return this.getNodeExpansion0(node);
   }

   private final boolean getNodeExpansion0(int node) {
      return (this._info[node] & 1) != 0;
   }

   public final boolean getVisible(int node) {
      this.validateNodeId(node);
      return (this._info[node] & 2) != 0;
   }

   public final int nextNode(int node, boolean followCollapsed) {
      return this.nextSubtreeNode(node, 0, followCollapsed);
   }

   public final int nextSubtreeNode(int node, int root, boolean followCollapsed) {
      this.validateNodeIdSuperOK(node);
      this.validateNodeIdSuperOK(root);
      if (followCollapsed || this.getNodeExpansion0(node)) {
         int child = this._firstChild[node];
         if (child != -1) {
            return child;
         }
      }

      if (node == root) {
         return -1;
      }

      int nextSibling = this._nextSibling[node];
      if (nextSibling != -1) {
         return nextSibling;
      }

      for (int var5 = this._parent[node]; var5 != -1 && var5 != root; var5 = this._parent[var5]) {
         int var7 = this._nextSibling[var5];
         if (var7 != -1) {
            return var7;
         }
      }

      return -1;
   }

   public final int getLastNode(int node, boolean followCollapsed) {
      this.validateNodeIdSuperOK(node);

      while (followCollapsed || this.getNodeExpansion0(node)) {
         int next = this._firstChild[node];
         if (next == -1) {
            break;
         }

         node = next;

         while (true) {
            int var4 = this._nextSibling[node];
            if (var4 == -1) {
               break;
            }

            node = var4;
         }
      }

      return node == 0 ? -1 : node;
   }

   public final int previousNode(int node, boolean followCollapsed) {
      this.validateNodeIdSuperOK(node);
      if (node != 0) {
         int previousSibling = this._previousSibling[node];
         if (previousSibling == -1) {
            int parent = this._parent[node];
            if (parent == 0) {
               parent = -1;
            }

            return parent;
         }

         node = previousSibling;
      }

      return this.getLastNode(node, followCollapsed);
   }

   public final int getIndexOf(int node, boolean followCollapsed) {
      this.validateNodeId(node);
      int index = 0;
      int test = this._firstChild[0];

      while (test != node) {
         index++;
         test = this.nextNode(test, followCollapsed);
      }

      return index;
   }

   public final int getNodeDepth(int node) {
      this.validateNodeId(node);
      int depth = 0;

      while (true) {
         node = this._parent[node];
         if (node == 0) {
            return depth;
         }

         depth++;
      }
   }

   public final void setCookie(int node, Object cookie) {
      this.validateNodeId(node);
      this._cookie[node] = cookie;
   }

   public final Object getCookie(int node) {
      this.validateNodeId(node);
      return this._cookie[node];
   }

   private final short allocateNode() {
      if (this._firstFreeNode == -1) {
         int sectionSize = Array.getSectionSize(this._cookie);
         int len = this._cookie.length;
         if (len >= sectionSize) {
            len += sectionSize;
         } else {
            len += len;
            len = Math.min(len, sectionSize);
         }

         if (len > 32767) {
            throw new RuntimeException("Tree too large: " + len);
         }

         Array.resize(this._parent, len);
         Array.resize(this._firstChild, len);
         Array.resize(this._nextSibling, len);
         Array.resize(this._previousSibling, len);
         Array.resize(this._cookie, len);
         Array.resize(this._info, len);
         this.initFreeListPointers(this._nodeCount);
      }

      short id = (short)this._firstFreeNode;
      this._firstFreeNode = this._nextSibling[id];
      this._nodeCount++;
      this._firstChild[id] = -1;
      this._info[id] = this._defaultInfo;
      return id;
   }
}
