package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.container.MainScreen;

public class ConvenienceKeyOptionsProvider {
   private static final long KEY = 6253790733817506976L;

   public static void register(ConvenienceKeyOptionsProvider provider) {
      ApplicationRegistry.getApplicationRegistry().put(6253790733817506976L, provider);
   }

   public static ConvenienceKeyOptionsProvider getInstance() {
      return (ConvenienceKeyOptionsProvider)ApplicationRegistry.getApplicationRegistry().get(6253790733817506976L);
   }

   public void populateMainScreen(MainScreen _1) {
      throw null;
   }

   public void save() {
      throw null;
   }

   public void discard() {
      throw null;
   }

   public void onThemeChangeEvent(int _1) {
      throw null;
   }

   public Object getConvenienceKey1App(boolean _1) {
      throw null;
   }

   public Object getConvenienceKey2App(boolean _1) {
      throw null;
   }

   public String getConvenienceKey1Owner() {
      throw null;
   }

   public String getConvenienceKey2Owner() {
      throw null;
   }

   public void setConvenienceKey2App(String _1, String _2) {
      throw null;
   }

   public void setConvenienceKey2ClickAndHoldApp(String _1, String _2) {
      throw null;
   }

   public boolean invokeConvenienceKey1App() {
      throw null;
   }

   public boolean invokeConvenienceKey2App() {
      throw null;
   }

   public boolean invokeConvenienceKey2ClickAndHoldApp() {
      throw null;
   }
}
