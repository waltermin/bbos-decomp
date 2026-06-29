package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class AbstractPhoneNumberModel implements PersistableRIMModel, PaintProvider, MatchProvider, Recognizer, URLProvider, FieldLabelProvider {
   public static final int DEFAULT_DIGITS_IN_REVERSE_LOOKUP_HASH;
   public static final int SHORT_NUMBER_TYPE;
   public static final int TYPE_IN_PARENS;
   protected static WeakReference _strBufferWR = (WeakReference)(new Object(null));
   protected static ContextObjectWR _friendlyContextWR = (ContextObjectWR)(new Object(10));
   protected static String[] _nameStrings = new Object[2];

   @Override
   public int paint(Graphics g, int x, int y, int width, int height, Object context) {
      if (ContextObject.getFlag(context, 42)) {
         int flags = 64;
         Object graphicsFlags = ContextObject.get(context, 5299087291278695360L);
         if (graphicsFlags != null) {
            Integer i = (Integer)graphicsFlags;
            flags = i;
         }

         return this.paintRawPhoneNumber(g, x, y, width, height, flags, context);
      } else {
         if (ContextObject.getFlag(context, 34)) {
            boolean parentheses = ContextObject.getFlag(context, 41);
            StringBuffer _strBuffer = WeakReferenceUtilities.getStringBuffer(_strBufferWR);
            formatTypeString(_strBuffer, getTypeString(this.getType()), parentheses, true);
            return g.drawText(_strBuffer, 0, _strBuffer.length(), x, y, 64, width);
         }

         if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 2)) {
            int typeFlags = 0;
            if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 8)) {
               typeFlags |= 2;
            }

            if (ContextObject.getFlag(context, 41)) {
               typeFlags |= 4;
            }

            StringBuffer buf = WeakReferenceUtilities.getStringBuffer(_strBufferWR);
            buf.setLength(0);
            buf.append(getTypeString(this.getType(), typeFlags));
            buf.append(' ');
            buf.append(this.getDisplayablePhoneNumber());
            return g.drawText(buf, 0, buf.length(), x, y, 64, width);
         } else {
            Object associatedAddress = this.getAddressBookEntry(context);
            if (!(associatedAddress instanceof Object)) {
               return this.paintRawPhoneNumber(g, x, y, width, height, 64, context);
            }

            PaintProvider paintProvider = (PaintProvider)associatedAddress;
            return paintProvider.paint(g, x, y, width, height, context);
         }
      }
   }

   @Override
   public int getURLType() {
      return 3;
   }

   @Override
   public int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      if (crit.getType() == 29) {
         return this.indexOfMatch((String)crit.getValue());
      }

      if (!(crit.getValue() instanceof Object[])) {
         return -1;
      }

      Object[] values = (Object[])crit.getValue();
      String[] testWords = (Object[])values[2];
      int result = Match.nameStringMatch(this.getValue(), testWords);
      if (result == 1) {
         return result;
      }

      Object[] addressBookEntries = this.getAddressBookEntries();
      if (addressBookEntries != null) {
         for (int index = 0; index < addressBookEntries.length; index++) {
            Object addressBookEntry = addressBookEntries[index];
            this.getAddressAndFriendlyNameGivenAddress(addressBookEntry, _nameStrings);
            if (_nameStrings[0] != null) {
               result = Match.nameStringMatch(_nameStrings[0], testWords);
               if (result == 1) {
                  break;
               }
            }

            if (result == 0 && _nameStrings[1] != null) {
               result = Match.nameStringMatch(_nameStrings[1], testWords);
               if (result == 1) {
                  break;
               }
            }
         }
      }

      _nameStrings[0] = null;
      _nameStrings[1] = null;
      return result;
   }

   public int getType() {
      throw null;
   }

   public boolean canSpeedDial() {
      return true;
   }

   public String getValue() {
      throw null;
   }

   public byte[] getBytes() {
      throw null;
   }

   public void setType(int _1) {
      throw null;
   }

   public void setValue(String _1) {
      throw null;
   }

   public Object getAddressBookEntry() {
      Object[] results = this.getAddressBookEntries();
      return results != null && results.length != 0 ? results[0] : null;
   }

   public Object getAddressBookEntry(Object context) {
      Object obj = ContextObject.get(context, 252);
      return obj == null && !ContextObject.getFlag(context, 82) ? this.getAddressBookEntry() : obj;
   }

   public String getTypeString() {
      return getTypeString(this.getType());
   }

   protected int paintRawPhoneNumber(Graphics g, int x, int y, int width, int height, int flags, Object context) {
      String str = this.getValue();
      return g.drawText(str, 0, str.length(), x, y, flags, width);
   }

   public boolean compareTypes(int type) {
      return this.getType() == type;
   }

   public boolean typesEqual(Object other) {
      if (!(other instanceof AbstractPhoneNumberModel)) {
         return false;
      }

      AbstractPhoneNumberModel otherNumber = (AbstractPhoneNumberModel)other;
      return this.getType() == otherNumber.getType();
   }

   protected void getAddressAndFriendlyNameGivenAddress(Object addressBookEntry, String[] names) {
      if (!(addressBookEntry instanceof Object)) {
         names[0] = this.getValue();
         names[1] = "";
      } else {
         ConversionProvider converter = (ConversionProvider)addressBookEntry;
         converter.convert(_friendlyContextWR.getContextObject(), names);
         names[0] = this.getValue();
      }
   }

   protected void getAddressAndFriendlyName(String[] names) {
      Object addressBookEntry = this.getAddressBookEntry();
      this.getAddressAndFriendlyNameGivenAddress(addressBookEntry, names);
   }

   public String getDisplayablePhoneNumber() {
      return this.getValue();
   }

   public boolean equals(Object _1, boolean _2) {
      throw null;
   }

   public Object matchPhoneNumber(Object addressCard, Object context) {
      if (addressCard instanceof Object) {
         addressCard = ((LongHashtable)addressCard).get(254);
      }

      if (addressCard instanceof Object) {
         ReadableList list = (ReadableList)addressCard;
         int size = list.size();

         for (int i = 0; i < size; i++) {
            Object element = list.getAt(i);
            if (this.equals(element, true)) {
               return element;
            }
         }
      }

      return null;
   }

   @Override
   public String getURL() {
      String value = this.getValue();
      if (value != null) {
         value = StringUtilities.removeChars(value, " ");
      } else {
         value = "";
      }

      return ((StringBuffer)(new Object("tel:"))).append(value).toString();
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new Object("Unsupported API");
   }

   @Override
   public void setLabel(String label) {
   }

   @Override
   public boolean recognize(Object o) {
      return this.equals(o) ? true : this.matchPhoneNumber(o, null) != null;
   }

   @Override
   public String getLabel() {
      return getTypeString(this.getType());
   }

   private String formatStringForComparison(String original) {
      int length = original.length();
      char[] originalCharArray = new char[length];
      original.getChars(0, length, originalCharArray, 0);
      StringBuffer output = (StringBuffer)(new Object());
      StringBuffer dtmf = (StringBuffer)(new Object());
      PhoneNumberConverter.convertForTransmission(output, dtmf, originalCharArray, false);
      output.append(' ');
      output.append(dtmf);
      return output.toString();
   }

   private int indexOfMatch(String queryString) {
      String addressNumber = this.formatStringForComparison(this.getValue());
      StringMatch queryStringMatch = (StringMatch)(new Object(this.formatStringForComparison(queryString), false, true));
      return Match.stringMatchMatch(queryStringMatch, addressNumber);
   }

   static String getTypeString(int type) {
      return getTypeString(type, 0);
   }

   public static String[] getPhoneNumberTypeStrings(Locale locale) {
      ResourceBundleFamily rbf = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone");
      if (locale != null) {
         try {
            ResourceBundle rb = rbf.getBundle(locale);
            return (Object[])rb.getObject(601);
         } finally {
            return (Object[])rbf.getObject(601);
         }
      } else {
         return (Object[])rbf.getObject(601);
      }
   }

   public static String getTypeString(int type, int flags) {
      ResourceBundle resources = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone");
      if (type >= 12) {
         return (String)((Object[])resources.getObject(601))[0];
      } else if (type >= 9) {
         return (String)((Object[])resources.getObject(602))[type - 9];
      } else if ((flags & 2) != 0) {
         return (String)((flags & 4) != 0 ? ((Object[])resources.getObject(6079))[type] : ((Object[])resources.getObject(6078))[type]);
      } else {
         return (String)((Object[])resources.getObject(601))[type];
      }
   }

   @Override
   public final boolean equals(Object other) {
      ContextObject context = null;
      boolean typeIndependent = false;
      if (other instanceof Object) {
         context = (ContextObject)other;
         other = context.get(254);
         typeIndependent = context.getFlag(93);
      }

      return this.equals(other, typeIndependent);
   }

   private final Object[] getAddressBookEntries() {
      return AddressBookServices.reverseLookup(this, this, false);
   }

   public static String[] getPhoneNumberTypeStrings() {
      return getPhoneNumberTypeStrings(null);
   }

   @Override
   public String toString() {
      return this.getValue();
   }

   static void formatTypeString(StringBuffer buf, String typeString, boolean parentheses, boolean reset) {
      if (reset) {
         buf.setLength(0);
      }

      if (parentheses) {
         buf.append('(');
      }

      buf.append(typeString);
      if (parentheses) {
         buf.append(')');
      }
   }

   public static void addViewContactVerb(Verb[] verbs, Object addressCard) {
      if (verbs != null && addressCard != null) {
         Array.resize(verbs, verbs.length + 1);
         verbs[verbs.length - 1] = new AbstractPhoneNumberModel$ViewAddressVerb((RIMModel)addressCard);
      }
   }
}
