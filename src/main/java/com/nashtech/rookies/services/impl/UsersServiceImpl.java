package com.nashtech.rookies.services.impl;

import com.nashtech.rookies.dto.request.user.UserRequestDto;
import com.nashtech.rookies.entity.Users;
import com.nashtech.rookies.exceptions.InvalidDataInputException;
import com.nashtech.rookies.mapper.UserMapper;
import com.nashtech.rookies.repository.UsersRepository;
import com.nashtech.rookies.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UsersServiceImpl implements com.nashtech.rookies.services.interfaces.UsersService {

    UsersRepository usersRepository;
    UserMapper userMapper;
    UserUtil userUtil;

    @Autowired
    public UsersServiceImpl(UserUtil userUtil, UsersRepository usersRepository, UserMapper userMapper) {
        this.userUtil = userUtil;
        this.usersRepository = usersRepository;
        this.userMapper = userMapper;
    }

    public static final String location = "HCM";

//    Find all users by admin locations
    @Override
    public List<Users> showAll(){
        return usersRepository.findByLocation(location);
    }

    @Override
    public List<Users> search(String search, List<String> role) throws Exception {

        if(search.equals("undefined") || role.contains("undefined")){
            throw new Exception("Search or role must not be undefined");
        }

//      Show all users
        if(search.equals("null") && role.contains("null") || role.contains("all")) {
            return usersRepository.findByLocation(location);
        }
//      Show all users by search username or code
        else if((Objects.equals(role.get(0), "null") || Objects.equals(role.get(0), "all")) && !search.equals("null")){
            return usersRepository.findByLocationAndUsernameContainingOrCodeContaining(location, search, search);
        }
//       Show all users list by role
        else if (!role.contains("all")  && search.equals("null")){
            return usersRepository.findByLocationAndRoleIn(location, role);
        }
//       Show all users list by role and (username or code)
        else{
            return usersRepository.search(location, role, search);
        }
    }
    

//    Show information of user by id
    @Override
    public Users findByUserId(Long userId) throws Exception {
        if(userId == 0){
            throw new Exception("UserID must not be empty");
        }

        Users users = usersRepository.findUsersById(userId);

        if(users == null){
            throw new Exception("Can't find any user with id: " + userId);
        }else{
            return users;
        }
    }

//    Sort users by JoinedDate
    @Override
    public List<Users> sortByJoinedDateDesc() {
        return usersRepository.findByLocationOrderByJoinedDateDesc(location);
    }

    @Override
    public List<Users> sortByJoinedDateAsc() {
        return usersRepository.findByLocationOrderByJoinedDateAsc(location);
    }

//    Sort users by code
    @Override
    public List<Users> sortByCodeDesc() {
        return usersRepository.findByLocationOrderByCodeDesc(location);
    }

    @Override
    public List<Users> sortByCodeAsc() {
        return usersRepository.findByLocationOrderByCodeAsc(location);
    }

//    Sort users by full name
    @Override
    public List<Users> sortByFullNameDesc() {
        return usersRepository.findByLocationOrderByFullNameDesc(location);
    }

    @Override
    public List<Users> sortByFullNameAsc() {
        return usersRepository.findByLocationOrderByFullNameAsc(location);
    }

//    Sort users by role
    @Override
    public List<Users> sortByRoleDesc() {
        return usersRepository.findByLocationOrderByRoleDesc(location);
    }

    @Override
    public List<Users> sortByRoleAsc() {
        return usersRepository.findByLocationOrderByRoleAsc(location);
    }

    @Override
    public void createUser(UserRequestDto dto) {

        if (!dto.getRole().equals("ADMIN") && !dto.getRole().equals("STAFF")) {
            throw new InvalidDataInputException("Role is invalid");
        }

        if (!userUtil.isValidDate(dto.getDob())) {
            throw new InvalidDataInputException("Date of birth is invalid");
        }

        if (!userUtil.isValidDate(dto.getJoinedDate())) {
            throw new InvalidDataInputException("Join date is invalid");
        }

        Date dobDate = userUtil.convertStrDateToObDate(dto.getDob());

        Date joinedDate = userUtil.convertStrDateToObDate(dto.getJoinedDate());

        if (!userUtil.isValidAge(dobDate)) {
            throw new InvalidDataInputException("User is under 18");
        }

        if (joinedDate.before(dobDate)) {
            throw new InvalidDataInputException("Joined date is not later than Date of Birth");
        }

        dto.setFirstName(userUtil.capitalizeWord(dto.getFirstName()));
        dto.setLastName(userUtil.capitalizeWord(dto.getLastName()));

        String username = userUtil.generatePrefixUsername(dto.getFirstName(), dto.getLastName());

        List<Users> users = usersRepository.findByUsernameContaining(username);

        if (!users.isEmpty()) {
            String suffix = userUtil.generateSuffixUsername(users, username);
            username = username + suffix;
        }
        String password = userUtil.generatePassword(username, dto.getDob());

        Long count = usersRepository.count();

        String code = userUtil.generateCode(count);

        Users user = userMapper.mapUserUpdateDtoToUser(dto, username, dobDate, joinedDate, code);

        user.setLocation("HCM");
        user.setPassword(password);
        user = usersRepository.save(user);

    }

}
