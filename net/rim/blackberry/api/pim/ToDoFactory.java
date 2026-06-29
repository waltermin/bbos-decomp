package net.rim.blackberry.api.pim;

public class ToDoFactory {
   private ToDoFactory() {
   }

   public static ToDo createToDo(int param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.blackberry.api.pim.ToDoImpl"
      // 03: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 06: astore 2
      // 07: aload 2
      // 08: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0b: astore 3
      // 0c: aload 3
      // 0d: dup
      // 0e: instanceof net/rim/blackberry/api/pim/BlackBerryToDo
      // 11: ifne 18
      // 14: pop
      // 15: goto 2c
      // 18: checkcast net/rim/blackberry/api/pim/BlackBerryToDo
      // 1b: astore 1
      // 1c: aload 1
      // 1d: iload 0
      // 1e: invokeinterface net/rim/blackberry/api/pim/BlackBerryToDo.initialize (I)V 2
      // 23: aload 1
      // 24: areturn
      // 25: astore 2
      // 26: aconst_null
      // 27: areturn
      // 28: astore 2
      // 29: aconst_null
      // 2a: areturn
      // 2b: astore 2
      // 2c: aconst_null
      // 2d: areturn
      // try (0 -> 18): 19 null
      // try (0 -> 18): 22 null
      // try (0 -> 18): 25 null
   }

   public static ToDo createToDo(int param0, Object param1, ToDoList param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.blackberry.api.pim.ToDoImpl"
      // 03: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 06: astore 4
      // 08: aload 4
      // 0a: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0d: astore 5
      // 0f: aload 5
      // 11: dup
      // 12: instanceof net/rim/blackberry/api/pim/BlackBerryToDo
      // 15: ifne 1c
      // 18: pop
      // 19: goto 35
      // 1c: checkcast net/rim/blackberry/api/pim/BlackBerryToDo
      // 1f: astore 3
      // 20: aload 3
      // 21: iload 0
      // 22: aload 1
      // 23: aload 2
      // 24: invokeinterface net/rim/blackberry/api/pim/BlackBerryToDo.initialize (ILjava/lang/Object;Lnet/rim/blackberry/api/pim/ToDoList;)V 4
      // 29: aload 3
      // 2a: areturn
      // 2b: astore 4
      // 2d: aconst_null
      // 2e: areturn
      // 2f: astore 4
      // 31: aconst_null
      // 32: areturn
      // 33: astore 4
      // 35: aconst_null
      // 36: areturn
      // try (0 -> 20): 21 null
      // try (0 -> 20): 24 null
      // try (0 -> 20): 27 null
   }

   public static boolean isInternalToDoModel(Object selected) {
      BlackBerryToDo todo = (BlackBerryToDo)createToDo(1);
      return todo == null ? false : todo.isInternalModel(selected);
   }
}
