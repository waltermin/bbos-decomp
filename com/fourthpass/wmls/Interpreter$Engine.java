package com.fourthpass.wmls;

import java.util.Stack;

final class Interpreter$Engine implements InstructionSet {
   private FunctionCall _currentFunctionCall;
   private int _ip;
   private Stack _callStack;
   private OperandStack _operandStack;
   private boolean _done;
   private final Interpreter this$0;

   Interpreter$Engine(Interpreter _1) {
      this.this$0 = _1;
      this._callStack = (Stack)(new Object());
      this._operandStack = new OperandStack();
   }

   public final void interpret() {
      this._ip = 0;
      this._done = false;
      int param = 0;
      BitOperation bitoperation = new BitOperation();
      MathOperation mathOperation = new MathOperation();
      LogicalOperation logicalOperation = new LogicalOperation();
      this._callStack.push(null);
      this._operandStack.pushValue(StringValue.EMPTY_STRING);
      if (this.this$0._args != null) {
         for (int i = 0; i < this.this$0._args.length; i++) {
            this._operandStack.pushValue(this.this$0._args[i]);
         }
      }

      int index = 0;
      if (this.this$0._method != null && this.this$0._method.length() != 0) {
         index = this.this$0._script.getFunctionPool().getIndex(this.this$0._method);
      }

      this.callFunction(this.this$0._script, index);

      while (!this._done) {
         byte code = this._currentFunctionCall.getInstruction(this._ip);
         if ((code & -64) != 0) {
            if ((code & -128) == -128) {
               param = code & 31;
               code = (byte)(code & 224);
            } else if ((code & -32) == 64) {
               param = code & 15;
               code = (byte)(code & 240);
            } else if ((code & -32) == 96) {
               param = code & 7;
               code = (byte)(code & 120);
            }
         }

         switch (code) {
            case -128:
               this._ip += param + 1;
               break;
            case -96:
               this._ip -= param;
               break;
            case -64:
               Value tjumpfws = this._operandStack.popValue();
               tjumpfws = tjumpfws.toBooleanValue();
               if (!tjumpfws.equals(BooleanValue.TRUE)) {
                  this._ip += param + 1;
               } else {
                  this._ip++;
               }
               break;
            case -32:
               this._operandStack.pushValue(this._currentFunctionCall.getVar(param));
               this._ip++;
               break;
            case 1:
               int j1 = this.getArg();
               this._ip += j1 + 1;
               break;
            case 2:
               int k1 = this.getArgW();
               this._ip += k1 + 1;
               break;
            case 3:
               int l1 = this.getArg();
               this._ip -= l1 + 1;
               break;
            case 4:
               int i2 = this.getArgW();
               this._ip -= i2 + 2;
               break;
            case 5:
               int j2 = this.getArg();
               Value tjumpfw = this._operandStack.popValue();
               tjumpfw = tjumpfw.toBooleanValue();
               if (!tjumpfw.equals(BooleanValue.TRUE)) {
                  this._ip += j2 + 1;
               } else {
                  this._ip++;
               }
               break;
            case 6:
               int k2 = this.getArgW();
               Value tjumpfww = this._operandStack.popValue();
               tjumpfww = tjumpfww.toBooleanValue();
               if (!tjumpfww.equals(BooleanValue.TRUE)) {
                  this._ip += k2 + 1;
               } else {
                  this._ip++;
               }
               break;
            case 7:
               int l2 = this.getArg();
               Value tjumpbw = this._operandStack.popValue();
               tjumpbw = tjumpbw.toBooleanValue();
               if (!tjumpbw.equals(BooleanValue.TRUE)) {
                  this._ip -= l2 + 1;
               } else {
                  this._ip++;
               }
               break;
            case 8:
               int i3 = this.getArgW();
               Value tjumpbww = this._operandStack.popValue();
               tjumpbww = tjumpbww.toBooleanValue();
               if (!tjumpbww.equals(BooleanValue.TRUE)) {
                  this._ip -= i3 + 2;
               } else {
                  this._ip++;
               }
               break;
            case 9:
               int i = this.getArg();
               this._ip++;
               this.callFunction(this._currentFunctionCall.getScript(), i);
               break;
            case 10:
               int l4 = this.getArg();
               int i6 = this.getArg();
               this._ip++;
               this.stdLib(i6, l4);
               break;
            case 11:
               int i5 = this.getArg();
               int j6 = this.getArgW();
               this._ip++;
               this.stdLib(j6, i5);
               break;
            case 12:
               String k6 = this.this$0._script.getConstantPool().getConstant(this.getArg()).toString();
               String j5 = this.this$0._script.getConstantPool().getConstant(this.getArg()).toString();
               int j3 = this.getArg();
               this._ip++;
               this.CallUrl(k6, j5, j3);
               break;
            case 13:
               String l6 = this.this$0._script.getConstantPool().getConstant(this.getArg()).toString();
               String k5 = this.this$0._script.getConstantPool().getConstant(this.getArg()).toString();
               int k3 = this.getArg();
               this._ip++;
               this.CallUrl(l6, k5, k3);
               break;
            case 14:
               this._operandStack.pushValue(this._currentFunctionCall.getVar(this.getArg()));
               this._ip++;
               break;
            case 15:
               this._currentFunctionCall.setVar(this.getArg(), this._operandStack.popValue());
               this._ip++;
               break;
            case 16: {
               int arg = this.getArg();
               Value incrvar = this._currentFunctionCall.getVar(arg);
               incrvar = UnaryOperation.getConversion(incrvar);
               this._currentFunctionCall.setVar(arg, incrvar.Incr());
               this._ip++;
               break;
            }
            case 17: {
               int arg = this.getArg();
               Value decrvar = this._currentFunctionCall.getVar(arg);
               decrvar = UnaryOperation.getConversion(decrvar);
               this._currentFunctionCall.setVar(arg, decrvar.Decr());
               this._ip++;
               break;
            }
            case 18:
               this._operandStack.pushValue(this.this$0._script.getConstantPool().getConstant(this.getArg()).getValue());
               this._ip++;
               break;
            case 19:
               this._operandStack.pushValue(this.this$0._script.getConstantPool().getConstant(this.getArgW()).getValue());
               this._ip++;
               break;
            case 20:
               this._operandStack.pushValue(IntegerValue.ZERO.clone());
               this._ip++;
               break;
            case 21:
               this._operandStack.pushValue(IntegerValue.ONE.clone());
               this._ip++;
               break;
            case 22:
               this._operandStack.pushValue(IntegerValue.MINUS_ONE.clone());
               this._ip++;
               break;
            case 23:
               this._operandStack.pushValue(StringValue.EMPTY_STRING.clone());
               this._ip++;
               break;
            case 24:
               this._operandStack.pushValue(Value.INVALID);
               this._ip++;
               break;
            case 25:
               this._operandStack.pushValue(BooleanValue.TRUE);
               this._ip++;
               break;
            case 26:
               this._operandStack.pushValue(BooleanValue.FALSE);
               this._ip++;
               break;
            case 27:
               Value incr = this._operandStack.popValue();
               incr = UnaryOperation.getConversion(incr);
               this._operandStack.pushValue(incr.Incr());
               this._ip++;
               break;
            case 28:
               Value decr = this._operandStack.popValue();
               decr = UnaryOperation.getConversion(decr);
               this._operandStack.pushValue(decr.Decr());
               this._ip++;
               break;
            case 29: {
               int arg = this.getArg();
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._currentFunctionCall.getVar(arg);
               logicalOperation.convert(arg2, arg1);
               this._currentFunctionCall.setVar(arg, logicalOperation.Add());
               this._ip++;
               break;
            }
            case 30: {
               int arg = this.getArg();
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._currentFunctionCall.getVar(arg);
               mathOperation.convert(arg2, arg1);
               this._currentFunctionCall.setVar(arg, mathOperation.Sub());
               this._ip++;
               break;
            }
            case 31:
               Value uminus = this._operandStack.popValue();
               uminus = UnaryOperation.getConversion(uminus);
               this._operandStack.pushValue(uminus.UMinus());
               this._ip++;
               break;
            case 32: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.Add());
               this._ip++;
               break;
            }
            case 33: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               mathOperation.convert(arg2, arg1);
               this._operandStack.pushValue(mathOperation.Sub());
               this._ip++;
               break;
            }
            case 34: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               mathOperation.convert(arg2, arg1);
               this._operandStack.pushValue(mathOperation.Mul());
               this._ip++;
               break;
            }
            case 35: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               mathOperation.convert(arg2, arg1);
               this._operandStack.pushValue(mathOperation.Div());
               this._ip++;
               break;
            }
            case 36: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.IDiv());
               this._ip++;
               break;
            }
            case 37: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.Rem());
               this._ip++;
               break;
            }
            case 38: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.BAnd());
               this._ip++;
               break;
            }
            case 39: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.BOr());
               this._ip++;
               break;
            }
            case 40: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.BXOr());
               this._ip++;
               break;
            }
            case 41:
               Value bnot = this._operandStack.popValue();
               this._operandStack.pushValue(bnot.BNot());
               this._ip++;
               break;
            case 42: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.BLShift());
               this._ip++;
               break;
            }
            case 43: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.BRSShift());
               this._ip++;
               break;
            }
            case 44: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               bitoperation.convert(arg2, arg1);
               this._operandStack.pushValue(bitoperation.BRSZShift());
               this._ip++;
               break;
            }
            case 45: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.Eq());
               this._ip++;
               break;
            }
            case 46: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.LE());
               this._ip++;
               break;
            }
            case 47: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.LT());
               this._ip++;
               break;
            }
            case 48: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.GE());
               this._ip++;
               break;
            }
            case 49: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.GT());
               this._ip++;
               break;
            }
            case 50: {
               Value arg1 = this._operandStack.popValue();
               Value arg2 = this._operandStack.popValue();
               logicalOperation.convert(arg2, arg1);
               this._operandStack.pushValue(logicalOperation.NE());
               this._ip++;
               break;
            }
            case 51:
               Value notValue = this._operandStack.popValue();
               notValue = notValue.toBooleanValue();
               this._operandStack.pushValue(notValue.Not());
               this._ip++;
               break;
            case 52:
               Value scandValue = this._operandStack.popValue();
               scandValue = scandValue.toBooleanValue();
               this._operandStack.pushValue(scandValue);
               if (!scandValue.equals(BooleanValue.TRUE)) {
                  this._operandStack.pushValue(BooleanValue.FALSE);
               }

               this._ip++;
               break;
            case 53:
               Value scorValue = this._operandStack.popValue();
               scorValue = scorValue.toBooleanValue();
               if (scorValue.equals(BooleanValue.FALSE)) {
                  this._operandStack.pushValue(BooleanValue.TRUE);
               } else {
                  this._operandStack.pushValue(scorValue);
                  this._operandStack.pushValue(BooleanValue.FALSE);
               }

               this._ip++;
               break;
            case 54:
               Value tobool = this._operandStack.popValue();
               this._operandStack.pushValue(tobool.toBooleanValue());
               this._ip++;
               break;
            case 55:
               this._operandStack.popValue();
               this._ip++;
               break;
            case 56:
               Value value17 = this._operandStack.popValue();
               IntegerValue wsintval = new IntegerValue(value17.typeOf());
               this._operandStack.pushValue(wsintval);
               this._ip++;
               break;
            case 57:
               Value value18 = this._operandStack.popValue();
               if (value18.isInvalid()) {
                  this._operandStack.pushValue(BooleanValue.FALSE);
               } else {
                  this._operandStack.pushValue(BooleanValue.TRUE);
               }

               this._ip++;
               break;
            case 58:
               this.returnCall();
               break;
            case 59:
               this._operandStack.pushValue(StringValue.EMPTY_STRING.clone());
               this.returnCall();
               break;
            case 60:
               this._ip++;
               break;
            case 64:
               this._currentFunctionCall.setVar(param, this._operandStack.popValue());
               this._ip++;
               break;
            case 80:
               this._operandStack.pushValue(this.this$0._script.getConstantPool().getConstant(param).getValue());
               this._ip++;
               break;
            case 96:
               this._ip++;
               this.callFunction(this._currentFunctionCall.getScript(), param);
               break;
            case 104:
               int k4 = param;
               int l5 = this.getArg();
               this._ip++;
               this.stdLib(l5, k4);
               break;
            case 112:
               Value value = this._currentFunctionCall.getVar(param);
               value = UnaryOperation.getConversion(value);
               this._currentFunctionCall.setVar(param, value.Incr());
               this._ip++;
               break;
            default:
               throw new Object("Bad Instruction Set.");
         }
      }

      this.this$0._result = this._operandStack.popValue();
   }

   public final void halt() {
      this._done = true;
      this.this$0._exited = true;
   }

   public final IBrowser getBrowser() {
      return this.this$0._browser;
   }

   public final IDialog getDialog() {
      return this.this$0._dialog;
   }

   public final Value popStack() {
      return this._operandStack.empty() ? StringValue.EMPTY_STRING : this._operandStack.popValue();
   }

   private final void returnCall() {
      this._callStack.pop();
      this._currentFunctionCall = (FunctionCall)this._callStack.peek();
      if (this._currentFunctionCall != null) {
         this._ip = this._currentFunctionCall.getOffset();
         this.this$0._script = this._currentFunctionCall.getScript();
      } else {
         this._done = true;
      }
   }

   private final void callFunction(WMLScript script, int index) {
      if (this._callStack.size() > 200) {
         throw new Object("Stack overflow");
      }

      if (this._currentFunctionCall != null) {
         this._currentFunctionCall.setOffset(this._ip);
      }

      this._ip = 0;
      this.this$0._script = script;
      this._currentFunctionCall = new FunctionCall(this.this$0._script, index, this._operandStack);
      this._callStack.push(this._currentFunctionCall);
   }

   private final void stdLib(int libraryId, int methodId) {
      Value value = this.this$0._library.invoke(libraryId, methodId, this);
      this._operandStack.push(value);
   }

   private final void CallUrl(String param1, String param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: iload 3
      // 001: anewarray 4167
      // 004: astore 4
      // 006: iload 3
      // 007: bipush 1
      // 008: isub
      // 009: istore 5
      // 00b: iload 5
      // 00d: iflt 025
      // 010: aload 4
      // 012: iload 5
      // 014: aload 0
      // 015: getfield com/fourthpass/wmls/Interpreter$Engine._operandStack Lcom/fourthpass/wmls/OperandStack;
      // 018: invokevirtual com/fourthpass/wmls/OperandStack.popValue ()Lcom/fourthpass/wmls/Value;
      // 01b: invokevirtual com/fourthpass/wmls/Value.toString ()Ljava/lang/String;
      // 01e: aastore
      // 01f: iinc 5 -1
      // 022: goto 00b
      // 025: aload 0
      // 026: getfield com/fourthpass/wmls/Interpreter$Engine.this$0 Lcom/fourthpass/wmls/Interpreter;
      // 029: getfield com/fourthpass/wmls/Interpreter._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 02c: ifnonnull 030
      // 02f: return
      // 030: aconst_null
      // 031: astore 5
      // 033: aconst_null
      // 034: astore 6
      // 036: aload 0
      // 037: getfield com/fourthpass/wmls/Interpreter$Engine.this$0 Lcom/fourthpass/wmls/Interpreter;
      // 03a: getfield com/fourthpass/wmls/Interpreter._script Lcom/fourthpass/wmls/WMLScript;
      // 03d: invokevirtual com/fourthpass/wmls/WMLScript.getURL ()Lcom/fourthpass/wmls/URL;
      // 040: invokevirtual com/fourthpass/wmls/URL.toString ()Ljava/lang/String;
      // 043: astore 7
      // 045: aconst_null
      // 046: astore 8
      // 048: new java/lang/Object
      // 04b: dup
      // 04c: aload 1
      // 04d: aload 7
      // 04f: invokespecial net/rim/device/apps/api/utility/general/URI.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 052: astore 8
      // 054: goto 0a6
      // 057: astore 9
      // 059: aload 0
      // 05a: getfield com/fourthpass/wmls/Interpreter$Engine._operandStack Lcom/fourthpass/wmls/OperandStack;
      // 05d: getstatic com/fourthpass/wmls/Value.INVALID Lcom/fourthpass/wmls/Value;
      // 060: invokevirtual com/fourthpass/wmls/OperandStack.pushValue (Lcom/fourthpass/wmls/Value;)V
      // 063: aload 5
      // 065: ifnull 06d
      // 068: aload 5
      // 06a: invokevirtual java/io/InputStream.close ()V
      // 06d: aload 6
      // 06f: ifnull 0a5
      // 072: aload 6
      // 074: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 079: return
      // 07a: astore 10
      // 07c: return
      // 07d: astore 10
      // 07f: aload 6
      // 081: ifnull 0a5
      // 084: aload 6
      // 086: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 08b: return
      // 08c: astore 10
      // 08e: return
      // 08f: astore 11
      // 091: aload 6
      // 093: ifnull 0a2
      // 096: aload 6
      // 098: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 09d: goto 0a2
      // 0a0: astore 12
      // 0a2: aload 11
      // 0a4: athrow
      // 0a5: return
      // 0a6: new java/lang/Object
      // 0a9: dup
      // 0aa: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 0ad: astore 9
      // 0af: aload 9
      // 0b1: aload 7
      // 0b3: invokestatic net/rim/device/apps/internal/browser/common/RenderingUtilities.setReferrer (Lnet/rim/device/api/io/http/HttpHeaders;Ljava/lang/String;)V
      // 0b6: new java/lang/Object
      // 0b9: dup
      // 0ba: aload 8
      // 0bc: invokevirtual net/rim/device/apps/api/utility/general/URI.getAbsoluteURL ()Ljava/lang/String;
      // 0bf: aload 9
      // 0c1: bipush 0
      // 0c2: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 0c5: astore 10
      // 0c7: aload 0
      // 0c8: getfield com/fourthpass/wmls/Interpreter$Engine.this$0 Lcom/fourthpass/wmls/Interpreter;
      // 0cb: getfield com/fourthpass/wmls/Interpreter._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 0ce: aload 10
      // 0d0: aconst_null
      // 0d1: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 0d6: astore 6
      // 0d8: aload 6
      // 0da: ifnonnull 12a
      // 0dd: aload 0
      // 0de: getfield com/fourthpass/wmls/Interpreter$Engine._operandStack Lcom/fourthpass/wmls/OperandStack;
      // 0e1: getstatic com/fourthpass/wmls/Value.INVALID Lcom/fourthpass/wmls/Value;
      // 0e4: invokevirtual com/fourthpass/wmls/OperandStack.pushValue (Lcom/fourthpass/wmls/Value;)V
      // 0e7: aload 5
      // 0e9: ifnull 0f1
      // 0ec: aload 5
      // 0ee: invokevirtual java/io/InputStream.close ()V
      // 0f1: aload 6
      // 0f3: ifnull 129
      // 0f6: aload 6
      // 0f8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0fd: return
      // 0fe: astore 11
      // 100: return
      // 101: astore 11
      // 103: aload 6
      // 105: ifnull 129
      // 108: aload 6
      // 10a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 10f: return
      // 110: astore 11
      // 112: return
      // 113: astore 13
      // 115: aload 6
      // 117: ifnull 126
      // 11a: aload 6
      // 11c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 121: goto 126
      // 124: astore 14
      // 126: aload 13
      // 128: athrow
      // 129: return
      // 12a: aload 6
      // 12c: invokeinterface javax/microedition/io/ContentConnection.getType ()Ljava/lang/String; 1
      // 131: astore 11
      // 133: aload 6
      // 135: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 13a: sipush 200
      // 13d: if_icmpne 150
      // 140: aload 11
      // 142: ifnull 150
      // 145: aload 11
      // 147: ldc_w "application/vnd.wap.wmlscriptc"
      // 14a: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 14d: ifne 19d
      // 150: aload 0
      // 151: getfield com/fourthpass/wmls/Interpreter$Engine._operandStack Lcom/fourthpass/wmls/OperandStack;
      // 154: getstatic com/fourthpass/wmls/Value.INVALID Lcom/fourthpass/wmls/Value;
      // 157: invokevirtual com/fourthpass/wmls/OperandStack.pushValue (Lcom/fourthpass/wmls/Value;)V
      // 15a: aload 5
      // 15c: ifnull 164
      // 15f: aload 5
      // 161: invokevirtual java/io/InputStream.close ()V
      // 164: aload 6
      // 166: ifnull 19c
      // 169: aload 6
      // 16b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 170: return
      // 171: astore 12
      // 173: return
      // 174: astore 12
      // 176: aload 6
      // 178: ifnull 19c
      // 17b: aload 6
      // 17d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 182: return
      // 183: astore 12
      // 185: return
      // 186: astore 15
      // 188: aload 6
      // 18a: ifnull 199
      // 18d: aload 6
      // 18f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 194: goto 199
      // 197: astore 16
      // 199: aload 15
      // 19b: athrow
      // 19c: return
      // 19d: new java/lang/Object
      // 1a0: dup
      // 1a1: aload 7
      // 1a3: aload 7
      // 1a5: invokespecial net/rim/device/apps/api/utility/general/URI.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 1a8: astore 12
      // 1aa: aload 6
      // 1ac: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 1b1: astore 5
      // 1b3: new com/fourthpass/wmls/WMLInputStream
      // 1b6: dup
      // 1b7: aload 5
      // 1b9: invokespecial com/fourthpass/wmls/WMLInputStream.<init> (Ljava/io/InputStream;)V
      // 1bc: astore 13
      // 1be: aconst_null
      // 1bf: astore 14
      // 1c1: aconst_null
      // 1c2: astore 15
      // 1c4: aconst_null
      // 1c5: aload 8
      // 1c7: invokevirtual net/rim/device/apps/api/utility/general/URI.getAbsoluteURL ()Ljava/lang/String;
      // 1ca: invokestatic com/fourthpass/wmls/URL.parse (Lcom/fourthpass/wmls/URL;Ljava/lang/String;)Lcom/fourthpass/wmls/URL;
      // 1cd: astore 15
      // 1cf: new com/fourthpass/wmls/WMLScript
      // 1d2: dup
      // 1d3: aload 13
      // 1d5: aload 15
      // 1d7: ldc_w "UTF-8"
      // 1da: aload 12
      // 1dc: invokespecial com/fourthpass/wmls/WMLScript.<init> (Lcom/fourthpass/wmls/WMLInputStream;Lcom/fourthpass/wmls/URL;Ljava/lang/String;Lnet/rim/device/apps/api/utility/general/URI;)V
      // 1df: astore 14
      // 1e1: goto 233
      // 1e4: astore 16
      // 1e6: aload 0
      // 1e7: getfield com/fourthpass/wmls/Interpreter$Engine._operandStack Lcom/fourthpass/wmls/OperandStack;
      // 1ea: getstatic com/fourthpass/wmls/Value.INVALID Lcom/fourthpass/wmls/Value;
      // 1ed: invokevirtual com/fourthpass/wmls/OperandStack.pushValue (Lcom/fourthpass/wmls/Value;)V
      // 1f0: aload 5
      // 1f2: ifnull 1fa
      // 1f5: aload 5
      // 1f7: invokevirtual java/io/InputStream.close ()V
      // 1fa: aload 6
      // 1fc: ifnull 232
      // 1ff: aload 6
      // 201: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 206: return
      // 207: astore 17
      // 209: return
      // 20a: astore 17
      // 20c: aload 6
      // 20e: ifnull 232
      // 211: aload 6
      // 213: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 218: return
      // 219: astore 17
      // 21b: return
      // 21c: astore 18
      // 21e: aload 6
      // 220: ifnull 22f
      // 223: aload 6
      // 225: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 22a: goto 22f
      // 22d: astore 19
      // 22f: aload 18
      // 231: athrow
      // 232: return
      // 233: new com/fourthpass/wmls/Interpreter
      // 236: dup
      // 237: aload 0
      // 238: getfield com/fourthpass/wmls/Interpreter$Engine.this$0 Lcom/fourthpass/wmls/Interpreter;
      // 23b: getfield com/fourthpass/wmls/Interpreter._browser Lcom/fourthpass/wmls/IBrowser;
      // 23e: aload 0
      // 23f: getfield com/fourthpass/wmls/Interpreter$Engine.this$0 Lcom/fourthpass/wmls/Interpreter;
      // 242: getfield com/fourthpass/wmls/Interpreter._dialog Lcom/fourthpass/wmls/IDialog;
      // 245: aload 0
      // 246: getfield com/fourthpass/wmls/Interpreter$Engine.this$0 Lcom/fourthpass/wmls/Interpreter;
      // 249: getfield com/fourthpass/wmls/Interpreter._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 24c: invokespecial com/fourthpass/wmls/Interpreter.<init> (Lcom/fourthpass/wmls/IBrowser;Lcom/fourthpass/wmls/IDialog;Lnet/rim/device/api/browser/field/RenderingApplication;)V
      // 24f: astore 16
      // 251: getstatic com/fourthpass/wmls/Value.INVALID Lcom/fourthpass/wmls/Value;
      // 254: astore 17
      // 256: aload 16
      // 258: aload 15
      // 25a: aload 14
      // 25c: aload 2
      // 25d: aload 4
      // 25f: invokevirtual com/fourthpass/wmls/Interpreter.exec (Lcom/fourthpass/wmls/URL;Lcom/fourthpass/wmls/WMLScript;Ljava/lang/String;[Ljava/lang/String;)Lcom/fourthpass/wmls/Value;
      // 262: astore 17
      // 264: goto 269
      // 267: astore 18
      // 269: aload 0
      // 26a: getfield com/fourthpass/wmls/Interpreter$Engine._operandStack Lcom/fourthpass/wmls/OperandStack;
      // 26d: aload 17
      // 26f: invokevirtual com/fourthpass/wmls/OperandStack.pushValue (Lcom/fourthpass/wmls/Value;)V
      // 272: aload 16
      // 274: invokevirtual com/fourthpass/wmls/Interpreter.hasExited ()Z
      // 277: ifeq 27f
      // 27a: aload 0
      // 27b: bipush 1
      // 27c: putfield com/fourthpass/wmls/Interpreter$Engine._done Z
      // 27f: aload 5
      // 281: ifnull 289
      // 284: aload 5
      // 286: invokevirtual java/io/InputStream.close ()V
      // 289: aload 6
      // 28b: ifnonnull 291
      // 28e: goto 3a4
      // 291: aload 6
      // 293: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 298: return
      // 299: astore 8
      // 29b: return
      // 29c: astore 8
      // 29e: aload 6
      // 2a0: ifnonnull 2a6
      // 2a3: goto 3a4
      // 2a6: aload 6
      // 2a8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2ad: return
      // 2ae: astore 8
      // 2b0: return
      // 2b1: astore 20
      // 2b3: aload 6
      // 2b5: ifnull 2c4
      // 2b8: aload 6
      // 2ba: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2bf: goto 2c4
      // 2c2: astore 21
      // 2c4: aload 20
      // 2c6: athrow
      // 2c7: astore 8
      // 2c9: aload 5
      // 2cb: ifnull 2d3
      // 2ce: aload 5
      // 2d0: invokevirtual java/io/InputStream.close ()V
      // 2d3: aload 6
      // 2d5: ifnonnull 2db
      // 2d8: goto 3a4
      // 2db: aload 6
      // 2dd: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2e2: return
      // 2e3: astore 8
      // 2e5: return
      // 2e6: astore 8
      // 2e8: aload 6
      // 2ea: ifnonnull 2f0
      // 2ed: goto 3a4
      // 2f0: aload 6
      // 2f2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2f7: return
      // 2f8: astore 8
      // 2fa: return
      // 2fb: astore 22
      // 2fd: aload 6
      // 2ff: ifnull 30e
      // 302: aload 6
      // 304: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 309: goto 30e
      // 30c: astore 23
      // 30e: aload 22
      // 310: athrow
      // 311: astore 8
      // 313: aload 5
      // 315: ifnull 31d
      // 318: aload 5
      // 31a: invokevirtual java/io/InputStream.close ()V
      // 31d: aload 6
      // 31f: ifnull 3a4
      // 322: aload 6
      // 324: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 329: return
      // 32a: astore 8
      // 32c: return
      // 32d: astore 8
      // 32f: aload 6
      // 331: ifnull 3a4
      // 334: aload 6
      // 336: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 33b: return
      // 33c: astore 8
      // 33e: return
      // 33f: astore 24
      // 341: aload 6
      // 343: ifnull 352
      // 346: aload 6
      // 348: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 34d: goto 352
      // 350: astore 25
      // 352: aload 24
      // 354: athrow
      // 355: astore 26
      // 357: aload 5
      // 359: ifnull 361
      // 35c: aload 5
      // 35e: invokevirtual java/io/InputStream.close ()V
      // 361: aload 6
      // 363: ifnull 3a1
      // 366: aload 6
      // 368: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 36d: goto 3a1
      // 370: astore 27
      // 372: goto 3a1
      // 375: astore 27
      // 377: aload 6
      // 379: ifnull 3a1
      // 37c: aload 6
      // 37e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 383: goto 3a1
      // 386: astore 27
      // 388: goto 3a1
      // 38b: astore 28
      // 38d: aload 6
      // 38f: ifnull 39e
      // 392: aload 6
      // 394: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 399: goto 39e
      // 39c: astore 29
      // 39e: aload 28
      // 3a0: athrow
      // 3a1: aload 26
      // 3a3: athrow
      // 3a4: return
      // try (35 -> 41): 42 null
      // try (51 -> 55): 56 null
      // try (47 -> 51): 58 null
      // try (59 -> 63): 64 null
      // try (47 -> 51): 66 null
      // try (58 -> 59): 66 null
      // try (67 -> 71): 72 null
      // try (66 -> 67): 66 null
      // try (108 -> 112): 113 null
      // try (104 -> 108): 115 null
      // try (116 -> 120): 121 null
      // try (104 -> 108): 123 null
      // try (115 -> 116): 123 null
      // try (124 -> 128): 129 null
      // try (123 -> 124): 123 null
      // try (154 -> 158): 159 null
      // try (150 -> 154): 161 null
      // try (162 -> 166): 167 null
      // try (150 -> 154): 169 null
      // try (161 -> 162): 169 null
      // try (170 -> 174): 175 null
      // try (169 -> 170): 169 null
      // try (197 -> 210): 211 null
      // try (220 -> 224): 225 null
      // try (216 -> 220): 227 null
      // try (228 -> 232): 233 null
      // try (216 -> 220): 235 null
      // try (227 -> 228): 235 null
      // try (236 -> 240): 241 null
      // try (235 -> 236): 235 null
      // try (260 -> 267): 268 null
      // try (283 -> 288): 289 null
      // try (279 -> 283): 291 null
      // try (292 -> 297): 298 null
      // try (279 -> 283): 300 null
      // try (291 -> 292): 300 null
      // try (301 -> 305): 306 null
      // try (300 -> 301): 300 null
      // try (33 -> 47): 309 null
      // try (76 -> 104): 309 null
      // try (133 -> 150): 309 null
      // try (179 -> 216): 309 null
      // try (245 -> 279): 309 null
      // try (314 -> 319): 320 null
      // try (310 -> 314): 322 null
      // try (323 -> 328): 329 null
      // try (310 -> 314): 331 null
      // try (322 -> 323): 331 null
      // try (332 -> 336): 337 null
      // try (331 -> 332): 331 null
      // try (33 -> 47): 340 null
      // try (76 -> 104): 340 null
      // try (133 -> 150): 340 null
      // try (179 -> 216): 340 null
      // try (245 -> 279): 340 null
      // try (345 -> 349): 350 null
      // try (341 -> 345): 352 null
      // try (353 -> 357): 358 null
      // try (341 -> 345): 360 null
      // try (352 -> 353): 360 null
      // try (361 -> 365): 366 null
      // try (360 -> 361): 360 null
      // try (33 -> 47): 369 null
      // try (76 -> 104): 369 null
      // try (133 -> 150): 369 null
      // try (179 -> 216): 369 null
      // try (245 -> 279): 369 null
      // try (309 -> 310): 369 null
      // try (340 -> 341): 369 null
      // try (374 -> 378): 379 null
      // try (370 -> 374): 381 null
      // try (382 -> 386): 387 null
      // try (370 -> 374): 389 null
      // try (381 -> 382): 389 null
      // try (390 -> 394): 395 null
      // try (389 -> 390): 389 null
      // try (369 -> 370): 369 null
   }

   private final int getArg() {
      return this._currentFunctionCall.getUByte(++this._ip);
   }

   private final int getArgW() {
      int i = this._currentFunctionCall.getUByte(++this._ip);
      int j = this._currentFunctionCall.getUByte(++this._ip);
      return (i << 8) + j;
   }
}
