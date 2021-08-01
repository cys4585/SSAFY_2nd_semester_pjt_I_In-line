package com.inline.sub2.api.service;

import com.inline.sub2.api.dto.UserDto;
import com.inline.sub2.api.dto.UserRegistDto;
import com.inline.sub2.api.dto.UserUpdateDto;
import com.inline.sub2.db.entity.DeptEntity;
import com.inline.sub2.db.entity.JobEntity;
import com.inline.sub2.db.entity.OfficeEntity;
import com.inline.sub2.db.entity.UserEntity;
import com.inline.sub2.db.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    DeptService deptService;

    @Autowired
    JobService jobService;

    @Autowired
    OfficeService officeService;

    @Autowired
    OnBoardService onBoardService;

    @Override
    public UserEntity registAdmin(UserRegistDto admin) {
        Date now = new Date();
        UserEntity userEntity = new UserEntity();
        try {
            OfficeEntity officeEntity = officeService.registOffice(admin.getOfficeName()); //회사 등록
            log.info("회사 등록 완료");
            DeptEntity deptEntity = deptService.getDeptId(admin.getDeptName(), 1l); //부서 번호 조회
            JobEntity jobEntity = jobService.getJobId(admin.getJobName(), 1l); //직책 번호 조회

            //유저 정보 기입\
            userEntity.setEmail(admin.getEmail());
            userEntity.setDeptId(deptEntity.getDeptId());
            userEntity.setJobId(jobEntity.getJobId());
            userEntity.setName(admin.getName());
            userEntity.setPhone(admin.getPhone());
            userEntity.setPassword(passwordEncoder.encode(admin.getPassword()));
            userEntity.setAuth("ROLE_ADMIN");
            userEntity.setJoinDate(now);
            userEntity.setOfficeId(officeEntity.getOfficeId());

        } catch (Exception e) {
            log.error("회사 등록 실패 : {}", e);
        }

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity registUser(UserRegistDto user) {
        Date now = new Date();
        UserEntity userEntity = new UserEntity();

        //부서 번호 조회
        DeptEntity deptEntity = deptService.getDeptId(user.getDeptName(), 1l);
        //직책 번호 조회
        JobEntity jobEntity = jobService.getJobId(user.getJobName(), 1l);

        //유저정보 기입
        userEntity.setEmail(user.getEmail());
        userEntity.setOfficeId(user.getOfficeId());
        userEntity.setDeptId(deptEntity.getDeptId());
        userEntity.setJobId(jobEntity.getJobId());
        userEntity.setName(user.getName());
        userEntity.setPhone(user.getPhone());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setAuth("ROLE_USER");
        userEntity.setJoinDate(now);

        //onBoard테이블 내 사용자 데이터 삭제
        try {
            onBoardService.deleteUserOnboard(user.getEmail());
            log.info("onBoard테이블 사용자 데이터 삭제 성공");
        }catch(Exception e){
            log.error("onBoard테이블 사용자 데이터 삭제 실패 : {}", e);
        }

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity getUserInfo(Long userId) {
        return userRepository.findByUserId(userId);
    }


    @Override
    public UserEntity updateUser(UserUpdateDto userUpdateDto) {
        UserEntity userEntity = userRepository.findByUserId(userUpdateDto.getUserId());

        DeptEntity deptEntity = deptService.getDeptId(userUpdateDto.getDeptName(),userEntity.getOfficeId());
        JobEntity jobEntity = jobService.getJobId(userUpdateDto.getJobName(), userEntity.getOfficeId());

        userEntity.setDeptId(deptEntity.getDeptId());
        userEntity.setJobId(jobEntity.getJobId());

        userEntity.setUserId(userUpdateDto.getUserId());
        userEntity.setName(userUpdateDto.getName());
        userEntity.setNickName(userUpdateDto.getNickName());
        userEntity.setPhone(userUpdateDto.getPhone());
        return userRepository.save(userEntity);

    }

    @Override
    public void updatePassword(UserEntity userEntity,String password) {
        userEntity.setPassword(passwordEncoder.encode(password));
         userRepository.save(userEntity);
    }



}