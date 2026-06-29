package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.vm.Array;

public class SystemEnabledMenu extends Menu {
   private ContextObject _context;
   private Verb[] _verbs = new Verb[0];
   private Verb[] _verbHolder = new Verb[0];
   private Verb _defaultVerb;
   private AppsMainScreen _screen;
   private boolean _promotedVerbs;
   private Verb _priorityVerb;
   private int _priorityValue = Integer.MAX_VALUE;

   public SystemEnabledMenu(Object creationContext, AppsMainScreen screen) {
      this(!(creationContext instanceof ContextObject) ? null : (ContextObject)creationContext, screen, 0);
   }

   public SystemEnabledMenu(Object creationContext, AppsMainScreen screen, int instance) {
      this(!(creationContext instanceof ContextObject) ? null : (ContextObject)creationContext, screen, instance);
   }

   public SystemEnabledMenu(ContextObject creationContext, AppsMainScreen screen, int instance) {
      super(65536);
      this._screen = screen;
      this._context = creationContext;
      if (instance == 0) {
         this.addSystemItems(creationContext);
      }
   }

   public void add(Verb verb) {
      if (verb != null) {
         Arrays.add(this._verbs, verb);
      }
   }

   public void add(Verb verb, int priority) {
      VerbMenuItem item = new VerbMenuItem(verb, priority);
      item.setContext(this._context);
      item.setNotify(this._screen);
      super.add(item);
   }

   public void add(Verb[] verbs) {
      if (verbs != null) {
         int length = verbs.length;

         for (int lv = 0; lv < length; lv++) {
            this.add(verbs[lv]);
         }
      }
   }

   public void add(VerbProvider verbProvider) {
      this.add((Object)verbProvider, Integer.MAX_VALUE);
   }

   public void add(VerbProvider verbProvider, int priority) {
      Verb defaultVerb = verbProvider.getVerbs(null, this._verbHolder);
      int end = this._verbHolder.length;

      for (int lv = 0; lv < end; lv++) {
         Verb verb = this._verbHolder[lv];
         int prioritySet = verb != defaultVerb && priority != Integer.MAX_VALUE ? priority + 1 : priority;
         this.add((Object)verb, prioritySet);
      }
   }

   public void addInternal(Verb verb, int priority) {
      VerbMenuItem item = new VerbMenuItem(verb, priority);
      item.setContext(this._context);
      item.setNotify(this._screen);
      super.add(item);
   }

   private void addInternal(Verb[] verbs) {
      if (verbs != null) {
         int length = verbs.length;

         for (int lv = 0; lv < length; lv++) {
            this.addInternal(verbs[lv], Integer.MAX_VALUE);
         }
      }
   }

   private void addSystemItems(Object creationContext) {
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(8522643724050848398L);
      if (verbFactories != null && verbFactories.length > 0) {
         ContextObject context = ContextObject.castOrCreate(creationContext);
         boolean oldSystemFlag = ContextObject.getFlag(context, 90);
         context.setFlag(90);

         for (int i = 0; i < verbFactories.length; i++) {
            VerbFactory factory = verbFactories[i];
            Verb[] verbs = factory.getVerbs(context);
            this.add(verbs);
         }

         if (!oldSystemFlag) {
            ContextObject.clearFlag(context, 90);
         }
      }
   }

   public void coalesce(long verbCombinerCollectionId, DefaultVerbProvider defaultVerbProvider) {
      Verb defaultVerb = coalesce(this._verbs, this._defaultVerb, verbCombinerCollectionId, defaultVerbProvider);
      if (defaultVerb != null) {
         this.setDefault(defaultVerb);
      }
   }

