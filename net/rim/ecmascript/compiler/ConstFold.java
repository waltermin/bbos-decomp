package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Expr;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;

class ConstFold implements OpcodeConstants {
   static Node BooleanToNode(boolean b) {
      return b ? new NodeTrue() : new NodeFalse();
   }

   static Node ValueToNode(Function f, long value) {
      switch (Value.getType(value)) {
         case 0:
            return new NodeInteger(Value.getIntegerValue(value));
         case 4:
            if (Value.getBooleanValue(value)) {
               return new NodeTrue();
            }

            return new NodeFalse();
         case 5:
            return new NodeString(Value.getStringValue(value));
         case 7:
            return new NodeDouble(f, Value.getDoubleValue(value));
         default:
            return null;
      }
   }

   static Node foldUnary(Function f, int op, Node lhs) {
      long lValue = lhs.fold(f).getFoldableValue();
      if (lValue == Value.UNDEFINED) {
         return null;
      }

      long folded = Value.UNDEFINED;

      try {
         switch (op) {
            case 2:
               folded = Expr.bitnot(lValue);
               break;
            case 90:
               folded = Expr.neg(lValue);
         }
      } catch (ThrownValue tv) {
         folded = Value.UNDEFINED;
      }

      return ValueToNode(f, folded);
   }

   static Node foldBinary(Function f, int op, Node lhs, Node rhs) {
      long lValue = lhs.fold(f).getFoldableValue();
      if (lValue == Value.UNDEFINED) {
         return null;
      }

      long rValue = rhs.fold(f).getFoldableValue();
      if (rValue == Value.UNDEFINED) {
         return null;
      }

      try {
         switch (op) {
            case -126:
               return ValueToNode(f, Expr.sub(lValue, rValue));
            case -118:
               return ValueToNode(f, Expr.ursh(lValue, rValue));
            case 0:
               return ValueToNode(f, Expr.add(lValue, rValue));
            case 1:
               return ValueToNode(f, Expr.bitand(lValue, rValue));
            case 3:
               return ValueToNode(f, Expr.bitor(lValue, rValue));
            case 4:
               return ValueToNode(f, Expr.bitxor(lValue, rValue));
            case 24:
               return ValueToNode(f, Expr.div(lValue, rValue));
            case 87:
               return ValueToNode(f, Expr.lsh(lValue, rValue));
            case 88:
               return ValueToNode(f, Expr.mod(lValue, rValue));
            case 89:
               return ValueToNode(f, Expr.mul(lValue, rValue));
            case 127:
               return ValueToNode(f, Expr.rsh(lValue, rValue));
            default:
               return null;
         }
      } catch (ThrownValue tv) {
         return null;
      }
   }

   static Node foldRelational(Function f, int op, Node lhs, Node rhs) {
      long lValue = lhs.fold(f).getFoldableValue();
      if (lValue == Value.UNDEFINED) {
         return null;
      }

      long rValue = rhs.fold(f).getFoldableValue();
      if (rValue == Value.UNDEFINED) {
         return null;
      }

      try {
         switch (op) {
            case 10:
               return null;
            case 11:
            default:
               return BooleanToNode(Expr.eq(lValue, rValue));
            case 12:
               return BooleanToNode(Expr.ge(lValue, rValue));
            case 13:
               return BooleanToNode(Expr.gt(lValue, rValue));
            case 14:
               return BooleanToNode(Expr.le(lValue, rValue));
            case 15:
               return BooleanToNode(Expr.lt(lValue, rValue));
            case 16:
               return BooleanToNode(!Expr.eq(lValue, rValue));
            case 17:
               return BooleanToNode(Expr.stricteq(lValue, rValue));
            case 18:
               return BooleanToNode(!Expr.stricteq(lValue, rValue));
         }
      } catch (ThrownValue tv) {
         return null;
      }
   }
}
