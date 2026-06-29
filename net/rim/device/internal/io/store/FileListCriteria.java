package net.rim.device.internal.io.store;

final class FileListCriteria {
   private FolderImpl[] _rootFolders;
   private boolean _searchSubFolders;
   private int _attrOn = 32;
   private int _attrOff = 8;
   private int _drmAttrOn;
   private int _drmAttrOff;
   private String _contentType;
   private boolean _filterDeadLinks;

   public FileListCriteria() {
   }

   public final boolean filterDeadLinks() {
      return this._filterDeadLinks;
   }

   public final int getAttributesOff() {
      return this._attrOff;
   }

   public final int getAttributesOn() {
      return this._attrOn;
   }

   public final int getDrmAttributesOff() {
      return this._drmAttrOff;
   }

   public final int getDrmAttributesOn() {
      return this._drmAttrOn;
   }

   public final String getContentType() {
      return this._contentType;
   }

   public final FolderImpl getRootFolder() {
      return this._rootFolders != null && this._rootFolders.length > 0 ? this._rootFolders[0] : null;
   }

   public final FolderImpl[] getRootFolders() {
      return this._rootFolders;
   }

   public final boolean isSubfoldersIncluded() {
      return this._searchSubFolders;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final boolean matches(FileImpl file) {
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         int e = file.getAttributes();
         int len = this._rootFolders.length;

         for (int i = 0; i < len; i++) {
            FolderImpl folder = this._rootFolders[i];
            if ((this._searchSubFolders ? folder.isAncestorOf((FolderImpl)file.getFolder()) : folder == file.getFolder())
               && (e & this._attrOff) == 0
               && (e & this._attrOn) == this._attrOn) {
               return true;
            }
         }

         var7 = false;
      } finally {
         if (var7) {
            if (file instanceof SymbolicLinkImpl) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public final boolean matchesCustom(FileImpl file) {
      return true;
   }

   public final void setAttributes(int attrOn, int attrOff) {
      this._attrOn = attrOn;
      this._attrOff = attrOff;
   }

   public final void setDrmAttributes(int drmAttrOn, int drmAttrOff) {
      this._drmAttrOn = drmAttrOn;
      this._drmAttrOff = drmAttrOff;
   }

   public final void setContentType(String contentType) {
      if (contentType != null && contentType.length() == 0) {
         contentType = null;
      }

      this._contentType = contentType;
   }

   public final void setFilterDeadLinks(boolean filterDeadLinks) {
      this._filterDeadLinks = filterDeadLinks;
   }

   public final void setRootFolder(FolderImpl rootFolder, boolean searchSubFolders) {
      if (this._rootFolders == null || this._rootFolders.length != 1) {
         this._rootFolders = new FolderImpl[1];
         this._rootFolders[0] = rootFolder;
      }

      this._searchSubFolders = searchSubFolders;
   }

   public final void setRootFolder(String rootFolderName, boolean searchSubFolders) {
      ContentStoreImpl store = ContentStoreImpl.getInstance();
      FolderImpl folder = store.getFolder(rootFolderName);
      this.setRootFolder(folder, searchSubFolders);
   }

   public final void setRootFolders(FolderImpl[] rootFolders, boolean searchSubFolders) {
      if (rootFolders == null) {
         this._rootFolders = null;
      } else {
         this._rootFolders = new FolderImpl[rootFolders.length];

         for (int index = rootFolders.length - 1; index >= 0; index--) {
            this._rootFolders[index] = rootFolders[index];
         }
      }

      this._searchSubFolders = searchSubFolders;
   }

   public final void setRootFolders(String[] rootFolderNames, boolean searchSubFolders) {
      if (rootFolderNames != null) {
         int len = rootFolderNames.length;
         FolderImpl[] rootFolders = new FolderImpl[len];

         for (int i = 0; i < len; i++) {
            ContentStoreImpl store = ContentStoreImpl.getInstance();
            rootFolders[i] = store.getFolder(rootFolderNames[i]);
         }

         this.setRootFolders(rootFolders, searchSubFolders);
      }
   }
}
