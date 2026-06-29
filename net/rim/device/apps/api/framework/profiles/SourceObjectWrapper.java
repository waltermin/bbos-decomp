package net.rim.device.apps.api.framework.profiles;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.vm.Array;

public class SourceObjectWrapper implements RIMModel, PaintProvider, FieldProvider, KeyProvider, MatchProvider {
   private Object _nameObject;

   @Override
   public int paint(Graphics aGraphics, int xInt, int yInt, int widthInt, int heightInt, Object contextObject) {
      aGraphics.drawText(this._nameObject.toString(), xInt, yInt, 64, widthInt);
      return 0;
   }

   public Object getObject() {
      return this._nameObject;
   }

   @Override
   public int match(Object searchCriteriaObject) {
      if (!(searchCriteriaObject instanceof String)) {
         return 0;
      }

      String name = this._nameObject.toString();
      if (name != null && name.length() > 0) {
         String searchString = (String)searchCriteriaObject;
         if (name.regionMatches(true, 0, searchString, 0, searchString.length())) {
            return 1;
         }
      }

      return 0;
   }

   public String getString() {
      return this._nameObject.toString();
   }

   @Override
   public boolean grabDataFromField(Field aField, Object contextObject) {
      return true;
   }

   @Override
   public boolean validate(Field aField, Object contextObject) {
      return true;
   }

   @Override
   public int getOrder(Object contextObject) {
      return 0;
   }

   @Override
   public int getKeys(Object contextObject, Object[] keyArray, int indexInt, long keyRequestedLong) {
      String name = this._nameObject.toString();
      if (name != null) {
         if (keyArray.length == indexInt) {
            Array.resize(keyArray, indexInt + 1);
         }

         keyArray[indexInt] = name;
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public int getKeys(Object contextObject, int[] keyArray, int indexInt, long keyRequestedLong) {
      return 0;
   }

   @Override
   public int getKeys(Object contextObject, long[] keyArray, int indexInt, long keyRequestedLong) {
      return 0;
   }

   @Override
   public Field getField(Object contextObject) {
      Field field = null;
      String name = this._nameObject.toString();
      if (ContextObject.getFlag(contextObject, 69)) {
         String[] pair = (String[])ContextObject.get(contextObject, 251);
         if (pair != null && pair.length == 2 && pair[0] != null && pair[1] != null && pair[1].length() > 0) {
            String[] names = new String[]{name, pair[1]};
            return new LabelField(MessageFormat.format(pair[0], names), 64);
         } else {
            return new LabelField(name, 0);
         }
      } else {
         return new LabelField(name, 1170935903116328960L);
      }
   }

   public SourceObjectWrapper(Object nameString, long idLong) {
      this._nameObject = nameString;
   }
}
