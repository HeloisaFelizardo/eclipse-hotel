package akross.eclipsehotel.dto;

import akross.eclipsehotel.model.Room;
import akross.eclipsehotel.model.RoomType;

import java.math.BigDecimal;

public record RoomDTO(
        Long id,
        String number,
        RoomType type,
        BigDecimal price) {

    public RoomDTO(Room model) {
        this(
                model.getId(),
                model.getNumber(),
                model.getType(),
                model.getPrice()
        );
    }

    public Room toModel() {
        Room model = new Room();
        model.setId(id);
        model.setNumber(number);
        model.setType(type);
        model.setPrice(price);
        return model;
    }
}