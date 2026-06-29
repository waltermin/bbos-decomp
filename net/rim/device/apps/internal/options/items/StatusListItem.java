package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.vm.Array;

public class StatusListItem implements VerbProvider {
   protected int _resourceID;

   public void setResourceID(int resourceID) {
      this._resourceID = resourceID;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Verb v = new StatusListItem$CopyStatusListItemVerb(this);
      Array.resize(verbs, 1);
      verbs[0] = v;
      return v;
   }

   public String getDisplayValue() {
      throw null;
   }

   public String getDisplayName() {
      return OptionsResources.getString(this._resourceID);
   }

   public StatusListItem() {
   }

   public StatusListItem(int resourceID) {
      this._resourceID = resourceID;
   }
}
