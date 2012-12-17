package org.jchecksum;

import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Benchmark {
    private static final List<String> FILENAMES = Arrays.asList("1K", "2K", "4K", "8K", "16K", "32K", "64K", "128K",
            "256K", "512K", "1M", "2M", "4M", "8M", "16M", "32M", "64M", "128M", "256M", "512M", "1G");

    private static final int RUNS = 200;

    private static final int MIN_BUFFER_SIZE = 128 * 1024;

    private static final int MAX_BUFFER_SIZE = 512 * 1024;

    public static void main(String... args) {
        try {
            new Benchmark().benchmark();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void benchmark() throws IOException, NoSuchAlgorithmException {
        final Map<Integer, Long> totals = new HashMap<>();
        for (final String filename : FILENAMES) {
            final List<Times> times = benchmarkFile(filename);
            Collections.sort(times);
            System.out.println(String.format("Best result for %s file : %s", filename, times.get(0)));
            displayTimes(times);

            for (final Times time : times) {
                Long total = totals.get(time.getBufferSize());
                if (total == null) {
                    totals.put(time.getBufferSize(), time.getTotal());
                }
                else {
                    total += time.getTotal();
                }
            }
        }

        for (Map.Entry<Integer, Long> entry : totals.entrySet()) {
            System.out.println(String.format("grand total for %d buffer size =>  %d ms.", entry.getKey(), entry.getValue()));
        }
    }

    private void displayTimes(List<Times> times) {
        for (final Times time : times) {
            System.out.println(time);
        }
    }

    private List<Times> benchmarkFile(String filename) throws IOException, NoSuchAlgorithmException {
        final List<Times> result = new ArrayList<>();

        for (int bufferSize = MIN_BUFFER_SIZE; bufferSize < MAX_BUFFER_SIZE; bufferSize *= 2) {
            result.add(benchBufferSize(filename, bufferSize));
        }

        return result;
    }

    private Times benchBufferSize(String filename, int bufferSize) throws IOException, NoSuchAlgorithmException {
        System.out.println(String.format("benchmarking %s file with %d buffer size ...", filename, bufferSize));
        final List<Long> elapsedTimes = new ArrayList<>();

        for (int run = 0; run < RUNS; run++) {
            elapsedTimes.add(computeSha1(filename, bufferSize));
        }

        return new Times(bufferSize, elapsedTimes);
    }

    private long computeSha1(String filename, final int bufferSize) throws IOException, NoSuchAlgorithmException {
        final Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        new JChecksum(){
            @Override
            public int withBufferSizeOf() {
                return bufferSize;
            }
        }.sha1(filename);

        return stopwatch.elapsedMillis();
    }
}
