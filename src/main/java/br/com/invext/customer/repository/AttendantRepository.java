package br.com.invext.customer.repository;

import br.com.invext.customer.domain.dto.AttendantDTO;
import br.com.invext.customer.domain.enumerated.SquadEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttendantRepository {

    private AttendantRepository() {}

    public static List<AttendantDTO> getAttendants() {
        AttendantDTO attendantCard = AttendantDTO.builder()
            .name("Atendente 01")
            .squad(SquadEnum.CARDS)
            .requests(new ArrayList<>())
            .build();

        AttendantDTO attendantLoan = AttendantDTO.builder()
            .name("Atendente 02")
            .squad(SquadEnum.LOANS)
            .requests(new ArrayList<>())
            .build();

        AttendantDTO attendantOthers = AttendantDTO.builder()
            .name("Atendente 03")
            .squad(SquadEnum.OTHERS)
            .requests(new ArrayList<>())
            .build();

        return Arrays.asList(attendantCard, attendantLoan, attendantOthers);
    }

}
