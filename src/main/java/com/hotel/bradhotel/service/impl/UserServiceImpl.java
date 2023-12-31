package com.hotel.bradhotel.service.impl;

import com.hotel.bradhotel.dao.UserDao;
import com.hotel.bradhotel.dto.UserLoginRequest;
import com.hotel.bradhotel.dto.UserRegisterRequest;
import com.hotel.bradhotel.model.User;
import com.hotel.bradhotel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public User getUserByEmail(UserRegisterRequest userRegisterRequest) {

        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        if(user != null){
            log.warn("該email {} 已經被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        log.warn("該email {} 尚未被註冊",userRegisterRequest.getEmail());

        return userDao.getUserByEmail(userRegisterRequest.getEmail());
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
      //檢查註冊email
      User user =  userDao.getUserByEmail(userRegisterRequest.getEmail());

      if(user != null){
          log.warn("該email {} 已經被註冊", userRegisterRequest.getEmail());
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      //使用MD5生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        userRegisterRequest.setPassword(hashedPassword);

      //創建註冊的帳號
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {

        User user = userDao.getUserByEmail(userLoginRequest.getEmail());

        //檢查user是否存在
        if (user == null){
            log.warn("該email {} 未被註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //使用MD5生成密碼的雜湊值
        String hashedPassword =DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());



        //比較密碼
        if (user.getPassword().equals(hashedPassword)){
            return user;
        }else {
            log.warn("該email {} 密碼不正確", userLoginRequest.getEmail());
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }


    }
}
