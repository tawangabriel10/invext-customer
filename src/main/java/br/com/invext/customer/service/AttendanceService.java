package br.com.invext.customer.service;

import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_FINISHED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_NOT_FOUND;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_STARTED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_UNABLE;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT;

import br.com.invext.customer.domain.AttendantDTO;
import br.com.invext.customer.domain.CustomerRequestDTO;
import br.com.invext.customer.domain.FinishRequestDTO;
import br.com.invext.customer.domain.IRequest;
import br.com.invext.customer.domain.RequestDTO;
import br.com.invext.customer.domain.ResponseDTO;
import br.com.invext.customer.domain.enumerated.SquadEnum;
import br.com.invext.customer.exception.BusinessException;
import br.com.invext.customer.mapper.AttendanceMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    private List<AttendantDTO> attendants = createAttendants();
    private List<RequestDTO> requestsWaiting = new ArrayList<>();

    @Autowired
    private AttendanceMapper mapper;

    private List<AttendantDTO> createAttendants() {
        AttendantDTO attendanceCard = AttendantDTO.builder()
            .name("Atendente 01")
            .squad(SquadEnum.CARDS)
            .requests(new ArrayList<>())
            .build();

        AttendantDTO attendantLoan = AttendantDTO.builder()
            .name("Atendente 02")
            .squad(SquadEnum.LOANS)
            .requests(new ArrayList<>())
            .build();

        AttendantDTO attendantOthers = AttendantDTO.builder()
            .name("Atendente 03")
            .squad(SquadEnum.OTHERS)
            .requests(new ArrayList<>())
            .build();

        return Arrays.asList(attendanceCard, attendantLoan, attendantOthers);
    }

    public ResponseDTO direct(@NonNull CustomerRequestDTO requestDTO) {
        if (this.validateSubject(SquadEnum.CARDS, requestDTO)) {
            return validateAndDirect(SquadEnum.CARDS, requestDTO);
        } else if (SquadEnum.LOANS.getSubject().equalsIgnoreCase(requestDTO.getSubject())) {
            return validateAndDirect(SquadEnum.LOANS, requestDTO);
        } else {
            return validateAndDirect(SquadEnum.OTHERS, requestDTO);
        }
    }

    private ResponseDTO validateAndDirect(SquadEnum squadEnum, CustomerRequestDTO requestDTO) {
        var attendantDTO = attendants.stream()
            .filter(attendant -> squadEnum.equals(attendant.getSquad()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MESSAGE_ATTENDANCE_UNABLE));

        if (attendantDTO.getRequests().size() >= 3) {
            requestsWaiting.add(mapper.toRequest(requestDTO));
            throw new BusinessException(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT);
        } else if (requestsWaiting.stream().anyMatch(request -> this.validateSubject(squadEnum, request))) {
            var requestWaitingDTO = requestsWaiting.stream()
                .filter(request -> this.validateSubject(squadEnum, request))
                .findFirst().get();
            attendantDTO.getRequests().add(requestWaitingDTO);
            requestsWaiting.remove(requestWaitingDTO);
            requestsWaiting.add(mapper.toRequest(requestDTO));
            throw new BusinessException(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT);
        } else {
            attendantDTO.getRequests().add(mapper.toRequest(requestDTO));
            return ResponseDTO.builder().response(MESSAGE_ATTENDANCE_STARTED).build();
        }
    }

    private boolean validateSubject(SquadEnum squadEnum, IRequest requestDTO) {
        return squadEnum.getSubject().equalsIgnoreCase(requestDTO.getSubject());
    }

    public ResponseDTO finish(@NonNull FinishRequestDTO requestDTO) {
        var attendantDTO = attendants.stream()
            .filter(attendant -> attendant.getSquad().equals(requestDTO.getType()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MESSAGE_ATTENDANCE_UNABLE));

        var request = attendantDTO.getRequests().stream()
            .filter(r -> r.getName().equalsIgnoreCase(requestDTO.getCustomerName()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MESSAGE_ATTENDANCE_NOT_FOUND));

        attendantDTO.getRequests().remove(request);
        return ResponseDTO.builder().response(MESSAGE_ATTENDANCE_FINISHED).build();
    }
}
