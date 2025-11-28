package ai.parent.parentaibackend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Время ошибки (на сервере).
     */
    private LocalDateTime timestamp;

    /**
     * HTTP статус (например, 400, 404).
     */
    private int status;

    /**
     * Короткое сообщение (общее).
     */
    private String message;

    /**
     * Детальные ошибки (например, ошибки поля).
     * Может быть null или пустым списком.
     */
    private List<String> errors;
}
