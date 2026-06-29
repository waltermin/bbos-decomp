package net.rim.device.apps.api.ui;

import java.util.Vector;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public class CookieProviderUtilities {
   private CookieProviderUtilities() {
   }

   public static Verb getFocusVerbs(CookieProvider cookieProvider, Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      Object cookie = ContextObject.get(context, 5304031625032856186L);
      if (cookie == null && cookieProvider != null) {
         cookie = getDefaultCookie(cookieProvider.getCookieWithFocus());
      }

      if (cookie instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)cookie;
         ContextObject contextObject = ContextObject.castOrCreate(context);
         boolean oldHyperlinkFlag = ContextObject.getFlag(contextObject, 83);
         contextObject.setFlag(83);
         if (context == null) {
            contextObject.setFlag(2);
         }

         defaultVerb = verbProvider.getVerbs(contextObject, verbs);
         if (!oldHyperlinkFlag) {
            contextObject.clearFlag(83);
         }
      }

      return defaultVerb;
   }

   public static MenuItem getFocusVerbsForActiveField(ActiveFieldCookie cookie, Object context, Vector items) {
      Verb[] verbs = new Verb[0];
      boolean addedCookie = false;
      ContextObject co = ContextObject.castOrCreate(context);
      if (co.get(5304031625032856186L) == null) {
         addedCookie = true;
         co.put(5304031625032856186L, cookie);
      }

      if (context == null) {
         co.setFlag(2);
      }

      Verb defaultVerb = getFocusVerbs(null, co, verbs);
      VerbMenuItem vmi = null;

      for (int i = verbs.length - 1; i >= 0; i--) {
         VerbMenuItem wrapped;
         if (verbs[i] == defaultVerb) {
            if (defaultVerb == null) {
               throw new Object();
            }

            wrapped = new VerbMenuItem(null, defaultVerb.getOrdering(), 10, defaultVerb, co);
            vmi = wrapped;
         } else {
            wrapped = new VerbMenuItem(null, verbs[i].getOrdering(), 500, verbs[i], co);
         }

         items.insertElementAt(wrapped, 0);
      }

      if (addedCookie) {
         co.remove(5304031625032856186L);
      }

      return vmi;
   }

   public static Object getDefaultCookie(Object cookie) {
      if (cookie instanceof Object[]) {
         Object[] cookies = (Object[])cookie;

         for (int i = 0; i < cookies.length; i++) {
            if (cookies[i] instanceof RIMModel) {
               return cookies[i];
            }
         }

         if (cookies.length > 0) {
            return cookies[0];
         }
      }

      return cookie;
   }
}
