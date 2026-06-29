package net.rim.ecmascript.runtime;

import java.util.Vector;
import net.rim.ecmascript.util.IntVector;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;
import net.rim.vm.Array;
import net.rim.vm.Reflect;

class JavaCallable extends HostFunction {
   private JavaClass _javaClass;
   private int _returnType;
   private int[] _parameterTypes;

   JavaCallable(String type, JavaClass jc, String name, int returnType, int[] parms, boolean isConstructor) {
      super(type, name, 0, isConstructor);
      this._javaClass = jc;
      this._returnType = returnType;
      this._parameterTypes = parms;
   }

   String getFullName() {
      return ((StringBuffer)(new Object())).append(this._javaClass.getName()).append(".").append(this.getName()).toString();
   }

   static void convertOneArg(long arg, int want, IntVector javaParms, Vector v) throws ThrownValue {
      int argType = Value.getType(arg);
      switch (Reflect.classify(want)) {
         case 0:
         case 9:
         case 13:
            return;
         case 1:
            javaParms.addElement(Convert.toBoolean(arg) ? 1 : 0);
            return;
         case 2:
            javaParms.addElement((byte)Convert.toInt32(arg));
            return;
         case 3:
            if (argType == 5) {
               String str = Value.getStringValue(arg);
               if (str.length() == 1) {
                  javaParms.addElement(str.charAt(0));
                  return;
               }
            }

            javaParms.addElement((char)Convert.toInt32(arg));
            return;
         case 4:
            javaParms.addElement((short)Convert.toInt32(arg));
            return;
         case 5:
            javaParms.addElement(Convert.toInt32(arg));
            return;
         case 6:
            long var12 = (long)Convert.toDouble(arg);
            javaParms.addElement((int)var12);
            javaParms.addElement((int)(var12 >> 32));
            return;
         case 7:
            Object var15;
            switch (argType) {
               case -1:
                  var15 = null;
                  break;
               case 0:
               case 7:
                  var15 = (JavaObject)(new Object(Convert.toDouble(arg)));
                  v.addElement(var15);
                  javaParms.addElement(Misc.toInt(var15));
                  break;
               case 1:
               case 2:
               default:
                  var15 = Convert.toString(arg);
                  v.addElement(var15);
                  javaParms.addElement(Misc.toInt(var15));
                  break;
               case 3:
                  javaParms.addElement(0);
                  return;
               case 4:
                  var15 = (JavaObject)(new Object(Value.getBooleanValue(arg)));
                  v.addElement(var15);
                  javaParms.addElement(Misc.toInt(var15));
                  break;
               case 5:
                  var15 = Value.getStringValue(arg);
                  v.addElement(var15);
                  javaParms.addElement(Misc.toInt(var15));
                  break;
               case 6:
                  var15 = Value.getObjectValue(arg);
                  if (!(var15 instanceof JavaObject)) {
                     throw ThrownValue.typeError(Resources.getString(39), Convert.toString(arg), "java.lang.Object");
                  }

                  var15 = (JavaObject)var15.getJavaObject();
                  javaParms.addElement(Misc.toInt(var15));
            }

            if ((Reflect.getClassAttributes(want) & 32) != 0 && var15 instanceof ESThunkInterface) {
               return;
            } else {
               if (!Reflect.isAssignableFrom(Reflect.getObjectClass(var15), want)) {
                  throw ThrownValue.typeError(Resources.getString(39), Reflect.getClassName(Reflect.getObjectClass(var15)), Reflect.getClassName(want));
               }

               return;
            }
         case 8:
            if (argType != 6) {
               throw ThrownValue.typeError(Resources.getString(39), Convert.toString(arg), Reflect.getClassName(want));
            } else {
               ESObject obj = Value.getObjectValue(arg);
               if (!(obj instanceof JavaObject)) {
                  throw ThrownValue.typeError(Resources.getString(40));
               } else {
                  Object var13 = ((JavaObject)obj).getJavaObject();
                  if (!Reflect.isAssignableFrom(Reflect.getObjectClass(var13), want)) {
                     throw ThrownValue.typeError(Resources.getString(39), Reflect.getClassName(Reflect.getObjectClass(var13)), Reflect.getClassName(want));
                  }

                  javaParms.addElement(Misc.toInt(var13));
                  return;
               }
            }
         case 10:
            return;
         case 11:
            javaParms.addElement(Misc.toInt((float)Convert.toDouble(arg)));
            return;
         case 12:
            double d = Convert.toDouble(arg);
            long l = Misc.toLong(d);
            javaParms.addElement((int)l);
            javaParms.addElement((int)(l >> 32));
            return;
         case 14:
         default:
            Object o = Convert.toString(arg);
            v.addElement(o);
            javaParms.addElement(Misc.toInt(o));
      }
   }

   int[] convertArgs(int[] parms) {
      int nParms = this.getNumParms();
      if (parms.length != nParms) {
      }

      Vector v = (Vector)(new Object());
      IntVector javaParms = new IntVector();

      for (int i = 0; i < nParms; i++) {
         convertOneArg(this.getParm(i), parms[i], javaParms, v);
      }

      int[] a = javaParms.toArray();
      Array.markAsMixed(a);
      return a;
   }

   int[] convertArgs() {
      return this.convertArgs(this._parameterTypes);
   }

   int getReturnType() {
      return this._returnType;
   }

   int[] getParameterTypes() {
      return this._parameterTypes;
   }

   JavaClass getJavaClass() {
      return this._javaClass;
   }

   int getRequiredNumParms() {
      return this._parameterTypes.length;
   }
}
