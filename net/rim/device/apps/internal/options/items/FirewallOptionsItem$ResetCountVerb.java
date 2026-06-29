package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.firewall.Firewall;

final class FirewallOptionsItem$ResetCountVerb extends Verb {
   private LabelField _lf;
   private byte _type;

   public FirewallOptionsItem$ResetCountVerb(byte type, LabelField lf) {
      super(16986368, OptionsResources.getResourceBundle(), 1998);
      this._type = type;
      this._lf = lf;
   }

   @Override
   public final Object invoke(Object parameter) {
      Firewall.getInstance().resetBlockedCount(this._type);
      String blocked = OptionsResources.getString(1996);
      String[] c = new String[]{"0"};
      this._lf.setText(MessageFormat.format(blocked, c));
      return null;
   }
}
