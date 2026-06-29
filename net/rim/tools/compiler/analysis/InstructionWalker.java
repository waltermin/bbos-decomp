package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public class InstructionWalker implements Constants {
   public void walkInstruction(InstructionTarget ins) {
   }

   public void walkInstruction(Instruction ins) {
   }

   public void walkInstruction(InstructionBranch ins) {
   }

   public void walkInstruction(InstructionInts ins) {
   }

   public void walkInstruction(InstructionBytes ins) {
   }

   public void walkInstruction(InstructionLong ins) {
   }

   public void walkInstruction(InstructionNameAndType ins) {
   }

   public void walkInstruction(InstructionString ins) {
   }

   public void walkInstruction(InstructionStringArray ins) {
   }

   public void walkInstruction(InstructionType ins) {
   }

   public void unexpectedInstruction(Instruction ins) {
      throw new CompileException(((StringBuffer)(new Object("Unexpected instruction: "))).append(ins.getOpcode()).toString());
   }
}
