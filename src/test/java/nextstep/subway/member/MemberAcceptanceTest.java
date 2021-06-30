package nextstep.subway.member;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.AuthAcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;

    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> findResponse = 회원_정보_조회_요청(createResponse);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);
        // then
        회원_삭제됨(deleteResponse);
    }

    @DisplayName("나의 정보를 관리한다.")
    @Test
    void manageMyInfo() {
        // Given
        MemberRequest updateMemberRequest = new MemberRequest("joojimin@naver.com", "123123", 30);
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> 로그인_요청_결과 = AuthAcceptanceTest.로그인_요청(EMAIL, PASSWORD);
        // then
        TokenResponse 토큰 = AuthAcceptanceTest.로그인_성공(로그인_요청_결과);

        // when
        ExtractableResponse<Response> 내정보_조회_결과 = 내정보_조회_요청(토큰.getAccessToken());
        // then
        내정보_조회_성공(내정보_조회_결과);

        // when
        ExtractableResponse<Response> 내정보_업데이트_결과 = 내정보_업데이트_요청(토큰.getAccessToken(), updateMemberRequest);
        // then
        내정보_업데이트_성공(내정보_업데이트_결과, updateMemberRequest);

        // when
        ExtractableResponse<Response> 내정보_삭제_결과 = 내정보_삭제_요청(토큰.getAccessToken());
        // then
        내정보_삭제_성공(내정보_삭제_결과);

        // when
        ExtractableResponse<Response> 삭제후_로그인_결과 = AuthAcceptanceTest.로그인_요청("joojimin@naver.com", "123123");
        // then
        AuthAcceptanceTest.로그인_실패(삭제후_로그인_결과);

    }

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(memberRequest)
            .when().post("/members")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(uri)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(memberRequest)
            .when().put(uri)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured
            .given().log().all()
            .when().delete(uri)
            .then().log().all()
            .extract();
    }

    public static void 회원_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 회원_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 회원_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 내정보_조회_요청(String accessToken) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/members/me")
            .then().log().all().extract();
    }

    public static MemberResponse 내정보_조회_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response.as(MemberResponse.class);
    }

    public static void 내정보_조회_실패(final ExtractableResponse<Response> response) {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 내정보_업데이트_요청(final String accessToken, final MemberRequest request) {
        // when
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/members/me")
            .then().log().all().extract();
    }

    public static void 내정보_업데이트_성공(final ExtractableResponse<Response> response, final MemberRequest request) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        MemberResponse 업데이트된_멤버_정보 = response.as(MemberResponse.class);
        assertAll(() -> {
            assertThat(업데이트된_멤버_정보.getEmail()).isEqualTo(request.getEmail());
            assertThat(업데이트된_멤버_정보.getAge()).isEqualTo(request.getAge());
        });
    }

    public static ExtractableResponse<Response> 내정보_삭제_요청(final String accessToken) {
        // when
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/members/me")
                .then().log().all().extract();
    }

    public static void 내정보_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
