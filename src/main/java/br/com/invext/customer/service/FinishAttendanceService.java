package br.com.invext.customer.service;

import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_FINISHED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_NOT_FOUND;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_UNABLE;

import br.com.invext.customer.domain.AttendanceWaiting;
import br.com.invext.customer.domain.Attendants;
import br.com.invext.customer.domain.dto.AttendantDTO;
import br.com.invext.customer.domain.dto.FinishRequestDTO;
import br.com.invext.customer.domain.dto.ResponseDTO;
import br.com.invext.customer.domain.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FinishAttendanceService {

    private final Attendants attendants;
    private final AttendanceWaiting attendanceWaiting;

    public FinishAttendanceService() {
        this.attendants = Attendants.getInstance();
        this.attendanceWaiting = AttendanceWaiting.getInstance();
    }

    public ResponseDTO finish(@NonNull FinishRequestDTO requestDTO) {
        var attendantDTO = attendants.get().stream()
            .filter(attendant -> attendant.getSquad().equals(requestDTO.getType()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MESSAGE_ATTENDANCE_UNABLE));

        var request = attendantDTO.getRequests().stream()
            .filter(r -> r.getName().equalsIgnoreCase(requestDTO.getCustomerName()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(MESSAGE_ATTENDANCE_NOT_FOUND));

        attendantDTO.getRequests().remove(request);
        validateRequestsWaiting(attendantDTO);
        return ResponseDTO.builder().response(MESSAGE_ATTENDANCE_FINISHED).build();
    }

    private void validateRequestsWaiting(AttendantDTO attendantDTO) {
        this.attendanceWaiting.getRequests().stream()
            .filter(r -> attendantDTO.getSquad().getSubject().equals(r.getSubject()))
            .findFirst()
            .ifPresent(request -> {
                attendantDTO.getRequests().add(request);
                this.attendanceWaiting.getRequests().remove(request);
            });
    }
}
