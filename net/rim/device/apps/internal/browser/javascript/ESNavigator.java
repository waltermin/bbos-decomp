package net.rim.device.apps.internal.browser.javascript;

import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.i18n.Locale;
import net.rim.ecmascript.runtime.ESArray;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;

final class ESNavigator {
   private ESNavigator() {
   }

   public static final ESObject getInstance(RenderingSession renderingSession) {
      ESObject instance = new ESObject();
      instance.setGrowthIncrement(12);
      instance.addField("appCodeName", 5, Value.makeStringValue("Mozilla"));
      instance.addField("appName", 5, Value.makeStringValue("Netscape"));
      RenderingOptions renderingOptions = renderingSession != null ? renderingSession.getRenderingOptions() : null;
      String appVersion = "";
      if (renderingOptions != null) {
         appVersion = renderingOptions.getPropertyWithStringValue(4550690918222697397L, 23, RenderingOptions.APP_VERSION_DEFAULT);
      }

      instance.addField("appVersion", 5, JavaScriptEngine.makeStringValue(appVersion));
      instance.addField("language", 5, Value.makeStringValue(Locale.getDefault().getLanguage()));
      Vector types = null;
      if (renderingOptions != null) {
         types = renderingSession.getSupportedMimeType();
      }

      if (types != null) {
         int count = types.size();
         ESArray mimeTypes = new ESArray(count);

         for (int i = 0; i < count; i++) {
            try {
               mimeTypes.putIndex(i, Value.makeObjectValue(createMimeType((String)types.elementAt(i))));
            } finally {
               continue;
            }
         }

         instance.addField(Names.mimeTypes, 5, Value.makeObjectValue(mimeTypes));
      }

      instance.addField("platform", 5, Value.makeStringValue("BlackBerry"));
      ESArray plugins = new ESArray(0);
      plugins.addHostFunction(new NoopHostFunction(Names.Navigator, "refresh"));
      instance.addField("plugins", 5, Value.makeObjectValue(plugins));
      String userAgent = "";
      if (renderingOptions != null) {
         userAgent = renderingOptions.getPropertyWithStringValue(4550690918222697397L, 24, RenderingOptions.USER_AGENT_DEFAULT);
      }

      if (userAgent != null && userAgent.startsWith("BlackBerry")) {
         userAgent = "Mozilla/4.0 " + userAgent;
      }

      instance.addField("userAgent", 5, JavaScriptEngine.makeStringValue(userAgent));
      instance.addHostFunction(new ConstantHostFunction(Names.Navigator, "javaEnabled", Value.makeBooleanValue(true)));
      instance.addHostFunction(new ESNavigator$1(Names.Navigator, "preference", 0, renderingOptions));
      instance.addHostFunction(new NoopHostFunction(Names.Navigator, "savePreferences"));
      instance.addHostFunction(new ConstantHostFunction(Names.Navigator, "taintEnabled", Value.makeBooleanValue(false)));
      return instance;
   }

   private static final ESObject createMimeType(String type) {
      ESObject obj = new ESObject();
      obj.setGrowthIncrement(4);
      obj.addField("type", 5, Value.makeStringValue(type));
      obj.addField("description", 5, Value.makeStringValue(type));
      obj.addField("enabledPlugin", 5, Value.NULL);
      obj.addField("suffixes", 5, Value.makeStringValue(type));
      return obj;
   }
}
