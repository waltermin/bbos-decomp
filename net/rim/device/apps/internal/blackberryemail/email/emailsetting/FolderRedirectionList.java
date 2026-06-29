package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolderListener;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.SystemIcon;

class FolderRedirectionList extends FolderList implements Confirmation, EmailFolderListener {
   ServiceRecord _sr;
   private SelectAllFolderVerb _selectAllVerb;
   private SelectAllFolderVerb _deselectAllVerb;
   private FolderRedirectionList$ChangeOptionVerb _changeOptionVerb;
   private static final int UNCHECKED_ICON;
   private static final int CHECKED_ICON;
   private static final int DISABLED_ICON;

   public FolderRedirectionList(Folder startFolder, String title, boolean shouldMakeSelectVerbDefaultInitially, ServiceRecord sr) {
      super(startFolder, title, shouldMakeSelectVerbDefaultInitially);
      this.setTitle(EmailResources.getString(162));
      this._sr = sr;
      this._selectAllVerb = new SelectAllFolderVerb(this, true);
      this._deselectAllVerb = new SelectAllFolderVerb(this, false);
      this._changeOptionVerb = new FolderRedirectionList$ChangeOptionVerb(this);
      this.setSelectVerb(this._changeOptionVerb);
      EmailHierarchy.addEmailFolderListener(this);
   }

   @Override
   public boolean confirm(Verb verb, Object context) {
      if (super._disp.isDirty()) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case -1:
               return false;
            case 1:
               SaveVerb.getInstance(this, (FolderRedirectionTreeField)super._disp, this._sr).invoke(null);
         }
      }

      EmailHierarchy.removeEmailFolderListener(this);
      return true;
   }

   @Override
   public void folderAdded(EmailFolder ef) {
      this.exitLater();
   }

   @Override
   public void folderRemoved(EmailFolder ef) {
      this.exitLater();
   }

   @Override
   public void folderUpdated(EmailFolder ef) {
      this.exitLater();
   }

   private void exitLater() {
      if (this.isDisplayed()) {
         this.getApplication().invokeLater(new FolderRedirectionList$1(this));
      } else {
         EmailHierarchy.removeEmailFolderListener(this);
      }
   }

   private void forceExit() {
      EmailHierarchy.removeEmailFolderListener(this);
      Dialog.alert(EmailResources.getString(1005));
      this.cancel();
   }

   @Override
   protected TreeField makeTreeField() {
      FolderRedirectionTreeField treeField = new FolderRedirectionTreeField(this, 0);
      treeField.setEmptyString(MessageResources.getString(175), 4);
      treeField.setDefaultExpanded(false);
      this._selectAllVerb.setTreeField(treeField);
      this._deselectAllVerb.setTreeField(treeField);
      return treeField;
   }

   @Override
   public void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) {
      int x = 0;
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      if (treeField != null) {
         FolderRedirectionTreeField folderRedirTreeField = (FolderRedirectionTreeField)treeField;
         int height = Math.max(fontHeight, SystemIcon.COLLECTION.getHeight(fontHeight, fontHeight));
         int iconWidth = SystemIcon.COLLECTION.getWidth(fontHeight, fontHeight);
         int icon = 0;
         if (!folderRedirTreeField.isSupported(node) || folderRedirTreeField.isLocalFolder(node)) {
            icon = 7;
         } else if (folderRedirTreeField.isRedirected(node, false)) {
            icon = 1;
         }

         SystemIcon.COLLECTION.paint(graphics, x + indent, y, iconWidth, height, icon);
         x += iconWidth + 2;
         String label = this.getLabel(treeField.getCookie(node));
         if (label != null) {
            graphics.drawText(label, x + indent, y, 64, Display.getWidth() - x - indent + treeField.getManager().getHorizontalScroll());
         }
      }
   }

   private void toggleFolder() {
      FolderRedirectionTreeField treeField = (FolderRedirectionTreeField)super._disp;
      int id = treeField.getCurrentNode();
      if (treeField.isSupported(id)) {
         treeField.toggleRedirected(id);
      }

      this.invalidate();
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      int id = super._disp.getCurrentNode();
      switch (key) {
         case '\n':
            this.toggleFolder();
            return true;
         case '\u001b':
            if (!this.confirm(null, null)) {
               return true;
            }
            break;
         case ' ':
            if (status != 1) {
               this.toggleFolder();
               return true;
            }

            Object obj = super._disp.getCookie(id);
            if (obj instanceof EmailHeaderModel) {
               EmailFolder ef = (EmailHeaderModel)obj;
               if (!ef.containsSubFolders()) {
                  return true;
               }
            }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               FolderRedirectionTreeField treeField = (FolderRedirectionTreeField)super._disp;
               int id = treeField.getCurrentNode();
               if (treeField.getFirstChild(id) != -1) {
                  if (treeField.isSupported(id)) {
                     return false;
                  }

                  treeField.setExpanded(id, !treeField.getExpanded(id));
                  return true;
               }

               this.toggleFolder();
               return true;
         }
      }

      return handled;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      FolderRedirectionTreeField treeField = (FolderRedirectionTreeField)super._disp;
      Verb defaultVerb = null;
      if (instance == 0) {
         Verb saveVerb = SaveVerb.getInstance(this, treeField, this._sr);
         menu.add(ExitVerb.createCloseVerb(0, this));
         menu.add(saveVerb);
         if (treeField.isAnyFolderInState(true)) {
            menu.add(this._selectAllVerb);
         }

         if (treeField.isAnyFolderInState(false)) {
            menu.add(this._deselectAllVerb);
         }

         if (super._disp.isDirty()) {
            defaultVerb = saveVerb;
         }
      }

      int id = treeField.getCurrentNode();
      if (treeField.isSupported(id)) {
         menu.add(this._changeOptionVerb);
      }

      if (defaultVerb != null) {
         menu.setDefault(defaultVerb);
      }
   }
}
