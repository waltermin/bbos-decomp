package net.rim.device.api.crypto.asn1;

public interface DERTags {
   int TAG_EXPLICIT;
   int TAG_IMPLICIT;
   int TAG_NONE;
   int CONSTRUCTED_FLAG;
   int CLASS_MASK;
   int UNIVERSAL_CLASS_FLAG;
   int APPLICATION_SPECIFIC_CLASS_FLAG;
   int CONTEXT_SPECIFIC_CLASS_FLAG;
   int PRIVATE_CLASS_FLAG;
   int TAG_MASK;
   int BOOLEAN;
   int INTEGER;
   int ENUMERATED;
   int BIT_STRING;
   int OCTET_STRING;
   int NULL;
   int OBJECT_IDENTIFIER;
   int SEQUENCE;
   int SET;
   int UTF8_STRING;
   int PRINTABLE_STRING;
   int T61_STRING;
   int IA5_STRING;
   int UTC_TIME;
   int GENERALIZED_TIME;
   int BMP_STRING;
}
