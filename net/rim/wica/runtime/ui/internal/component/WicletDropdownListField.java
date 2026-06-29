package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;

final class WicletDropdownListField extends WicletDropdownListFrame implements View, FieldChangeListener, Focusable {
   private ScreenContext _context;
   private ChoiceControl _model;
   private byte _visibility;

   WicletDropdownListField(ScreenContext context, ChoiceControl model, int row, long style) {
      super((model.getBorder() ? 1 : 0) | style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      Vector values = (Vector)model.getValue();
      if (values == null) {
         values = new Vector();
      }

      Object[] itemArray = new Object[values.size()];
      values.copyInto(itemArray);
      ObjectChoiceField choice = new ObjectChoiceField("", null, 0, 4294967296L);
      choice.setEditable(!model.isReadOnly());
      choice.setChoices(itemArray);
      if (itemArray.length > 0) {
         int index = model.getSelectedIndex();
         if (index == -1) {
            index = 0;
         }

         choice.setSelectedIndex(index);
         model.setSelectedIndex(index, true);
      }

      choice.setChangeListener(this);
      this.add(choice);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         ObjectChoiceField choice = (ObjectChoiceField)this.getField(0);
         if (choice.isDirty()) {
            int index = choice.getSelectedIndex();
            this._model.setSelectedIndex(index, true);
            this._model.eventOccurred(1);
            choice.setDirty(false);
         }
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
   protected final void sublayout(int width, int height) {
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

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (ChoiceControl)model;
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
      ObjectChoiceField choice = (ObjectChoiceField)this.getField(0);
      Vector values = (Vector)this._model.getValue();
      if (values == null) {
         values = new Vector();
      }

      Object[] itemArray = new Object[values.size()];
      values.copyInto(itemArray);
      choice.setChoices(itemArray);
      if (itemArray.length > 0) {
         int index = this._model.getSelectedIndex();
         if (index == -1 || index > choice.getSize()) {
            index = 0;
         }

         choice.setSelectedIndex(index);
         this._model.setSelectedIndex(index, true);
      }

      choice.setEditable(!this._model.isReadOnly());
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
