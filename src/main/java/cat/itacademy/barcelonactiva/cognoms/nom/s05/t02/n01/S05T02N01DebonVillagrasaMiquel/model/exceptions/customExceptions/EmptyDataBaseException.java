package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.MessageException;

public class EmptyDataBaseException extends RuntimeException{
    public EmptyDataBaseException() {
        super(MessageException.EMPTY_DATABASE);
    }

    public EmptyDataBaseException(String message) {
        super(message);
    }
}
