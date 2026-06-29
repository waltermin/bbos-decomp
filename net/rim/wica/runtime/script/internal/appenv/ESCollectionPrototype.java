package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.MDSHelper;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.component.CompositeKey;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.WicaAppContext;

public final class ESCollectionPrototype extends ESObject {
   private WicaAppContext _context;
   private static final String All;
   private static final String Create;
   private static final String Find;
   private static final String FindWhere;
   private static final String RemoveAll;

   public ESCollectionPrototype(WicaAppContext context) {
      this._context = context;
      this.setGrowthIncrement(7);
      this.addHostFunction(new ESCollectionPrototype$1(this, "MDSCollection", "all", 0));
      this.addHostFunction(new ESCollectionPrototype$2(this, "MDSCollection", "create", 1));
      this.addHostFunction(new ESCollectionPrototype$3(this, "MDSCollection", "find", 1));
      this.addHostFunction(new ESCollectionPrototype$4(this, "MDSCollection", "findWhere", 1));
      this.addHostFunction(new ESCollectionPrototype$5(this, "MDSCollection", "removeAll", 0));
      this.addHostFunction(new ESCollectionPrototype$6(this, "MDSCollection", "removeAll", 0));
      this.addHostFunction(new ESCollectionPrototype$7(this, "MDSCollection", "toString", 0));
   }

   private final Object createKeyFromParams(long value, KeyDataCollection collection) {
      DataComponentDef keyDef = collection.getKeyDef();
      int[] keyFields = keyDef.getKeyFields();
      int numKeys = keyFields.length;
      if (numKeys == 1) {
         ESObject esObj = Convert.toObject(value);
         Object keyValue = null;
         int type = 6;
         if (!(esObj instanceof ESData)) {
            type = keyDef.getFieldType(keyFields[0]);
         }

         keyValue = this._context.getConverter().convertESValueToMDSObject(value, type);
         if (keyValue == null) {
            EcmaUtilities.throwESError(RuntimeResources.getString(77));
         }

         return keyValue;
      } else {
         ESObject obj = Convert.toObject(value);
         Vector properties = MDSHelper.enumerateProperties(obj);
         int length = properties.size();
         CompositeKey compositeKey = new CompositeKey(numKeys);

         for (int i = 0; i < length && numKeys > 0; i++) {
            String propertyName = (String)properties.elementAt(i);
            int propertyIndex = keyDef.getFieldHandle(propertyName);
            if (keyDef.isKeyField(propertyIndex)) {
               numKeys--;
               Object keyValue = this._context.getConverter().convertESValueToMDSObject(obj.getField(propertyName), keyDef.getFieldType(propertyIndex));
               if (keyValue == null) {
                  EcmaUtilities.throwESError(RuntimeResources.getString(77));
               }

               compositeKey.setPart(Arrays.binarySearch(keyFields, propertyIndex), keyValue);
            }
         }

         if (numKeys > 0) {
            EcmaUtilities.throwESError(RuntimeResources.getString(80));
         }

         return compositeKey;
      }
   }
}
