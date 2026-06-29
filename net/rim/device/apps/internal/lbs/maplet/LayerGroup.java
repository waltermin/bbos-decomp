package net.rim.device.apps.internal.lbs.maplet;

final class LayerGroup {
   private int _id;
   private String _name;
   private boolean _visible = true;
   private int[] _lids;

   public LayerGroup(int id, String name) {
      this._id = id;
      this._name = name;
      this._lids = new int[0];
   }

   public final void setVisible(boolean visible) {
      this._visible = visible;
   }

   public final boolean isVisible() {
      return this._visible;
   }

   public final boolean containsLID(int lid) {
      for (int i = 0; i < this._lids.length; i++) {
         if (this._lids[i] == lid) {
            return true;
         }
      }

      return false;
   }

   public final void setLayerIDs(int[] ids) {
      this._lids = ids;
   }

   @Override
   public final String toString() {
      return this._name;
   }
}
