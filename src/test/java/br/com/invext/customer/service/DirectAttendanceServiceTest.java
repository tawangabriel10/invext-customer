package br.com.invext.customer.service;

import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_STARTED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT;
import static br.com.invext.customer.util.Constants.SUBJECT_HIRING_LOAN;
import static br.com.invext.customer.util.Constants.SUBJECT_OTHERS;
import static br.com.invext.customer.util.Constants.SUBJECT_PROBLEM_WITH_CARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.invext.customer.domain.AttendanceWaiting;
import br.com.invext.customer.domain.Attendants;
import br.com.invext.customer.domain.dto.CustomerRequestDTO;
import br.com.invext.customer.domain.dto.RequestDTO;
import br.com.invext.customer.domain.dto.ResponseDTO;
import br.com.invext.customer.domain.exception.BusinessException;
import br.com.invext.customer.mapper.AttendanceMapper;
import br.com.invext.customer.repository.AttendantRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DirectAttendanceServiceTest {

    @InjectMocks private DirectAttendanceService service;
    @Mock private AttendanceMapper mapper;

    private Attendants attendants;

    @BeforeEach
    public void setUp() {
        var attendantList = AttendantRepository.getAttendants();
        attendants = mock(Attendants.class);
        when(attendants.get()).thenReturn(attendantList);

        var attendanceWaiting = mock(AttendanceWaiting.class);
        when(attendanceWaiting.getRequests()).thenReturn(new ArrayList<>());

        service = new DirectAttendanceService(attendants, attendanceWaiting, mapper);
    }

    @Test
    void givenRequestCard_whenCallDirect_thenRegisterAttendance() {
        CustomerRequestDTO requestCard = buildCustomerRequestCard();
        RequestDTO requestDTO = buildRequestCard();

        when(mapper.toRequest(requestCard)).thenReturn(requestDTO);

        ResponseDTO responseDTO = service.direct(requestCard);
        assertNotNull(responseDTO);
        assertEquals(MESSAGE_ATTENDANCE_STARTED, responseDTO.getResponse());

        verify(mapper).toRequest(requestCard);
    }

    @Test
    void givenRequestLoan_whenCallDirect_thenRegisterAttendance() {
        CustomerRequestDTO requestCard = buildCustomerRequestLoan();
        RequestDTO requestDTO = buildRequestLoan();

        when(mapper.toRequest(requestCard)).thenReturn(requestDTO);

        ResponseDTO responseDTO = service.direct(requestCard);
        assertNotNull(responseDTO);
        assertEquals(MESSAGE_ATTENDANCE_STARTED, responseDTO.getResponse());

        verify(mapper).toRequest(requestCard);
    }

    @Test
    void givenRequestOthers_whenCallDirect_thenRegisterAttendance() {
        CustomerRequestDTO requestCard = buildCustomerRequestOther();
        RequestDTO requestDTO = buildRequestOther();

        when(mapper.toRequest(requestCard)).thenReturn(requestDTO);

        ResponseDTO responseDTO = service.direct(requestCard);
        assertNotNull(responseDTO);
        assertEquals(MESSAGE_ATTENDANCE_STARTED, responseDTO.getResponse());

        verify(mapper).toRequest(requestCard);
    }

    @Test
    void givenManyRequests_whenCallDirect_thenThrowsBusinessEception() {
        CustomerRequestDTO requestCard1 = buildCustomerRequestCard();
        RequestDTO requestDTO1 = buildRequestCard();
        CustomerRequestDTO requestCard2 = buildCustomerRequestCard();
        RequestDTO requestDTO2 = buildRequestCard();
        CustomerRequestDTO requestCard3 = buildCustomerRequestCard();
        RequestDTO requestDTO3 = buildRequestCard();
        CustomerRequestDTO requestCard4 = buildCustomerRequestCard();
        RequestDTO requestDTO4 = buildRequestCard();

        when(mapper.toRequest(requestCard1)).thenReturn(requestDTO1);
        when(mapper.toRequest(requestCard2)).thenReturn(requestDTO2);
        when(mapper.toRequest(requestCard3)).thenReturn(requestDTO3);
        when(mapper.toRequest(requestCard4)).thenReturn(requestDTO4);

        service.direct(requestCard1);
        service.direct(requestCard2);
        service.direct(requestCard3);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.direct(requestCard4));
        assertNotNull(exception);
        assertEquals(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT, exception.getMessage());

        verify(mapper, times(4)).toRequest(requestCard1);
    }

    @Test
    void givenManyRequestsAndFinishAttendanceAndDirectNewRequest_whenCallDirect_thenThrowsBusinessEceptionAndFinishOneAttendanceAndInitNew() {
        CustomerRequestDTO requestLoan1 = buildCustomerRequestLoan();
        RequestDTO requestDTO1 = buildRequestLoan();
        CustomerRequestDTO requestLoan2 = buildCustomerRequestLoan();
        RequestDTO requestDTO2 = buildRequestLoan();
        CustomerRequestDTO requestLoan3 = buildCustomerRequestLoan();
        RequestDTO requestDTO3 = buildRequestLoan();
        CustomerRequestDTO requestLoan4 = buildCustomerRequestLoan();
        RequestDTO requestDTO4 = buildRequestLoan();

        when(mapper.toRequest(requestLoan1)).thenReturn(requestDTO1);
        when(mapper.toRequest(requestLoan2)).thenReturn(requestDTO2);
        when(mapper.toRequest(requestLoan3)).thenReturn(requestDTO3);
        when(mapper.toRequest(requestLoan4)).thenReturn(requestDTO4);

        service.direct(requestLoan1);
        service.direct(requestLoan2);
        service.direct(requestLoan3);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.direct(requestLoan4));
        assertNotNull(exception);
        assertEquals(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT, exception.getMessage());

        attendants.get().forEach(attendantDTO -> attendantDTO.getRequests().remove(requestDTO1));

        CustomerRequestDTO requestLoan5 = buildCustomerRequestLoan();
        RequestDTO requestDTO5 = buildRequestLoan();
        when(mapper.toRequest(requestLoan5)).thenReturn(requestDTO5);

        BusinessException exception2 = assertThrows(BusinessException.class, () -> service.direct(requestLoan5));
        assertNotNull(exception2);
        assertEquals(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT, exception2.getMessage());

        verify(mapper, times(5)).toRequest(requestLoan1);
    }

    private CustomerRequestDTO buildCustomerRequestCard() {
        return CustomerRequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_PROBLEM_WITH_CARD)
            .build();
    }

    private RequestDTO buildRequestCard() {
        return RequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_PROBLEM_WITH_CARD)
            .build();
    }

    private CustomerRequestDTO buildCustomerRequestLoan() {
        return CustomerRequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_HIRING_LOAN)
            .build();
    }

    private RequestDTO buildRequestLoan() {
        return RequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_HIRING_LOAN)
            .build();
    }

    private CustomerRequestDTO buildCustomerRequestOther() {
        return CustomerRequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_OTHERS)
            .build();
    }

    private RequestDTO buildRequestOther() {
        return RequestDTO.builder()
            .name("Tawan Souza")
            .subject(SUBJECT_OTHERS)
            .build();
    }

}
