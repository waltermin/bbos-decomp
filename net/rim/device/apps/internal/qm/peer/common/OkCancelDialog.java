package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.internal.qm.resource.QmResources;

public class OkCancelDialog extends QmThemedPopupScreen implements FieldChangeListener {
   public ButtonField _okField;
   private ButtonField _cancelField;
   private ButtonField _pasteField;
   private BasicEditField _editField;
   private boolean _result;
   private FlowFieldManager _hfm = (FlowFieldManager)(new Object(12884901888L));

   public void addTitle(String title) {
      if (title != null && title.length() > 0) {
         this.add((Field)(new Object(title)));
         this.add((Field)(new Object()));
      }
   }

   public void addCancelButton() {
      this._cancelField = (ButtonField)(new Object(CommonResources.getString(9042)));
      this._cancelField.setChangeListener(this);
      this._hfm.add(this._cancelField);
      this.add(this._hfm);
   }

   public void addOkCancelButtons() {
      this.addButtons(false);
   }

   public void addPasteButton(BasicEditField field) {
      this._editField = field;
      if (this._pasteField == null) {
         this._pasteField = (ButtonField)(new Object(QmResources.getString(55)));
         this._pasteField.setChangeListener(this);
      }

      if (this._pasteField.getManager() == null) {
         this._hfm.add(this._pasteField);
      }
   }

   public boolean validate() {
      return true;
   }

   public boolean doModal() {
      Ui.getUiEngine().getActiveScreen().doPaint();
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   public void close(boolean result) {
      if (!result || this.validate()) {
         this._result = result;
         this.close();
      }
   }

   public boolean isEmpty(String name) {
      return QmUtil.isEmpty(name);
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._okField) {
            this.close(true);
         } else if (field == this._cancelField) {
            this.close(false);
         } else {
            if (field == this._pasteField) {
               String paste = Clipboard.getClipboard().toString();
               if (paste.length() > 0) {
                  try {
                     this._editField.setText(paste);
                     return;
                  } finally {
                     PopupStatus.show(QmResources.getString(39), 700);
                     return;
                  }
               }
            }
         }
      }
   }

   public OkCancelDialog() {
      super(new OkCancelDialog$OkCancelDialogManager(), 0);
      this.applyTheme();
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(false);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private void addButtons(boolean okOnly) {
      this._okField = (ButtonField)(new Object(CommonResources.getString(117)));
      this._okField.setChangeListener(this);
      this._hfm.add(this._okField);
      if (!okOnly) {
         this._cancelField = (ButtonField)(new Object(CommonResources.getString(9042)));
         this._cancelField.setChangeListener(this);
         this._hfm.add(this._cancelField);
      }

      this.add(this._hfm);
   }
}
