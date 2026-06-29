package net.rim.device.internal.ui;

public class UiSettingsRegistry {
   private UiSettingsRegistry() {
   }

   public static int getParamInt(long key, int def) {
      return UiOptionsRegistry.getInstance().getInt(key);
   }

   public static String getParamString(long key) {
      return UiOptionsRegistry.getInstance().getString(key);
   }

   public static void setParamInt(long key, int value) {
      UiOptionsRegistry.getInstance().setInt(key, value);
   }

   public static void setParamString(long key, String value) {
      UiOptionsRegistry.getInstance().setString(key, value);
   }
}
