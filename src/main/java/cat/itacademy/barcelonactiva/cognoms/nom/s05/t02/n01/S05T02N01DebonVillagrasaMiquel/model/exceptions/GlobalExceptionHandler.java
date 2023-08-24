package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.DuplicateUserEmailException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.DuplicateUserNameException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.EmptyDataBaseException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> invalidUser(UserNotFoundException ex, WebRequest request){
        ErrorObject message = new ErrorObject(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateUserNameException.class)
    public ResponseEntity<?> duplicatedName(DuplicateUserNameException ex){
        Map<String, String> mapError = new HashMap<>();
        mapError.put("ErrorMessage", ex.getMessage());
        return new ResponseEntity<>(mapError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateUserEmailException.class)
    public ResponseEntity<?> duplicatedEmail(DuplicateUserEmailException ex){
        Map<String, String> mapError = new HashMap<>();
        mapError.put("ErrorMessage", ex.getMessage());
        return new ResponseEntity<>(mapError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyDataBaseException.class)
    public ResponseEntity<?> emptyDatabase(EmptyDataBaseException ex){
        Map<String, String> mapError = new HashMap<>();
        mapError.put("ErrorMessage", ex.getMessage());
        return new ResponseEntity<>(mapError, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internalErroException(Exception ex){
        Map<String, String> mapError = new HashMap<>();
        mapError.put("ErrorMessage", ex.getMessage());
        return new ResponseEntity<>(mapError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
