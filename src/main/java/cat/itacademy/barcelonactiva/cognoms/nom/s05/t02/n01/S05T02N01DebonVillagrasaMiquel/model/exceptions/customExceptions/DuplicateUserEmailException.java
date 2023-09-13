package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions;

public class DuplicateUserEmailException extends RuntimeException{
    public DuplicateUserEmailException() {
        super("Duplicated Email");
    }

    public DuplicateUserEmailException(String message) {
        super(message);
    }
}
