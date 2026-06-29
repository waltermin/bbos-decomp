package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.util.LongVector;

class ChoiceField extends VerticalFieldManager implements View, PagedListModifier {
   protected PagingController _paging;
   private byte _visibility;
   private ThemeAttributeSet _attributes;
   private boolean _editable;
   private Vector _values;
   private ChoiceControl _model;
   private ScreenContext _context;

   protected String getLabel(int index) {
      Object value = this._values.elementAt(index);
      return value == null ? "" : value.toString();
   }

   protected void addInternal(int _1, int _2) {
      throw null;
   }

   public Vector getValues() {
      return this._values;
   }

   @Override
   public void update(int row) {
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      this.init();
      this._paging.setSelectedAndSize(this._model.getSelectedIndex(), this._values.size());
   }

   @Override
   public void beginReconstruction() {
   }

   @Override
   public void endReconstruction() {
      this.updateLayout();
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
   public void setModel(UIComponent model) {
      this._model = (ChoiceControl)model;
   }

   @Override
   public UIComponent getModel() {
      return this._model;
   }

   @Override
   public void add(int fromIndex, int count) {
      if (!this._values.isEmpty()) {
         this.addInternal(fromIndex, count);
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
   protected boolean incrementalLayout(int index, int added, int deleted) {
      return this._context.getSuspendLayout() ? true : super.incrementalLayout(index, added, deleted);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      if (this._attributes != null) {
         this.setThemeAttributeSet(this._attributes);
      }

      super.applyFont();
   }

   public ChoiceField(ScreenContext context, ChoiceControl model, long style) {
      super(2251799813685248L | style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this.init();
      this._paging = new PagingController(this, this._values.size(), model.getVisibleRows(), model.getSelectedIndex());
   }

   private void init() {
      this._editable = !this._model.isReadOnly();
      this._values = this.convertToVector(this._model.getValue());
      this._attributes = null;
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         this._attributes = this._context.getStyleFactory().getStyle(styleId);
      }
   }

   private Vector convertToVector(Object values) {
      Vector vector = null;
      if (values instanceof LongVector) {
         DateFormat df = DateFormat.getInstance(54);
         LongVector v = (LongVector)values;
         int size = v.size();
         vector = new Vector(size);

         for (int i = 0; i < size; i++) {
            vector.addElement(df.formatLocal(v.elementAt(i)));
         }
      } else {
         if (values instanceof Vector) {
            return (Vector)values;
         }

         vector = new Vector();
      }

      return vector;
   }

   @Override
   protected void makeMenu(Menu menu, int context) {
      this._paging.makeContextMenu(menu, context);
   }

   @Override
   public boolean isEditable() {
      return this._editable;
   }

   @Override
   public boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }
}
