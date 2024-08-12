package br.com.invext.customer.domain.dto;

import br.com.invext.customer.domain.IRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO implements IRequest {

    private String name;
    private String subject;

}
