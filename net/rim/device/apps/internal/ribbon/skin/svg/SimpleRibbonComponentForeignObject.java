package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObjectPeer;

public final class SimpleRibbonComponentForeignObject extends AbstractForeignObject implements RibbonComponent$RibbonComponentChangeListener, RibbonComponent {
   private int _width = -1;
   private int _height = -1;
   private SimpleRibbonComponent _component;
   private RibbonComponent$RibbonComponentChangeListener _changeListener;

   SimpleRibbonComponentForeignObject(SimpleRibbonComponent component) {
      this._component = component;
      this._component.setChangeListener(this);
   }

   public final SimpleRibbonComponent getComponent() {
      return this._component;
   }

   @Override
   public final void draw(Object graphics, int x, int y) {
      if (this._component != null) {
         this._component.paintComponent((Graphics)graphics, x, y, this.getWidth(), this.getHeight(), null);
      }
   }

   @Override
   public final int getWidth() {
      return this._width == -1 && this._component != null ? this._component.getComponentWidth() : this._width;
   }

   @Override
   public final int getHeight() {
      return this._height == -1 && this._component != null ? this._component.getComponentHeight() : this._height;
   }

   public final void invalidate() {
      ForeignObjectPeer peer = this.getPeer();
      if (peer != null) {
         peer.invalidate(this);
      }

      if (this._changeListener != null) {
         this._changeListener.ribbonComponentChanged(this);
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

   @Override
   public final void setExtent(int width, int height) {
      this._width = width;
      this._height = height;
   }
}
