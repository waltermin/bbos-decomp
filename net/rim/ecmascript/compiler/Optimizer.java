package net.rim.ecmascript.compiler;

import java.util.Vector;

class Optimizer {
   private Compiler _c;
   private Function _f;
   private Vector _blocks;

   Optimizer(Compiler c, Function f) {
      this._f = f;
      this._blocks = this._f.getBlocks();
      this._c = c;
   }

   private void optimizeLocals() {
      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         ((Block)this._blocks.elementAt(i)).optimizeLocals(this._f);
      }
   }

   private void postOptimize() {
      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         ((Block)this._blocks.elementAt(i)).postOptimize(this._f);
      }
   }

   private boolean optimizeJsrRet() {
      boolean change = false;

      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         Object o1 = this._blocks.elementAt(i);
         if (o1 instanceof BlockJsr) {
            BlockJsr b1 = (BlockJsr)o1;
            Block b2 = b1.getJsr().getBlock();
            if (b2 instanceof RetBlockJsr && b2.isEmpty()) {
               this._blocks.setElementAt(b1.makeGoto(), i);
               change = true;
            }
         }
      }

      return change;
   }

   private boolean optimizeGoto() {
      boolean change = false;

      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         Object o1 = this._blocks.elementAt(i);
         if (o1 instanceof BlockGoto) {
            BlockGoto b1 = (BlockGoto)o1;
            if (b1.isEmpty()) {
               b1.getLabel().setRedirect(b1.getTarget());
            }
         }
      }

      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         if (((Block)this._blocks.elementAt(i)).redirectLabels()) {
            change = true;
         }
      }

      return change;
   }

   private void markReachableHelper(Block b, int depth, Vector toMark) {
      if (!b.getReachable()) {
         b.setReachable(true);

         for (int j = b.getNumTargets() - 1; j >= 0; j--) {
            Label l = b.getTarget(j);
            Block other = l.getBlock();
            if (depth >= 50) {
               toMark.addElement(other);
            } else {
               this.markReachableHelper(other, depth + 1, toMark);
            }
         }
      }
   }

   private void markReachable(Block b) {
      Vector toMark = new Vector();
      this.markReachableHelper(b, 0, toMark);

      while (true) {
         int size = toMark.size();
         if (size == 0) {
            return;
         }

         b = (Block)toMark.elementAt(--size);
         toMark.removeElementAt(size);
         this.markReachableHelper(b, 0, toMark);
      }
   }

   private boolean optimizeDeadCode() {
      if (this._blocks.size() == 0) {
         return false;
      }

      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         Block b = (Block)this._blocks.elementAt(i);
         b.setReachable(false);
      }

      this.markReachable((Block)this._blocks.elementAt(0));

      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         Block b = (Block)this._blocks.elementAt(i);
         if (!b.getReachable()) {
            this._blocks.removeElement(b);
         }
      }

      return false;
   }

   void optimize() {
      boolean change;
      do {
         change = false;
         if (this.optimizeJsrRet()) {
            change = true;
         }

         if (this.optimizeGoto()) {
            change = true;
         }

         if (this.optimizeDeadCode()) {
            change = true;
         }
      } while (change);

      if (!this._c.compilingForEval() && !this._c.compilingForDebug()) {
         this.optimizeLocals();
      }

      this.postOptimize();
      if (this._c.compilingForDebug()) {
         this._f.setNeedsScope();
      }
   }
}
