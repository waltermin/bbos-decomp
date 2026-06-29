package com.sun.cldc.io;

import javax.microedition.io.Connection;

public interface ConnectionBaseInterface {
   int INTERNAL = 1;
   int EXTERNAL = 2;
   int MDS = 4;
   int CELLULAR = 8;
   int WIFI = 16;
   int SIM = 32;
   int BLUETOOTH = 64;
   int COMM_PORT = 128;
   int FILE = 256;

   Connection openPrim(String var1, int var2, boolean var3);

   int getProperties(String var1);
}