   public static Verb coalesce(Verb[] verbs, Verb originalDefaultVerb, long verbCombinerCollectionId, DefaultVerbProvider defaultVerbProvider) {
      VerbCombiner[] combiners = VerbCombinerRepository.getCombiners(verbCombinerCollectionId);
      Verb coalescedDefaultVerb = null;
      Verb defaultWrapperVerb = null;
      if (combiners != null && combiners.length > 0) {
         int verbCount = verbs.length;
         if (verbCount > 0) {
            Verb[] tmpVerbs = new Verb[0];

            for (VerbCombiner combiner : combiners) {
               int index = 0;
               verbCount = verbs.length;
               Array.resize(tmpVerbs, verbCount);
               coalescedDefaultVerb = null;

               for (int j = 0; j < verbCount; j++) {
                  Verb theVerb = verbs[j];
                  if (theVerb != null && combiner.recognize(theVerb)) {
                     if (originalDefaultVerb == theVerb) {
                        coalescedDefaultVerb = theVerb;
                     }

                     tmpVerbs[index++] = theVerb;
                     verbs[j] = null;
                  }
               }

               if (index > 0) {
                  Array.resize(verbs, verbCount + 1);
                  if (index == 1) {
                     verbs[verbCount] = tmpVerbs[0];
                  } else if (index > 1) {
                     Array.resize(tmpVerbs, index);
                     Verb newDefaultVerb = null;
                     if (coalescedDefaultVerb != null) {
                        newDefaultVerb = coalescedDefaultVerb;
                     }

                     if (defaultVerbProvider != null) {
                        newDefaultVerb = defaultVerbProvider.getDefaultVerb(tmpVerbs);
                     }

                     Verb wrapperVerb = combiner.createWrapperVerb(tmpVerbs, newDefaultVerb);
                     verbs[verbCount] = wrapperVerb;
                     if (coalescedDefaultVerb != null) {
                        defaultWrapperVerb = wrapperVerb;
                        coalescedDefaultVerb = null;
                     }
                  }
               }
            }

            int index = 0;
            int length = verbs.length;

            for (int lv = 0; lv < length; lv++) {
               if (verbs[lv] != null) {
                  verbs[index] = verbs[lv];
                  index++;
               }
            }

            Array.resize(verbs, index);
            return defaultWrapperVerb;
         }
      }

      return null;
   }

   public Verb getDefaultVerb() {
      return this._defaultVerb;
   }

   public Verb getFirstVerbAt(int ordinal) {
      Arrays.sort(this._verbs, new SystemEnabledMenu$VerbOrdinalComparator(null));
      int length = this._verbs.length;

      for (int lv = 0; lv < length; lv++) {
         Verb verb = this._verbs[lv];
         if (verb.getOrdering() >= ordinal) {
            return verb;
         }
      }

      return null;
   }

   public ContextObject getContext() {
      return this._context;
   }

   public Object getResult() {
      Object cookie = this.getSelectedItem();
      if (!(cookie instanceof VerbMenuItem)) {
         return null;
      }

      VerbMenuItem vmi = (VerbMenuItem)cookie;
      return vmi.getResult();
   }

   public Verb getVerb() {
      Object cookie = this.getSelectedItem();
      if (!(cookie instanceof VerbMenuItem)) {
         return null;
      }

      VerbMenuItem vmi = (VerbMenuItem)cookie;
      return (Verb)vmi.getVerb();
   }

   public Verb[] getVerbs() {
      return this._verbs;
   }

   public AppsMainScreen getScreen() {
      return this._screen;
   }

   public void promoteVerbs() {
      this._promotedVerbs = true;
      this.addInternal(this._verbs);

      for (int lv = this.getSize() - 1; lv >= 0; lv--) {
         MenuItem mi = this.getItem(lv);
         if (mi instanceof VerbMenuItem) {
            VerbMenuItem vmi = (VerbMenuItem)mi;
            if (vmi.getVerb() == this._defaultVerb) {
               super.setDefault(vmi);
            }

            if (vmi.getVerb() == this._priorityVerb) {
               vmi.setPriority(this._priorityValue);
            }
         }
      }
   }

   public void remove(Verb verb) {
      int length = this._verbs.length;

      for (int lv = 0; lv < length; lv++) {
         Verb tmpVerb = this._verbs[lv];
         if (ObjectUtilities.objEqual(verb, tmpVerb)) {
            Arrays.removeAt(this._verbs, lv);
            break;
         }
      }

      if (this._defaultVerb == verb) {
         this._defaultVerb = null;
      }
   }

   @Override
   public int show() {
      if (!this._promotedVerbs) {
         this.promoteVerbs();
         MenuItem defaultMenuItem = this.getDefault();
         if (defaultMenuItem.getOrdinal() == 0) {
            int count = this.getSize();

            for (int lv = 0; lv < count; lv++) {
               Object mi = this.getItem(lv);
               if (mi instanceof VerbMenuItem) {
                  VerbMenuItem vmi = (VerbMenuItem)mi;
                  Verb verb = (Verb)vmi.getVerb();
                  if (verb.getOrdering() > 4096) {
                     this.setDefault(vmi);
                     break;
                  }
               }
            }
         }
      }

      return super.show();
   }

   public void setDefault(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setVerbPriority(Verb verb, int priority) {
      this._priorityVerb = verb;
      this._priorityValue = priority;
   }
}
