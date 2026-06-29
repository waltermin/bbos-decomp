package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.verb.Verb;

public class AliasFolderEntry extends AliasFileEntry {
   public AliasFolderEntry(String path, String name, Verb verb) {
      super(path, name, verb, null);
   }

   public boolean isActiveInView(String path, int mediaType, boolean writeableOnly) {
      return true;
   }
}
