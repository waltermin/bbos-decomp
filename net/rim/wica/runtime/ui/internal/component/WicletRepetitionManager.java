package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.RepetitionControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;
import net.rim.wica.runtime.ui.internal.WicaScreen;

final class WicletRepetitionManager extends VerticalFieldManager implements View, FocusChangeListener, PagedListModifier, Focusable {
   private ScreenContext _context;
   private boolean _isCollapsible;
   private ThemeAttributeSet _attributes;
   private RepetitionControl _model;
   private PagingController _paging;
   private long _style;
   private WicletRepetitionManager$RowsContainer _rowsContainer;
   private byte _visibility;
   private int _screenContentHeight = -1;
   private int _screenContentHeightMinusScrollHeight;
   private int _maxRowHeight;
   private static Tag TAG = Tag.create("list");
   private static final int BORDER_SIZE = 3;

   public final void updateModel() {
      this._model.setSelectedIndex(this._paging.getSelected(), true);
   }

   @Override
   public final void focusChanged(Field field, int event) {
      if (field == this._rowsContainer) {
         this.invalidate();
         if (event == 3) {
            this._paging.setSelected(-1);
         }

         this._model.setSelectedIndex(this._paging.getSelected(), true);
         this._model.eventOccurred(1);
      }
   }

   @Override
   public final void add(int fromIndex, int count) {
      this._rowsContainer = new WicletRepetitionManager$RowsContainer(this);

      for (int i = fromIndex; i < fromIndex + count; i++) {
         WicletRepetitionManager$RowManager row = new WicletRepetitionManager$RowManager(this, i);
         this._rowsContainer.add(row);
      }

      this._rowsContainer.setFocusListener(this);
      this.add(this._rowsContainer);
   }

   @Override
   public final void update(int fromIndex, int count) {
      int i = fromIndex;

      for (int j = 0; i < fromIndex + count; j++) {
         WicletRepetitionManager$RowManager row = (WicletRepetitionManager$RowManager)this._rowsContainer.getField(j);
         row.recycle(i);
         i++;
      }
   }

   @Override
   public final void setSelected(int index) {
      Field fieldWithFocus = this.getFieldWithFocus();
      if (index > -1 && this._rowsContainer != fieldWithFocus && fieldWithFocus != null) {
         this.delete(fieldWithFocus);
         this.insert(fieldWithFocus, 0);
      }

      int fieldWidthFocusIndex = this._rowsContainer.getFieldWithFocusIndex();
      if (fieldWidthFocusIndex > -1 && index != fieldWidthFocusIndex) {
         int rowIndex = index < 0 ? fieldWidthFocusIndex : index;
         WicletRepetitionManager$FocusField focusField = (WicletRepetitionManager$FocusField)this.getLeafFieldWithFocus();
         Field newFocusField = ((WicletRepetitionManager$RowManager)this._rowsContainer.getField(rowIndex)).getFirstFocus();
         if (focusField != newFocusField) {
            focusField.setFocusRelay(newFocusField);
         }
      }
   }

   @Override
   public final void beginReconstruction() {
   }

   @Override
   public final void endReconstruction() {
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (RepetitionControl)model;
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
   public final void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      this.init();
      this._paging.setSelectedAndSize(this._model.getSelectedIndex(), this._model.getRepetitionCount());
   }

   @Override
   public final void unFocus() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final int getPreferredWidth() {
      return Graphics.getScreenWidth();
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   WicletRepetitionManager(ScreenContext context, RepetitionControl model, int row, long style) {
      super(2533274790395904L | style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this._style = style;
      this.init();
      this._paging = new PagingController(this, model.getRepetitionCount(), model.getVisibleRows(), model.getSelectedIndex());
      model.setSelectedIndex(this._paging.getSelected(), true);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      if (this._attributes != null) {
         this.setThemeAttributeSet(this._attributes);
      }

      super.applyFont();
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._screenContentHeight == -1) {
         this._screenContentHeight = ((WicaScreen)this.getScreen()).getMaxScreenContentHeight();
         this._screenContentHeightMinusScrollHeight = this._screenContentHeight - 20;
         this._maxRowHeight = 3 * this._screenContentHeightMinusScrollHeight;
      }

      if (this._visibility != 1) {
         super.sublayout(width, height);
      } else {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         super.subpaint(graphics);
      }
   }

   private final void init() {
      this._isCollapsible = this._model.isCollapsible();
      this._attributes = null;
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         this._attributes = this._context.getStyleFactory().getStyle(styleId);
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int context) {
      this._paging.makeContextMenu(menu, context);
   }
}
