package net.rim.device.apps.api.framework.model;

import net.rim.device.api.ui.Field;

public interface FieldProvider extends RIMModel {
   Field getField(Object var1);

   int getOrder(Object var1);

   boolean grabDataFromField(Field var1, Object var2);

   boolean validate(Field var1, Object var2);
}
