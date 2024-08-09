package br.com.invext.customer.domain;

import br.com.invext.customer.domain.enumerated.SquadEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendantDTO {

    private String name;
    private SquadEnum squad;
    private List<RequestDTO> requests = new ArrayList<>();
}
