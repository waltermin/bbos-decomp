package net.rim.tools.compiler.vm;

public interface Optimization extends Constants {
   int NONE;
   int DEADCODE;
   int ARRAYINIT;
   int CHECKCAST;
   int NOP;
   int TRIVIAL;
   int JUMP;
   int ACCESSOR;
   int MUTATOR;
   int PUSHPOP;
   int USELESS_CASE;
   int CLINIT_FINAL;
   int INNER_ACCESSOR;
   int BOOL_RET;
   int STRARRAYINIT;
   int ALL;
   int DEVICE_ONLY;
}
