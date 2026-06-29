package net.rim.device.apps.internal.task;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class ShowAllNamesVerb extends Verb {
   private KeywordFilteredScreen _screen;
   private static ShowAllNamesVerb _theVerb;

   private ShowAllNamesVerb(KeywordFilteredScreen screen) {
      super(479488);
      this._screen = screen;
   }

   public static final Verb getInstance(KeywordFilteredScreen screen) {
      if (_theVerb == null || _theVerb._screen != screen) {
         _theVerb = new ShowAllNamesVerb(screen);
      }

      return _theVerb;
   }

   @Override
   public final String toString() {
      return TaskResources.getString(46);
   }

   @Override
   public final Object invoke(Object parameter) {
      this._screen.setSearchPattern(null);
      return null;
   }
}
