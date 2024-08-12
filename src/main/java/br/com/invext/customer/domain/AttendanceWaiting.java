package br.com.invext.customer.domain;

import br.com.invext.customer.domain.dto.RequestDTO;
import java.util.ArrayList;
import java.util.List;

public class AttendanceWaiting {

    private static AttendanceWaiting instance;

    private final List<RequestDTO> requests = new ArrayList<>();

    private AttendanceWaiting() {
    }

    public static synchronized AttendanceWaiting getInstance() {
        if (instance == null)
            instance = new AttendanceWaiting();

        return instance;
    }

    public List<RequestDTO> getRequests() {
        return this.requests;
    }
}
