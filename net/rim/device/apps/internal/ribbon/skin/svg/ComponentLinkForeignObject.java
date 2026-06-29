package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;

class ComponentLinkForeignObject extends AbstractForeignObject {
   protected ModelInteractorImpl _mi;
   protected String _id;
   protected int _node = -1;

   ComponentLinkForeignObject(ModelInteractorImpl mi, String id) {
      this._mi = mi;
      this._id = id;
   }

   protected void resolveId() {
      this._node = this._mi.getHandle(this._id);
   }

   @Override
   public void draw(Object graphics, int x, int y) {
   }

   @Override
   public int getWidth() {
      return 0;
   }

   @Override
   public int getHeight() {
      return 0;
   }
}
