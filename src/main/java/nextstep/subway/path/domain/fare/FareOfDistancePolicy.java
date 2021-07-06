package nextstep.subway.path.domain.fare;

import nextstep.subway.path.domain.Fare;

public class FareOfDistancePolicy implements FareCalculator {

    @Override
    public int calculate(Fare fare) {
        return FareOfDistancePolicyFactory.calculate(fare.getDistance());
    }
}
