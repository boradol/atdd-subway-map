package subway.line;

import org.springframework.stereotype.Service;
import subway.station.Station;
import subway.station.StationRepository;

import javax.transaction.Transactional;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow();
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow();
        Line line = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return LineResponse.createLineResponse(line);
    }
}
