package org.w3c.dom;

public class DOMException extends RuntimeException {
   public short code;
   public static final short INDEX_SIZE_ERR;
   public static final short DOMSTRING_SIZE_ERR;
   public static final short HIERARCHY_REQUEST_ERR;
   public static final short WRONG_DOCUMENT_ERR;
   public static final short INVALID_CHARACTER_ERR;
   public static final short NO_DATA_ALLOWED_ERR;
   public static final short NO_MODIFICATION_ALLOWED_ERR;
   public static final short NOT_FOUND_ERR;
   public static final short NOT_SUPPORTED_ERR;
   public static final short INUSE_ATTRIBUTE_ERR;
   public static final short INVALID_STATE_ERR;
   public static final short SYNTAX_ERR;
   public static final short INVALID_MODIFICATION_ERR;
   public static final short NAMESPACE_ERR;
   public static final short INVALID_ACCESS_ERR;

   public DOMException(short code, String message) {
      super(message);
      this.code = code;
   }
}
