package net.rim.device.apps.internal.globalsearch;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class GlobalSearchResultsScreen$ExpandAndCollapseVerb extends Verb {
   private SearchResultsFieldManager _field;

   public GlobalSearchResultsScreen$ExpandAndCollapseVerb(SearchResultsFieldManager field) {
      super(1115504);
      this._field = field;
   }

   @Override
   public final String toString() {
      return this._field.getExpanded() ? CommonResource.getString(12) : CommonResource.getString(11);
   }

   @Override
   public final Object invoke(Object parameter) {
      this._field.toggleExpansion();
      return null;
   }
}
