import java.util.*;
import java.util.function.IntConsumer;

public class Main {

    //Вариант №1. Наивный метод, перебор всех чисел
    public static long naiveMethod(int n) {
        if (n == 2) return 1;
        long count = 0;
        boolean isComposite = false;
        for (long i = 2L; i <= n - 1; i++) {
            isComposite = false;
            for (long j = 2L; j <= i; j++) {
                if ((i % j == 0) && (i != j)) {
                    isComposite = true;
                    break;
                }
            }
            if (!isComposite) count++;
        }
        return count;
    }

    //Вариант №2. Оптимизированный наивный метод
    public static long optimizeNaiveMethod(int n) {
        if (n == 1) return 0;
        if (n == 2) return 1;
        long count = 0;
        boolean isComposite = false;
        for (long i = 3L; i <= n - 1; i += 2L) {//Все четные числа кроме 2 - составные, анализируем только нечетные
            long k = (long) Math.sqrt(i);//Наименьший делитель составного числа не превосходит квадратного корня из этого составного числа
            isComposite = false;
            for (long j = 2L; j <= k; j++) {
                if ((i % j == 0) && (i != j)) {
                    isComposite = true;
                    break;
                }
            }
            if (!isComposite) count++;
        }
        return count + 1;//Добавляем число 2
    }

    //Вариант №3. Оптимизированный вариант №2, учитывающий утверждение, что наименьший делитель составного числа является простым числом
    //Медленее чем вариант №2...
    public static long optimizeNaiveMethodWithSimpleDivider(int n) {
        if (n == 1) return 0;
        if (n == 2) return 1;
        long count = 0;
        boolean isComposite = false;

        ArrayList<Long> simpleDivider = new ArrayList<>();//Массив простых делителей
        simpleDivider.add(3L);//Добавим первый простой делитель

        for (long i = 3L; i <= n - 1; i += 2L) {//Все четные числа кроме 2 - составные, анализируем только нечетные
            long k = (long) Math.sqrt(i);//Наименьший делитель составного числа не превосходит квадратного корня из этого составного числа
            long div;
            isComposite = false;
            long jMax = simpleDivider.size();
            for (long j = 0; j < jMax; j++) {
                div = simpleDivider.get((int) j);//приведение типа...
                if ((i % div == 0) && (div <= k)) {
                    isComposite = true;
                    break;
                }
            }
            if (!isComposite) {
                simpleDivider.add(i);
                count++;
            }
        }
        return count + 1;//Добавляем число 2
    }

    //Вариант №4. Решето Сундарама. Реализация алгоритма взята из статьи "Решето Сундарама" на русскоязычном Wiki.
    public static long sieveOfSundarama(int n) {
        if (n == 1) return 0;
        if (n == 2) return 1;
        //n < Integer.MAX_VALUE!!!
        byte[] simpleNum = new byte[n + 1];//Массив простых чисел. Числа == индексам, расположены в массиве начиная с индекса 1.
        long count = 0;

        for (int i = 1; i <= n; i++) {
            simpleNum[i] = 1;
        }
        int iMax = (int) (Math.sqrt(2 * n + 1) - 1) / 2;
        for (int i = 1; i <= iMax; i++) {
            int jMax = (n - i) / (2 * i + 1);//В статье ошибка: вместо (n - i) указано (n -1)
            for (int j = i; j <= jMax; j++) {
                if (2 * i * j + i + j < n)
                    simpleNum[2 * i * j + i + j] = 0;
            }
        }

        for (int i = 1; i < simpleNum.length; i++) {
            if (simpleNum[i] == 1) {
                if (2 * i + 1 < n) count++;
            }
        }
        return count + 1;//алгоритм работает только с нечетными числами, поэтому добавляем число 2.
    }

    //Вариант №5. Решето Эратосфена(базовое, есть варианты). Алгоритм взят из статьи "https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes". Реализация своя.
    public static long sieveOfEratosthenes(int n) {
        if (n == 1) return 0;
        if (n == 2) return 1;
        byte[] compositeNum = new byte[n + 1];
        int k;
        int count = 0;
        for (int i = 2; i < n; i++) {
            int jMax = n / i;
            for (int j = i; j <= jMax; j++) {
                if (compositeNum[k = i * j] != 1) compositeNum[k] = 1;
            }
        }

        for (int i = 1; i < compositeNum.length; i++)

            if (compositeNum[i] == 1) {
                count++;
            }

        return n - count - 1;
    }

    //Вариант №6. Решето Аткина. Реализация алгоритма взята из статьи "Решето Аткина" на сайте https://ru.wikibooks.org/wiki/Реализации_алгоритмов/Решето_Аткина
    public static long sieveOfAtkin(int limit) {
        BitSet sieve = new BitSet();
        // Предварительное просеивание
        for (long x2 = 1L, dx2 = 3L; x2 < limit; x2 += dx2, dx2 += 2L)
            for (long y2 = 1L, dy2 = 3L, n; y2 < limit; y2 += dy2, dy2 += 2L) {
                // n = 4x² + y²
                n = (x2 << 2L) + y2;
                if (n < limit && (n % 12L == 1L || n % 12L == 5L))
                    sieve.flip((int) n);
                // n = 3x² + y²
                n -= x2;
                if (n < limit && n % 12L == 7L)
                    sieve.flip((int) n);
                // n = 3x² - y² (при x > y)
                if (x2 > y2) {
                    n -= y2 << 1L;
                    if (n < limit && n % 12L == 11L)
                        sieve.flip((int) n);
                }
            }
        // Все числа, кратные квадратам, помечаются как составные
        int r = 5;
        for (long r2 = r * r, dr2 = (r << 1L) + 1L; r2 < limit; ++r, r2 += dr2, dr2 += 2L)
            if (sieve.get(r))
                for (long mr2 = r2; mr2 < limit; mr2 += r2)
                    sieve.set((int) mr2, false);
        // Числа 2 и 3 — заведомо простые
        if (limit > 2)
            sieve.set(2, true);
        if (limit > 3)
            sieve.set(3, true);
        return sieve.stream().count();
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        int n = 100000001;
        long t1;
        t1 = System.currentTimeMillis();
        //System.out.println(naiveMethod(n));
        System.out.println("Время выполнения \"Наивного метода\" составляет: " + (System.currentTimeMillis() - t1) / 1000.0);

        t1 = System.currentTimeMillis();
        //System.out.println(optimizeNaiveMethod(n));
        System.out.println("Время выполнения \"Оптимизированного наивного метода\" составляет: " + (System.currentTimeMillis() - t1) / 1000.0);

        t1 = System.currentTimeMillis();
        //System.out.println(optimizeNaiveMethodWithSimpleDivider(n));
        System.out.println("Время выполнения \"Оптимизированного наивного метода с учетом простых делителей\" составляет: " + (System.currentTimeMillis() - t1) / 1000.0);

        t1 = System.currentTimeMillis();
        System.out.println(sieveOfSundarama(n));
        System.out.println("Время выполнения алгоритма \"Решето Сундарама\" составляет: " + (System.currentTimeMillis() - t1) / 1000.0);

        t1 = System.currentTimeMillis();
        System.out.println(sieveOfEratosthenes(n));
        System.out.println("Время выполнения алгоритма \"Решето Эратосфена\" составляет: " + (System.currentTimeMillis() - t1) / 1000.0);

        t1 = System.currentTimeMillis();
        System.out.println(sieveOfAtkin(n));
        System.out.println("Время выполнения алгоритма \"Решето Аткина\" составляет: " + (System.currentTimeMillis() - t1) / 1000.0);
    }
}