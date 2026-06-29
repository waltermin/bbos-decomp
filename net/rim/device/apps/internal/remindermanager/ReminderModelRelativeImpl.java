package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.ui.Field;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.reminders.ReminderModelRelative;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.TimeChoiceField;

public final class ReminderModelRelativeImpl extends ReminderModelImpl implements PersistableRIMModel, ReminderModelRelative {
   private static final long[] REMINDER_CHOICES = new long[]{
      0L,
      300000L,
      600000L,
      900000L,
      1800000L,
      2700000L,
      3600000L,
      7200000L,
      10800000L,
      14400000L,
      18000000L,
      21600000L,
      25200000L,
      28800000L,
      32400000L,
      36000000L,
      39600000L,
      43200000L,
      86400000L,
      172800000L,
      259200000L,
      345600000L,
      432000000L,
      518400000L,
      604800000L,
      8359243861842984964L,
      7955894618263126022L,
      -3457638566669028247L,
      3328210921980168448L,
      -3457638566665637327L,
      7236837569803192576L,
      -3457638506531949979L,
      8241980339360241408L,
      8020102169462073443L,
      7238494203266689396L,
      -3457638407755268050L,
      7598186598780251648L,
      7305798977753198189L,
      7955941290069221678L,
      8227632734973093236L,
      3343187641640121189L,
      8241979218375500099L,
      7784890369L,
      7453017779705479178L,
      11434971311626L,
      222533648385L,
      4055009087275139148L,
      36028865739514169L,
      2449958192994582528L,
      3170535237180523769L,
      4503668346847232L,
      561729012534280704L,
      6192196968036176080L,
      -3802858467318206864L,
      8702061365974964824L,
      4251398405639537553L,
      4055009087275139225L,
      36028917279908153L,
      2449958192994582528L,
      3170535237180523769L,
      7881419606982656L,
      -2248517154939862528L,
      6786954438681829024L,
      6956359772234048979L,
      -5331156205948484103L,
      -2403190922620818452L,
      6815501292384343273L,
      2849571725725947249L,
      3077200785810262937L,
      -2650344568693511563L,
      4384720399761415038L,
      -3211841917802455341L,
      7290631998210381901L,
      2622064495873732848L,
      -2825893692940306968L,
      253403093224L,
      5138132777706979478L,
      936748722662541837L,
      576460753683630153L,
      3302964068352L,
      859744370176L,
      635859966620169223L,
      -3260785542993800683L,
      8311754233313492992L,
      -1150106750812553216L,
      653718733313L,
      297767030790L,
      62381726994792447L,
      -48609054930370560L,
      318644937984L,
      7195195657945368853L,
      -3019602657759146120L,
      5201657602835089458L,
      176671876478213L,
      4804852955411447808L,
      -3457638609618697632L,
      800352583993589923L,
      72169106143838464L,
      3115244560543606L,
      7972024885946840577L,
      7351563458917065313L,
      7014227185227795467L,
      28154648989750131L,
      576599811097624584L,
      513721691243840038L,
      4920603059029739520L,
      6081470781366274048L,
      8102047232261700456L,
      576578989742257733L,
      8315127890470531137L,
      576587304008249955L,
      -4097908686864748479L,
      -4409744832696156149L,
      -6754827694243944544L,
      60724116075871844L,
      8389209267074581512L,
      -6393413822727321600L,
      5262456973211205735L,
      576670867580457059L,
      7426519382792432969L,
      5910975500684036096L,
      88754811992146235L,
      88754811985602278L,
      4274487796280994790L,
      7598244868598851073L,
      5324944963703144584L,
      5324944963703144516L,
      -1873150746501836701L,
      -1873150746501775788L,
      -5555751736023973761L,
      7949706563756123148L,
      -6742049989507518609L,
      6062904526531723380L,
      576630920674355454L,
      -120462492237268652L,
      28446955647797248L,
      7278212196116026632L,
      7810404386178664448L,
      576588184003760675L,
      -6475212661915619755L,
      8314220326208697506L,
      576587631701985280L,
      -4681491196534906184L,
      1821354239922800640L,
      2529744717215170751L,
      5620022244044222019L,
      7544067071401444457L,
      7950293754081575028L,
      8404462525699334504L,
      7598953607880707072L,
      101170761213042022L,
      5252887583238712165L,
      102751561233228833L,
      864730115575468518L,
      -8486070027538763923L,
      1009066295397741633L,
      877664464075101696L,
      3320842269282430835L,
      3347142197221207573L,
      32622516120595994L,
      8077256608781250070L,
      8720054367922302579L,
      1526181513515525934L,
      1886572219194021678L,
      2337419303895908142L,
      1670295799482839920L,
      7362851227604883502L,
      8029124748201503858L,
      1670295799482838022L,
      7362851227604883502L,
      825898619199106162L,
      3321173918711414882L,
      8751389190894350360L,
      1670295799482835822L,
      -6183976451525568466L,
      1742355592530236928L,
      3323496046217032560L,
      848683810676632844L,
      1742355592530236928L,
      3323496046217032560L,
      34167330098458138L,
      3322018352231427606L,
      1670295799482866495L,
      31024032307354158L,
      3322018352231427606L,
      54708839919153006L,
      3322018352231427606L,
      3321173918711414954L,
      28494012120444442L,
      3322018352231427606L,
      3097744027889322L,
      3322018352231427606L,
      3320842368219885226L,
      63664034713906709L,
      837438732806467094L,
      34190552898346862L,
      837438732806467094L,
      1526181515836137326L,
      6851486956215170606L,
      7919018562648236642L
   };

