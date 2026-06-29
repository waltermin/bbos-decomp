package net.rim.device.api.crypto;

public interface SelfTestModule {
   int getNumTests(boolean var1);

   void test(int var1);

   String[] getTestNames(boolean var1);
}
