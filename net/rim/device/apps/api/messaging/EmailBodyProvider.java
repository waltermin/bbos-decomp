package net.rim.device.apps.api.messaging;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncodingProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;

public interface EmailBodyProvider extends FieldProvider, PaintProvider, ConversionProvider, MatchProvider, CloneProvider, EncodingProvider {
   void setFocus(Field var1, Application var2, Object var3);

   int getCurrentCursorPosition(Field var1, Object var2);

   void setCursorPosition(Field var1, int var2, Object var3);

   int getCursorCount(Field var1, Object var2);
}
