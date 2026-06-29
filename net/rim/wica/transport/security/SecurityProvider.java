package net.rim.wica.transport.security;

public interface SecurityProvider {
   byte[] sign(byte[] var1, SecurityAlgorithm var2, Key var3);

   int sign(byte[] var1, int var2, int var3, byte[] var4, int var5, SecurityAlgorithm var6, Key var7);

   byte[] encrypt(byte[] var1, SecurityAlgorithm var2, Key var3, byte[] var4);

   byte[] encrypt(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, SecurityAlgorithm var7, Key var8);

   boolean verifySignature(byte[] var1, byte[] var2, SecurityAlgorithm var3, Key var4);

   boolean verifySignature(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, SecurityAlgorithm var7, Key var8);

   byte[] decrypt(byte[] var1, SecurityAlgorithm var2, Key var3, byte[] var4);

   byte[] decrypt(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6, SecurityAlgorithm var7, Key var8);

   Key generateKey(KeyType var1);

   KeyPair generateKeyPair(KeyType var1);

   byte[] encodeKey(Key var1);

   Key decodeKey(KeyType var1, byte[] var2);

   byte[] deriveSecret(SecurityAlgorithm var1, Key var2, Key var3);

   byte[] generateIV(int var1);

   boolean isServer();
}
