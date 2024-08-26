package akross.eclipsehotel.dto;

import akross.eclipsehotel.model.Reservation;
import akross.eclipsehotel.model.ReservationStatus;

import java.time.LocalDate;

public record ReservationDTO(
        Long id,
        Long customerId,
        Long roomId,
        LocalDate checkin,
        LocalDate checkout,
        ReservationStatus status) {

    public ReservationDTO(Reservation model) {
        this(
                model.getId(),
                model.getCustomer().getId(),
                model.getRoom().getId(),
                model.getCheckin(),
                model.getCheckout(),
                model.getStatus()
        );
    }

    public Reservation toModel() {
        Reservation model = new Reservation();
        model.setId(id);
        model.setCheckin(checkin);
        model.setCheckout(checkout);
        model.setStatus(status);
        return model;
    }
}
