package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.device.internal.system.RadioInternal;

final class NetworkOptionsItem$MyVerb extends Verb {
   private int _type;
   private final NetworkOptionsItem this$0;

   public NetworkOptionsItem$MyVerb(NetworkOptionsItem _1, int type, int menuOrder) {
      super(menuOrder, _1._rb, type);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case 911:
            this.this$0.manualSelectNetwork(this.this$0._networkListField.getSelectedIndex());
            return null;
         case 1872:
            new PrefNetworkListOptions();
            return null;
         case 1910:
            PrefNetworkListOptions prefNet = new PrefNetworkListOptions();
            if (this.this$0._mainScreen.getFieldWithFocus() != this.this$0._activeNetworkField) {
               prefNet.addToList(this.this$0._networkInfos[this.this$0._networkListField.getSelectedIndex()]);
               return null;
            }

            int index = RadioInfo.getCurrentNetworkIndex();
            if (index != -1) {
               NetworkInfo netInfo = new NetworkInfo();
               netInfo.setName(NetworkOptionsUtils.getAvailableNetworkName(index));
               netInfo.setNetworkId(RadioInfo.getNetworkId(index));
               netInfo.setCategory(RadioInternal.getNetworkCategory(index));
               prefNet.addToList(netInfo);
               return null;
            }
            break;
         case 1921:
            this.this$0._iotaManager.initiateIOTA(RadioInternal.setup(8, 0) == 1 ? 0 : 1);
            return null;
         case 1922:
            this.this$0._iotaManager.cancelIOTA();
      }

      return null;
   }
}
