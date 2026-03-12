package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException() {
        ValidationException e = new ValidationException("ValidationException");

        ErrorResponse response = errorHandler.handleValidationException(e);

        assertEquals("Ошибка валидации", response.error());
        assertEquals("ValidationException", response.description());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException e = new NotFoundException("NotFoundException");

        ErrorResponse response = errorHandler.handleNotFoundException(e);

        assertEquals("Объект не найден", response.error());
        assertEquals("NotFoundException", response.description());
    }

    @Test
    void handleDuplicatedDataException() {
        DuplicatedDataException e = new DuplicatedDataException("DuplicatedDataException");

        ErrorResponse response = errorHandler.handleDuplicatedDataException(e);

        assertEquals("Ошибка сервера, найден дубликат", response.error());
        assertEquals("DuplicatedDataException", response.description());
    }
}