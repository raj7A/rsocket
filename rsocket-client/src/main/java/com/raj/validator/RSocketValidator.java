package com.raj.validator;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class RSocketValidator {
    public static boolean isGoodToProcess(String route, Object data, boolean isRSocketEnabled) {
        try {
            if (!isRSocketEnabled) {
                log.debug("Skipped processing - RSocket invoked without enabling, set rSocketEnabled flag to true");
                return false;
            }
            Objects.requireNonNull(route, "route cannot be null");
            Objects.requireNonNull(data, "data cannot be null");
        } catch (Exception exception) {
            log.error("Exception occurred during RSocket invocation :: {} ", exception.getMessage());
            return false;
        }
        log.debug("RSocket request is good to be processed");
        return true;
    }
}
