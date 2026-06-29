package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ImageNodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;

final class ImageRibbonComponentLink extends ComponentLinkForeignObject implements RibbonComponent$RibbonComponentChangeListener, RibbonComponent {
   private ImageProviderRibbonComponent _component;
   private RibbonComponent$RibbonComponentChangeListener _changeListener;
   private boolean _usePattern;

   ImageRibbonComponentLink(ImageProviderRibbonComponent component, ModelInteractorImpl mi, String id) {
      super(mi, id);
      this._component = component;
      ((RibbonComponent)this._component).setChangeListener(this);
   }

   @Override
   protected final void resolveId() {
      super.resolveId();
      if (super._node != -1) {
         switch (NodeImpl.getType(super._node, super._mi)) {
            case 36:
            case 40:
               this._usePattern = true;
            case 42:
               break;
            default:
               super._node = -1;
         }

         this.invalidate();
      }
   }

   public final void invalidate() {
      if (super._node != -1) {
         Object image = this._component.getImage();
         if (image != null) {
            if (this._usePattern) {
               VisualNodeImpl.setFillPattern(image, super._node, super._mi);
            } else {
               ImageNodeImpl.setImage(image, super._node, super._mi);
            }
         }

         if (this._changeListener != null) {
            this._changeListener.ribbonComponentChanged(this);
         }
      }
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this.invalidate();
   }

   @Override
   public final void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
