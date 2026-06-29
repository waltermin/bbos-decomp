package net.rim.wica.common.builtindata.componentdefn;

public interface FieldTypes {
   int TYPES_MASK = 65535;
   int TYPE_INFO_MASK = 32767;
   int BOOLEAN = 0;
   int INT = 1;
   int DECIMAL = 2;
   int STRING = 3;
   int DATE = 4;
   int ENUM = 5;
   int DATA = 6;
   int TYPE_RESERVED = 7;
   int LONG = 8;
   int BYTE = 9;
   int _ARRAY_BIT = 32768;
   int BOOLEAN_ARRAY = 32768;
   int INT_ARRAY = 32769;
   int DECIMAL_ARRAY = 32770;
   int STRING_ARRAY = 32771;
   int DATE_ARRAY = 32772;
   int ENUM_ARRAY = 32773;
   int DATA_ARRAY = 32774;
   int LONG_ARRAY = 32776;
   int BYTE_ARRAY = 32777;
   int _ACCESS_MASK = -268435456;
   int ACCESS_READWRITE = 268435456;
   int ACCESS_READONLY = 536870912;
   int ACCESS_DENIED = 1073741824;
}
