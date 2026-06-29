package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.util.OptionsRegistry;
import net.rim.device.internal.util.OptionsRegistry$ParameterDefinition;

final class Options extends OptionsRegistry {
   public static final long PARAM_TIMEOUT;
   public static final long PARAM_FILENAME;
   public static final long PARAM_FQN_FILENAME;
   public static final long PARAM_IMAGE_ROTATION;
   public static final long PARAM_IMAGE_SCALE;
   public static final long PARAM_IMAGE_TOP_X;
   public static final long PARAM_IMAGE_TOP_Y;
   public static final int MINUTES;
   private static final long GUID;
   private static Options _instance;

   Options() {
      super(-6239515778363321254L);
      this.define(-8475525960600792234L, (OptionsRegistry$ParameterDefinition)(new Object(600, -3600, 3600)));
      this.define(845737328485785096L, (OptionsRegistry$ParameterDefinition)(new Object(null)));
      this.define(5872329845182774168L, (OptionsRegistry$ParameterDefinition)(new Object(null)));
      this.define(2550679879375249665L, (OptionsRegistry$ParameterDefinition)(new Object(null)));
      this.define(2349937757985153567L, (OptionsRegistry$ParameterDefinition)(new Object(null)));
      this.define(-7340185234772503578L, (OptionsRegistry$ParameterDefinition)(new Object(null)));
      this.define(-3942144983241110768L, (OptionsRegistry$ParameterDefinition)(new Object(null)));
   }

   public static final Options getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (Options)ar.getOrWaitFor(-6239515778363321254L);
         if (_instance == null) {
            _instance = new Options();
            ar.put(-6239515778363321254L, _instance);
         }
      }

      return _instance;
   }
}
