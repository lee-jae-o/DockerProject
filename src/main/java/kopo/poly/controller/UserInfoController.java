package kopo.poly.controller;

import jakarta.persistence.Cacheable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {
    private final IUserInfoService userInfoService;

    @GetMapping(value="userRegForm")
    public String userRegForm() {
        log.info(this.getClass().getName() + ".user/userRegForm start");
        log.info(this.getClass().getName() + ".user/userRegForm end");

        return "user/userRegForm";
    }

    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserExist(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".getuserIdExist Start!");
        String userId = CmmUtil.nvl(request.getParameter("userId"));
        log.info("userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getUserIdExists Emd!");

        return rDTO;
    }
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".insertUserInfo start!");

        String msg;

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String password = CmmUtil.nvl(request.getParameter("password"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
        String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("password : " + password);
        log.info("email : " + email);
        log.info("addr1 : " + addr1);
        log.info("addr2 : " + addr2);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .addr1(addr1)
                .addr2(addr2)
                .regId(userId)
                .chgId(userId)
                .build();

        int res = userInfoService.insertUserInfo(pDTO);

        log.info("회원가입 결과(res) " + res);

        if (res == 1) {
            msg = "회원가입되었습니다.";
        } else if (res == 2) {
            msg = "이미 가입된 아이디입니다.";

        } else {
            msg = "오류로 인해 회원가입이 실패하였습니다.";
        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();

        log.info(this.getClass().getName() + ".insertUserInfo End");

        return dto;
    }
    @GetMapping(value = "login")
    public String login() {
        log.info(this.getClass().getName() + ".user/login start");
        log.info(this.getClass().getName() + ".user/login End!");

        return "user/login";
    }
    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".loginProc start");

        String msg;

        String user_id = CmmUtil.nvl(request.getParameter("user_id"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("user_id : "  + user_id);
        log.info("password : " + password);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(user_id)
                .password(EncryptUtil.encHashSHA256(password)).build();

        int res = userInfoService.getUserLogin(pDTO);

        log.info("res : " + res);

        if (res == 1) {
            msg = "로그인이 성공했습니다.";
            session.setAttribute("SS_USER_ID", user_id);
        } else {
            msg = "아이디와 비밀번호가 올바르지 않습니다.";
        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();
        log.info(this.getClass().getName() + ".loginProc End!");

        return dto;
    }
    @GetMapping(value = "loginSuccess")
    public String loginSuccess() {
        log.info(this.getClass().getName() + ".user/loginSuccess Start!");
        log.info(this.getClass().getName() + ".user/loginSuccess End!");

        return "user/loginSuccess";
    }
    @ResponseBody
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {
        log.info(this.getClass().getName() + ".logout Start!");

        session.setAttribute("SS_USER_ID", "");
        session.removeAttribute("SS_USER_ID");

        MsgDTO dto = MsgDTO.builder().result(1).msg("로그아웃하였습니다.").build();

        log.info(this.getClass().getName() + ".logout End!");

        return dto;
    }


    @GetMapping(value = "/withdrawForm")
    public String withdrawForm() {
        return "user/withdrawForm";
    }


    @PostMapping(value = "/withdrawUser")
    @ResponseBody
    public MsgDTO withdrawUser(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".withdrawUser start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        String password = request.getParameter("password");

        // 세션에 로그인된 사용자 정보가 없는 경우
        if (userId == null || userId.isEmpty()) {
            return MsgDTO.builder().result(0).msg("세션이 만료되었습니다. 다시 로그인해주세요.").build();
        }

        // 아이디와 비밀번호를 이용하여 회원 탈퇴 시도
        int res = userInfoService.withdrawUser(userId, password);

        String msg;
        if (res == 1) {
            // 회원 탈퇴 성공 시 세션에서 사용자 정보 제거
            session.setAttribute("SS_USER_ID", "");
            session.removeAttribute("SS_USER_ID");
            msg = "회원 탈퇴가 완료되었습니다.";
        } else {
            msg = "회원 탈퇴 중 오류가 발생했습니다.";
        }

        log.info(this.getClass().getName() + ".withdrawUser End!");

        return MsgDTO.builder().result(res).msg(msg).build();
    }




}
