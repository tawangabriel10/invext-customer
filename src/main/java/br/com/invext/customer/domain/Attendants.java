package br.com.invext.customer.domain;

import br.com.invext.customer.domain.dto.AttendantDTO;
import br.com.invext.customer.repository.AttendantRepository;
import java.util.ArrayList;
import java.util.List;

public class Attendants {

    private static Attendants instance;

    private static List<AttendantDTO> attendantDTOS = new ArrayList<>();

    private Attendants() {
    }

    public static synchronized Attendants getInstance() {
        if (instance == null) {
            instance = new Attendants();
            attendantDTOS = AttendantRepository.getAttendants();
        }

        return instance;
    }

    public List<AttendantDTO> get() {
        return attendantDTOS;
    }
}
