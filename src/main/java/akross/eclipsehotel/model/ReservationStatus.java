package akross.eclipsehotel.model;

public enum ReservationStatus {
    SCHEDULED,  // Indica que o quarto selecionado está reservado para o período de check-in escolhido
    IN_USE,     // Indica que o quarto está neste momento ocupado pela reserva realizada
    ABSENCE,    // Indica que o responsável pela reserva não compareceu ao hotel
    FINISHED,   // Indica que a reserva foi concluída com sucesso
    CANCELED    // Indica uma reserva cancelada antes de iniciar o check-in
}
