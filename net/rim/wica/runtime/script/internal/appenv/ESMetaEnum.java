package net.rim.wica.runtime.script.internal.appenv;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.IntLongHashtable;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.WicaAppContext;

public final class ESMetaEnum extends RedirectedObject {
   private WicaAppContext _context;
   private String _id;
   private int _type;
   private IntLongHashtable _values;

   public ESMetaEnum(String id, int type, WicaAppContext context) {
      super("Enumeration", GlobalObject.getInstance().getObjectPrototype());
      this._id = id;
      this._type = type;
      this._values = new IntLongHashtable(4);
      this._context = context;
   }

   public final String getId() {
      return this._id;
   }

   @Override
   public final long requestFieldValue(String name) {
      EnumCollection collection = this._context.getEnumCollection();
      if (!collection.isValidEnumValue(this._type, name)) {
         EcmaUtilities.throwESError(this._id, RuntimeResources.getString(125, name));
      }

      return this.getESEnum(collection.getEnumValueAsInt(this._type, name));
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      EcmaUtilities.throwESError(this._id, RuntimeResources.getString(112));
      return false;
   }

   public final long getESEnum(int value) {
      String name = this._context.getEnumCollection().getEnum(this._type)[value];
      long objId = this._values.get(value);
      if (objId == -1) {
         objId = Value.makeObjectValue(new ESEnum(name, this._type, value, this._context));
         this._values.put(value, objId);
         this.addField(GlobalObject.stringIntern(ObjectGroup.isInGroup(name) ? new String(name) : name), 5, objId);
      }

      return objId;
   }
}
