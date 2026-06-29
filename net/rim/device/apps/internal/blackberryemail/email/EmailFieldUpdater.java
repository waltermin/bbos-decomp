package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;

public interface EmailFieldUpdater {
   boolean supportsEmailFieldUpdate(Object var1);

   boolean performEmailFieldUpdate(Field var1, Object var2);
}
