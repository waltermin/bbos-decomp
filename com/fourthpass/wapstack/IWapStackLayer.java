package com.fourthpass.wapstack;

public interface IWapStackLayer {
   void setUserLayer(IWapStackLayer var1);

   void eventOccured(Object var1);

   void close();
}
