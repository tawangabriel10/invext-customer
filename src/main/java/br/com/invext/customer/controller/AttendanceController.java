package br.com.invext.customer.controller;

import br.com.invext.customer.domain.dto.CustomerRequestDTO;
import br.com.invext.customer.domain.dto.FinishRequestDTO;
import br.com.invext.customer.domain.dto.ResponseDTO;
import br.com.invext.customer.service.DirectAttendanceService;
import br.com.invext.customer.service.FinishAttendanceService;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final DirectAttendanceService directService;
    private final FinishAttendanceService finishService;

    @PostMapping()
    public ResponseEntity<ResponseDTO> direct(@RequestBody CustomerRequestDTO requestDTO)
        throws URISyntaxException {
        var response = directService.direct(requestDTO);
        return ResponseEntity.created(new URI("")).body(response);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseDTO> finish(@RequestBody FinishRequestDTO requestDTO) {
        var response = finishService.finish(requestDTO);
        return ResponseEntity.ok(response);
    }
}
