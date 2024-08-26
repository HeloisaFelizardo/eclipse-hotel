package akross.eclipsehotel.repository;

import akross.eclipsehotel.model.Reservation;
import akross.eclipsehotel.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByRoomNumberAndStatusInAndCheckoutAfterAndCheckinBefore(
            String roomNumber,
            List<ReservationStatus> statuses,
            LocalDate checkin,
            LocalDate checkout
    );
    List<Reservation> findByCheckinBetween(LocalDate start, LocalDate end);
    List<Reservation> findByStatus(ReservationStatus status);
}
