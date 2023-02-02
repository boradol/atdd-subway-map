package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse create(LineRequest request) {
        Station upStation = stationService.findOneById(request.getUpStationId());
        Station downStation = stationService.findOneById(request.getDownStationId());
        Line line = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        line.addSection(upStation, downStation, request.getDistance());
        return LineResponse.of(line);
    }
    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOneById(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow();
    }

    @Transactional
    public LineResponse updateById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow();
        return LineResponse.of(line.updateLine(lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(final Long id, final SectionRequest sectionRequest) {
        final Line line = getLine(id);
        final Station upStation = stationService.findOneById(sectionRequest.getUpStationId());
        final Station downStation = stationService.findOneById(sectionRequest.getDownStationId());

        line.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    @Transactional
    public void deleteSection(final Long id, final Long stationId) {
        final Line line = getLine(id);
        final Station station = stationService.findOneById(stationId);
        line.removeSection(station);
    }

    private Line getLine(final long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);
    }
}
