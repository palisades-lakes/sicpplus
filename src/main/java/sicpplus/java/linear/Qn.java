package sicpplus.java.linear;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.CollectionSampler;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import sicpplus.java.algebra.OneSetOneOperation;
import sicpplus.java.algebra.Set;
import sicpplus.java.algebra.TwoSetsOneOperation;
import sicpplus.java.numbers.Doubles;
import sicpplus.java.numbers.Floats;
import sicpplus.java.numbers.Numbers;
import sicpplus.java.numbers.Q;
import sicpplus.java.numbers.RationalFloat;
import sicpplus.java.numbers.RationalFloats;
import sicpplus.java.prng.Generator;
import sicpplus.java.prng.GeneratorBase;
import sicpplus.java.prng.Generators;

/** The set of arrays of some fixed length <code>n</code>,
 *  of primitive numbers, or
 * of certain subclasses of <code>Number</code>,
 * interpreted as tuples of rational numbers.
 *
 * This is primarily intended to support implementing the standard
 * <em>rational</em> linear space <b>Q</b><sup>n</sup>.
 * (Essentially <b>R</b><sup>n</sup> except over the rational
 * numbers rather than the reals.)

 * TODO: generalize to tuples
 * implemented with lists, <code>int</code> indexed maps
 * for sparse vectors, etc.
 * Long term goal is to support any useful data structures
 * that can be used to represent tuples of rational numbers.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-10-12
 */
@SuppressWarnings("unchecked")
public final class Qn extends LinearSpaceLike {

  //--------------------------------------------------------------
  // operations for algebraic structures over rational arrays.
  //--------------------------------------------------------------
  /** A <code>BinaryOperator</code> that adds elementwise
   * <code>RationalFloat[]</code> instances of length
   * <code>dimension</code>.
   */

  @Override
  public final Object add (final Object x0,
                           final Object x1) {
    assert contains(x0);
    assert contains(x1);
    final RationalFloat[] q0 = RationalFloats.toRationalFloat(x0);
    final RationalFloat[] q1 = RationalFloats.toRationalFloat(x1);
    final RationalFloat[] qq = new RationalFloat[dimension()];
    for (int i=0;i<dimension();i++) { qq[i] = q0[i].add(q1[i]); }
    return qq; }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat[] zero (final int n) {
    final RationalFloat[] z = new RationalFloat[n];
    Arrays.fill(z,RationalFloat.ZERO);
    return z; }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat[] negate (final Object x) {
    assert contains(x);
    final RationalFloat[] q = RationalFloats.toRationalFloat(x);
    final RationalFloat[] qq = new RationalFloat[dimension()];
    for (int i=0;i<dimension();i++) { qq[i] = q[i].negate(); }
    return qq; }

  //--------------------------------------------------------------

  @Override
  public final RationalFloat[] scale (final Object a,
                                 final Object x) {
    assert contains(x);
    final RationalFloat b = RationalFloat.valueOf(a);
    final RationalFloat[] q = RationalFloats.toRationalFloat(x);
    final RationalFloat[] qq = new RationalFloat[dimension()];
    for (int i=0;i<dimension();i++) { qq[i] = q[i].multiply(b); }
    return qq; }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean equals (final Object x0,
                               final Object x1) {
    assert contains(x0);
    assert contains(x1);
    final RationalFloat[] q0 = RationalFloats.toRationalFloat(x0);
    final RationalFloat[] q1 = RationalFloats.toRationalFloat(x1);
    for (int i=0;i<dimension();i++) {
      if (! q0[i].equals(q1[i])) { return false; } }
    return true; }

  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    if (null == element) { return false; }
    final Class c = element.getClass();
    if (! c.isArray()) { return false; }
    final int n = Array.getLength(element);
    if (n != dimension()) { return false; }
    if (Q.knownRational(c.getComponentType())) { return true; }
    for (int i=0;i<n;i++) {
      if (! Q.get().contains(Array.get(element,i))) {
        return false; } }
    return true; }

  //--------------------------------------------------------------
  /** Intended primarily for testing.
   */

  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    return
      new Supplier () {
      final Generator g = RationalFloats.generator(dimension(),urp);
      @Override
      public final Object get () { return g.next(); } }; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final String toString () { return "Q^" + dimension(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------
  // TODO: support zero-dimensional space?

  //--------------------------------------------------------------
  /** Generate arrays representing vectors in an n-dimensional
   * rational linear space, returning all possible number array
   * types.
   */

  public static final Generator
  qnGenerator (final int n,
               final UniformRandomProvider urp) {
    return new GeneratorBase ("qnGenerator:" + n) {
      private final CollectionSampler<Generator> generators =
        new CollectionSampler(
          urp,
          List.of(
            Generators.byteGenerator(n,urp),
            Generators.shortGenerator(n,urp),
            Generators.intGenerator(n,urp),
            Generators.longGenerator(n,urp),
            Generators.bigIntegerGenerator(n,urp),
            //bigDecimalGenerator(n,urp),
            Floats.finiteGenerator(n,urp),
            Doubles.finiteGenerator(n,urp),
            RationalFloats.generator(urp)
            // ERational correct, but slow
            //ERationals.eIntegerGenerator(n,urp),
            //ERationals.eRationalFromDoubleGenerator(n,urp)
            // clojure.lang.Ratio doesn't round correctly
            // BigFraction.doubleValue() doesn't round correctly.
            //,bigFractionGenerator(n,urp),
            ,Numbers.finiteNumberGenerator(n,urp)));
      @Override
      public final Object next () {
        return generators.sample().next(); } }; }

  private Qn (final int dimension) { super(dimension); }

  private static final IntObjectMap<Qn> _cache =
    new IntObjectHashMap();

  public static final Qn get (final int dimension) {
    final Qn s0 = _cache.get(dimension);
    if (null != s0) { return s0; }
    final Qn s1 = new Qn(dimension);
    _cache.put(dimension,s1);
    return s1; }

  //--------------------------------------------------------------

  public static final OneSetOneOperation group (final int n) {
    final Qn g = get(n);
    return OneSetOneOperation.commutativeGroup(
      g.adder(),
      g,
      g.additiveIdentity(),
      g.additiveInverse()); }

  //--------------------------------------------------------------
  /** n-dimensional rational vector space, implemented with
   * any known rational array.
   */

  private static final TwoSetsOneOperation
  makeSpace (final int n) {
    return
      TwoSetsOneOperation.linearSpaceLike(
        get(n).scaler(),
        group(n),
        Q.FIELD); }

  private static final IntObjectMap<TwoSetsOneOperation>
  _spaceCache = new IntObjectHashMap();

  /** n-dimensional rational vector space, implemented with
   * <code>RationalFloat[]</code>.
   */
  public static final TwoSetsOneOperation
  space (final int dimension) {
    final TwoSetsOneOperation space0 = _spaceCache.get(dimension);
    if (null != space0) { return space0; }
    final TwoSetsOneOperation space1 = makeSpace(dimension);
    _spaceCache.put(dimension,space1);
    return space1; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

