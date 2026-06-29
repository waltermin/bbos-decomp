package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.util.StringTokenizer;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.util.ValueResolver;

public final class ValueResolverImpl implements ValueResolver {
   private Wiclet _wiclet;
   private ScreenModel _screenModel;

   public ValueResolverImpl(Wiclet wiclet, ScreenModel screenModel) {
      this._wiclet = wiclet;
      this._screenModel = screenModel;
   }

   private final boolean isValid() {
      return this._wiclet != null && this._screenModel != null;
   }

   @Override
   public final Object getVariableValue(String name) {
      if (this.isValid() && name != null && name != "") {
         Object result = null;
         StringTokenizer strTok = (StringTokenizer)(new Object(name, '.'));
         if (!strTok.hasMoreElements()) {
            return null;
         }

         long resultHandle = -1;
         DataCollection collection = null;
         ComponentDef def = null;
         int fieldId = -1;
         int fieldType = -1;
         String currentToken = strTok.nextToken();
         Data globals = this._wiclet.getGlobals();
         def = globals != null ? globals.getDef() : null;
         if (def != null && (fieldId = def.getFieldHandle(currentToken)) >= 0) {
            resultHandle = -4294967296L;
            fieldType = def.getFieldType(fieldId);
            switch (fieldType) {
               case -1:
               case 7:
                  throw new Object("Invalid field type in value string");
               case 0:
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 8:
               default:
                  return globals.getFieldValueAsObject(fieldId);
               case 6:
                  resultHandle = globals.getReferenceField(fieldId);
                  if (resultHandle == -1) {
                     return null;
                  }

                  collection = this._wiclet.getDataCollection((int)(resultHandle >> 32));
                  def = collection.getDef();
            }
         } else {
            resultHandle = this._screenModel.getVarValueByName(currentToken);
            if (resultHandle == -1) {
               return null;
            }

            collection = this._wiclet.getDataCollection((int)(resultHandle >> 32));
            def = collection.getDef();
         }

         while (strTok.hasMoreTokens() && result == null && def != null) {
            fieldId = def.getFieldHandle(strTok.nextToken());
            fieldType = def.getFieldType(fieldId);
            switch (fieldType) {
               case -1:
               case 7:
                  throw new Object("Invalid field type in value string");
               case 0:
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 8:
               default:
                  return collection.getFieldValueAsObject(resultHandle, fieldId);
               case 6:
            }

            resultHandle = collection.getReferenceField(resultHandle, fieldId);
            if (resultHandle == -1) {
               return null;
            }

            collection = this._wiclet.getDataCollection((int)(resultHandle >> 32));
            def = collection.getDef();
         }

         return strTok.hasMoreTokens() ? null : new Object(resultHandle);
      } else {
         return null;
      }
   }
}
