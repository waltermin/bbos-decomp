package net.rim.tools.compiler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public interface Host {
   int PROGRESS_TICK = -1;
   int PROGRESS_LOADING = 0;
   int PROGRESS_LINKING = 1;
   int PROGRESS_SAVING = 2;
   int NUM_PROGRESS = 3;

   PrintStream openDiagnose();

   InputStream openInput(String var1);

   OutputStream openOutput(String var1);

   void advanceProgress(int var1);

   Object getClassInfo(String var1);

   String getModuleName(Object var1);

   String getModuleVersion(Object var1);

   int getClassAttributes(Object var1);
}
