package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.ui.View;

public class PagedView extends VerticalFieldManager implements View, PagedListModifier {
   private byte _visibility;
   private UIControl _model;
   private boolean _editable;

   public PagedView(ScreenContext context, UIControl model, long style) {
      super(style);
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
   }

   @Override
   public UIComponent getModel() {
      return this._model;
   }

   @Override
   public void setModel(UIComponent model) {
      this._model = (UIControl)model;
   }

   @Override
   public void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      this._editable = !this._model.isReadOnly();
   }

   @Override
   public void beginReconstruction() {
   }

   @Override
   public void endReconstruction() {
   }

   @Override
   public void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
         if (this._visibility != 0 && this.getLeafFieldWithFocus() != null) {
            this.onUnfocus();
         }

         if (this._visibility != 2) {
            this.updateLayout();
            return;
         }

         this.invalidate();
      }
   }

   @Override
   public void unFocus() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public boolean isEditable() {
      return this._editable;
   }

   @Override
   public boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   @Override
   protected void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         super.subpaint(graphics);
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this._visibility != 1) {
         super.sublayout(width, height);
      } else {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      }
   }

   @Override
   public void setSelected(int _1) {
      throw null;
   }

   @Override
   public void update(int _1, int _2) {
      throw null;
   }

   @Override
   public void add(int _1, int _2) {
      throw null;
   }
}
