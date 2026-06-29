package net.rim.device.apps.internal.explorer.Media.verbs;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class MediaVerb extends Verb {
   private ActionProvider _actionProvider;
   private long _guid;
   public static final long MOVE_IN_PLAYLIST;
   public static final long ADD_TO_PLAYLIST;
   public static final long ADD_RESULTS_TO_PLAYLIST;
   public static final long ADD_VIEW_TO_PLAYLIST;
   public static final long ADD_SONGS;
   public static final long DELETE;
   public static final long PLAY;
   public static final long RECORD;
   public static final long PLAY_RESULTS;
   public static final long SELECT;
   public static final long SELECT_ALL;
   public static final long CREATE_SMARTLIST;
   private static final ResourceBundleFamily _bundle = ExplorerResources.getResourceBundleFamily();

   public MediaVerb(long guid, ActionProvider actionProvider, int order, ResourceBundleFamily bundleFamily, int resourceId) {
      super(order, bundleFamily == null ? _bundle : bundleFamily, resourceId);
      this._guid = guid;
      this._actionProvider = actionProvider;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._actionProvider != null) {
         this._actionProvider.perform(this._guid, context);
      }

      return null;
   }
}
