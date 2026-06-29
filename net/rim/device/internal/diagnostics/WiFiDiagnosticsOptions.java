package net.rim.device.internal.diagnostics;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.util.OptionsRegistry;
import net.rim.device.internal.util.OptionsRegistry$ParameterDefinition;

public final class WiFiDiagnosticsOptions extends OptionsRegistry {
   private static final long WIFI_DIAGNOSTICS_OPTIONS_GUID = -2886879053204416204L;
   public static final long DISPLAY_MODE_KEY = 4738717690654536784L;
   public static final long DEFAULT_EMAIL_RECIPIENT_KEY = 1486128106401969997L;
   public static final long DEFAULT_PIN_RECIPIENT_KEY = -6758119585724106862L;
   private static WiFiDiagnosticsOptions _instance;

   private WiFiDiagnosticsOptions() {
      super(-2886879053204416204L);
      this.define(4738717690654536784L, (OptionsRegistry$ParameterDefinition)(new Object(0, 0, 1)));
      this.define(1486128106401969997L, (OptionsRegistry$ParameterDefinition)(new Object("")));
      this.define(-6758119585724106862L, (OptionsRegistry$ParameterDefinition)(new Object("")));
   }

   public static final WiFiDiagnosticsOptions getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (WiFiDiagnosticsOptions)ar.getOrWaitFor(-2886879053204416204L);
         if (_instance == null) {
            _instance = new WiFiDiagnosticsOptions();
            ar.put(-2886879053204416204L, _instance);
         }
      }

      return _instance;
   }
}
