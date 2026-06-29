package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.math.Fixed32;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.TextProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.TextRibbonComponent;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.TextNodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;

final class TextRibbonComponentLink extends ComponentLinkForeignObject implements RibbonComponent$RibbonComponentChangeListener, RibbonComponent {
   private TextProviderRibbonComponent _component;
   private RibbonComponent$RibbonComponentChangeListener _changeListener;

   TextRibbonComponentLink(TextProviderRibbonComponent component, ModelInteractorImpl mi, String id) {
      super(mi, id);
      this._component = component;
      ((RibbonComponent)this._component).setChangeListener(this);
   }

   @Override
   protected final void resolveId() {
      super.resolveId();
      this._component.setTargetNode(super._node);
      this.invalidate();
   }

   public final void invalidate() {
      if (super._node != -1) {
         synchronized (super._mi) {
            TextNodeImpl.setString(this._component.getCharArrayText(), super._node, super._mi);
            int width = ((TextRibbonComponent)this._component).getComponentWidth();
            if (width != 0) {
               TextNodeImpl.setWidth(Fixed32.toFP(width), super._node, super._mi);
               int hints = VisualNodeImpl.getTextRenderingHint(super._node, super._mi);
               hints |= 4;
               VisualNodeImpl.setTextRenderingHint(hints, super._node, super._mi);
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
