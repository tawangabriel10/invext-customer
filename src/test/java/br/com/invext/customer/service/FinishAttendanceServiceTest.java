package br.com.invext.customer.service;

import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_FINISHED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_NOT_FOUND;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_UNABLE;
import static br.com.invext.customer.util.Constants.SUBJECT_OTHERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.invext.customer.domain.AttendanceWaiting;
import br.com.invext.customer.domain.Attendants;
import br.com.invext.customer.domain.dto.FinishRequestDTO;
import br.com.invext.customer.domain.dto.RequestDTO;
import br.com.invext.customer.domain.dto.ResponseDTO;
import br.com.invext.customer.domain.enumerated.SquadEnum;
import br.com.invext.customer.domain.exception.BusinessException;
import br.com.invext.customer.repository.AttendantRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinishAttendanceServiceTest {

    @InjectMocks private FinishAttendanceService service;

    private Attendants attendants;
    private AttendanceWaiting attendanceWaiting;

    @BeforeEach
    public void setUp() {
        var attendantList = AttendantRepository.getAttendants();
        attendants = mock(Attendants.class);
        when(attendants.get()).thenReturn(attendantList);

        attendanceWaiting = mock(AttendanceWaiting.class);

        service = new FinishAttendanceService(attendants, attendanceWaiting);
    }

    @Test
    void givenFinishRequestOther_whenCallFinish_thenFinishingAttendance() {
        RequestDTO requestDTO = buildRequestOther();
        FinishRequestDTO finishRequestDTO = buildFinishRequestOthers();

        when(attendanceWaiting.getRequests()).thenReturn(new ArrayList<>());
        attendants.get().forEach(attendantDTO -> attendantDTO.getRequests().add(requestDTO));

        ResponseDTO responseDTO2 = service.finish(finishRequestDTO);
        assertNotNull(responseDTO2);
        assertEquals(MESSAGE_ATTENDANCE_FINISHED, responseDTO2.getResponse());
    }

    @Test
    void givenFinishRequestAttendantUnable_whenCallFinish_thenThrowsBusinessException() {
        FinishRequestDTO finishRequestDTO = buildFinishRequestOthers();
        when(attendants.get()).thenReturn(new ArrayList<>());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.finish(finishRequestDTO));
        assertNotNull(exception);
        assertEquals(MESSAGE_ATTENDANCE_UNABLE, exception.getMessage());
        verifyNoInteractions(attendanceWaiting);
    }

    @Test
    void givenFinishRequestAttendanceNotStarted_whenCallFinish_thenThrowsBusinessException() {
        FinishRequestDTO finishRequestDTO = buildFinishRequestOthers();

        BusinessException exception = assertThrows(BusinessException.class, () -> service.finish(finishRequestDTO));
        assertNotNull(exception);
        assertEquals(MESSAGE_ATTENDANCE_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(attendanceWaiting);
    }

    private RequestDTO buildRequestOther() {
        return RequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_OTHERS)
            .build();
    }

    private FinishRequestDTO buildFinishRequestOthers() {
        return FinishRequestDTO.builder()
            .customerName("Tawan Souza")
            .type(SquadEnum.OTHERS)
            .build();
    }

}
