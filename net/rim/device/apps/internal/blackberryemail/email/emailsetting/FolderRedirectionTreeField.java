package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

class FolderRedirectionTreeField extends TreeField {
   private int[] support;
   private int[] redirect;

   public FolderRedirectionTreeField(TreeFieldCallback callback, long style) {
      super(callback, style);
      this.resetSupportAndRedirectionArrays();
   }

   @Override
   public int addSiblingNode(int previousSibling, Object cookie) {
      int id = super.addSiblingNode(previousSibling, cookie);
      this.cacheAttributes(id);
      return id;
   }

   @Override
   public int addChildNode(int parent, Object cookie) {
      int id = super.addChildNode(parent, cookie);
      this.cacheAttributes(id);
      return id;
   }

   private void cacheAttributes(int id) {
      Object obj = this.getCookie(id);
      if (obj instanceof EmailHeaderModel) {
         EmailFolder folder = (EmailHeaderModel)obj;
         if (folder.folderAttributesFlagsSet(4)) {
            int currentIndex = Arrays.getIndex(this.support, id);
            if (currentIndex > -1) {
               this.support[currentIndex] = id;
               this.redirect[currentIndex] = folder.folderAttributesFlagsSet(8) ? 1 : 0;
               return;
            }

            Arrays.add(this.support, id);
            Arrays.add(this.redirect, folder.folderAttributesFlagsSet(8) ? 1 : 0);
         }
      }
   }

   public boolean isLocalFolder(int id) {
      Object obj = this.getCookie(id);
      if (!(obj instanceof EmailHeaderModel)) {
         return false;
      }

      EmailFolder folder = (EmailHeaderModel)obj;
      return (folder.getFolderAttributes() & 1) != 0;
   }

   public void toggleRedirected(int id) {
      int pos = Arrays.getIndex(this.support, id);
      this.redirect[pos] = this.redirect[pos] == 1 ? 0 : 1;
      this.setDirty(true);
   }

   public void selectAll(boolean select) {
      for (int i = this.redirect.length - 1; i >= 0; i--) {
         this.redirect[i] = select ? 1 : 0;
      }

      this.setDirty(true);
   }

   public boolean isAnyFolderInState(boolean select) {
      for (int i = this.redirect.length - 1; i >= 0; i--) {
         if (this.redirect[i] != (select ? 1 : 0)) {
            return true;
         }
      }

      return false;
   }

   public boolean isRedirected(int id, boolean index) {
      return index ? this.redirect[id] == 1 : this.redirect[Arrays.getIndex(this.support, id)] == 1;
   }

   public boolean isSupported(int id) {
      return Arrays.getIndex(this.support, id) >= 0;
   }

   public int[] getFolderCookies() {
      return this.support;
   }

   @Override
   public void deleteAll() {
      super.deleteAll();
      this.resetSupportAndRedirectionArrays();
   }

   @Override
   public void deleteSubtree(int node) {
      super.deleteSubtree(node);
      this.resetSupportAndRedirectionArrays();
   }

   private void resetSupportAndRedirectionArrays() {
      this.support = new int[0];
      this.redirect = new int[0];
   }
}
