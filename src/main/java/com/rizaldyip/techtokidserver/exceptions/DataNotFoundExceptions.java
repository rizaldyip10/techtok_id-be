package com.rizaldyip.techtokidserver.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataNotFoundExceptions extends RuntimeException {
    public DataNotFoundExceptions(String message) { super(message); }
}
