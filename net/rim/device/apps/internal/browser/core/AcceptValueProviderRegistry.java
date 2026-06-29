package net.rim.device.apps.internal.browser.core;

import com.sun.cldc.i18n.Helper;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.common.AcceptValueProvider;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;

public final class AcceptValueProviderRegistry {
   private Vector _acceptValueProviders = (Vector)(new Object());
   static final String MIME_TYPE_BINARY_CSS = "application/vnd.rim.css;v=1";
   static final String MIME_TYPE_TEXT_CSS = "text/css;media=";

   private AcceptValueProviderRegistry() {
   }

   public static final AcceptValueProviderRegistry getInstance(long key) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      AcceptValueProviderRegistry instance = (AcceptValueProviderRegistry)ar.getOrWaitFor(key);
      if (instance == null) {
         instance = new AcceptValueProviderRegistry();
         ar.put(key, instance);
      }

      return instance;
   }

   public final void registerAcceptValueProvider(AcceptValueProvider acceptValueProvider) {
      this._acceptValueProviders.addElement(acceptValueProvider);
   }

   public static final String getAcceptCharsetValues() {
      String[] encodings = Helper.getSupportedEncodings();
      if (encodings != null && encodings.length != 0) {
         StringBuffer buff = (StringBuffer)(new Object("ISO-8859-1"));

         for (int i = 0; i < encodings.length; i++) {
            if (!StringUtilities.strEqualIgnoreCase("ISO-8859-1", encodings[i], 1701707776)) {
               buff.append(',');
               buff.append(encodings[i]);
            }
         }

         return buff.toString();
      } else {
         return "";
      }
   }

   public final Vector getSupportedMimeType(RenderingOptions renderingOptions) {
      Vector currentVector = (Vector)(new Object());
      Hashtable currentSet = (Hashtable)(new Object());
      int size = this._acceptValueProviders.size();

      for (int i = 0; i < size; i++) {
         AcceptValueProvider element = (AcceptValueProvider)this._acceptValueProviders.elementAt(i);
         String[] acceptValue = element.getAccept(renderingOptions);
         if (acceptValue != null) {
            int valueSize = acceptValue.length;

            for (int j = 0; j < valueSize; j++) {
               String stringToFind = StringUtilities.toLowerCase(acceptValue[j], 1701707776);
               if (!currentSet.containsKey(stringToFind)) {
                  currentVector.addElement(stringToFind);
                  currentSet.put(stringToFind, currentSet);
               }
            }
         }
      }

      if (renderingOptions == null) {
         return currentVector;
      }

      if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false) && JavaScriptRegistry.isInstalled()) {
         String[] jscriptTypes = JavaScriptRegistry.getMimeTypes(true);
         if (jscriptTypes != null) {
            for (int j = 0; j < jscriptTypes.length; j++) {
               currentVector.addElement(jscriptTypes[j]);
            }
         }
      }

      if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 22, true)
         && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 18, true)) {
         String value = "application/vnd.rim.css;v=1";
         currentVector.addElement(value);
         value = ((StringBuffer)(new Object("text/css;media=")))
            .append(renderingOptions.getPropertyWithStringValue(4550690918222697397L, 19, "handheld"))
            .toString();
         currentVector.addElement(value);
      }

      return currentVector;
   }

   public final String getAcceptTypes(RenderingOptions renderingOptions) {
      Vector currentVector = this.getSupportedMimeType(renderingOptions);
      StringBuffer acceptValues = (StringBuffer)(new Object());
      int currentVectorSize = currentVector.size();
      boolean appendSeparator = false;
      boolean acceptTextHtml = false;
      boolean acceptRimHtml = false;
      boolean wapMode = renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 25, false);

      for (int i = 0; i < currentVectorSize; i++) {
         String element = (String)currentVector.elementAt(i);
         if (element.startsWith("text/html")) {
            acceptTextHtml = true;
         } else if (element.startsWith("application/vnd.rim.html")) {
            acceptRimHtml = true;
         } else if (!element.startsWith("text/vnd.wap.wml") || !wapMode) {
            if (appendSeparator) {
               acceptValues.append(',');
            } else {
               appendSeparator = true;
            }

            acceptValues.append(element);
         }
      }

      if (acceptTextHtml) {
         if (appendSeparator) {
            acceptValues.insert(0, ',');
         } else {
            appendSeparator = true;
         }

         acceptValues.insert(0, "text/html");
      }

      if (acceptRimHtml) {
         if (appendSeparator) {
            acceptValues.insert(0, ',');
         } else {
            appendSeparator = true;
         }

         acceptValues.insert(0, "application/vnd.rim.html");
      }

      return acceptValues.toString();
   }
}
