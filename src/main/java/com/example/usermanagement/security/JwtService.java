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
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(String fullName, String username, String roleCode){
        try{
            Instant now = Instant.now();
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("fullName", fullName)
                    .claim("role", roleCode)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(jwtProperties.getTokenValidityInSeconds())))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
            signedJWT.sign(new MACSigner(decodedSecret()));
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
