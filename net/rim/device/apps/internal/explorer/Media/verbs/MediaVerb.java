package net.rim.device.apps.internal.explorer.Media.verbs;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class MediaVerb extends Verb {
   private ActionProvider _actionProvider;
   private long _guid;
   public static final long MOVE_IN_PLAYLIST = 5204271450404318686L;
   public static final long ADD_TO_PLAYLIST = 5933455911148432190L;
   public static final long ADD_RESULTS_TO_PLAYLIST = 6640987057326686423L;
   public static final long ADD_VIEW_TO_PLAYLIST = 7051267296949023454L;
   public static final long ADD_SONGS = 6021295727530465908L;
   public static final long DELETE = 6068253483645874830L;
   public static final long PLAY = 3712387911241450002L;
   public static final long RECORD = 3121618378580614779L;
   public static final long PLAY_RESULTS = -6042251527357905929L;
   public static final long SELECT = 8725737741808251806L;
   public static final long SELECT_ALL = 1518675114010876469L;
   public static final long CREATE_SMARTLIST = -6715256456950413284L;
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
