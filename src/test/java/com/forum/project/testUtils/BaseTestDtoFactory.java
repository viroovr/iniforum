package com.forum.project.testUtils;

import java.time.Instant;
import java.time.LocalDateTime;

public abstract class BaseTestDtoFactory {
    protected static final LocalDateTime DATE_TIME =
            LocalDateTime.ofInstant(Instant.parse(TestUtils.getIsoDateTime()), TestUtils.getZonedId());
}
