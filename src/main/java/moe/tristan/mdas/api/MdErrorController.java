package moe.tristan.mdas.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import moe.tristan.mdas.model.ExceptionResponse;

@RestController
public class MdErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public MdErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ExceptionResponse handleError(WebRequest webRequest) {
        Throwable error = errorAttributes.getError(webRequest);

        List<String> causes = new ArrayList<>();
        Throwable cause = error.getCause();
        while (cause != null && cause.getCause() != cause) {
            causes.add(cause.getMessage());
            cause = cause.getCause();
        }

        return ExceptionResponse.of(error.getMessage(), causes);
    }

    @Override
    public String getErrorPath() {
        return null;
    }

}
