package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.Array;

public final class RootFileItemField extends FileItemField implements RootItem {
   AliasFolderEntry[] _aliasEntries;

   public RootFileItemField(String path, String displayName) {
      super(FileUtilities.getPath(path), FileUtilities.getName(path), displayName, false, null, null);
      this.setRoot();
      this._aliasEntries = new AliasFolderEntry[0];
   }

   public final void update(String pathAndFilename, AliasFolderEntry[] entries) {
      this.setPath(FileUtilities.getPath(pathAndFilename));
      this.setName(FileUtilities.getName(pathAndFilename));
      synchronized (this._aliasEntries) {
         Array.resize(this._aliasEntries, entries.length);
         System.arraycopy(entries, 0, this._aliasEntries, 0, entries.length);
      }
   }

   public final void addAlias(AliasFolderEntry alias) {
      synchronized (this._aliasEntries) {
         Arrays.add(this._aliasEntries, alias);
      }
   }

   public final void removeAlias(AliasFolderEntry alias) {
      synchronized (this._aliasEntries) {
         Arrays.remove(this._aliasEntries, alias);
      }
   }

   public final AliasFolderEntry[] getAliases() {
      synchronized (this._aliasEntries) {
         AliasFolderEntry[] entries = new AliasFolderEntry[this._aliasEntries.length];
         System.arraycopy(this._aliasEntries, 0, entries, 0, entries.length);
         return entries;
      }
   }

   @Override
   public final FileConnectionHolder find(String path) {
      String rootPath = this.getFullPath();
      if (StringUtilities.startsWithIgnoreCase(path, rootPath, 1701707776)) {
         return !path.equalsIgnoreCase(rootPath) && FileUtilities.checkFileExists(FileUtilities.makeFileURL(path)) ? new FileItemField(path) : this;
      }

      synchronized (this._aliasEntries) {
         int i = this._aliasEntries.length;

         while (--i >= 0) {
            AliasFolderEntry alias = this._aliasEntries[i];
            String aliasPath = alias.getPath();
            if (StringUtilities.startsWithIgnoreCase(path, aliasPath)) {
               if (!path.equalsIgnoreCase(aliasPath) && FileUtilities.checkFileExists(path)) {
                  return new FileItemField(path);
               }

               return new AliasFileItemField(alias);
            }
         }

         return this;
      }
   }

   @Override
   public final FileConnectionHolder getParent(FileItemField srcFileItem) {
      return this.find(srcFileItem.isAlias() ? FileUtilities.getPath(srcFileItem.getPath()) : srcFileItem.getPath());
   }
}
