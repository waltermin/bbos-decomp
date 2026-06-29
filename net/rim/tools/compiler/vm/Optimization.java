package net.rim.tools.compiler.vm;

public interface Optimization extends Constants {
   int NONE = 0;
   int DEADCODE = 1;
   int ARRAYINIT = 4;
   int CHECKCAST = 2;
   int NOP = 8;
   int TRIVIAL = 16;
   int JUMP = 32;
   int ACCESSOR = 64;
   int MUTATOR = 128;
   int PUSHPOP = 256;
   int USELESS_CASE = 512;
   int CLINIT_FINAL = 1024;
   int INNER_ACCESSOR = 2048;
   int BOOL_RET = 4096;
   int STRARRAYINIT = 8192;
   int ALL = 11007;
   int DEVICE_ONLY = 15103;
}
