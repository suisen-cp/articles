package arbitary_mod_binom;

/**
 * @task https://atcoder.jp/contests/arc012/tasks/arc012_4
 * @submission https://atcoder.jp/contests/arc012/submissions/18497804
 */
public class ArbitaryModBinom3 {
    private static final int MAX_PRIME_FACTOR_NUM = 9;

    /**
     * Calculates [C(N,0),C(N,1),...,C(N,N)] in O(N(loglogM)(logloglogM)) time.
     * @param N <= 10^7
     * @param M <= 10^9
     * @return long[]{C(N,0),C(N,1),...,C(N,N)}
     */
    public static long[] solve(final int N, final long M) {
        // M=1 は不都合なので場合分けしておく
        if (M == 1) return new long[N + 1];
        // 後の inv[1]=1 が配列外参照にならないために, N=0 も別で処理する
        if (N == 0) return new long[]{1};

        // modulo 演算のクラス
        ModArithmetic ma = new ModArithmeticBarrett(M);

        // 線形篩 (閉区間)
        LinearSieve.Result sieve = LinearSieve.sieve(N);

        // M の素因数の個数
        byte primeFactorCount = 0;
        // M の素因数のリスト
        int[] primeFactors = new int[MAX_PRIME_FACTOR_NUM];

        // commonPrimeFactor[i] = p_j が i を割り切るような j (存在しなければ -1)
        byte[] commonPrimeFactorOrd = new byte[N + 1];
        java.util.Arrays.fill(commonPrimeFactorOrd, (byte) -1);
        for (int prime : sieve.primes()) {
            if (M % prime == 0) {
                primeFactors[primeFactorCount] = prime;
                for (int i = prime; i <= N; i += prime) {
                    commonPrimeFactorOrd[i] = primeFactorCount;
                }
                primeFactorCount++;
            }
        }

        // 逆元の前計算 (参考 : https://37zigen.com/linear-sieve/)
        long[] inv = new long[N + 1];
        {
            int[] div = sieve.minPrimeFactor();
            inv[1] = 1;
            for (int i = 2; i <= N; i++) {
                if (div[i] == i) {
                    if (M % i != 0) inv[i] = ma.inv(i);
                } else {
                    int prime = div[i];
                    inv[i] = ma.mul(inv[prime], inv[i / prime]);
                }
            }
        }

        // 累乗の前計算
        long[][] pow = new long[primeFactorCount][];
        for (int i = 0; i < primeFactorCount; i++) {
            int prime = primeFactors[i];
            int maxIndex = N / (prime - 1);
            long[] powi = pow[i] = new long[maxIndex + 1];
            powi[0] = 1;
            for (int j = 1; j <= maxIndex; j++) {
                powi[j] = ma.mul(powi[j - 1], prime);
            }
        }

        // 共通素因数の指数
        int[] index = new int[primeFactorCount];

        // 共通素因数冪の積を管理するセグメント木
        ModProdSegTree seg = new ModProdSegTree(primeFactorCount, ma);

        // 二項係数の計算結果 (binom[i]=C(N,i))
        long[] binom = new long[N + 1];
        binom[0] = 1;

        // M と互いに素な部分
        long coprimeProd = 1;
        for (int i = 1; i <= N >> 1; i++) {
            int num = N - i + 1;
            for (byte ord = commonPrimeFactorOrd[num]; ord >= 0; ord = commonPrimeFactorOrd[num]) {
                int primeFactor = primeFactors[ord];
                int count = 0;
                do {
                    num /= primeFactor;
                    count++;
                } while (num % primeFactor == 0);
                seg.set(ord, pow[ord][index[ord] += count]);
            }
            coprimeProd = ma.mul(coprimeProd, num);

            int den = i;
            for (int ord = commonPrimeFactorOrd[den]; ord >= 0; ord = commonPrimeFactorOrd[den]) {
                int primeFactor = primeFactors[ord];
                int count = 0;
                do {
                    den /= primeFactor;
                    count++;
                } while (den % primeFactor == 0);
                seg.set(ord, pow[ord][index[ord] -= count]);
            }
            coprimeProd = ma.mul(coprimeProd, inv[den]);

            long commonProd = seg.prodAll();

            binom[i] = ma.mul(coprimeProd, commonProd);
        }

        for (int i = N; i > N >> 1; i--) {
            binom[i] = binom[N - i];
        }

        return binom;
    }
}
