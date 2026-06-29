package net.rim.device.api.crypto.asn1;

public interface DERTags {
   int TAG_EXPLICIT = 1;
   int TAG_IMPLICIT = 2;
   int TAG_NONE = 3;
   int CONSTRUCTED_FLAG = 32;
   int CLASS_MASK = 192;
   int UNIVERSAL_CLASS_FLAG = 0;
   int APPLICATION_SPECIFIC_CLASS_FLAG = 64;
   int CONTEXT_SPECIFIC_CLASS_FLAG = 128;
   int PRIVATE_CLASS_FLAG = 192;
   int TAG_MASK = 31;
   int BOOLEAN = 1;
   int INTEGER = 2;
   int ENUMERATED = 10;
   int BIT_STRING = 3;
   int OCTET_STRING = 4;
   int NULL = 5;
   int OBJECT_IDENTIFIER = 6;
   int SEQUENCE = 16;
   int SET = 17;
   int UTF8_STRING = 12;
   int PRINTABLE_STRING = 19;
   int T61_STRING = 20;
   int IA5_STRING = 22;
   int UTC_TIME = 23;
   int GENERALIZED_TIME = 24;
   int BMP_STRING = 30;
}
