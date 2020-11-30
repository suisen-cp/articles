package arbitary_mod_binom;

/**
 * @author https://atcoder.jp/users/suisen
 * 
 * @article https://suisen-kyopro.hatenablog.com/entry/2020/11/21/071007
 * 
 * @verify_task https://atcoder.jp/contests/arc012/tasks/arc012_4
 * @verify_submission https://atcoder.jp/contests/arc012/submissions/18497787
 */
public class ArbitaryModBinom1 {
    /**
     * Calculates [C(N,0),C(N,1),...,C(N,N)] in O(N(logN)(loglogN)) time.
     * @param N <= 10^6
     * @param M <= 10^9
     * @return long[]{C(N,0),C(N,1),...,C(N,N)}
     */
    public static long[] solve(final int N, final long M) {
        // modulo 演算のクラス
        final ModArithmetic MA = new ModArithmeticBarrett(M);

        // 線形篩 (閉区間)
        LinearSieve.Result sieve = LinearSieve.sieve(N);

        // divisor[i] は i を割り切る素数
        int[] divisor = sieve.minPrimeFactor();

        // N 以下の素数の昇順リスト
        int[] primes = sieve.primes();

        // N 以下の素数の個数
        final int primeNum = sieve.primeNum();

        // 素数 p に対して, primes[primeOrd[p]] = p.
        // つまり, 何番目に小さい素数か
        int[] primeOrd = new int[N + 1];

        for (int i = 0; i < primeNum; i++) {
            primeOrd[primes[i]] = i;
        }

        // 素因数分解における指数
        int[] primeCount = new int[primeNum];

        // 素因数分解形から積を復元するセグメント木
        // prodSegTree[i] = primes[i] ^ primeCount[i] (累乗)
        ModProdSegTree seg = new ModProdSegTree(primeNum, MA);

        // 二項係数 binom[i] = C(N, i)
        long[] binom = new long[N + 1];
        binom[0] = 1;

        for (int i = 1; i <= N >> 1; i++) {
            // N - i + 1 を素因数分解
            for (int mul = N - i + 1; mul > 1;) {
                // 素因数 と 指数
                int prime = divisor[mul];
                int count = 0;
                do {
                    mul /= prime;
                    count++;
                } while (mul % prime == 0);
                int ord = primeOrd[prime];
                primeCount[ord] += count;
                seg.set(ord, MA.pow(prime, primeCount[ord]));
            }
            // i を素因数分解
            for (int div = i; div > 1;) {
                int prime = divisor[div];
                int count = 0;
                do {
                    div /= prime;
                    count++;
                } while (div % prime == 0);
                int ord = primeOrd[prime];
                primeCount[ord] -= count;
                seg.set(ord, MA.pow(prime, primeCount[ord]));
            }
            binom[i] = seg.prodAll();
        }

        for (int i = N; i > N >> 1; i--) {
            binom[i] = binom[N - i];
        }

        return binom;
    }
}
