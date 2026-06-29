package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

final class ApplicationLauncherFieldHelper {
   private ApplicationLauncherField _launcher;
   HierarchyManager _hierarchyManager;
   private InternalApplicationFolder[] _folderStack;
   InternalApplicationFolder _parentFolder;
   InternalApplicationFolder _activeFolder;
   boolean _showHiddenApps;

   ApplicationLauncherFieldHelper(ApplicationLauncherField launcherField, HierarchyManager hierarchyManager) {
      this._launcher = launcherField;
      this._hierarchyManager = hierarchyManager;
      this._folderStack = new InternalApplicationFolder[0];
   }

   final void clearFolderStack() {
      synchronized (this._folderStack) {
         Array.resize(this._folderStack, 0);
      }

      this._activeFolder = null;
   }

   final void setActiveFolder(InternalApplicationFolder folder) {
      if (folder != this._activeFolder) {
         this._activeFolder = folder;
         this._launcher.deleteAllApplications();
         this._launcher.loadApplications();
         this._launcher.setFocus(folder.getLastActiveApplication());
      }
   }

   private final void switchToFolder(InternalApplicationFolder folder) {
      this._activeFolder.setLastActiveApplication(this._launcher.getSelectedApplication());
      this.setActiveFolder(folder);
      ((Manager)this._launcher).invalidate();
   }

   final int getApplicationIconHeight() {
      Theme theme = ThemeManager.getActiveTheme();
      return theme.getRibbonIconHeight();
   }

   final int getApplicationIconWidth() {
      int fieldWidth = ((Field)this._launcher).getWidth();
      return this.getApplicationIconWidth(fieldWidth);
   }

   final int getApplicationIconWidth(int fieldWidth) {
      Theme theme = ThemeManager.getActiveTheme();
      int width = theme.getRibbonIconWidth();
      if (width != 0 && fieldWidth != 0) {
         width = fieldWidth / (fieldWidth / width);
      }

      return width;
   }

   final boolean isRootFolder() {
      synchronized (this._folderStack) {
         return this._folderStack.length == 0;
      }
   }

   final boolean goToFolder(String folderName) {
      InternalApplicationFolder folder = this._hierarchyManager.getFolder(folderName);
      if (folder != null) {
         synchronized (this._folderStack) {
            int stackLength = this._folderStack.length;
            if (folder == HierarchyManager.getInstance().getFolder(InternalApplicationFolder.ROOT_FOLDER_NAME) && stackLength == 1) {
               return this.popFolder();
            } else {
               return stackLength > 1 && folder == this._folderStack[stackLength - 2] ? this.popFolder() : this.pushFolder(folderName);
            }
         }
      } else {
         return false;
      }
   }

   final boolean pushFolder(String folderName) {
      InternalApplicationFolder folder = this._hierarchyManager.getFolder(folderName);
      if (folder != null) {
         synchronized (this._folderStack) {
            int stackLength = this._folderStack.length;
            if (stackLength > 0) {
               this._parentFolder = this._folderStack[stackLength - 1];
            } else {
               this._parentFolder = null;
            }

            Arrays.add(this._folderStack, folder);
            if (folderName.length() == 0) {
               Array.resize(this._folderStack, 0);
            }

            this.switchToFolder(folder);
            return true;
         }
      } else {
         return false;
      }
   }

   final boolean popFolder() {
      synchronized (this._folderStack) {
         int length = this._folderStack.length;
         if (length > 0) {
            InternalApplicationFolder folder = this._folderStack[--length];
            Arrays.removeAt(this._folderStack, length);
            if (length > 1) {
               this._parentFolder = this._folderStack[length - 2];
               this.switchToFolder(this._folderStack[length - 1]);
            } else if (length == 1) {
               this._parentFolder = HierarchyManager.getInstance().getFolder(InternalApplicationFolder.ROOT_FOLDER_NAME);
               this.switchToFolder(this._folderStack[0]);
            } else {
               this._parentFolder = null;
               this.switchToFolder(HierarchyManager.getInstance().getFolder(InternalApplicationFolder.ROOT_FOLDER_NAME));
            }

            return true;
         } else {
            return false;
         }
      }
   }

   final InternalApplicationFolder getParentFolder() {
      return this._parentFolder;
   }

   final boolean hidingIconsAllowed() {
      return this._activeFolder != null ? this._activeFolder.getAllowHideIcons() : false;
   }

   final boolean hasHiddenApplications() {
      InternalApplicationFolder activeFolder = this._activeFolder;
      if (activeFolder != null) {
         ApplicationEntry[] applications = activeFolder.getApplications();

         for (int i = applications.length - 1; i >= 0; i--) {
            if (!applications[i].isVisible()) {
               return true;
            }
         }
      }

      return false;
   }

   final int getVisibleApplicationCount() {
      InternalApplicationFolder activeFolder = this._activeFolder;
      if (activeFolder != null) {
         ApplicationEntry[] applications = activeFolder.getApplications();
         int count = 0;

         for (int i = applications.length - 1; i >= 0; i--) {
            if (applications[i].isVisible()) {
               count++;
            }
         }

         return count;
      } else {
         return 0;
      }
   }
}
