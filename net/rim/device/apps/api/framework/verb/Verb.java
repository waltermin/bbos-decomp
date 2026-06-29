package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.RIMModel;

public class Verb {
   private String _cachedDescription;
   protected int _ordering;
   private long _rbId;
   private String _rbName;
   protected int _rbKey;
   protected int _cachedLocaleCode;
   protected int _verbGroupId;

   protected Verb(int ordering) {
      this._ordering = ordering;
   }

   protected Verb(int ordering, ResourceBundleFamily rb, int rbKey) {
      this(ordering, rb.getId(), rb.getName(), rbKey);
   }

   protected Verb(int ordering, long rbId, String rbName, int rbKey) {
      this(ordering);
      this._rbId = rbId;
      this._rbName = rbName;
      this._rbKey = rbKey;
   }

   protected Verb(Verb original) {
      this(original._ordering, original._rbId, original._rbName, original._rbKey);
   }

   public int getOrdering() {
      return this._ordering;
   }

   public RIMModel getRIMModel() {
      return null;
   }

   public int getVerbGroupId() {
      return this._verbGroupId;
   }

   public Object invoke(Object _1) {
      throw null;
   }

   @Override
   public String toString() {
      int currentCode = Locale.getDefault().getCode();
      if (this._cachedLocaleCode != currentCode) {
         this._cachedLocaleCode = currentCode;
         ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
         this._cachedDescription = family.getString(this._rbKey);
      }

      return this._cachedDescription;
   }

   public String toString(Object context) {
      return this.toString();
   }
}
