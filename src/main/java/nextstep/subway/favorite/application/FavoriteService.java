package nextstep.subway.favorite.application;

import nextstep.subway.exception.CustomException;
import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.FavoriteRepository;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.CustomExceptionMessage.NOT_FOUND_FAVORITE;
import static nextstep.subway.exception.CustomExceptionMessage.NOT_OWNER_TO_FAVORITE;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StationService stationService;

    public FavoriteService(final FavoriteRepository favoriteRepository, final StationService stationService) {
        this.favoriteRepository = favoriteRepository;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> findFavorites(final Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMemberId(memberId);
        return convertToFavoriteResponse(favorites);
    }

    public FavoriteResponse saveFavorite(final Long memberId, final FavoriteRequest request) {
        Station sourceStation = stationService.findStationById(request.getSource());
        Station targetStation = stationService.findStationById(request.getTarget());
        Favorite persistFavorite = saveFavoriteToRepository(sourceStation, targetStation, memberId);
        return FavoriteResponse.of(persistFavorite);
    }

    public void removeFavorite(final Long memberId, final Long favoriteId) {
        Favorite favorite = findFavoriteById(favoriteId);
        if (!favorite.isOwner(memberId)) {
            throw new CustomException(NOT_OWNER_TO_FAVORITE);
        }
        favoriteRepository.deleteById(favoriteId);
    }

    private Favorite findFavoriteById(final Long favoriteId) {
        return favoriteRepository.findById(favoriteId)
                                 .orElseThrow(() -> new CustomException(NOT_FOUND_FAVORITE));
    }

    private List<FavoriteResponse> convertToFavoriteResponse(List<Favorite> favorites) {
        return favorites.stream()
                        .map(FavoriteResponse::of)
                        .collect(Collectors.toList());
    }

    private Favorite saveFavoriteToRepository(final Station source, final Station target, final Long memberId) {
        return favoriteRepository.save(new Favorite(source.getId(), target.getId(), memberId));
    }
}
