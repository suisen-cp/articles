package arbitary_mod_binom;

abstract class ModArithmetic {
    public abstract long getMod();
    public abstract long mod(long a);
    public abstract long add(long a, long b);
    public abstract long sub(long a, long b);
    public abstract long mul(long a, long b);
    public final long div(long a, long b) {
        return mul(a, inv(b));
    }
    public final long inv(long a) {
        a = mod(a);
        long b = getMod();
        long u = 1, v = 0;
        while (b >= 1) {
            long t = a / b;
            a -= t * b;
            long tmp1 = a; a = b; b = tmp1;
            u -= t * v;
            long tmp2 = u; u = v; v = tmp2;
        }
        if (a != 1) throw new AssertionError();
        return mod(u);
    }
    public final long pow(long a, long b) {
        long pow = 1;
        for (a = mod(a); b > 0; b >>= 1, a = mul(a, a)) {
            if ((b & 1) == 1) {
                pow = mul(pow, a);
            }
        }
        return pow;
    }
}
