package com.example.common.config.websocket;

import com.example.common.config.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Binds JWT from STOMP CONNECT to the session principal so {@code convertAndSendToUser(userId, ...)}
 * matches the client subscription to {@code /user/queue/notifications}.
 */
@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return message;
        }

        String token = authHeader.substring(7);
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token))
                || Boolean.TRUE.equals(redisTemplate.hasKey("JWT_BLACKLIST:" + token));
        if (!jwtService.isTokenValid(token) || isBlacklisted) {
            return message;
        }

        Claims claims = jwtService.extractAllClaims(token);
        Long userId = parseUserId(claims);
        String role = claims.get("role", String.class);
        if (userId == null) {
            return message;
        }

        String authRole = role != null && role.startsWith("ROLE_") ? role : "ROLE_" + (role != null ? role : "USER");
        var authorities = List.of(new SimpleGrantedAuthority(authRole));
        var auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities);
        accessor.setUser(auth);
        return message;
    }

    /** JWT may deserialize userId as Integer or Long depending on serializer. */
    private static Long parseUserId(Claims claims) {
        Object v = claims.get("userId");
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
