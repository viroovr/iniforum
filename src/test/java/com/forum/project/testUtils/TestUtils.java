package com.forum.project.testUtils;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.core.common.LogHelper;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import org.assertj.core.api.ThrowableAssert;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class TestUtils {
    /**
     * "2025-01-01T00:00:00.00Z", "Asia/Seoul"
     */
    public static void setFixedClock() {
        ClockUtil.setFixedClock("2025-01-01T00:00:00.00Z", "Asia/Seoul");
    }

    public static ZoneId getZonedId() {
        return ZoneId.of("Asia/Seoul");
    }

    public static String getIsoDateTime() {
        return "2025-01-01T00:00:00.00Z";
    }

    public static void assertApplicationException(
            ThrowableAssert.ThrowingCallable callable,
            ErrorCode expectedErrorCode
    ) {
        ApplicationException exception = catchThrowableOfType(callable, ApplicationException.class);
        assertThat(exception.getErrorCode()).isEqualTo(expectedErrorCode);
        LogHelper.logApplicationException(exception);
    }
}
