package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.container.FrameLayout;

public final class PasswordKeeperPasswordDialog extends PopupDialog implements FieldChangeListener {
   private BasicEditField _inputField;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private ButtonField _deleteButton;
   public static final int DEFAULT_MAXCHARS = 32;
   public static final int DELETE_ENTRY = 2;

   public final String getText() {
      return this._inputField.getText();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close(0);
      } else if (field == this._cancelButton) {
         this.close(-1);
      } else {
         if (field == this._deleteButton) {
            this.close(2);
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field field = this.getDelegate().getLeafFieldWithFocus();
         if (field == this._cancelButton) {
            this.close(-1);
            return true;
         }

         if (field == this._okButton || field == this._inputField) {
            this.close(0);
            return true;
         }

         if (field == this._deleteButton) {
            this.close(2);
            return true;
         }
      } else if (key == 27) {
         this.close(-1);
         return true;
      }

      return super.keyChar(key, status, time);
   }

   private final void addFields(boolean showPassword, String text, boolean showDeleteEntryButton) {
      VerticalFieldManager manager = (VerticalFieldManager)this.getDelegate();
      RichTextField textField = new RichTextField(text, 45035996273704960L);
      manager.add(textField);
      if (showPassword) {
         this._inputField = new BasicEditField(null, null, 32, 1610612736);
      } else {
         this._inputField = new PasswordEditField(null, null, 32, 2199560126464L);
      }

      FrameLayout layout = new FrameLayout(1);
      layout.add(this._inputField);
      manager.add(layout);
      this._okButton = new ButtonField(CommonResource.getString(100));
      this._okButton.setChangeListener(this);
      this._cancelButton = new ButtonField(CommonResource.getString(19));
      this._cancelButton.setChangeListener(this);
      if (showDeleteEntryButton) {
         this._deleteButton = new ButtonField(PasswordKeeper.getString(3034));
         this._deleteButton.setChangeListener(this);
      }

      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      buttonManager.add(this._okButton);
      buttonManager.add(this._cancelButton);
      manager.add(buttonManager);
      if (showDeleteEntryButton) {
         HorizontalFieldManager deleteButtonManager = new HorizontalFieldManager(12884901888L);
         deleteButtonManager.add(this._deleteButton);
         manager.add(deleteButtonManager);
      }

      this._inputField.setFocus();
   }

   public PasswordKeeperPasswordDialog(boolean showPassword, String text, boolean showDeleteEntryButton) {
      super(new VerticalFieldManager(1153220571769602048L));
      this.addFields(showPassword, text, showDeleteEntryButton);
   }
}
