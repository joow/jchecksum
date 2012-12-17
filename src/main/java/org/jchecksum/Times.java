package org.jchecksum;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;

import java.util.List;

public class Times implements Comparable<Times> {
    private final int bufferSize;

    private final long min;

    private final long max;

    private final long total;

    private final long avg;

    public Times(int bufferSize, List<Long> times) {
        this.bufferSize = bufferSize;
        min = Ordering.natural().min(times);
        max = Ordering.natural().max(times);
        total = sum(times, 0);
        avg = total / times.size();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    private long sum(List<Long> times, long acc) {
        if (times.isEmpty()) {
            return acc;
        }

        return sum(times.subList(1, times.size()), acc + times.get(0));
    }

    @Override
    public int compareTo(Times times) {
        long compare = total - times.total;

        if (compare != 0) {
            return (int) compare;
        }

        compare = avg - times.avg;

        if (compare != 0) {
            return (int) compare;
        }

        compare = min - times.min;

        if (compare != 0) {
            return (int) compare;
        }

        return (int) (times.max - max);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("bufferSize", bufferSize).add("min", min).add("max", max)
                .add("total", total).add("avg", avg).toString();
    }

    public long getTotal() {
        return total;
    }
}
