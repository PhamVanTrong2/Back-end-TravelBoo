package com.bootravel.common.security.jwt.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.common.security.jwt.entity.RefreshToken;
import com.bootravel.entity.UsersEntity;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Optional;

@Repository
public class RefreshTokenRepository extends CommonRepository {

    public RefreshTokenRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return Optional.of(new RefreshToken());
    }


    public int deleteByUserEntity(UsersEntity userEntity) {
        return -1;
    }

    public void delete(RefreshToken token) {
    }

    public RefreshToken save(RefreshToken userEntity) {
        return new RefreshToken();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlRefreshTokenRepository.xml";
    }
}