package com.example.usermanagement.security;

import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(Authentication authentication){
        try{
            Instant now = Instant.now();
            String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (ObjectUtils.isEmpty(userDetails)){
                throw AppException.builder().status(ErrorMessage.CANNOT_GENERATE_TOKEN).build();
            }
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(authentication.getName())
                    .claim("fullName", userDetails.getFullName())
                    .claim("email", userDetails.getEmail())
                    .claim("role", authorities)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(jwtProperties.getTokenValidityInSeconds())))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
            signedJWT.sign(new MACSigner(decodedSecret()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public Authentication getAuthentication(String token, JWTClaimsSet claimsSet) {
        try{
            String roles = claimsSet.getStringClaim("role");
            Collection<GrantedAuthority> authorities = StringUtils.hasText(roles) ?
                    Arrays.stream(claimsSet.getClaim("role").toString().split(","))
                    .filter(auth -> !auth.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
                    : List.of();
            CustomUserDetails userDetails = new CustomUserDetails(
                    claimsSet.getSubject(),
                    "",
                    authorities,
                    claimsSet.getStringClaim("fullName"),
                    claimsSet.getStringClaim("email"));
            return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
        }catch (ParseException e){
            throw AppException.builder().status(ErrorMessage.INVALID_JWT).build();
        }
    }

    public JWTClaimsSet verifyAndParse(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS512.equals(signedJWT.getHeader().getAlgorithm())) {
                throw AppException.builder().status(ErrorMessage.INVALID_JWT).build();
            }

            JWSVerifier verifier = new MACVerifier(decodedSecret());
            if (!signedJWT.verify(verifier)) {
                throw AppException.builder().status(ErrorMessage.INVALID_SIGNATURE_JWT).build();
            }
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date exp = claims.getExpirationTime();
            if (exp == null) {
                throw AppException.builder().status(ErrorMessage.INVALID_JWT).build();
            }
            if (exp.before(new Date())) {
                throw AppException.builder().status(ErrorMessage.JWT_EXPIRED).build();
            }
            return claims;
        } catch (ParseException | JOSEException e) {
            throw AppException.builder().status(ErrorMessage.INVALID_JWT).build();
        }
    }

    private byte[] decodedSecret() {
        return Base64.getDecoder().decode(jwtProperties.getBase64Secret());
    }
}
