package net.rim.device.apps.api.setupwizard;

public interface Log {
   void log(String var1);

   String getCategoryName();

   Log clone(String var1);
}
