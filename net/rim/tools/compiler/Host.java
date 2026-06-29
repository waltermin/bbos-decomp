package net.rim.tools.compiler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public interface Host {
   int PROGRESS_TICK;
   int PROGRESS_LOADING;
   int PROGRESS_LINKING;
   int PROGRESS_SAVING;
   int NUM_PROGRESS;

   PrintStream openDiagnose();

   InputStream openInput(String var1);

   OutputStream openOutput(String var1);

   void advanceProgress(int var1);

   Object getClassInfo(String var1);

   String getModuleName(Object var1);

   String getModuleVersion(Object var1);

   int getClassAttributes(Object var1);
}
