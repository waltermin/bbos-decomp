package net.rim.ecmascript.runtime;

import java.util.Vector;
import net.rim.ecmascript.util.IntVector;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;
import net.rim.vm.Reflect;

class JavaThunkHandler {
   ESObject _overrides;
   String _className;

   JavaThunkHandler(ESObject overrides, String name) {
      this._overrides = overrides;
      this._className = name;
   }

   public long runDWord(ESObject thiz, String name, int[] parmTypes, int[] parms, int returnType) throws ThrownValue {
      long funcValue = this._overrides.getField(name);
      ESFunction func = Value.checkIfFunctionValue(funcValue);
      if (func == null) {
         throw ThrownValue.typeError(Resources.getString(67), this._className + "." + name, Convert.toString(funcValue));
      }

      long[] funcParms = Misc.newMixedArray(parmTypes.length);
      int j = 0;

      for (int i = 0; i < parmTypes.length; j++) {
         switch (Reflect.classify(parmTypes[i])) {
            case 0:
            case 7:
            case 8:
            case 9:
            case 10:
               int parm = parms[j];
               if (parm != 0) {
                  funcParms[i] = Value.makeObjectValue(JavaObject.createInstance(Misc.toObject(parm)));
               } else {
                  funcParms[i] = Value.NULL;
               }
               break;
            case 1:
               funcParms[i] = Value.makeBooleanValue(parms[j] != 0);
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            default:
               funcParms[i] = Value.makeIntegerValue(parms[j]);
               break;
            case 6:
               funcParms[i] = Value.makeLongValue(((long)parms[j + 1] << 32) + parms[j]);
               j++;
               break;
            case 11:
               funcParms[i] = Value.makeDoubleValue(Misc.toFloat(parms[j]));
               break;
            case 12:
               funcParms[i] = Value.makeDoubleValue(Misc.toDouble(((long)parms[j + 1] << 32) + parms[j]));
               j++;
         }

         i++;
      }

      long result = GlobalObject.getInstance().callFunction(func, thiz, funcParms);
      IntVector javaParms = new IntVector();
      Vector v = new Vector();
      JavaCallable.convertOneArg(result, returnType, javaParms, v);
      int len = javaParms.size();
      switch (len) {
         case 0:
         default:
            return 0;
         case 1:
            return javaParms.elementAt(0);
         case 2:
            return ((long)javaParms.elementAt(1) << 32) + javaParms.elementAt(0);
      }
   }
}
