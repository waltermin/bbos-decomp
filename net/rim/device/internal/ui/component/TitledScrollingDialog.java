package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class TitledScrollingDialog extends PopupDialog implements FieldChangeListener {
   protected Font _boldFont = Font.getDefault();
   protected ButtonField _ok;
   protected VerticalFieldManager _nonScrollingRegion;
   protected SeparatorField _nonScrollingSeparator;
   protected VerticalIndentFieldManager _scrollingRegion;
   private static final int INDENT_PIXEL_WIDTH = 12;

   protected void setTitle(String dialogTitle) {
      LabelField dialogTitleField = new LabelField(dialogTitle, 64);
      dialogTitleField.setFont(this._boldFont);
      this.setTitle(dialogTitleField);
   }

   protected void setTitle(Field dialogTitleField) {
      Manager manager = this.getDelegate();
      manager.insert(dialogTitleField, 0);
      manager.insert(new SeparatorField(), 1);
   }

   protected void populateDialog() {
      this._ok = new ButtonField(CommonResource.getString(100), 12884901888L);
      this._ok.setChangeListener(this);
      this.addScrollingField(this._ok);
   }

   protected RichTextField addScrollingLabelAndValue(String label, String value) {
      if (label != null && value != null && value.length() != 0) {
         boolean setFocus = this._scrollingRegion.getFieldCount() == 0;
         LabelField labelField = new LabelField(label, 64);
         labelField.setFont(this._boldFont);
         RichTextField valueField = new RichTextField(value, 9007199254740992L);
         synchronized (Application.getEventLock()) {
            this._scrollingRegion.add(labelField);
            this._scrollingRegion.add(valueField, 12);
            if (setFocus) {
               this._scrollingRegion.setFocus();
            }

            return valueField;
         }
      } else {
         return null;
      }
   }

   protected void addScrollingField(Field field) {
      boolean setFocus = this._scrollingRegion.getFieldCount() == 0;
      synchronized (Application.getEventLock()) {
         this._scrollingRegion.add(field);
         if (setFocus) {
            this._scrollingRegion.setFocus();
         }
      }
   }

   protected void addNonScrollingField(Field field, int index) {
      synchronized (Application.getEventLock()) {
         int count = this._nonScrollingRegion.getFieldCount();
         if (count == 0) {
            this._nonScrollingSeparator = new SeparatorField();
            this.getDelegate().insert(this._nonScrollingSeparator, this._nonScrollingRegion.getIndex() + 1);
         }

         this._nonScrollingRegion.insert(field, Math.max(index, count));
      }
   }

   protected void addNonScrollingField(Field field) {
      this.addNonScrollingField(field, this._nonScrollingRegion.getFieldCount());
   }

   protected void addNonScrollingText(String text) {
      LabelField labelField = new LabelField(text, 64);
      labelField.setFont(this._boldFont);
      this.addNonScrollingField(labelField);
   }

   protected void deleteAllNonScrollingFields() {
      synchronized (Application.getEventLock()) {
         this._nonScrollingRegion.deleteAll();
         if (this._nonScrollingSeparator != null) {
            this.getDelegate().delete(this._nonScrollingSeparator);
            this._nonScrollingSeparator = null;
         }
      }
   }

   protected boolean handleFieldChanged(Field field, int context) {
      if (field == this._ok) {
         this.close(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      this.handleFieldChanged(field, context);
   }

   public TitledScrollingDialog() {
      this(0);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         new TitledScrollingDialog$PopulateDialogThread(this).start();
      }
   }

   public TitledScrollingDialog(long style) {
      super(new VerticalFieldManager(3458764513820540928L), style);
      this._boldFont = this._boldFont.derive(this._boldFont.getStyle() | 1);
      this._nonScrollingRegion = new VerticalFieldManager(1152921504606846976L);
      this._scrollingRegion = new VerticalIndentFieldManager(1153220571769602048L);
      Manager manager = this.getDelegate();
      manager.add(this._nonScrollingRegion);
      manager.add(this._scrollingRegion);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      } else if (key == 27) {
         this.close(-1);
         return true;
      } else {
         return false;
      }
   }
}
