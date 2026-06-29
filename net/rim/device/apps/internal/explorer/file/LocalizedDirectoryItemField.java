package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class LocalizedDirectoryItemField implements RootItem, FileConnectionHolder {
   private FileItemField[] _availablePaths = new FileItemField[0];
   private FileItemField[] _allPaths = new FileItemField[0];
   private String _displayName;
   private static final String MMS_FOLDER;

   public LocalizedDirectoryItemField(int localizedDirectoryKey, boolean isSelectWriteable) {
      if (localizedDirectoryKey != -1) {
         this._displayName = ExplorerResources.getStringArray(12)[localizedDirectoryKey];
         String[] topDirNames = ExplorerResources.getTokenizedStringArrayElement(13, localizedDirectoryKey);
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         String[] topDirPaths = ExplorerResources.getTokenizedStringArrayElement(0, 13, localizedDirectoryKey);
         int end = Math.min(topDirNames.length, topDirPaths.length);

         for (int i = 0; i < end; i++) {
            FileItemField fileItem = new FileItemField(topDirPaths[i], topDirNames[i], true);
            if (fileItem.exists() && (!isSelectWriteable || fileItem.canWrite())) {
               Arrays.add(this._availablePaths, fileItem);
            }

            Arrays.add(this._allPaths, fileItem);
         }
      } else {
         this._displayName = "";
         FileItemField fileItem = new FileItemField("/", "EX", true);
         Arrays.add(this._availablePaths, fileItem);
         Arrays.add(this._allPaths, fileItem);
      }
   }

   @Override
   public final String getName() {
      return this._displayName;
   }

   @Override
   public final String getPath() {
      return null;
   }

   public final FileItemField[] getPaths() {
      return this._availablePaths;
   }

   @Override
   public final String getURL() {
      return null;
   }

   @Override
   public final String toString() {
      return this.getName();
   }

   @Override
   public final FileConnectionHolder find(String path) {
      if (path.equals("/")) {
         return new FileItemField(path, "", true);
      }

      FileItemField foundFileItem = null;
      String foundPath = null;
      int i = this._availablePaths.length;

      while (--i >= 0) {
         FileItemField fileItem = this._availablePaths[i];
         String targetPath = fileItem.getPath();
         if (path.regionMatches(true, 0, targetPath, 0, targetPath.length())) {
            String checkPath = fileItem.getFullPath();
            if (foundPath == null || checkPath.length() > foundPath.length()) {
               foundPath = checkPath;
               foundFileItem = fileItem;
            }
         }
      }

      if (foundPath != null) {
         if (!path.equals(foundPath)) {
            foundFileItem = new FileItemField(path);
         }

         return foundFileItem;
      } else {
         return path.regionMatches(true, 0, "/store/samples/mms/", 0, 19) ? new FileItemField(path) : this;
      }
   }

   @Override
   public final FileConnectionHolder getParent(FileItemField srcFileItem) {
      String path = srcFileItem.toString();
      String matchedPath = null;
      int i = this._availablePaths.length;

      while (--i >= 0) {
         String rootPath = this._availablePaths[i].getPath();
         if (path.regionMatches(true, 0, rootPath, 0, rootPath.length())
            && path.length() != rootPath.length()
            && (matchedPath == null || matchedPath.length() < rootPath.length())) {
            matchedPath = rootPath;
         }
      }

      if (matchedPath != null && !path.equals(matchedPath)) {
         path = srcFileItem.getPath();
         return path.equals("/") ? new FileItemField(path, "", true) : new FileItemField(path);
      } else {
         return this;
      }
   }

   public final boolean onRootRemoved(String rootPath) {
      boolean changed = false;
      int i = this._availablePaths.length;

      while (--i >= 0) {
         if (StringUtilities.startsWithIgnoreCase(this._availablePaths[i].getPath(), rootPath)) {
            Arrays.removeAt(this._availablePaths, i);
            changed = true;
         }
      }

      return changed;
   }

   public final boolean onRootAdded(String rootPath) {
      boolean changed = false;
      int allIndex = this._allPaths.length - 1;

      for (int availableIndex = this._availablePaths.length - 1; allIndex != availableIndex && allIndex >= 0; allIndex--) {
         if (availableIndex >= 0 && this._allPaths[allIndex] == this._availablePaths[availableIndex]) {
            availableIndex--;
         } else if (StringUtilities.startsWithIgnoreCase(this._allPaths[allIndex].getPath(), rootPath)) {
            Arrays.insertAt(this._availablePaths, this._allPaths[allIndex], availableIndex + 1);
            changed = true;
         }
      }

      return changed;
   }
}
