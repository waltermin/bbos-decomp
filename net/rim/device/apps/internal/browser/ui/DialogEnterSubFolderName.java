package net.rim.device.apps.internal.browser.ui;

import java.util.Enumeration;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.i18n.CommonResource;

public final class DialogEnterSubFolderName extends PopupScreen implements FieldChangeListener {
   private EditField _editSubFolderName;
   private ButtonField _buttonOk;
   private ButtonField _buttonCancel;
   private boolean _cancelled;
   private Folder _currentFolder;

   public final String getName() {
      this._editSubFolderName.setFocus();
      UiApplication.getUiApplication().pushModalScreen(this);
      String typedData = this._editSubFolderName.getText();
      return !this._cancelled && typedData.length() != 0 ? typedData : null;
   }

   public final void handleSelection() {
      boolean popScreen = true;
      Field fieldWithFocus = this.getLeafFieldWithFocus();
      if (fieldWithFocus == this._buttonCancel) {
         this._cancelled = true;
      } else {
         this._editSubFolderName.setText(this._editSubFolderName.getText().trim());
         if (this._editSubFolderName.getText().length() > 0) {
            if (!checkForDuplicatePasses(this._editSubFolderName.getText(), this._currentFolder)) {
               popScreen = false;
            }
         } else {
            Dialog.alert(BrowserResources.getString(523));
            popScreen = false;
         }
      }

      if (popScreen) {
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof Object) {
         this.handleSelection();
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      this._cancelled = false;
      if (key == 27) {
         this._cancelled = true;
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else if (key == '\n') {
         this.handleSelection();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   public DialogEnterSubFolderName(Folder currentFolder, String defaultValue) {
      super((Manager)(new Object()), 0);
      this._currentFolder = currentFolder;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage((RichTextField)(new Object(BrowserResources.getString(595), 36028797018963968L)));
      this._editSubFolderName = (EditField)(new Object("", defaultValue));
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1125899906842624L));
      hfm.add(this._editSubFolderName);
      dfm.addCustomField(hfm);
      this._buttonOk = (ButtonField)(new Object(CommonResource.getString(100)));
      this._buttonCancel = (ButtonField)(new Object(CommonResource.getString(10005)));
      this._buttonOk.setChangeListener(this);
      this._buttonCancel.setChangeListener(this);
      HorizontalFieldManager hfmb = (HorizontalFieldManager)(new Object(12884901888L));
      hfmb.add(this._buttonOk);
      hfmb.add(this._buttonCancel);
      dfm.addCustomField(hfmb);
   }

   static final Folder findFolderByName(String name, Folder folder) {
      Folder returnValue = null;
      if (folder != null) {
         Enumeration subFolders = folder.getSubFolders();

         while (subFolders.hasMoreElements()) {
            Folder subFolder = (Folder)subFolders.nextElement();
            if (subFolder.getFriendlyName().equals(name)) {
               return subFolder;
            }
         }
      }

      return returnValue;
   }

   static final boolean checkForDuplicatePasses(String name, Folder folder) {
      boolean returnValue = true;
      Folder oldFolder = findFolderByName(name, folder);
      if (oldFolder != null) {
         Dialog.alert(BrowserResources.getString(524));
         returnValue = false;
      }

      return returnValue;
   }
}
