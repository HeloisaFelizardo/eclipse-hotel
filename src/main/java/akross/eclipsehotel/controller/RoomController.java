package akross.eclipsehotel.controller;

import akross.eclipsehotel.dto.RoomDTO;
import akross.eclipsehotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms Controller", description = "RESTful API for managing rooms.")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Get all rooms", description = "Retrieve a list of all registered rooms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful")
    })
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        var rooms = roomService.findAll();
        var roomsDto = rooms.stream().map(RoomDTO::new).toList();
        return ResponseEntity.ok(roomsDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by ID", description = "Retrieve a specific room based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        var room = roomService.findById(id);
        return ResponseEntity.ok(new RoomDTO(room));
    }

    @PostMapping
    @Operation(summary = "Create a new room", description = "Create a new room and return the created room's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room created successfully"),
            @ApiResponse(responseCode = "422", description = "Invalid room data provided")
    })
    public ResponseEntity<RoomDTO> create(@RequestBody RoomDTO roomDTO) {
        var room = roomService.create(roomDTO.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(room.getId())
                .toUri();
        return ResponseEntity.created(location).body(new RoomDTO(room));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a room", description = "Update the data of an existing room based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "422", description = "Invalid room data provided")
    })
    public ResponseEntity<RoomDTO> update(@PathVariable Long id, @RequestBody RoomDTO roomDTO) {
        var room = roomService.update(id, roomDTO.toModel());
        return ResponseEntity.ok(new RoomDTO(room));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Delete an existing room based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
