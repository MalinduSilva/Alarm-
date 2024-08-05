package com.malindu.alarm15.models;

public enum VibratePattern {

    PATTERN_ONE(
            new long[] { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 1000 },
            new int[] { 0, 64, 128, 192, 255, 255, 192, 128, 64, 0, 0 },
            1
    ),
    PATTERN_TWO(
            new long[] { 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 2000 },
            new int[] { 0, 32, 64, 96, 128, 128, 96, 64, 32, 0, 0 },
            0
    );

    private final long[] timings;
    private final int[] amplitudes;
    private final int repeat;
    public long[] getTimings() { return timings; }
    public int[] getAmplitudes() { return amplitudes; }
    public int getRepeat() { return repeat; }

    /**
     * Vibration patterns.
     * @param timings array of time-axis values
     * @param amplitudes array of amplitude-axis values
     * @param repeat position to start repetition. -1 means no repeat
     */
    VibratePattern(long[] timings, int[] amplitudes, int repeat) {
        this.timings = timings;
        this.amplitudes = amplitudes;
        this.repeat = repeat;
    }
}
