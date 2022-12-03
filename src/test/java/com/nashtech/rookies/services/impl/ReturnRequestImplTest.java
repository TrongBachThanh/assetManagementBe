package com.nashtech.rookies.services.impl;

import com.nashtech.rookies.dto.response.returnRequest.ReturnRequestDto;
import com.nashtech.rookies.entity.Assignment;
import com.nashtech.rookies.entity.ReturnRequest;
import com.nashtech.rookies.entity.Users;
import com.nashtech.rookies.exceptions.InvalidDataInputException;
import com.nashtech.rookies.mapper.AssignmentMapper;
import com.nashtech.rookies.mapper.ReturnRequestMapper;
import com.nashtech.rookies.repository.AssetRepository;
import com.nashtech.rookies.repository.AssignmentRepository;
import com.nashtech.rookies.repository.ReturnRequestRepository;
import com.nashtech.rookies.repository.UsersRepository;
import com.nashtech.rookies.security.userprincal.UserPrinciple;
import com.nashtech.rookies.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReturnRequestImplTest {
    ReturnRequestRepository returnRequestRepository;
    AssignmentRepository assignmentRepository;
    ReturnRequestMapper returnRequestMapper;
    UsersRepository usersRepository;
    Authentication authentication;


    UserPrinciple userPrinciple;
    ReturnRequestServiceImpl returnRequestServiceImpl;

    @BeforeEach
    void beforeEach() {
        assignmentRepository = mock(AssignmentRepository.class);
        usersRepository = mock(UsersRepository.class);
        userPrinciple = mock(UserPrinciple.class);
        authentication = mock(Authentication.class);
        returnRequestMapper = mock(ReturnRequestMapper.class);
        returnRequestRepository = mock(ReturnRequestRepository.class);
        returnRequestServiceImpl = new ReturnRequestServiceImpl(returnRequestRepository, assignmentRepository, returnRequestMapper, usersRepository);
    }

    @Test
    void createReturnRequest_ShouldReturnException_WhenASMIsNull(){
        Long id = 1L;
        InvalidDataInputException actualException = assertThrows(InvalidDataInputException.class, () -> {
            returnRequestServiceImpl.createReturnRequest(id);
        });
        assertEquals("Not found this assignment", actualException.getMessage());
    }

    @Test
    void createReturnRequest_ShouldReturnException_WhenStateNotCorrect(){
        Long id = 1L;
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        UserPrinciple userPrinciple1 = new UserPrinciple();
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        when(authentication.getPrincipal()).thenReturn(userPrinciple1);
        Assignment assignment = new Assignment();
        Optional<Assignment> checkAssignment= Optional.of(assignment);
        when( assignmentRepository.findById(id)).thenReturn(checkAssignment);
        assignment=checkAssignment.get();
        assignment.setState("123");
        InvalidDataInputException actualException = assertThrows(InvalidDataInputException.class, () -> {
            returnRequestServiceImpl.createReturnRequest(id);
        });
        assertEquals("State must be Accepted", actualException.getMessage());
    }
    @Test
    void createReturnRequest_ShouldReturnException_WhenAssetAlreadyReturned(){
        Long id = 1L;

        Assignment assignment = new Assignment();
        Optional<Assignment> checkAssignment= Optional.of(assignment);
        when( assignmentRepository.findById(id)).thenReturn(checkAssignment);
        assignment=checkAssignment.get();
        assignment.setState("Accepted");
        assignment.setCheckReturn(true);
        InvalidDataInputException actualException = assertThrows(InvalidDataInputException.class, () -> {
            returnRequestServiceImpl.createReturnRequest(id);
        });
        assertEquals("This asset is returned", actualException.getMessage());
    }
    @Test
    void createReturnRequest_ShouldReturnRequestDto_WhenDataValid(){
        Long id = 1L;
        Users user = mock(Users.class);
        ReturnRequestDto dto = mock(ReturnRequestDto.class);
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setState("Accepted");
        assignment.setCheckReturn(false);
        Optional<Assignment> checkAssignment= Optional.of(assignment);
        when( assignmentRepository.findById(id)).thenReturn(checkAssignment);
//        assignment=checkAssignment.get();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserPrinciple userPrinciple1 = new UserPrinciple();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userPrinciple1);
        when(usersRepository.findUsersById(userPrinciple1.getId())).thenReturn(user);
        ReturnRequest returnRequest = new ReturnRequest();
        ReturnRequest newRequest = new ReturnRequest();

        returnRequest.setAssignment(assignment);
        returnRequest.setState("Waiting for returning");
//        assignment.setCheckReturn(true);
        returnRequest.setRequestBy(user.getUsername());
        when(returnRequestRepository.save(any())).thenReturn(newRequest);
        when(returnRequestMapper.mapToReturnRequestDto(newRequest)).thenReturn(dto);
        ReturnRequestDto actual =returnRequestServiceImpl.createReturnRequest(id);
       assertEquals(actual,dto);
    }
}