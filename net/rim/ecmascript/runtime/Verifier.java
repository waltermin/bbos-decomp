package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.ByteCode;
import net.rim.ecmascript.compiler.OpcodeBranches;
import net.rim.ecmascript.compiler.OpcodeStackEffect;
import net.rim.ecmascript.compiler.OpcodeStackRequired;
import net.rim.ecmascript.compiler.OperandTypes;

class Verifier {
   private int _globalSize;
   private int _index;
   private byte[] _code;
   private static final byte BRANCHED_TO;
   private static final byte IS_INSTRUCTION;
   private static final byte STACK_DEPTH_SET;
   private static final byte END_BLOCK;
   private static final int START_STACK_DEPTH_MASK;
   private static final int START_STACK_DEPTH_SHIFT;

   Verifier(int globalSize) {
      this._globalSize = globalSize;
   }

   private void verifyAssert(boolean truth) {
      if (!truth) {
         throw new VerifyError();
      }
   }

   private int byteOp() {
      return this._code[this._index++];
   }

   private int unsignedByteOp() {
      return this._code[this._index++] & 0xFF;
   }

   private int unsignedShortOp() {
      return (this.unsignedByteOp() << 8) + this.unsignedByteOp();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void verify(CompiledScript script) {
      boolean var26 = false /* VF: Semaphore variable */;

      try {
         var26 = true;
         this._code = script.code;
         int var29 = this._code.length;
         int localSize = script.lastRealLocal;
         int integersSize = script.integers.length;
         int doublesSize = script.doubles.length;
         int stringsSize = script.strings.length;
         int numTemps = script.numTemps;
         int idsSize = script.ids.length;
         int maxStack = script.maxStack;
         byte[] insInfo = new byte[var29 + 1];
         this._index = 0;
         int opCode = 0;
         this.verifyAssert(var29 != 0);
         this.verifyAssert(numTemps <= maxStack);
         boolean instructionRequiresScope = false;

         while (this._index < var29) {
            int insIndex = this._index;
            insInfo[this._index] = (byte)(insInfo[this._index] | 2);
            opCode = this.unsignedByteOp();
            if (ByteCode.requiresScope(opCode)) {
               instructionRequiresScope = true;
            }

            this.verifyAssert(opCode < 145);
            switch (OperandTypes.get(opCode)) {
               case -1:
               case 19:
                  this.verifyAssert(false);
                  break;
               case 0:
               default:
                  int opIndex = this.byteOp();
                  this.verifyAssert(0 <= opIndex && opIndex < this._globalSize);
                  this.unsignedByteOp();
                  break;
               case 1:
                  int var37 = this.byteOp();
                  this.verifyAssert(0 <= var37 && var37 < this._globalSize);
                  break;
               case 2:
                  int var36 = this.byteOp();
                  this.verifyAssert(0 <= var36 && var36 < localSize);
                  break;
               case 3:
                  this.unsignedByteOp();
                  break;
               case 4:
                  this._index += 4;
                  break;
               case 5:
                  int funcIndex = this.unsignedShortOp();
                  this.verifyAssert(funcIndex < script.getNumFunctionExpressions());
                  break;
               case 6:
                  this.verifyAssert(this.unsignedShortOp() < doublesSize);
                  break;
               case 7:
                  int var35 = this.byteOp();
                  this.verifyAssert(0 <= var35 && var35 < stringsSize);
                  break;
               case 8:
                  this.verifyAssert(this.unsignedShortOp() < localSize);
                  this.unsignedByteOp();
                  break;
               case 9:
                  this.verifyAssert(this.unsignedShortOp() < numTemps);
                  break;
               case 10:
                  this.unsignedByteOp();
                  break;
               case 11:
                  int var34 = this.byteOp();
                  this.verifyAssert(0 <= var34 && var34 < numTemps);
                  break;
               case 12:
                  this.verifyAssert(this.unsignedShortOp() < localSize);
                  break;
               case 13: {
                  int branchTarget = this.unsignedShortOp();
                  this.verifyAssert(branchTarget < var29);
                  insInfo[branchTarget] = (byte)(insInfo[branchTarget] | 1);
                  if (opCode == 110) {
                     insInfo[branchTarget] = (byte)(insInfo[branchTarget] | 20);
                  }
                  break;
               }
               case 14:
                  this.verifyAssert(this.unsignedShortOp() < numTemps);
                  int var30 = this.unsignedShortOp();
                  this.verifyAssert(var30 < var29);
                  insInfo[var30] = (byte)(insInfo[var30] | 1);
                  break;
               case 15:
                  int var33 = this.byteOp();
                  this.verifyAssert(0 <= var33 && var33 < localSize);
                  this.byteOp();
               case 16:
                  break;
               case 17:
                  int var32 = this.byteOp();
                  this.verifyAssert(0 <= var32 && var32 < idsSize);
                  break;
               case 18:
                  this.unsignedShortOp();
                  break;
               case 20:
                  this.verifyAssert(this.unsignedShortOp() < stringsSize);
                  break;
               case 21:
                  int deadCode = this.byteOp();
                  this.verifyAssert(0 <= deadCode && deadCode < integersSize);
                  break;
               case 22: {
                  int branchTarget = this._index + this.byteOp();
                  this.verifyAssert(branchTarget < var29);
                  insInfo[branchTarget] = (byte)(insInfo[branchTarget] | 1);
                  break;
               }
               case 23:
                  this.verifyAssert(this.unsignedShortOp() < idsSize);
                  break;
               case 24:
                  this.verifyAssert(this.unsignedShortOp() < this._globalSize);
                  break;
               case 25:
                  this.verifyAssert(this.unsignedShortOp() < this._globalSize);
                  this.unsignedByteOp();
                  break;
               case 26:
                  this._index += 2;
                  break;
               case 27:
                  this.verifyAssert(this.unsignedShortOp() < idsSize);
                  this.unsignedByteOp();
            }

            if (OpcodeBranches.get(opCode) != 0) {
               insInfo[insIndex] = (byte)(insInfo[insIndex] | 8);
            }
         }

         if (instructionRequiresScope) {
            this.verifyAssert(script.needsScope);
         }

         this.verifyAssert(this._index == var29);
         boolean deadCode = false;
         int stackDepth = 0;

         for (int i = 0; i <= var29; i++) {
            int info = insInfo[i];
            if ((info & 2) == 0) {
               this.verifyAssert((info & 1) == 0);
            } else {
               if ((info & 1) != 0) {
                  int checkDepth = -1;
                  if (deadCode) {
                     deadCode = false;
                  } else {
                     checkDepth = stackDepth;
                  }

                  if ((info & 4) != 0) {
                     stackDepth = (info & 240) >> 4;
                  } else {
                     this.verifyAssert(stackDepth == 0);
                     insInfo[i] = (byte)(info | 1 | 4 | stackDepth << 4);
                  }

                  if (checkDepth != -1) {
                     this.verifyAssert(checkDepth == stackDepth);
                  }
               }

               opCode = this._code[i];
               this._index = i + 1;
               int operandTypes = OperandTypes.get(opCode);
               int stackEffect;
               int stackRequired;
               switch (operandTypes) {
                  case 3:
                     stackEffect = -this.unsignedByteOp() - 1;
                     stackRequired = -stackEffect;
                     break;
                  case 18:
                     stackEffect = -this.unsignedShortOp() - 1;
                     stackRequired = -stackEffect;
                     break;
                  default:
                     stackEffect = OpcodeStackEffect.get(opCode);
                     stackRequired = OpcodeStackRequired.get(opCode);
               }

               if (opCode == -113) {
                  stackEffect--;
                  stackRequired++;
               }

               this.verifyAssert(stackDepth >= stackRequired);
               stackDepth += stackEffect;
               if ((info & 8) == 0) {
                  this.verifyAssert(stackDepth >= 0 && numTemps + stackDepth <= maxStack);
               } else {
                  int branchType = OpcodeBranches.get(opCode);
                  this.verifyAssert(stackDepth == 0 || stackDepth > 0 && stackDepth <= 15);
                  this.verifyAssert(numTemps + stackDepth <= maxStack);
                  switch (branchType) {
                     case 0:
                        this.verifyAssert(false);
                        break;
                     case 3:
                     case 4:
                        int target;
                        switch (operandTypes) {
                           case 13:
                              target = this.unsignedShortOp();
                              break;
                           case 22:
                              target = this._index + this.byteOp();
                              break;
                           default:
                              target = 0;
                              this.verifyAssert(false);
                        }

                        this.verifyAssert(stackDepth == 0 || target > this._index);
                        int targetInfo = insInfo[target];
                        if ((targetInfo & 4) != 0) {
                           this.verifyAssert(stackDepth == (targetInfo & 240) >> 4);
                        }

                        insInfo[target] = (byte)(targetInfo | 1 | 4 | stackDepth << 4);
                  }

                  switch (branchType) {
                     case 0:
                     case 4:
                        break;
                     case 1:
                     case 2:
                     case 3:
                     case 5:
                     default:
                        stackDepth = 0;
                        deadCode = true;
                  }
               }
            }
         }

         boolean hasNestedFunction;
         int numFunctionDeclarations;
         int i;
         switch (OpcodeBranches.get(opCode)) {
            case 0:
            case 4:
               this.verifyAssert(false);
            default:
               this.verifyAssert(deadCode);
               hasNestedFunction = false;
               numFunctionDeclarations = script.getNumFunctionDeclarations();
               i = 0;
         }

         while (i < numFunctionDeclarations) {
            this.verify(script.functionDeclarations[i]);
            hasNestedFunction = true;
            i++;
         }

         i = script.getNumFunctionExpressions();

         for (int i = 0; i < i; i++) {
            this.verify(script.functionExpressions[i]);
            hasNestedFunction = true;
         }

         if (hasNestedFunction) {
            this.verifyAssert(script.needsScope);
         }

         if (script.needsScope || script.parmLocations != null) {
            this.verifyAssert(!script.sensible);
            return;
         }

         var26 = false;
      } catch (VerifyError var27) {
         var26 = false;
         throw var27;
      } finally {
         if (var26) {
            this.verifyAssert(false);
            return;
         }
      }
   }
}
