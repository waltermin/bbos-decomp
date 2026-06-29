package net.rim.wica.transport.security;

public class KeyType {
   private String _name;
   private String _curveSpec;
   public static final KeyType AES = new KeyType("AES", null);
   public static final KeyType RSA = new KeyType("RSA", null);
   public static final KeyType ECDSA = new KeyType("ECDSA", "sect163k1");

   private KeyType(String name, String curveSpec) {
      this._name = name;
      this._curveSpec = curveSpec;
   }

   public String getName() {
      return this._name;
   }

   public String getCurveSpec() {
      return this._curveSpec;
   }

   public boolean equals(KeyType keyType) {
      return keyType != null ? this._name.equals(keyType.getName()) : false;
   }
}
