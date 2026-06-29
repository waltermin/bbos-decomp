package net.rim.ecmascript.runtime;

import java.util.Vector;

class ESObjectPrototype$ToString extends HostFunction {
   ESObjectPrototype$ToString(String name) {
      super("Object", name);
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      if (this.getVersion() != 120) {
         return Value.makeStringValue(((StringBuffer)(new Object("[object "))).append(thiz.getObjectClass()).append("]").toString());
      }

      StringBuffer b = (StringBuffer)(new Object());
      b.append("{");
      Vector v = (Vector)(new Object());
      thiz.enumerate(v, false);
      int fields = v.size();
      if (fields != 0) {
         String fieldName = (String)v.elementAt(0);
         b.append(fieldName);
         b.append(":");
         b.append(Convert.toJoinString(thiz.getField(fieldName), false));

         for (int i = 1; i < fields; i++) {
            fieldName = (String)v.elementAt(i);
            b.append(", ");
            b.append(fieldName);
            b.append(":");
            b.append(Convert.toJoinString(thiz.getField(fieldName), false));
         }
      }

      b.append("}");
      return Value.makeStringValue(b.toString());
   }
}
