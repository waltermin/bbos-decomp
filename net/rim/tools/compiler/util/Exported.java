package net.rim.tools.compiler.util;

import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.Method;

public final class Exported {
   private String _name;
   private Object _object;

   public Exported(String name, byte[] data) {
      this._name = name;
      this._object = data;
   }

   public Exported(String name, Method method) {
      this._name = name;
      this._object = method;
   }

   public Exported(String name, ClassType classType, int index) {
      this._name = name;
      this._object = classType;
   }

   public final String getName() {
      return this._name;
   }

   public final byte[] getData() {
      return (byte[])this._object;
   }

   public final Method getMethod() {
      return (Method)this._object;
   }

   @Override
   public final boolean equals(Object other) {
      if (!(other instanceof Exported)) {
         return false;
      }

      Exported exported = (Exported)other;
      return this._name.equals(exported._name);
   }

   @Override
   public final String toString() {
      return this._name;
   }
}
