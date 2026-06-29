package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

public class FindVerbManager {
   Manager _baseManager;
   FindVerb _findVerb;
   FindNextVerb _findNextVerb;
   FieldsSearch _fieldsSearch;
   FieldsSearchResult _fieldsSearchResult;

   public FindVerbManager(Manager baseManager) {
      this._baseManager = baseManager;
   }

   public Verb[] getVerbs() {
      Verb[] verbs = new Verb[2];
      int verbCount = 0;
      if (this._findVerb == null) {
         this._findVerb = new FindVerb(this);
      }

      verbs[verbCount++] = this._findVerb;
      if (this._fieldsSearch != null && this._fieldsSearchResult != null) {
         if (this._findNextVerb == null) {
            this._findNextVerb = new FindNextVerb(this);
         }

         verbs[verbCount++] = this._findNextVerb;
      }

      Array.resize(verbs, verbCount);
      return verbs;
   }

   public Object invokeFind(boolean findNext, Object context) {
      Verb[] verbs = this.getVerbs();
      return findNext && verbs.length > 1 ? verbs[1].invoke(context) : verbs[0].invoke(context);
   }
}