   @Override
   public final byte getType() {
      return 1;
   }

   @Override
   public final Object copy() {
      ReminderModelRelativeImpl that = new ReminderModelRelativeImpl();
      that.setTime(this.getTime());
      return that;
   }

   @Override
   public final Field getField(Object context) {
      String label = CommonResources.getString(9052);
      boolean defaultChoices = true;
      if (context instanceof ContextObject) {
         ContextObject co = (ContextObject)context;
         if (co.getFlag(1)) {
            label = "";
         }

         if (co.getFlag(60)) {
            defaultChoices = false;
         }
      }

      long reminderDelta = this.getTime();
      int numChoices = REMINDER_CHOICES.length;
      int choiceLocation = Arrays.binarySearch(REMINDER_CHOICES, reminderDelta, 0, numChoices);
      int offset = 0;
      long[] reminderChoices;
      if (choiceLocation < 0) {
         if (defaultChoices) {
            offset = 1;
            reminderChoices = new long[++numChoices];
            reminderChoices[0] = -1;
         } else {
            reminderChoices = new long[++numChoices];
         }

         choiceLocation = choiceLocation * -1 - 1;
         System.arraycopy(REMINDER_CHOICES, 0, reminderChoices, offset, choiceLocation);
         if (choiceLocation < REMINDER_CHOICES.length) {
            System.arraycopy(REMINDER_CHOICES, choiceLocation, reminderChoices, choiceLocation + offset, numChoices - choiceLocation - 1);
         }

         reminderChoices[choiceLocation] = reminderDelta;
      } else if (defaultChoices) {
         reminderChoices = new long[REMINDER_CHOICES.length + 1];
         System.arraycopy(REMINDER_CHOICES, 0, reminderChoices, 1, REMINDER_CHOICES.length);
         reminderChoices[0] = -1;
      } else {
         reminderChoices = REMINDER_CHOICES;
      }

      return new TimeChoiceField(label, reminderChoices, this.getTime());
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof TimeChoiceField)) {
         return false;
      }

      long reminderDelta = ((TimeChoiceField)field).getSelectedTimeInMillis();
      this.setTime(reminderDelta);
      this.setState(1);
      this.setReminderFiredFor(Long.MIN_VALUE);
      return true;
   }
}
