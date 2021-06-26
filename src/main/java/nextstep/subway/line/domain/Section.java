package nextstep.subway.line.domain;

import nextstep.subway.exception.CustomException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

import static nextstep.subway.exception.CustomExceptionMessage.OVER_DISTANCE;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
        // empty;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void updateUpStation(Station station, int newDistance) {
        checkOverDistance(newDistance);
        this.upStation = station;
        this.distance -= newDistance;
    }

    public void updateDownStation(Station station, int newDistance) {
        checkOverDistance(newDistance);
        this.downStation = station;
        this.distance -= newDistance;
    }

    private void checkOverDistance(final int newDistance) {
        if (this.distance <= newDistance) {
            throw new CustomException(OVER_DISTANCE);
        }
    }

    public boolean isMatchUpStation(final Station station) {
        return this.upStation == station;
    }

    public boolean isMatchDownStation(final Station station) {
        return this.downStation == station;
    }
}
