package net.rim.device.apps.internal.commonmodels.categories;

class CategoriesScreen$3 implements Runnable {
   private final CategoriesScreen this$0;

   CategoriesScreen$3(CategoriesScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      CategoriesScreen.access$500(this.this$0).deleteAll();
      this.this$0.addCategoriesToMainScreen(CategoriesScreen.access$600(this.this$0));
   }
}
