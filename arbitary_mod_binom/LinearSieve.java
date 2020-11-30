package arbitary_mod_binom;

/**
 * @see https://37zigen.com/linear-sieve/
 */
class LinearSieve {
    /**
     * 線形篩. O(N) で N 以下の素数を列挙する. また, 副作用として 0,...,N に対する最小の素因数が求まる.
     * @param N 素数の上限 (閉区間)
     * @return 素数のリストおよび最小の素因数を求めた配列を保持する {@link LinearSieve.Result}.
     */
    public static Result sieve(final int N) {
        int[] minPrimeFactor = new int[N + 1];
        int[] primes = new int[N + 1];
        int primeNum = 0;
        for (int div = 2; div <= N; div++) {
            if (minPrimeFactor[div] == 0) {
                minPrimeFactor[div] = div;
                primes[primeNum++] = div;
            }
            int maxPrime = Math.min(N / div, minPrimeFactor[div]);
            for (int i = 0; i < primeNum; i++) {
                int prime = primes[i];
                if (prime > maxPrime) break;
                minPrimeFactor[prime * div] = prime;
            }
        }
        return new Result(minPrimeFactor, java.util.Arrays.copyOf(primes, primeNum));
    }

    public static class Result {
        private final int[] minPrimeFactor;
        private final int[] primes;
        Result(int[] minPrimeFactor, int[] primes) {
            this.minPrimeFactor = minPrimeFactor;
            this.primes = primes;
        }
        /**
         * @return 0,1,...,N に対して最小の素因数 (存在しない場合は 0) を求めた配列
         */
        public int[] minPrimeFactor() {
            return minPrimeFactor;
        }
        /**
         * @return N 以下の素数を昇順に並べた配列
         */
        public int[] primes() {
            return primes;
        }
        /**
         * @return N 以下の素数の個数
         */
        public int primeNum() {
            return primes.length;
        }
    }
}
