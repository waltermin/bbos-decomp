package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.icons.FileIcon;
import net.rim.device.internal.ui.Image;

public class AliasFileItemField extends FileItemField {
   AliasFileEntry _entry;
   boolean _selectable;
   boolean _executable;

   public AliasFileItemField(AliasFileEntry entry) {
      super(entry.getPath(), entry.getName(), true);
      this._entry = entry;
      this._executable = !(entry instanceof AliasFolderEntry);
      this._selectable = this._selectable | entry.getVerb() == null;
      if (entry instanceof AliasFolderEntry) {
         this.setParent();
      }
   }

   public AliasFileItemField(AliasFileEntry entry, boolean selectable) {
      this(entry);
      this._selectable = selectable;
   }

   @Override
   protected Image getImage(int size, Graphics graphics) {
      Image image = this._entry.getImage(size, graphics);
      if (image != null) {
         return image;
      } else {
         return this._executable ? FileIcon.getFileIconImage(0) : super.getImage(size, graphics);
      }
   }

   @Override
   public String getFullPath() {
      return this.isParent() ? this.getPath() : super.getFullPath();
   }

   @Override
   public void setPath(String path) {
      super.setPath(path);
      if (this.isSelectable()) {
         this.refreshFileAttributes();
      }
   }

   public boolean isSelectable() {
      return this._selectable;
   }

   @Override
   protected boolean isExecutable() {
      return this._executable;
   }

   public Object invoke(Object parameter) {
      Verb verb = this._entry.getVerb();
      return verb != null ? verb.invoke(parameter) : null;
   }

   @Override
   public String getDisplayName() {
      return this._executable ? this._entry.getVerb().toString() : super.getDisplayName();
   }
}
