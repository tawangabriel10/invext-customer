package br.com.invext.customer.service;

import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_STARTED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_UNABLE;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT;

import br.com.invext.customer.domain.AttendanceWaiting;
import br.com.invext.customer.domain.Attendants;
import br.com.invext.customer.domain.IRequest;
import br.com.invext.customer.domain.dto.CustomerRequestDTO;
import br.com.invext.customer.domain.dto.ResponseDTO;
import br.com.invext.customer.domain.enumerated.SquadEnum;
import br.com.invext.customer.domain.exception.BusinessException;
import br.com.invext.customer.mapper.AttendanceMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DirectAttendanceService {

    private final Attendants attendants;
    private final AttendanceWaiting attendanceWaiting;

    @Autowired
    private AttendanceMapper mapper;

    public DirectAttendanceService() {
        this.attendants = Attendants.getInstance();
        this.attendanceWaiting = AttendanceWaiting.getInstance();
    }

    public ResponseDTO direct(@NonNull CustomerRequestDTO requestDTO) {
        if (this.validateSubject(SquadEnum.CARDS, requestDTO)) {
            return validateAndDirect(SquadEnum.CARDS, requestDTO);
        } else if (this.validateSubject(SquadEnum.LOANS, requestDTO)) {
            return validateAndDirect(SquadEnum.LOANS, requestDTO);
        } else {
            return validateAndDirect(SquadEnum.OTHERS, requestDTO);
        }
    }

    private ResponseDTO validateAndDirect(SquadEnum squadEnum, CustomerRequestDTO requestDTO) {
        var attendantDTO = attendants.get().stream()
            .filter(attendant -> squadEnum.equals(attendant.getSquad()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MESSAGE_ATTENDANCE_UNABLE));

        if (attendantDTO.getRequests().size() >= 3) {
            this.attendanceWaiting.getRequests().add(mapper.toRequest(requestDTO));
            throw new BusinessException(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT);
        } else {
            var requestWaitingDTOOp = this.attendanceWaiting.getRequests().stream()
                .filter(request -> this.validateSubject(squadEnum, request))
                .findFirst();

            if (requestWaitingDTOOp.isPresent()) {
                attendantDTO.getRequests().add(requestWaitingDTOOp.get());
                this.attendanceWaiting.getRequests().remove(requestWaitingDTOOp.get());
                this.attendanceWaiting.getRequests().add(mapper.toRequest(requestDTO));
                throw new BusinessException(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT);
            } else{
                attendantDTO.getRequests().add(mapper.toRequest(requestDTO));
                return ResponseDTO.builder().response(MESSAGE_ATTENDANCE_STARTED).build();
            }
        }
    }

    private boolean validateSubject(SquadEnum squadEnum, IRequest requestDTO) {
        return squadEnum.getSubject().equalsIgnoreCase(requestDTO.getSubject());
    }

}
