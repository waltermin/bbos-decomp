package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.tid.awt.im.InputContext;

final class FolderList$SearchEngine {
   int current;
   int parent;
   String[] queryWords;
   BitSet _foundWordsSet;
   private final FolderList this$0;

   public FolderList$SearchEngine(FolderList _1) {
      this.this$0 = _1;
      this._foundWordsSet = new BitSet();
      _1._interrupted = false;
   }

   public final void startSearch() {
      synchronized (InputContext.getInstance(false)) {
         synchronized (this.this$0._lockObj) {
            FolderList.access$1008(this.this$0);
            this.setup(this.this$0._query);
            if (this.this$0._query.length == 0) {
               for (int c = this.this$0._nodeIndex.length - 1; c >= 0; c--) {
                  this.this$0._nodeIndex[c] = 2;
               }

               FolderList.access$1010(this.this$0);
               if (this.this$0._searchCount == 0 && !this.this$0._interrupted) {
                  this.this$0._application.invokeLater(this.this$0._updater);
                  this.this$0._startThread = false;
               }

               return;
            }

            this.marking();
            if (this.this$0._interrupted) {
               return;
            }

            this.extendedMarking();
            if (this.this$0._interrupted) {
               return;
            }

            FolderList.access$1010(this.this$0);
            if (this.this$0._searchCount == 0 && !this.this$0._interrupted) {
               this.this$0._startThread = false;
               this.this$0._application.invokeLater(this.this$0._updater);
            }
         }
      }
   }

   private final void setup(String query) {
      this.setup(StringUtilities.stringToWords(query));
   }

   private final void setup(String[] query) {
      this.current = this.this$0._treeField.getFirstRoot();
      this.queryWords = query;
      this.this$0._interrupted = false;
      this._foundWordsSet.reset();
   }

   public final void marking() {
      while (this.current != -1) {
         if (this.this$0._interrupted) {
            return;
         }

         String label = this.this$0.getLabel(this.this$0._treeField.getCookie(this.current));
         if (this.this$0.match(label, this.queryWords, this._foundWordsSet)) {
            this.this$0._nodeIndex[this.current] = 1;
         } else {
            this.this$0._nodeIndex[this.current] = 0;
         }

         this.current = this.this$0._treeField.nextNode(this.current, 0, true);
      }
   }

   public final void extendedMarking() {
      for (int c = 0; c < this.this$0._nodeIndex.length; c++) {
         if (this.this$0._interrupted) {
            return;
         }

         if (this.this$0._nodeIndex[c] == 1) {
            this.this$0._nodeIndex[c] = 2;

            try {
               this.parent = this.this$0._treeField.getParent(c);
            } finally {
               continue;
            }

            while (this.parent > 0) {
               this.this$0._nodeIndex[this.parent] = 2;
               this.current = this.parent;
               this.parent = this.this$0._treeField.getParent(this.current);
            }

            int child = this.this$0._treeField.getFirstChild(c);
            if (child > 0) {
               this.extendMarkSubtree(this.this$0._treeField, child);
            }
         }
      }
   }

   private final void extendMarkSubtree(TreeField sTree, int root) {
      int child = sTree.getFirstChild(root);
      int sibling = sTree.getNextSibling(root);
      if (!this.this$0._interrupted) {
         this.this$0._nodeIndex[root] = 2;
         if (child > 0) {
            this.extendMarkSubtree(sTree, child);
         }

         if (sibling > 0) {
            this.extendMarkSubtree(sTree, sibling);
         }
      }
   }
}
