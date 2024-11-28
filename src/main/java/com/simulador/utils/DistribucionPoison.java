package com.simulador.utils;

import java.util.Random;

public class DistribucionPoison {
    private final double lambda;
    private final Random random;

    public DistribucionPoison(double lambda) {
        this.lambda = lambda;
        this.random = new Random();
    }

    public double nextInterArrivalTime() {
        return -Math.log(1.0 - random.nextDouble()) / lambda;
    }

    public double nextBoundedInterArrivalTime(double minTime, double maxTime) {
        double time = nextInterArrivalTime();
        return Math.min(Math.max(time, minTime), maxTime);
    }
}
