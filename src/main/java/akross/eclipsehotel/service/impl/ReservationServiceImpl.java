package akross.eclipsehotel.service.impl;

import akross.eclipsehotel.exception.BusinessException;
import akross.eclipsehotel.exception.NotFoundException;
import akross.eclipsehotel.model.Reservation;
import akross.eclipsehotel.model.ReservationStatus;
import akross.eclipsehotel.repository.ReservationRepository;
import akross.eclipsehotel.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation openReservation(Reservation reservationToCreate) {
        logger.info("Opening reservation:");

        ofNullable(reservationToCreate).orElseThrow(() -> {
            logger.error("Reservation to create must not be null.");
            return new BusinessException("Reservation to create must not be null.");
        });
        ofNullable(reservationToCreate.getCustomer()).orElseThrow(() -> {
            logger.error("Reservation customer must not be null.");
            return new BusinessException("Reservation customer must not be null.");
        });
        ofNullable(reservationToCreate.getRoom()).orElseThrow(() -> {
            logger.error("Reservation room must not be null.");
            return new BusinessException("Reservation room must not be null.");
        });
        ofNullable(reservationToCreate.getCheckin()).orElseThrow(() -> {
            logger.error("Reservation checkin must not be null.");
            return new BusinessException("Reservation checkin must not be null.");
        });
        ofNullable(reservationToCreate.getCheckout()).orElseThrow(() -> {
            logger.error("Reservation checkout must not be null.");
            return new BusinessException("Reservation checkout must not be null.");
        });
        ofNullable(reservationToCreate.getStatus()).orElseThrow(() -> {
            logger.error("Reservation status must not be null.");
            return new BusinessException("Reservation status must not be null.");
        });

        if (reservationToCreate.getCheckin().isAfter(reservationToCreate.getCheckout())) {
            logger.error("Check-in date must be before or on the same day as the check-out date.");
            throw new BusinessException("Check-in date must be before or on the same day as the check-out date.");
        }

        List<ReservationStatus> occupiedStatuses = List.of(ReservationStatus.SCHEDULED, ReservationStatus.IN_USE);
        if (reservationRepository.existsByRoomNumberAndStatusInAndCheckoutAfterAndCheckinBefore(
                reservationToCreate.getRoom().getNumber(),
                occupiedStatuses,
                reservationToCreate.getCheckin(),
                reservationToCreate.getCheckout()
        )) {
            logger.error("Room {} is currently occupied or scheduled during the requested period.", reservationToCreate.getRoom().getNumber());
            throw new BusinessException("This room is currently occupied or scheduled during the requested period.");
        }

        updateReservationStatus(reservationToCreate);
        Reservation savedReservation = reservationRepository.save(reservationToCreate);
        logger.info("Reservation created successfully with ID: {}", savedReservation.getId());
        return savedReservation;
    }

    @Transactional
    public List<Reservation> findReservationsBetween(LocalDate start, LocalDate end) {
        logger.info("Finding reservations between {} and {}", start, end);
        List<Reservation> reservations = reservationRepository.findByCheckinBetween(start, end);
        if (reservations.isEmpty()) {
            logger.info("No reservations found between {} and {}.", start, end);
        } else {
            logger.info("Found {} reservations between {} and {}.", reservations.size(), start, end);
        }
        return reservations;
    }

    @Transactional(readOnly = true)
    public List<Reservation> findInUseReservations() {
        logger.info("Finding reservations with status IN_USE.");
        List<Reservation> reservations = reservationRepository.findByStatus(ReservationStatus.IN_USE);
        if (reservations.isEmpty()) {
            logger.info("No reservations are currently in use.");
        } else {
            logger.info("Found {} reservations currently in use.", reservations.size());
        }
        return reservations;
    }

    @Transactional
    protected void updateReservationStatus(Reservation reservation) {
        if (isFinalStatus(reservation.getStatus())) {
            logger.error("Cannot update reservation status. The reservation is in a final state: {}", reservation.getStatus());
            throw new BusinessException("Cannot update reservation status. The reservation is in a final state: " + reservation.getStatus());
        }

        LocalDate today = LocalDate.now();
        logger.info("Updating reservation status ");

        if (reservation.getCheckin().isAfter(today)) {
            reservation.setStatus(ReservationStatus.SCHEDULED);
        } else if (reservation.getCheckout().isBefore(today)) {
            if (reservation.getCheckin() == null) {
                reservation.setStatus(ReservationStatus.ABSENCE);
            } else {
                reservation.setStatus(ReservationStatus.FINISHED);
            }
        } else if (!reservation.getCheckin().isAfter(today) && !reservation.getCheckout().isBefore(today)) {
            reservation.setStatus(ReservationStatus.IN_USE);
        } else {
            reservation.setStatus(ReservationStatus.SCHEDULED);
        }
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        logger.info("Cancelling reservation with ID: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reservation not found with ID: {}", id);
                    return new NotFoundException("Reservation not found with ID: " + id);
                });

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            logger.info("Reservation with ID: {} is already canceled.", id);
        } else {
            reservation.setStatus(ReservationStatus.CANCELED);
            reservationRepository.save(reservation);
            logger.info("Reservation with ID: {} has been successfully canceled.", id);
        }

        return reservation;
    }

    private boolean isFinalStatus(ReservationStatus status) {
        return status == ReservationStatus.ABSENCE || status == ReservationStatus.CANCELED;
    }
}
