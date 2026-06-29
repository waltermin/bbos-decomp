package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class PrefNetworkListOptions$PrefNetworkAddVerb extends Verb {
   private int _priority;
   private PrefNetworkList _netList;
   private int _maxListSize;

   PrefNetworkListOptions$PrefNetworkAddVerb(int priority, int maxListSize, PrefNetworkList netList) {
      super(598288, OptionsResources.getResourceBundle(), 1873);
      this._priority = priority;
      this._netList = netList;
      this._maxListSize = maxListSize;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this._maxListSize <= this._netList.getListSize()) {
         Status.show(OptionsResources.getString(1892));
         return null;
      }

      boolean canUseAvailList = RadioInfo.getState() == 1;
      Object[] choices;
      if (canUseAvailList) {
         choices = new Object[]{
            new RichTextField(OptionsResources.getString(1880)),
            new RichTextField(OptionsResources.getString(1879)),
            new RichTextField(OptionsResources.getString(1919))
         };
      } else {
         choices = new Object[]{new RichTextField(OptionsResources.getString(1880)), new RichTextField(OptionsResources.getString(1919))};
      }

      Dialog dlg = new Dialog(OptionsResources.getString(1873), choices, null, 0, null);
      switch (dlg.doModal()) {
         case 0:
         default:
            UiApplication.getUiApplication().pushScreen(new PrefNetworkSelectOption(2, this._netList, this._priority));
            return null;
         case 1:
            if (canUseAvailList) {
               UiApplication.getUiApplication().pushScreen(new PrefNetworkSelectOption(1, this._netList, this._priority));
               return null;
            }

            UiApplication.getUiApplication().pushScreen(new PrefNetworkItemOptions(this._priority, this._netList, 1));
            return null;
         case 2:
            if (canUseAvailList) {
               UiApplication.getUiApplication().pushScreen(new PrefNetworkItemOptions(this._priority, this._netList, 1));
            }
         case -1:
            return null;
      }
   }
}
