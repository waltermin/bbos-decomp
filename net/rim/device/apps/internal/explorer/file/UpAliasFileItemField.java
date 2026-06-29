package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerIcons;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.apps.internal.explorer.file.verbs.UpNavigationVerb;

public final class UpAliasFileItemField extends AliasFileItemField {
   UpAliasFileItemField(ExploreManager explorer) {
      super(new AliasFileEntry(ExplorerResources.getString(46), new UpNavigationVerb(explorer), ExplorerIcons.getFolderIcon().getImage(1)), true);
      this.setParent();
   }

   @Override
   public final void setPath(String path) {
   }

   @Override
   public final String getPath() {
      return "/TMP/";
   }
}
