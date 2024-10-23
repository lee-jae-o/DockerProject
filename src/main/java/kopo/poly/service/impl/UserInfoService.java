package kopo.poly.service.impl;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.repository.UserInfoRepository;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {

    private final UserInfoRepository userInfoRepository;


    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO;

        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("userId : " + userId);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if (rEntity.isPresent()) {
            rDTO = UserInfoDTO.builder().existsYn("Y").build();
        } else {
            rDTO = UserInfoDTO.builder().existsYn("N").build();
        }
        log.info(this.getClass().getName() + ".getUserIdExist End!");

        return rDTO;
    }


    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertUserInfo start!");

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String userName = CmmUtil.nvl(pDTO.userName());
        String password = CmmUtil.nvl(pDTO.password());
        String email = CmmUtil.nvl(pDTO.email());
        String addr1 = CmmUtil.nvl(pDTO.addr1());
        String addr2 = CmmUtil.nvl(pDTO.addr2());

        log.info("pDTO : " +pDTO);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if (rEntity.isPresent()) {
            res = 2;
        } else {
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId).userName(userName)
                    .password(password)
                    .email(email)
                    .addr1(addr1).addr2(addr2)
                    .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            userInfoRepository.save(pEntity);

            userInfoRepository.save(pEntity);

            rEntity = userInfoRepository.findByUserId(userId);

            if (rEntity.isPresent()) {
                res = 1;
            } else {
                res = 0;
            }
        }
        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return res;
    }

    @Override
    public int getUserLogin(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getUserLogin Start!");

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String password = CmmUtil.nvl(pDTO.password());

        log.info("userId : " + userId);
        log.info("password : " + password);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserIdAndPassword(userId, password);

        if (rEntity.isPresent()) {
            if (rEntity.isPresent()) {
                res = 1;
            }
        }
        log.info(this.getClass().getName() + ".getUserLoginCheck End!");

        return res;

    }




    @Override
    public int withdrawUser(String userId, String password) throws Exception {
        log.info(this.getClass().getName() + ".withdrawUser Start!");

        int res = 0;

        // 사용자 아이디로 해당 사용자 정보를 조회합니다.
        Optional<UserInfoEntity> userEntityOptional = userInfoRepository.findByUserId(userId);

        // 사용자 정보가 존재하는 경우에만 탈퇴 처리를 수행합니다.
        if (userEntityOptional.isPresent()) {
            // 사용자 정보를 데이터베이스에서 삭제합니다.
            userInfoRepository.delete(userEntityOptional.get());
            // 삭제가 성공했는지 확인합니다.
            boolean isDeleted = userInfoRepository.findByUserId(userId).isEmpty();
            if (isDeleted) {
                res = 1; // 회원 탈퇴 성공
            } else {
                res = 0; // 회원 탈퇴 실패
            }
        } else {
            res = 0; // 사용자 정보가 존재하지 않음
        }

        log.info(this.getClass().getName() + ".withdrawUser End!");
        return res;
    }
}
