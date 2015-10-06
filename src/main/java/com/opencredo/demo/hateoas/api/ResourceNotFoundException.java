package com.opencredo.demo.hateoas.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Resource not found") 
public class ResourceNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 1L;

}
