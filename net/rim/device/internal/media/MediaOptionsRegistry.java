package net.rim.device.internal.media;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.util.OptionsRegistry;
import net.rim.device.internal.util.OptionsRegistry$BooleanParameterDefinition;
import net.rim.device.internal.util.OptionsRegistry$IntParameterDefinition;

public class MediaOptionsRegistry extends OptionsRegistry {
   private static final long GUID;
   public static final long REPEAT_OPTION_KEY;
   public static final long SHUFFLE_OPTION_KEY;
   public static final long SHUFFLE_SONGS_OPTION_KEY;
   public static final long FULLSCREEN_OPTION_KEY;
   public static final long VOLUME_BOOST_MODE_KEY;
   public static final long SIM_CARD_IMSI_KEY;
   public static final long VOLUME_BOOST_DONT_ASK_KEY;
   public static final long TURN_OFF_AUTO_BACKLIGHT;

   public static MediaOptionsRegistry getInstance() {
      MediaOptionsRegistry instance = (MediaOptionsRegistry)ApplicationRegistry.getApplicationRegistry().get(-6531603410989162255L);
      if (instance == null) {
         instance = new MediaOptionsRegistry();
         ApplicationRegistry.getApplicationRegistry().replace(-6531603410989162255L, instance);
      }

      return instance;
   }

   private MediaOptionsRegistry() {
      super(-6531603410989162255L);
      this.initialize();
   }

   private void initialize() {
      this.define(8428678949875667391L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-7071091113559016691L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-2846908971875712627L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-4212305096992551720L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(2886183832722201160L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-811168513825316359L, new OptionsRegistry$IntParameterDefinition(-1, Integer.MIN_VALUE, Integer.MAX_VALUE));
      this.define(-4387502259448276168L, new OptionsRegistry$BooleanParameterDefinition(false));
      this.define(-1314075862077144981L, new OptionsRegistry$BooleanParameterDefinition(true));
   }
}
