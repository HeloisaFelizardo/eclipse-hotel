package akross.eclipsehotel.service;

import akross.eclipsehotel.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    Reservation openReservation(Reservation reservation);
    List<Reservation> findReservationsBetween(LocalDate start, LocalDate end);
    List<Reservation> findInUseReservations();
    Reservation cancelReservation(Long id);
}
