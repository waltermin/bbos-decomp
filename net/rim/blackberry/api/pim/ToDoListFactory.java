package net.rim.blackberry.api.pim;

public class ToDoListFactory {
   private ToDoListFactory() {
   }

   public static ToDoList createToDoList(int param0) throws PIMException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.blackberry.api.pim.ToDoListImpl"
      // 03: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 06: astore 2
      // 07: aload 2
      // 08: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0b: astore 3
      // 0c: aload 3
      // 0d: instanceof net/rim/blackberry/api/pim/PIMList
      // 10: ifeq 2a
      // 13: aload 3
      // 14: checkcast net/rim/blackberry/api/pim/BlackBerryToDoList
      // 17: astore 1
      // 18: aload 1
      // 19: iload 0
      // 1a: invokeinterface net/rim/blackberry/api/pim/BlackBerryToDoList.initialize (I)V 2
      // 1f: aload 1
      // 20: areturn
      // 21: astore 2
      // 22: goto 2a
      // 25: astore 2
      // 26: goto 2a
      // 29: astore 2
      // 2a: new net/rim/blackberry/api/pim/PIMException
      // 2d: dup
      // 2e: invokespecial net/rim/blackberry/api/pim/PIMException.<init> ()V
      // 31: athrow
      // try (0 -> 16): 17 null
      // try (0 -> 16): 19 null
      // try (0 -> 16): 21 null
   }
}
