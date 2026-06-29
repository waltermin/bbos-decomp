package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.codfile.Code;
import net.rim.tools.compiler.vm.Constants;

public class Instruction implements Constants {
   private int _value;
   int _op;

   public Instruction(int ip, int opcode) {
      this(ip, opcode, 0);
   }

   public Instruction(int ip, int opcode, int op) {
      this._value = ip << 12 | opcode & 4095;
      this._op = op;
   }

   public Instruction setValueOp(int value, int op) {
      this._value = value;
      this._op = op;
      return this;
   }

   public int getValue() {
      return this._value;
   }

   public void setIp(int ip) {
      this._value = ip << 12 | this._value & 4095;
   }

   public int getIp() {
      return this._value >> 12;
   }

   public int getOpcode() {
      return this._value & 4095;
   }

   public int getOp() {
      return this._op;
   }

   public void setOp(int op) {
      this._op = op;
   }

   public void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }

   public int setOffset(int offset, boolean assumeFar) {
      this.setIp(offset);
      return offset + Code.getOpcodeSize(this.getOpcode(), 0, null, assumeFar, true);
   }
}
