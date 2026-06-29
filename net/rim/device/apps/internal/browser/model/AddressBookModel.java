package net.rim.device.apps.internal.browser.model;

import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;

public final class AddressBookModel implements RIMModel, VerbProvider, ActiveFieldCookie {
   private Object _friendlyNameEncoding;
   private Object _phoneNumberEncoding;

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return ControllerUtilities.invokeApplicationKeyVerb(this);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      ContextObject initialData = new ContextObject();
      initialData.put(253, PersistentContent.decode(this._phoneNumberEncoding));
      RIMModel phoneNumberModel = (RIMModel)FactoryUtil.createInstance(3797587162219887872L, initialData);
      ContextObject contextObject = null;
      if (context instanceof ContextObject) {
         contextObject = ((ContextObject)context).clone();
         contextObject.put(-4886909117188079897L, PersistentContent.decode(this._friendlyNameEncoding));
         contextObject.setFlag(112);
         contextObject.setFlag(82);
      }

      return ((VerbProvider)phoneNumberModel).getVerbs(contextObject, verbs);
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   public AddressBookModel(Object initialData) {
      String str = (String)ContextObject.get(initialData, 253);
      if (str != null) {
         AbstractString absStr = AbstractStringWrapper.createInstance(str);
         int prefixLen = AddressBookStringPattern.getPrefixLength(absStr);
         if (prefixLen > 0) {
            str = str.substring(prefixLen).trim();
         }

         int index = str.indexOf(59);
         if (index != -1) {
            this._phoneNumberEncoding = PersistentContent.encode(str.substring(0, index), true, true);
            int index2 = str.indexOf(33, index);
            if (index2 == -1) {
               this._friendlyNameEncoding = PersistentContent.encode(str.substring(index + 1), true, true);
               return;
            }

            this._friendlyNameEncoding = PersistentContent.encode(str.substring(index + 1, index2), true, true);
            if (index2 + 1 < str.length()) {
               return;
            }
         } else {
            this._phoneNumberEncoding = PersistentContent.encode(str, true, true);
            this._friendlyNameEncoding = PersistentContent.encode("", true, true);
         }
      }
   }
}
