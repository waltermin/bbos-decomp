package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class PrefNetworkListOptions$PrefNetworkViewVerb extends Verb {
   private int _priority;
   private PrefNetworkList _netList;

   PrefNetworkListOptions$PrefNetworkViewVerb(int priority, PrefNetworkList netList) {
      super(598288, OptionsResources.getResourceBundle(), 1875);
      this._priority = priority;
      this._netList = netList;
   }

   @Override
   public final Object invoke(Object parameter) {
      UiApplication.getUiApplication().pushScreen(new PrefNetworkItemOptions(this._priority, this._netList, 3));
      return null;
   }
}
