package com.eze.transactionservice.filter;

import com.eze.transactionservice.exception.ApiException;
import com.eze.transactionservice.exception.ExceptionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(ex.getClass().equals(ApiException.class)){
                ApiException exception = (ApiException) ex;
                status = exception.getStatus();
            }
            response.setStatus(status.value());
            response.getWriter().write(convertObjectToJson(new ExceptionResponse(ex.getMessage(), LocalDateTime.now(), request.getServletPath() )));
        }
    }

    private String convertObjectToJson (@NonNull Object o){
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        JsonNode node = mapper.valueToTree(o);
        return node.toPrettyString();
    }
}
