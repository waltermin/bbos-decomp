package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

public class ModelInteractorImpl$RootsHandles {
   private int[] _roots = new int[2];
   private static final int VISUAL_ROOT_IDX;
   private static final int BEHAVIORS_ROOT_IDX;

   public int getVisualRootHandle() {
      return this._roots[0];
   }

   public int getBehaviorsRootHandle() {
      return this._roots[1];
   }

   public void setVisualRootHandle(int vRoot) {
      this._roots[0] = vRoot;
   }

   public void setBehaviorsRootHandle(int bRoot) {
      this._roots[1] = bRoot;
   }
}
