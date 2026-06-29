package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;

final class NetworkOptionsItem$MyList extends ListField {
   private boolean _focusable;
   private final NetworkOptionsItem this$0;

   public NetworkOptionsItem$MyList(NetworkOptionsItem _1, int numLines) {
      super(numLines);
      this.this$0 = _1;
      this._focusable = false;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if ((key == '\n' || key == ' ') && this.this$0.canUserSelectNetwork()) {
         this.this$0.manualSelectNetwork(this.getSelectedIndex());
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   public final void setFocusable(boolean flag) {
      if (flag != this._focusable) {
         this._focusable = flag;
         if (!flag && this.this$0._mainScreen.getLeafFieldWithFocus() == this) {
            this.this$0._mainScreen.setFocus();
         }
      }
   }

   @Override
   public final boolean isFocusable() {
      return this._focusable;
   }

   @Override
   protected final void makeContextMenu(ContextMenu cm, int instance) {
      super.makeContextMenu(cm, instance);
      if (RadioInfo.getState() == 1 && this.this$0.canUserSelectNetwork() && this.getSize() != 0) {
         Verb v = new NetworkOptionsItem$MyVerb(this.this$0, 911, 16785665);
         VerbMenuItem vmi = (VerbMenuItem)(new Object(v, 100));
         cm.addItem(vmi);
         cm.setDefault(0);
         if (PrefNetworkListOptions.isFeatureSupported()) {
            v = new NetworkOptionsItem$MyVerb(this.this$0, 1910, 16785669);
            vmi = (VerbMenuItem)(new Object(v, 110));
            cm.addItem(vmi);
         }
      }
   }
}
