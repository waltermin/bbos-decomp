package net.rim.device.apps.internal.options;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.internal.ui.component.SimpleInputDialog;

class CustomDictUnitEditor$CustomDictEditorScreen extends SimpleInputDialog implements GlobalEventListener {
   static ResourceBundle _rb = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input");

   public CustomDictUnitEditor$CustomDictEditorScreen(String title, String initialString) {
      super(0, title, 0, Integer.MAX_VALUE, 4503601774870528L);
      TextFilter filter = new CustomDictUnitEditor$CustomDictEditorScreen$1(this);
      BasicEditField editField = this.getEditField();
      editField.setCookie(editField);
      editField.setFilter(filter);
      this.setText(initialString);
      editField.setFocus();
   }

   @Override
   protected boolean accept() {
      switch (Dialog.ask(1)) {
         case -2:
         case 0:
            return false;
         case -1:
            this.makeComposed();
            return false;
         case 1:
         default:
            return true;
         case 2:
            this.close();
            return false;
      }
   }

   public void makeComposed() {
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         UiApplication.getUiApplication().addGlobalEventListener(this);
      } else {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -8040378802380461050L) {
         this.close();
      }
   }

   @Override
   public void show() {
      try {
         super.show();
      } finally {
         try {
            String msg = OptionsResources.getString(1949);
            Status.show(msg, Bitmap.getPredefinedBitmap(0), 2000, 0, true, true, 0);
            return;
         } finally {
            return;
         }
      }
   }

   @Override
   public void setType(int type) {
      BasicEditField editField = this.getEditField();
      if (editField != null) {
         this.delete(editField);
      }

      editField = new EditField("", null, 127, 4503601774870528L);
      this.setEditField(editField);
      this.add(editField);
   }
}
