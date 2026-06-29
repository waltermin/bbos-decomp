package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.script.internal.handler.FieldHandlerFactory;
import net.rim.wica.runtime.script.internal.handler.PropertyHandler;

class ESMDSObject extends RedirectedObject {
   private String _id;

   public ESMDSObject(String clazz, String id, ESObject prototype) {
      super(clazz, prototype);
      this._id = id;
   }

   public String getId() {
      return this._id;
   }

   @Override
   public long requestFieldValue(String name) {
      PropertyHandler handler = FieldHandlerFactory.getHandler(this.getFieldTypeByName(name));
      return handler != null ? this.getFieldValue(handler, name) : Value.DEFAULT;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      PropertyHandler handler = FieldHandlerFactory.getHandler(this.getFieldTypeByName(name));
      if (handler != null) {
         this.setFieldValue(handler, name, value);
         return false;
      } else {
         return true;
      }
   }

   public int getFieldTypeByName(String _1) {
      throw null;
   }

   protected long getFieldValue(PropertyHandler _1, String _2) {
      throw null;
   }

   protected void setFieldValue(PropertyHandler _1, String _2, long _3) {
      throw null;
   }
}
