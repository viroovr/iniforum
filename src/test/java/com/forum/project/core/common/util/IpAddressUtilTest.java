package com.forum.project.core.common.util;

import com.forum.project.core.common.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IpAddressUtilTest {

    @Test
    public void testGetClientIp_XForwardedForHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.0.1");

        String clientIp = IpAddressUtil.getClientIp(request);

        assertEquals("192.168.0.1", clientIp);
    }

    @Test
    public void testGetClientIp_ProxyClientIPHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn("192.168.1.2");

        String clientIp = IpAddressUtil.getClientIp(request);

        assertEquals("192.168.1.2", clientIp);
    }

    @Test
    public void testGetClientIp_WLProxyClientIPHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("192.168.1.3");

        String clientIp = IpAddressUtil.getClientIp(request);

        assertEquals("192.168.1.3", clientIp);
    }

    @Test
    public void testGetClientIp_RemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.4");

        String clientIp = IpAddressUtil.getClientIp(request);

        assertEquals("192.168.1.4", clientIp);
    }

    @Test
    public void testGetClientIp_UnknownIp() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("unknown");
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn("unknown");
        when(request.getRemoteAddr()).thenReturn("192.168.1.5");

        String clientIp = IpAddressUtil.getClientIp(request);

        assertEquals("192.168.1.5", clientIp);
    }
}
