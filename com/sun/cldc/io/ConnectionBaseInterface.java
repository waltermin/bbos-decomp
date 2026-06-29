package com.sun.cldc.io;

import javax.microedition.io.Connection;

public interface ConnectionBaseInterface {
   int INTERNAL;
   int EXTERNAL;
   int MDS;
   int CELLULAR;
   int WIFI;
   int SIM;
   int BLUETOOTH;
   int COMM_PORT;
   int FILE;

   Connection openPrim(String var1, int var2, boolean var3);

   int getProperties(String var1);
}
