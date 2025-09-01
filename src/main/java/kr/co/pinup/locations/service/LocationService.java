package kr.co.pinup.locations.service;

import jakarta.transaction.Transactional;
import kr.co.pinup.api.kakao.model.dto.KakaoAddressDto;
import kr.co.pinup.api.kakao.service.KakaoMapService;
import kr.co.pinup.locations.Location;
import kr.co.pinup.locations.exception.LocationNotFoundException;
import kr.co.pinup.locations.model.dto.CreateLocationRequest;
import kr.co.pinup.locations.model.dto.LocationResponse;
import kr.co.pinup.locations.reposiotry.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LocationService  {

    private final LocationRepository locationRepository;
    private final KakaoMapService kakaoMapService;

    public LocationResponse createLocation(CreateLocationRequest request) {
        KakaoAddressDto coord = kakaoMapService.getLocationFromAddress(request.address());

        Location location = Location.builder()
                .name(request.name())
                .zoneCode(request.zoneCode())
                .state(request.state())
                .district(request.district())
                .address(request.address())
                .addressDetail(request.addressDetail())
                .latitude(Double.valueOf(coord.latitude()))
                .longitude(Double.valueOf(coord.longitude()))
                .build();

        return LocationResponse.from(locationRepository.save(location));
    }


    public LocationResponse getLocationId(Long id) {

        Location location = locationRepository.findById(id)
                    .orElseThrow(LocationNotFoundException::new);

            return LocationResponse.from(location);

    }

}


