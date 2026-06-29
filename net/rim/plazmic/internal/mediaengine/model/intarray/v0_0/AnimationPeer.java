package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0;

import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObjectPeer;

class AnimationPeer implements ForeignObjectPeer {
   private AnimationModel _model;

   void setModel(AnimationModel model) {
      this._model = model;
   }

   @Override
   public void invalidate(ForeignObject fo) {
      if (this._model != null) {
         synchronized (this._model) {
            if (this._model.nodes != null) {
               this._model.setBits(fo.getHandle(), 1);
               this._model.setIsModified(true);
            }
         }
      }
   }
}
