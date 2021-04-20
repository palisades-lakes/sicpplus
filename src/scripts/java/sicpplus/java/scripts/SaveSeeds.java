package sicpplus.java.scripts;

import java.io.File;
import java.time.LocalDate;

import sicpplus.java.prng.Seeds;

//----------------------------------------------------------------
/** <pre>
 * j --source 11 src/scripts/java/sicpplus/java/scripts/SaveSeeds.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-01-05
 */

@SuppressWarnings("unchecked")
public final class SaveSeeds {

  //--------------------------------------------------------------

  public static final void main (final String[] args) {

    // for Well44497b
    Seeds.write(
      Seeds.commonsRngSeed(1391),
      //Seeds.randomDotOrgSeed(1391),
      new File(
        "src/main/resources/seeds", 
        "Well44497b-" + LocalDate.now() + ".txt")); 

    // for Mersenne Twister
    Seeds.write(
      Seeds.commonsRngSeed(624),
      // Seeds.randomDotOrgSeed(624),
      new File(
        "src/main/resources/seeds", 
        "MT-" + LocalDate.now() + ".txt")); 
  }

  //--------------------------------------------------------------
  // disable constructor
  //--------------------------------------------------------------

  private SaveSeeds () {
    throw new UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------