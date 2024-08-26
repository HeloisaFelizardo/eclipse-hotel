package akross.eclipsehotel.exception;

public class NotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String s) {
        super("Resource not found.");
    }

}