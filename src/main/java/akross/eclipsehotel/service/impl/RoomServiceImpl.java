package akross.eclipsehotel.service.impl;

import akross.eclipsehotel.exception.BusinessException;
import akross.eclipsehotel.exception.NotFoundException;
import akross.eclipsehotel.model.Room;
import akross.eclipsehotel.repository.RoomRepository;
import akross.eclipsehotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            logger.info("No rooms found in the database.");
        } else {
            logger.info("Retrieved {} rooms from the database.", rooms.size());
        }
        return rooms;
    }

    @Transactional(readOnly = true)
    public Room findById(Long id) {
        logger.info("Searching for room with ID: {}", id);
        return roomRepository.findById(id).orElseThrow(() -> {
            logger.error("Room not found with ID: {}", id);
            return new NotFoundException("Room not found with ID: " + id);
        });
    }

    @Transactional
    public Room create(Room roomToCreate) {
        logger.info("Creating a new room: {}", roomToCreate);

        ofNullable(roomToCreate).orElseThrow(() -> {
            logger.error("Room to create must not be null.");
            return new BusinessException("Room to create must not be null.");
        });
        ofNullable(roomToCreate.getNumber()).orElseThrow(() -> {
            logger.error("Room number must not be null.");
            return new BusinessException("Room number must not be null.");
        });
        ofNullable(roomToCreate.getType()).orElseThrow(() -> {
            logger.error("Room type must not be null.");
            return new BusinessException("Room type must not be null.");
        });
        ofNullable(roomToCreate.getPrice()).orElseThrow(() -> {
            logger.error("Room price must not be null.");
            return new BusinessException("Room price must not be null.");
        });

        if (roomRepository.existsByNumber(roomToCreate.getNumber())) {
            logger.error("Room number {} already exists.", roomToCreate.getNumber());
            throw new BusinessException("This number of a room already exists.");
        }

        Room createdRoom = roomRepository.save(roomToCreate);
        logger.info("Room created successfully with ID: {}", createdRoom.getId());
        return createdRoom;
    }

    @Transactional
    public Room update(Long id, Room roomToUpdate) {
        logger.info("Updating room with ID: {}", id);
        Room dbRoom = findById(id);

        if (!dbRoom.getId().equals(roomToUpdate.getId())) {
            logger.error("Update IDs must be the same. Provided ID: {}, Existing ID: {}", roomToUpdate.getId(), dbRoom.getId());
            throw new BusinessException("Update IDs must be the same.");
        }

        dbRoom.setNumber(roomToUpdate.getNumber());
        dbRoom.setType(roomToUpdate.getType());
        dbRoom.setPrice(roomToUpdate.getPrice());

        Room updatedRoom = roomRepository.save(dbRoom);
        logger.info("Room with ID: {} updated successfully.", id);
        return updatedRoom;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting room with ID: {}", id);
        Room room = roomRepository.findById(id).orElseThrow(() -> {
            logger.error("Room not found with ID: {}", id);
            return new NotFoundException("Room not found with ID: " + id);
        });
        roomRepository.delete(room);
        logger.info("Room with ID: {} deleted successfully.", id);
    }
}
