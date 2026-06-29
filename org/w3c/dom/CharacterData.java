package org.w3c.dom;

public interface CharacterData extends Node {
   String getData();

   void setData(String var1);

   int getLength();

   String substringData(int var1, int var2);

   void appendData(String var1);

   void insertData(int var1, String var2);

   void deleteData(int var1, int var2);

   void replaceData(int var1, int var2, String var3);
}
