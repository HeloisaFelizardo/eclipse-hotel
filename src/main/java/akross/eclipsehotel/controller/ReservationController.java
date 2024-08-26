package akross.eclipsehotel.controller;

import akross.eclipsehotel.dto.ReservationDTO;
import akross.eclipsehotel.exception.BusinessException;
import akross.eclipsehotel.exception.NotFoundException;
import akross.eclipsehotel.model.Reservation;
import akross.eclipsehotel.model.ReservationStatus;
import akross.eclipsehotel.service.CustomerService;
import akross.eclipsehotel.service.ReservationService;
import akross.eclipsehotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservations Controller", description = "RESTful API for managing reservations.")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Create a new reservation and return the created reservation's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
            @ApiResponse(responseCode = "422", description = "Invalid reservation data provided")
    })
    public ResponseEntity<ReservationDTO> openReservation(@RequestBody ReservationDTO reservationDTO) {

        Reservation reservation = reservationDTO.toModel();
        reservation.setCustomer(customerService.findById(reservationDTO.customerId()));
        reservation.setRoom(roomService.findById(reservationDTO.roomId()));
        reservation = reservationService.openReservation(reservation);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();
        return ResponseEntity.created(location).body(new ReservationDTO(reservation));
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Get reservations by date range", description = "Retrieve a list of reservations within a specified date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid date range provided")
    })
    public ResponseEntity<List<ReservationDTO>> findReservationsBetween(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Reservation> reservations = reservationService.findReservationsBetween(start, end);
        var reservationsDto = reservations.stream().map(ReservationDTO::new).toList();
        return ResponseEntity.ok(reservationsDto);
    }

    @GetMapping("/in-use")
    @Operation(summary = "Get all rooms in use", description = "Retrieve a list of all rooms that are currently in use")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful")
    })
    public ResponseEntity<List<ReservationDTO>> getInUseReservations() {
        List<Reservation> inUseReservations = reservationService.findInUseReservations();
        List<ReservationDTO> inUseReservationsDto = inUseReservations.stream()
                .map(ReservationDTO::new).toList();
        return ResponseEntity.ok(inUseReservationsDto);
    }

    @PostMapping("/cancel/{id}")
    @Operation(summary = "Cancel a reservation", description = "Cancel a reservation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation canceled successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
    })
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.cancelReservation(id);
            ReservationDTO reservationDto = new ReservationDTO(reservation);
            return ResponseEntity.ok(reservationDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
