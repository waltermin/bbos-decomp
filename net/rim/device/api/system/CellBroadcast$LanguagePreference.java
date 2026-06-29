package net.rim.device.api.system;

import net.rim.device.api.util.Persistable;

public final class CellBroadcast$LanguagePreference extends CellBroadcast$Info implements Persistable {
   private int _priority;

   public CellBroadcast$LanguagePreference() {
      this.setEnabled(false);
   }

   public CellBroadcast$LanguagePreference(int lang) {
      super(lang);
      this.setEnabled(false);
   }

   public final int getPriority() {
      return this._priority;
   }

   public final void setPriority(int priority) {
      this._priority = priority;
   }

   public final CellBroadcast$LanguagePreference clone() {
      CellBroadcast$LanguagePreference lp = new CellBroadcast$LanguagePreference();
      lp._priority = this._priority;
      this.copyInto(lp);
      return lp;
   }
}
