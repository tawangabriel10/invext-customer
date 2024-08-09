package br.com.invext.customer.domain;

import br.com.invext.customer.domain.enumerated.SquadEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishRequestDTO {

    private String customerName;
    private SquadEnum type;

}
