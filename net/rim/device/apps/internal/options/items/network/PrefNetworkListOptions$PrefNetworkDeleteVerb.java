package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.NetworkInfo;

final class PrefNetworkListOptions$PrefNetworkDeleteVerb extends Verb {
   private int _index;
   private PrefNetworkList _netList;

   PrefNetworkListOptions$PrefNetworkDeleteVerb(int selectedIndex, PrefNetworkList netList) {
      super(598352, CommonResource.getBundle(), 17);
      this._index = selectedIndex;
      this._netList = netList;
   }

   @Override
   public final Object invoke(Object parameter) {
      NetworkInfo netInfo = this._netList.getItem(this._index);
      String name = NetworkOptionsUtils.getPredefinedNetworkName(netInfo.getNetworkId());
      if (name == null || name.length() == 0) {
         name = NetworkOptionsUtils.buildNetIdString(netInfo);
      }

      String prompt = CommonResource.format(10025, name);
      if (Dialog.ask(2, prompt, -1) == 3) {
         this._netList.remove(this._index);
         return prompt;
      } else {
         return null;
      }
   }
}
