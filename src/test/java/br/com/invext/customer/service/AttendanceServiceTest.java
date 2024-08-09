package br.com.invext.customer.service;

import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_FINISHED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_NOT_FOUND;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_STARTED;
import static br.com.invext.customer.util.Constants.MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT;
import static br.com.invext.customer.util.Constants.SUBJECT_HIRING_LOAN;
import static br.com.invext.customer.util.Constants.SUBJECT_OTHERS;
import static br.com.invext.customer.util.Constants.SUBJECT_PROBLEM_WITH_CARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.invext.customer.domain.CustomerRequestDTO;
import br.com.invext.customer.domain.FinishRequestDTO;
import br.com.invext.customer.domain.RequestDTO;
import br.com.invext.customer.domain.ResponseDTO;
import br.com.invext.customer.domain.enumerated.SquadEnum;
import br.com.invext.customer.exception.BusinessException;
import br.com.invext.customer.mapper.AttendanceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @InjectMocks private AttendanceService service;
    @Mock private AttendanceMapper mapper;

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

        FinishRequestDTO finishRequestDTO = buildFinishRequestLoans();
        ResponseDTO responseDTO2 = service.finish(finishRequestDTO);
        assertNotNull(responseDTO2);
        assertEquals(MESSAGE_ATTENDANCE_FINISHED, responseDTO2.getResponse());

        CustomerRequestDTO requestLoan5 = buildCustomerRequestLoan();
        RequestDTO requestDTO5 = buildRequestLoan();
        when(mapper.toRequest(requestLoan5)).thenReturn(requestDTO5);

        BusinessException exception2 = assertThrows(BusinessException.class, () -> service.direct(requestLoan5));
        assertNotNull(exception2);
        assertEquals(MESSAGE_ATTENDANCE_WAITING_PLEASE_WAIT, exception2.getMessage());

        verify(mapper, times(5)).toRequest(requestLoan1);
    }

    @Test
    void givenFinishRequestOther_whenCallFinish_thenFinishingAttendance() {
        CustomerRequestDTO requestCard = buildCustomerRequestOther();
        RequestDTO requestDTO = buildRequestOther();
        FinishRequestDTO finishRequestDTO = buildFinishRequestOthers();

        when(mapper.toRequest(requestCard)).thenReturn(requestDTO);

        ResponseDTO responseDTO = service.direct(requestCard);
        assertNotNull(responseDTO);
        assertEquals(MESSAGE_ATTENDANCE_STARTED, responseDTO.getResponse());

        ResponseDTO responseDTO2 = service.finish(finishRequestDTO);
        assertNotNull(responseDTO2);
        assertEquals(MESSAGE_ATTENDANCE_FINISHED, responseDTO2.getResponse());

        verify(mapper).toRequest(requestCard);
    }

    @Test
    void givenFinishRequestAttendanceNotStarted_whenCallFinish_thenThrowsBusinessException() {
        FinishRequestDTO finishRequestDTO = buildFinishRequestOthers();

        BusinessException exception = assertThrows(BusinessException.class, () -> service.finish(finishRequestDTO));
        assertNotNull(exception);
        assertEquals(MESSAGE_ATTENDANCE_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(mapper);
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

    private FinishRequestDTO buildFinishRequestLoans() {
        return FinishRequestDTO.builder()
            .customerName("Tawan Souza")
            .type(SquadEnum.LOANS)
            .build();
    }

    private FinishRequestDTO buildFinishRequestOthers() {
        return FinishRequestDTO.builder()
            .customerName("Tawan Souza")
            .type(SquadEnum.OTHERS)
            .build();
    }

}
