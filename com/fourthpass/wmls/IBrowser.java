package com.fourthpass.wmls;

public interface IBrowser {
   String getCurrentCard(boolean var1);

   void newContext();

   String getCurrentUrl();

   String getVar(String var1);

   String go(String var1);

   String prev();

   String refresh();

   void setVar(String var1, String var2);

   String loadString(String var1, String var2);
}
