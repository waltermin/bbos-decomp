package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.script.internal.handler.PropertyHandler;

final class ESMessage extends ESMDSObject {
   private Msg _message;

   ESMessage(String id, Msg message, ESObject prototype) {
      super("MDSMessage", id, prototype);
      this._message = message;
   }

   final Msg getMessage() {
      return this._message;
   }

   @Override
   public final int getFieldTypeByName(String name) {
      int fieldHandle = this._message.getDef().getFieldHandle(name);
      return fieldHandle == -1 ? fieldHandle : this._message.getDef().getFieldType(fieldHandle);
   }

   @Override
   protected final long getFieldValue(PropertyHandler handler, String name) {
      return handler.getProperty(this._message, name);
   }

   @Override
   protected final void setFieldValue(PropertyHandler handler, String name, long value) {
      handler.putProperty(this._message, name, value);
   }
}
