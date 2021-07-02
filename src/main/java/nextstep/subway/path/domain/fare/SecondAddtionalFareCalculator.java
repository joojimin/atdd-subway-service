package nextstep.subway.path.domain.fare;

import nextstep.subway.path.common.FareOfDistanceUtils;

public class SecondAddtionalFareCalculator implements FareCalculator {

    private static final int MIN_DISTANCE = 50;
    private static final int INTERVAL_OF_DISTANCE = 8;
    private static final int INTERVAL_RATE = 100;

    @Override
    public int calculate(int distance) {
        int targetDistance = distance - MIN_DISTANCE;
        if (targetDistance > 0) {
            return FareOfDistanceUtils.getIntervalRateOfDistance(targetDistance, INTERVAL_OF_DISTANCE, INTERVAL_RATE);
        }
        return 0;
    }
}
