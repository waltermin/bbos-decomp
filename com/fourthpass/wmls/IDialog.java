package com.fourthpass.wmls;

public interface IDialog {
   String alert(String var1);

   String confirm(String var1, String var2, String var3);

   String prompt(String var1, String var2);
}
