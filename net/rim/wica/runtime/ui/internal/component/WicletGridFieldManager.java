package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.ui.View;

final class WicletGridFieldManager extends Manager implements View {
   private ScreenContext _context;
   private UIContainer _model;
   private Field[][] _grid;
   private int[] _heights;
   private int _maxX;
   private int _maxY;
   private byte _visibility;
   private static int MIN_FLEXIBLE_WIDTH = Graphics.getScreenWidth() / 3;

   WicletGridFieldManager(ScreenContext context, UIContainer model, int row, long style) {
      super(1155173304420532224L);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      ComponentHelper.buildLayout(this._context, this, model, row, style);
   }

   final int getColumnsCount() {
      return this._maxX;
   }

   final int getRowsCount() {
      return this._maxY;
   }

   private final void buildArrays() {
      this._maxX = 0;
      this._maxY = 0;
      int numFields = this.getFieldCount();
      int placementX = 0;
      int placementY = 0;

      for (int i = 0; i < numFields; i++) {
         placementX = ((View)this.getField(i)).getModel().getX();
         placementY = ((View)this.getField(i)).getModel().getY();
         this._maxX = Math.max(this._maxX, placementX);
         this._maxY = Math.max(this._maxY, placementY);
      }

      this._maxX++;
      this._maxY++;
      this._heights = new int[this._maxY];
      this._grid = new Object[this._maxX][this._maxY];

      for (int var10 = 0; var10 < numFields; var10++) {
         Field field = this.getField(var10);
         int x = ((View)field).getModel().getX();
         int y = ((View)field).getModel().getY();
         this._grid[x][y] = field;
         this._heights[y] = Math.max(this._heights[y], this.getFieldHeight(field));
      }
   }

   private final int getFieldHeight(Field field) {
      return this.getPreferredHeightOfChild(field) + field.getMarginTop() + field.getMarginBottom();
   }

   private final void resetArrays() {
      this._grid = (Object[][])null;
      this._heights = null;
   }

   @Override
   public final void add(Field field) {
      this.resetArrays();
      int numFields = this.getFieldCount();
      if (numFields <= 0) {
         super.add(field);
      } else {
         int placementX = 0;
         int placementY = 0;
         int newPlacementX = ((View)field).getModel().getX();
         int newPlacementY = ((View)field).getModel().getY();

         int i;
         for (i = numFields - 1; i >= 0; i--) {
            placementX = ((View)this.getField(i)).getModel().getX();
            placementY = ((View)this.getField(i)).getModel().getY();
            if (placementY < newPlacementY || placementY == newPlacementY && placementX < newPlacementX) {
               break;
            }
         }

         super.insert(field, i + 1);
      }
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

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         super.subpaint(graphics);
      }
   }

   @Override
   public final void delete(Field field) {
      this.resetArrays();
      super.delete(field);
   }

   @Override
   public final void deleteAll() {
      this.resetArrays();
      super.deleteAll();
   }

   @Override
   public final void deleteRange(int start, int count) {
      this.resetArrays();
      super.deleteRange(start, count);
   }

   @Override
   public final int getPreferredHeight() {
      this.buildArrays();
      int count = this._heights.length;
      int sum = 0;

      for (int i = 0; i < count; i++) {
         sum += this._heights[i];
      }

      return sum;
   }

   @Override
   public final int getPreferredWidth() {
      return MIN_FLEXIBLE_WIDTH;
   }

   @Override
   public final void insert(Field field, int index) {
      this.resetArrays();
      super.insert(field, index);
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      if (this._visibility == 1) {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      } else {
         int width = 0;
         int height = 0;
         if (this._grid == null) {
            this.buildArrays();
         }

         int colWidth = this._maxX == 0 ? 0 : maxWidth / this._maxX;
         int y = 0;
         int x = 0;

         for (int i = 0; i < this._maxY; y += this._heights[i++]) {
            x = 0;
            this._heights[i] = 0;

            for (int j = 0; j < this._maxX; j++) {
               Field field = this._grid[j][i];
               if (field != null) {
                  this.layoutChild(field, colWidth, Integer.MAX_VALUE);
                  this._heights[i] = Math.max(field.getHeight(), this._heights[i]);
               }
            }

            for (int var15 = 0; var15 < this._maxX; var15++) {
               Field field = this._grid[var15][i];
               if (field != null) {
                  int vGap = this._heights[i] - field.getHeight() >> 1;
                  this.setPositionChild(field, x, y + vGap);
               }

               x += colWidth;
            }
         }

         width = x;
         height = y;
         this.setVirtualExtent(width, height);
         if (width < maxWidth && (this.getStyle() & 1152921504606846976L) > 0) {
            width = maxWidth;
         }

         if (height < maxHeight && (this.getStyle() & 2305843009213693952L) > 0) {
            height = maxHeight;
         }

         this.setExtent(Math.min(width, maxWidth), Math.min(height, maxHeight));
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
