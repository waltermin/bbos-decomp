package net.rim.device.apps.internal.browser.bookmark;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.util.SortedCollection;

public class ProvisionedBookmarksFolder implements Folder {
   private long _folderID;
   private Folder _parentFolder;
   private Folder[] _subfolders;
   private String _name;
   private SortedCollection _bookmarks;
   private String _configUID;
   private boolean _expandByDefault;

   public void setBookmarks(SortedCollection bookmarks) {
      this._bookmarks = bookmarks;
   }

   public void setFriendlyName(String name) {
      this._name = name;
   }

   public void setSubfolders(Folder[] subfolders) {
      this._subfolders = subfolders;
   }

   public void setConfigUID(String configUID) {
      this._configUID = configUID;
   }

   public String getConfigUID() {
      return this._configUID;
   }

   public void setExpandByDefault(boolean expandByDefault) {
      this._expandByDefault = expandByDefault;
   }

   public boolean expandByDefault() {
      return this._expandByDefault;
   }

   @Override
   public Folder getFolder(long folderUid) {
      if (folderUid == this._folderID) {
         return this;
      }

      if (this._subfolders != null) {
         for (int i = this._subfolders.length - 1; i >= 0; i--) {
            Folder folder = this._subfolders[i].getFolder(folderUid);
            if (folder != null) {
               return folder;
            }
         }
      }

      return null;
   }

   @Override
   public Folder getParentFolder() {
      return this._parentFolder;
   }

   @Override
   public Enumeration getSubFolders() {
      return (Enumeration)(this._subfolders != null ? new Object(this._subfolders) : new Object());
   }

   @Override
   public boolean containsSubFolders() {
      return this._subfolders != null;
   }

   @Override
   public Collection getContainedItems() {
      return this._bookmarks;
   }

   @Override
   public Folder getBaseFolder() {
      return this._parentFolder;
   }

   @Override
   public boolean canContainItems() {
      return this._bookmarks != null;
   }

   @Override
   public boolean isVisible(Object context) {
      return true;
   }

   @Override
   public String getFriendlyName() {
      return this._name;
   }

   @Override
   public long getLUID() {
      return this._folderID;
   }

   public ProvisionedBookmarksFolder(long folderID, Folder parentFolder) {
      this._folderID = folderID;
      this._parentFolder = parentFolder;
   }

   @Override
   public String toString() {
      return this.getFriendlyName();
   }
}
