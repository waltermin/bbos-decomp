package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.io.file.FileUtilities;

final class MediaAliasFolderEntry extends AliasFolderEntry {
   private int _mediaType;

   MediaAliasFolderEntry(String name, Verb verb, String path, int mediaType) {
      super(path, name, verb);
      this._mediaType = mediaType;
   }

   @Override
   public final boolean isActiveInView(String path, int mediaType, boolean writeableOnly) {
      return mediaType == this._mediaType && path.equalsIgnoreCase(FileUtilities.getDefaultPath(this._mediaType));
   }
}
