package kopo.poly.service;

import kopo.poly.dto.UserInfoDTO;

public interface IUserInfoService {

    int insertUserInfo(UserInfoDTO pDTO) throws Exception;


    int getUserLogin(UserInfoDTO pDTO) throws Exception;


    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;


    int withdrawUser(String userId, String password) throws Exception;



}
