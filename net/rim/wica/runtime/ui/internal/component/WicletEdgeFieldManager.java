package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.ui.View;

final class WicletEdgeFieldManager extends Manager implements View {
   private ScreenContext _context;
   private UIContainer _model;
   private byte _visibility;
   private static final int PADDING = 1;

   WicletEdgeFieldManager(ScreenContext context, UIContainer model, int row, long style) {
      super(1155173304420532224L);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      ComponentHelper.buildLayout(context, this, model, row, style);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
         this.setThemeAttributeSet(attributes);
      }

      super.applyFont();
   }

   private final int getFieldWidth(Field field) {
      return this.getPreferredWidthOfChild(field) + field.getMarginLeft() + field.getMarginRight();
   }

   private final int getFieldHeight(Field field) {
      return this.getPreferredHeightOfChild(field) + field.getMarginTop() + field.getMarginBottom();
   }

   private final int getRealWidth(Field field) {
      return field.getWidth() + field.getMarginLeft() + field.getMarginRight();
   }

   @Override
   public final int getPreferredHeight() {
      int numChildren = this.getFieldCount();
      int height = 0;

      for (int i = 0; i < numChildren; i++) {
         height += this.getFieldHeight(this.getField(i));
      }

      return height;
   }

   @Override
   public final int getPreferredWidth() {
      return Graphics.getScreenWidth();
   }

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._visibility == 1) {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      } else {
         int numChildren = this.getFieldCount();
         int heightUsed = 0;

         for (int i = 0; i < numChildren; i++) {
            Field left = this.getField(i);
            this.layoutChild(left, width, height - heightUsed);
            if (++i == numChildren) {
               this.setPositionChild(left, 0, heightUsed);
               heightUsed += left.getHeight() + left.getMarginTop() + left.getMarginBottom();
               break;
            }

            Field right = this.getField(i);
            int rightPreferredWidth = this.getFieldWidth(right);
            if (width - this.getRealWidth(left) < rightPreferredWidth) {
               this.setPositionChild(left, 0, heightUsed);
               heightUsed += left.getHeight() + left.getMarginTop() + left.getMarginBottom();
               this.layoutChild(right, width, height - heightUsed);
               this.setPositionChild(right, width - right.getWidth(), heightUsed);
               heightUsed += right.getHeight() + right.getMarginTop() + right.getMarginBottom();
            } else {
               int rightWidth = width - this.getRealWidth(left) - 1;
               this.layoutChild(right, rightWidth, height - heightUsed);
               int leftHeight = Math.max(this.getFieldHeight(left), left.getHeight());
               int rightHeight = Math.max(this.getFieldHeight(right), right.getHeight());
               int rowHeight = Math.max(leftHeight, rightHeight);
               int vGap = rowHeight - left.getHeight() >> 1;
               this.setPositionChild(left, 0, heightUsed + vGap);
               vGap = rowHeight - right.getHeight() >> 1;
               this.setPositionChild(right, width - right.getWidth(), heightUsed + vGap);
               heightUsed += rowHeight;
            }
         }

         this.setVirtualExtent(width, heightUsed);
         this.setExtent(width, Math.min(height, heightUsed));
      }
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         super.subpaint(graphics);
      }
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (UIContainer)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
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
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      return this._context.getSuspendLayout() ? true : super.incrementalLayout(index, added, deleted);
   }

   @Override
   public final void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      ComponentHelper.updateLayout(this._context, this, this._model, row);
      this.updateLayout();
   }
}
