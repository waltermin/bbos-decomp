package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.VariableRowHeightProvider;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class VariableRowHeightProxy {
   private VariableRowHeightProvider _variableHeightAdjuster;

   public VariableRowHeightProxy(VariableRowHeightProvider variableHeightAdjuster) {
      this._variableHeightAdjuster = variableHeightAdjuster;
   }

   public final void setVariableHeightProvider(VariableRowHeightProvider variableHeightAdjuster) {
      this._variableHeightAdjuster = variableHeightAdjuster;
   }

   public final VariableRowHeightProvider getVariableHeightProvider() {
      return this._variableHeightAdjuster;
   }

   public final int getAdjustedY(Font font, String text, int currentY) {
      return this._variableHeightAdjuster != null ? this._variableHeightAdjuster.getAdjustedY(font, text, currentY) : currentY;
   }

   public final int getAdjustedY(Font font, StringBuffer text, int offset, int len, int currentY) {
      return this._variableHeightAdjuster != null ? this._variableHeightAdjuster.getAdjustedY(font, text, offset, len, currentY) : currentY;
   }

   public final int getAdjustedY(int currentY) {
      return this._variableHeightAdjuster != null ? this._variableHeightAdjuster.getAdjustedY(currentY) : currentY;
   }

   public static final int getAdjustedY(Object context, Font font, String text, int currentY) {
      Object variableHeightAdjuster = ContextObject.get(context, 1715204984290923528L);
      return variableHeightAdjuster != null ? ((VariableRowHeightProvider)variableHeightAdjuster).getAdjustedY(font, text, currentY) : currentY;
   }

   public static final int getAdjustedY(Object context, Font font, StringBuffer text, int offset, int len, int currentY) {
      Object variableHeightAdjuster = ContextObject.get(context, 1715204984290923528L);
      return variableHeightAdjuster != null ? ((VariableRowHeightProvider)variableHeightAdjuster).getAdjustedY(font, text, offset, len, currentY) : currentY;
   }

   public static final int getAdjustedY(Object context, int currentY) {
      Object variableHeightAdjuster = ContextObject.get(context, 1715204984290923528L);
      return variableHeightAdjuster != null ? ((VariableRowHeightProvider)variableHeightAdjuster).getAdjustedY(currentY) : currentY;
   }

   public static final Object addHeightAdjusterToContext(Object context, VariableRowHeightProvider variableHeightAdjuster) {
      ContextObject contextObject = null;
      if (variableHeightAdjuster != null) {
         contextObject = ContextObject.castOrCreate(context);
         if (ContextObject.get(contextObject, 1715204984290923528L) != variableHeightAdjuster) {
            ContextObject.put(contextObject, 1715204984290923528L, variableHeightAdjuster);
         }
      }

      return contextObject;
   }
}
