package net.rim.device.apps.api.options;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.internal.i18n.CollatorImpl;
import net.rim.vm.Array;

public class OptionsListItem implements RIMModel, PaintProvider, ActionProvider, KeyProvider, MatchProvider, VerbProvider, ValidationProvider {
   private String _displayName;
   private boolean _initialized;
   private CollatorImpl _collator = (CollatorImpl)(new Object());
   private long _rbId;
   private String _rbName;
   private int _rbKey;
   private int _cachedLocaleCode;
   private long _groupId = 1888231790844671165L;
   public static final long OPTIONS_GROUP_BASIC = 1888231790844671165L;
   public static final long OPTIONS_GROUP_ADVANCED = -1514481539159318190L;
   public static final long OPTIONS_GROUP_SECURITY = 5294015899860238835L;
   public static final int DEFAULT_SCREEN_ORDER = 10000;

   @Override
   public int paint(Graphics g, int x, int y, int width, int height, Object context) {
      String displayName = this.getDisplayName();
      y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), displayName, y);
      return g.drawText(displayName, x, y, 64, width);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   @Override
   public int match(Object searchCriteria) {
      if (!(searchCriteria instanceof Object)) {
         return 0;
      }

      String displayName = this.getDisplayName();
      if (displayName != null && displayName.length() > 0) {
         String searchString = (String)searchCriteria;
         if (this._collator.compare(displayName, searchString, searchString.length()) == 0) {
            return 1;
         }
      }

      return 0;
   }

   protected String getInternalDisplayName() {
      ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
      return family.getString(this._rbKey);
   }

   @Override
   public boolean perform(long actionId, Object context) {
      if (actionId == 6099736323056465049L) {
         if (!this._initialized) {
            this.initialize();
            this._initialized = true;
         }

         this.open();
         return true;
      } else {
         return false;
      }
   }

   public int getOptionsScreenOrder() {
      return 10000;
   }

   protected void initialize() {
   }

   protected void open() {
      throw null;
   }

   @Override
   public boolean isValid(Object context) {
      return true;
   }

   public long getGroupId() {
      return this._groupId;
   }

   protected boolean dynamicDisplayName() {
      return false;
   }

   protected void setDisplayName(String displayName) {
      this._rbId = 0;
      this._rbName = null;
      this._displayName = displayName;
   }

   public String getDisplayName() {
      int currentCode = Locale.getDefault().getCode();
      if (this.dynamicDisplayName() || this._rbId != 0 && this._cachedLocaleCode != currentCode) {
         this._cachedLocaleCode = currentCode;
         this._displayName = StringUtilities.removeChars(this.getInternalDisplayName(), "̲");
      }

      return this._displayName;
   }

   @Override
   public int getKeys(Object contextObject, long[] keyArray, int indexInt, long keyRequestedLong) {
      return 0;
   }

   @Override
   public int getKeys(Object contextObject, int[] keyArray, int indexInt, long keyRequestedLong) {
      return 0;
   }

   @Override
   public int getKeys(Object contextObject, Object[] keyArray, int indexInt, long keyRequestedLong) {
      if (this.getDisplayName() != null) {
         if (keyArray.length == indexInt) {
            Array.resize(keyArray, indexInt + 1);
         }

         keyArray[indexInt] = this.getDisplayName();
         return 1;
      } else {
         return 0;
      }
   }

   public OptionsListItem(String displayName, long group) {
      this._displayName = displayName;
      this._groupId = group;
   }

   public OptionsListItem(String displayName) {
      this._displayName = displayName;
   }

   public OptionsListItem(ResourceBundleFamily rb, int key) {
      this._rbId = rb.getId();
      this._rbName = rb.getName();
      this._rbKey = key;
   }

   public OptionsListItem(ResourceBundleFamily rb, int key, long group) {
      this(rb, key);
      this._groupId = group;
   }

   @Override
   public String toString() {
      return this.getDisplayName();
   }
}
