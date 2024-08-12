package br.com.invext.customer.mapper;

import br.com.invext.customer.domain.dto.CustomerRequestDTO;
import br.com.invext.customer.domain.dto.RequestDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public RequestDTO toRequest(@NonNull CustomerRequestDTO requestDTO) {
        return RequestDTO.builder()
            .name(requestDTO.getName())
            .subject(requestDTO.getSubject())
            .build();
    }

}
