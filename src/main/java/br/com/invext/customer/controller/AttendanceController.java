package br.com.invext.customer.controller;

import br.com.invext.customer.domain.CustomerRequestDTO;
import br.com.invext.customer.domain.FinishRequestDTO;
import br.com.invext.customer.domain.ResponseDTO;
import br.com.invext.customer.service.AttendanceService;
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

    private final AttendanceService service;

    @PostMapping()
    public ResponseEntity<ResponseDTO> direct(@RequestBody CustomerRequestDTO requestDTO)
        throws URISyntaxException {
        var response = service.direct(requestDTO);
        return ResponseEntity.created(new URI("")).body(response);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseDTO> finish(@RequestBody FinishRequestDTO requestDTO) {
        var response = service.finish(requestDTO);
        return ResponseEntity.ok(response);
    }
}
