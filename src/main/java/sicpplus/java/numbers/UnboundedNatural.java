package sicpplus.java.numbers;

import static sicpplus.java.numbers.Numbers.hiWord;
import static sicpplus.java.numbers.Numbers.unsigned;

import org.apache.commons.rng.UniformRandomProvider;

import sicpplus.java.prng.Generator;
import sicpplus.java.prng.GeneratorBase;

/** /** A proof-of-concept implementation of unbounded natural
 * numbers. Only implementing a commutative monoid 
 * (ie just addition) for now.
 * 
 * This is in contrast to {@link Natural} and
 * {@link java.math.BigInteger}, which both have bounded ranges,
 * limited, for one thing, by the fact that bits are addressable
 * by <code>int</code>.
 *  
 * @author palisades dot lakes at gmail dot com
 * @version 2021-05-27
 */

@SuppressWarnings("unchecked")
public final class UnboundedNatural 
// not ring-like, at most group-like, actually just a monoid
// implements Ringlike<UnboundedNatural> {
{

  //--------------------------------------------------------------
  // an unbounded immutable sequence of ints
  //--------------------------------------------------------------
  // TODO: Any value to implementing Iterator<Integer> or
  // PrimitiveIterator.OfInt?
  //
  // TODO: any advantage to doing this with a <code>record</code>?
  //
  // TODO: What about zero-length sequences? 
  // Is there a better choice than just null?

  private static final class Words {
    private final int _value;
    private final int value () { return _value; }
    // simplicity, not efficiency, for now.
    private static final long uvalue (final Words w) { 
      if (null==w) { return 0L; }
      return unsigned(w.value()); }
    
    /** may be <code>null</code>, indicating end of sequence. */
    private final Words _rest;
    private final Words rest () { return _rest; }
    
    /** 
     * @param r may be <code>null</code>, 
     * indicating an empty, zero-length sequence. 
     */
    private Words (final int v, final Words r) {  
      _value = v; _rest = r; }
    
    // TODO: skip hashcode and equals until needed
    
    /** 
     * @param r may be <code>null</code>, 
     * indicating an empty, zero-length sequence. 
     */
    private static final Words prepend (final int v,
                                              final Words r) {
      return new Words(v,r); }
    
    /** Will return <code>null</code> if <code>s</code> is 
     * <code>null</code>.
     */
    private static final Words reverse (final Words s) {
      if (null == s) { return null; }
      Words out = prepend(s.value(),null);
      Words in = s.rest();
      while (null != in) {
        out = prepend(in.value(),out);
        in = in.rest(); }
      return out; }
  }
  //--------------------------------------------------------------
  // fields
  //--------------------------------------------------------------
  /** Least significant word first.
   * This sequence is never modified.
   */

  private final Words _words;
  private final Words words () { return _words; }

  /** Singleton. */
  public static final UnboundedNatural ZERO = 
    new UnboundedNatural(null); 

  //--------------------------------------------------------------
  // ordering
  //--------------------------------------------------------------

//  @Override
//  public final int compareTo (final UnboundedNatural u) {
//    final int b0 = hiBit();
//    final int b1 = u.hiBit();
//    if (b0<b1) { return -1; }
//    if (b0>b1) { return 1; }
//    int i = hiInt()-1;
//    for (;i>=0;i--) {
//      final long u0i = uword(i);
//      final long u1i = u.uword(i);
//      if (u0i<u1i) { return -1; }
//      if (u0i>u1i) { return 1; } }
//    return 0; }

  //--------------------------------------------------------------
  // monoid operation
  //--------------------------------------------------------------

  public final UnboundedNatural add (final UnboundedNatural u) {
    Words tt = words();
    Words uu = u.words();
    Words vv = null;
    long sum = 0L;
    while ((null!=tt)||(null!=uu)) {
      sum += Words.uvalue(tt) + Words.uvalue(uu);
      vv = Words.prepend((int) sum,vv); 
      sum = hiWord(sum);}
    if (0L!=sum) { vv = Words.prepend(1,vv); } 
    return new UnboundedNatural(Words.reverse(vv)); }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

//  @Override
//  public final int hashCode () { 
//    int hashCode = 0;
//    for (int i=0; i<hiInt(); i++) {
//      hashCode = ((31 * hashCode) + _words[i]); }
//    return hashCode; }
//
//  @Override
//  public final boolean equals (final Object x) {
//    if (x==this) { return true; }
//    if (!(x instanceof UnboundedNatural)) { return false; }
//    final UnboundedNatural u = (UnboundedNatural) x;
//    final int nt = hiInt();
//    if (nt!=u.hiInt()) { return false; }
//    for (int i=0; i<nt; i++) {
//      if (_words[i]!=u._words[i]) { return false; } }
//    return true; }

  // might easily exceed maximum string length
//  public final String toHexString () {
//    final StringBuilder b = new StringBuilder("");
//    final int n = hiInt()-1;
//    if (0>n) { b.append('0'); }
//    else {
//      b.append(String.format("%x",Long.valueOf(uword(n))));
//      for (int i=n-1;i>=0;i--) {
//        //b.append(" ");
//        b.append(String.format("%08x",Long.valueOf(uword(i)))); } }
//    return b.toString(); }
//
//  /** hex string. */
//  @Override
//  public final String toString () { return toHexString(); }

  //--------------------------------------------------------------
  // Is this characteristic of most inputs?

  public static final Generator
  fromNaturalGenerator (final UniformRandomProvider urp) {
   return new GeneratorBase ("fromNaturalGenerator") {
      private final Generator g = Natural.generator(urp);
      @Override
      public Object next () {
        return valueOf((Natural) g.next()); } }; }

  /** Intended primarily for testing. <b>
   * Generate enough bytes to at least cover the range of
   * <code>double</code> values.
   */

  public static final Generator
  generator (final UniformRandomProvider urp)  {
    return fromNaturalGenerator(urp); }

  //--------------------------------------------------------------
  // construction
  //-------------------------------------------------------------
  /** UNSAFE: doesn't copy <code>words</code> or check 
   * <code>loInt</code> or <code>hiInt</code.
   */

  private UnboundedNatural (final Words words) { 
    _words = words; }

  public static final UnboundedNatural valueOf (final Natural u) {
    final int n = u.hiInt();
    Words r = null;
    for (int i=0;i<n;i++) { r = Words.prepend(u.word(i),r); }
    return new UnboundedNatural(Words.reverse(r)); }

//  //--------------------------------------------------------------
//  /** From a big endian {@code byte[]}, as produced by
//   * {@link BigInteger#toByteArray()}.
//   */
//
//  private static final UnboundedNatural fromBigEndianBytes (final byte[] a) {
//    final int nBytes = a.length;
//    int keep = 0;
//    while ((keep<nBytes) && (0==a[keep])) { keep++; }
//    final int nInts = ((nBytes-keep) + 3) >>> 2;
//      final int[] result = new int[nInts];
//      int b = nBytes-1;
//      for (int i = nInts - 1; i >= 0; i--) {
//        result[i] = a[b--] & 0xff;
//        final int bytesRemaining = (b - keep) + 1;
//        final int bytesToTransfer = Math.min(3,bytesRemaining);
//        for (int j = 8; j <= (bytesToTransfer << 3); j += 8) {
//          result[i] |= ((a[b--] & 0xff) << j); } }
//      Ints.reverse(result);
//      return make(result); }
//
//  public static final UnboundedNatural valueOf (final BigInteger u) {
//    assert 0<=u.signum();
//    return fromBigEndianBytes(u.toByteArray()); }
//
  //-------------------------------------------------------------

//  public static final UnboundedNatural valueOf (final String s,
//                                                final int radix) {
//    return make(Ints.littleEndian(s,radix)); }
//
//  public static final UnboundedNatural valueOf (final String s) {
//    return valueOf(s,0x10); }

//  /** <code>0L<=u</code>. */
//
//  public static final UnboundedNatural valueOf (final long u) {
//    assert 0L<=u;
//    //if (0L==u) { return zero(); }
//    final int lo = (int) u;
//    final int hi = (int) hiWord(u);
//    if (0==hi) { 
//      if (0==lo) { return new UnboundedNatural(new int[0]); }
//      return new UnboundedNatural(new int[] {lo}); }
//    return new UnboundedNatural(new int[] { lo,hi }); }
//
//  public static final UnboundedNatural valueOf (final long u,
//                                                final int upShift) {
//    assert 0<=u;
//    assert 0<=upShift;
//    assert 0<=u;
//    assert 0<=upShift;
//    final int iShift = (upShift>>>5);
//    final int bShift = (upShift&0x1f);
//    if (0==bShift) { 
//      final int[] vv = new int[iShift+2];
//      vv[iShift] = (int) u;
//      vv[iShift+1] = (int) hiWord(u);
//      return unsafe(vv); }
//    final long us = (u<<bShift);
//    final int vv0 = (int) us;
//    final int vv1 = (int) hiWord(us);
//    final int vv2 = (int) (u>>>(64-bShift)); 
//    if (0!=vv2) {
//      final int[] vv = new int[iShift+3];
//      vv[iShift] = vv0;
//      vv[iShift+1] = vv1;
//      vv[iShift+2] = vv2;
//      return new UnboundedNatural(vv); }
//    if (0!=vv1) { 
//      final int[] vv = new int[iShift+2];
//      vv[iShift] = vv0;
//      vv[iShift+1] = vv1;
//      return new UnboundedNatural(vv); }
//    if (0!=vv0) { 
//      final int[] vv = new int[iShift+1];
//      vv[iShift] = vv0;
//      return new UnboundedNatural(vv); }
//    return ZERO; }
//
  //--------------------------------------------------------------
}
//--------------------------------------------------------------
