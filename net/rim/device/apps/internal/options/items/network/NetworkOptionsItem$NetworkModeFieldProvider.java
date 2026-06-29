package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

interface NetworkOptionsItem$NetworkModeFieldProvider extends FieldChangeListener {
   Field getField();

   void update();

   void discard();

   void save();

   boolean isDirty();
}
