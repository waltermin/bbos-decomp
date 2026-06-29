package net.rim.plazmic.internal.mediaengine.util;

public class ProfileTimer extends ProfileElement {
   private int _state;
   private int _count;
   private long _timeStart;
   private long _timeEnd;
   private long _duration;
   private int _countSaved;
   private long _durationSaved;
   private int _resetCount;
   static final int TIMING_STOPPED;
   static final int TIMING_STARTED;
   static final int TIMING_ARMED;

   ProfileTimer(String name) {
      super(name);
      this.initialize();
   }

   public boolean start() {
      this._timeStart = System.currentTimeMillis();
      this._state = 1;
      return true;
   }

   public boolean startSingleShot() {
      if (this._state == 2) {
         this._timeStart = System.currentTimeMillis();
         this._state = 1;
         return true;
      } else {
         return false;
      }
   }

   public boolean stop() {
      if (this._state == 1) {
         this._timeEnd = System.currentTimeMillis();
         this._duration = this._duration + (this._timeEnd - this._timeStart);
         this._count++;
         this._state = 0;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void reset() {
      this._resetCount++;
      this._durationSaved = this._durationSaved + this._duration;
      this._countSaved = this._countSaved + this._count;
      this._count = 0;
      this._timeStart = 0;
      this._timeEnd = 0;
      this._duration = 0;
      this._state = 2;
   }

   @Override
   public void initialize() {
      this._resetCount = 0;
      this._durationSaved = 0;
      this._countSaved = 0;
      this._count = 0;
      this._timeStart = 0;
      this._timeEnd = 0;
      this._duration = 0;
      this._state = 0;
   }

   public int getCount() {
      return this._count;
   }

   public int getTotalCount() {
      return this._countSaved + this._count;
   }

   public int getResetCount() {
      return this._resetCount;
   }

   public long getDuration() {
      return this._duration;
   }

   public long getTotalDuration() {
      return this._durationSaved + this._duration;
   }

   public double getAverageDuration() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._count I
      // 04: ifle 14
      // 07: aload 0
      // 08: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._duration J
      // 0b: l2d
      // 0c: aload 0
      // 0d: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._count I
      // 10: i2d
      // 11: ddiv
      // 12: nop
      // 13: dreturn
      // 14: nop
      // 15: ldc2_w -4616189618054758400
      // 18: nop
      // 19: dreturn
   }

   public double getAverageResetDuration() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._resetCount I
      // 04: ifle 19
      // 07: aload 0
      // 08: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._durationSaved J
      // 0b: aload 0
      // 0c: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._duration J
      // 0f: ladd
      // 10: l2d
      // 11: aload 0
      // 12: getfield net/rim/plazmic/internal/mediaengine/util/ProfileTimer._resetCount I
      // 15: i2d
      // 16: ddiv
      // 17: nop
      // 18: dreturn
      // 19: nop
      // 1a: ldc2_w -4616189618054758400
      // 1d: nop
      // 1e: dreturn
   }

   public double getAverageCount() {
      return this._resetCount > 0 ? (double)(this._countSaved + this._count) / this._resetCount : this._count;
   }
}
