package net.rim.vm;

public interface VMConstants {
   int T_UNUSED = 0;
   int T_BOOLEAN = 1;
   int T_BYTE = 2;
   int T_CHAR = 3;
   int T_SHORT = 4;
   int T_INTEGER = 5;
   int T_LONG = 6;
   int T_CLASS = 7;
   int T_ARRAY = 8;
   int T_CLASS_UNINITIALIZED = 9;
   int T_VOID = 10;
   int T_FLOAT = 11;
   int T_DOUBLE = 12;
   int T_LONG_2 = 13;
   int T_STRING = 14;
   int T_MAX = 14;
   int ATTRIB_StaticMethod = 16;
   int ATTRIB_AbstractMethod = 32;
   int ATTRIB_Exceptional = 64;
   int ATTRIB_Init = 128;
   int ATTRIB_Clinit = 256;
   int ATTRIB_Public = 1;
   int ATTRIB_Private = 2;
   int ATTRIB_Protected = 4;
   int ATTRIB_Final = 8;
   int ATTRIB_Abstract = 16;
   int ATTRIB_Interface = 32;
   int TRACEBACK_IMMEDIATE = 0;
   int TRACEBACK_OUTSIDE_MODULE = 1;
   int TRACEBACK_OUTSIDE_SIBLINGS = 2;
   int TRACEBACK_FIRST_3RD_PARTY = 3;
}
