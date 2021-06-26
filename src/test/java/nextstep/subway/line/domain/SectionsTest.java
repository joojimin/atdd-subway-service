package nextstep.subway.line.domain;

import nextstep.subway.exception.CustomException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.exception.CustomExceptionMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

	Station 강남역;
	Station 역삼역;
	Station 구디역;
	Station 신림역;

	Line 이호선;
	Sections 이호선_구간_집합;

	@BeforeEach
	void setUp() {
		// given
		강남역 = new Station("강남역");
		역삼역 = new Station("역삼역");
		구디역 = new Station("구디역");
		신림역 = new Station("신림역");

		이호선 = new Line();
		이호선_구간_집합 = new Sections();
		이호선_구간_집합.addSection(이호선, 강남역, 역삼역, 20);
		이호선_구간_집합.addSection(이호선, 강남역, 구디역, 10);
		이호선_구간_집합.addSection(이호선, 역삼역, 신림역, 10);
	}

	@DisplayName("구간 집합에 소속된 모든 역 조회 테스트")
	@Test
	void getStationsTest() {
		// when
		assertThat(이호선_구간_집합.getStations()) // 강남, 구디, 역삼, 신림
			.isNotEmpty()
			.containsExactly(강남역, 구디역, 역삼역, 신림역);
	}

	@DisplayName("등록된 구간을 재등록 요청시 실패 테스트")
	@Test
	void addDuplicateSectionTest() {
		// when
		assertThatThrownBy(() -> 이호선_구간_집합.addSection(이호선, 강남역, 역삼역, 20))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(EXIST_ALL_STATION_IN_SECTIONS.getMessage());
	}

	@DisplayName("연결할 지하철 역이 없는 구간 등록 테스트")
	@Test
	void addNotExistAllStationsTest() {
		// given
		Station 홍대역 = new Station("홍대역");
		Station 잠실역 = new Station("잠실역");

		assertThatThrownBy(() -> 이호선_구간_집합.addSection(이호선, 홍대역, 잠실역, 10))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(NOT_EXIST_ALL_STATION_IN_SECTIONS.getMessage());
	}

	@DisplayName("구간에 등록된 지하철 역 제거 테스트")
	@Test
	void removeSectionTest() {
		// when
		이호선_구간_집합.removeSection(이호선, 강남역);

		// then
		assertThat(이호선_구간_집합.getStations())
			.isNotEmpty()
			.containsExactly(구디역, 역삼역, 신림역);
	}

	@DisplayName("한 개 뿐인 구간 삭제 테스트")
	@Test
	void removeOnlyOneSectionTest() {
		// given
		Station 신대방역 = new Station("신대방역");
		Station 사당역 = new Station("사당역");

		Line 새로운_이호선 = new Line();
		Sections 새로운_이호선_구간_집합 = new Sections();
		새로운_이호선_구간_집합.addSection(새로운_이호선, 신대방역, 사당역, 20);

		// when
		assertThatThrownBy(() -> 새로운_이호선_구간_집합.removeSection(새로운_이호선, 신대방역))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(IMPOSSIBLE_MIN_SECTION_SIZE.getMessage());
	}
}
