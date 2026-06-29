package net.rim.device.apps.internal.explorer.file.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class UpNavigationVerb extends Verb {
   private ExploreManager _explorer;

   public UpNavigationVerb(ExploreManager explorer) {
      super(0, ExplorerResources.getResourceBundleFamily(), 46);
      this._explorer = explorer;
   }

   @Override
   public final Object invoke(Object context) {
      this._explorer.navigateUp(true);
      return null;
   }
}
