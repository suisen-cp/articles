package arbitary_mod_binom;

class ModProdSegTree {
    static final long E = 1;

    final int N;
    final ModArithmetic ma;

    final long[] data;

    public ModProdSegTree(int n, ModArithmetic ma) {
        int k = 1;
        while (k < n) k <<= 1;
        this.N = k;
        this.ma = ma;
        this.data = new long[N << 1];
        java.util.Arrays.fill(data, E);
    }

    public void set(int p, long x) {
        data[p += N] = x;
        for (p >>= 1; p > 0; p >>= 1) {
            data[p] = ma.mul(data[p << 1 | 0], data[p << 1 | 1]);
        }
    }

    public long get(int p) {
        return data[p + N];
    }

    public long prod(int l, int r) {
        long prod = E;
        l += N; r += N;
        while (l < r) {
            if ((l & 1) == 1) prod = ma.mul(prod, data[l++]);
            if ((r & 1) == 1) prod = ma.mul(data[--r], prod);
            l >>= 1; r >>= 1;
        }
        return prod;
    }

    public long prodAll() {
        return data[1];
    }
}
