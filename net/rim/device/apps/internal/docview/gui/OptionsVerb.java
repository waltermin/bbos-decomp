package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.options.OptionsItemVerb;

final class OptionsVerb extends OptionsItemVerb {
   private BaseMenuModel _baseMenuModel;

   OptionsVerb(String label, BaseMenuModel model) {
      super(label, 16986368);
      this._baseMenuModel = model;
   }

   @Override
   public final Object invoke(Object context) {
      this._baseMenuModel.perform(12, null);
      return null;
   }
}
