package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.CategoriesDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.CategoryRequest;
import com.example.wealthwise_api.DTO.UsersListRequest;
import com.example.wealthwise_api.Entity.Categories;
import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.DTO.UserData;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserDAO userDAO;
    private final CategoriesDAO categoriesDAO;
    private final JWTUtil jwtUtil;


    public AdminService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil, CategoriesDAO categoriesDAO) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.categoriesDAO=categoriesDAO;
    }

    private String getUserEmailFromToken(HttpServletRequest request) throws BadRequest {
        String token = request.getHeader("Authorization").split("Bearer ")[1];

        if(token==null || token.isEmpty()) throw new IllegalArgumentException("Token is empty");

        return jwtUtil.getEmail(token);
    }

    public ResponseEntity<List<UserData>> getAllUsers(HttpServletRequest request){
            try {
                String emailPresentUser = getUserEmailFromToken(request);

                List<UserEntity> userEntityList = userDAO.findAll();

                if (userEntityList.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }

                List<UserData> userDataResponsesList = userEntityList.stream()
                        .filter(userEntity -> !emailPresentUser.equals(userEntity.getEmail()))
                        .map(userEntity -> new UserData(userEntity.getIdUser(), userEntity.getName(),
                                userEntity.getSurname(), userEntity.getEmail(), userEntity.getActive(),
                                userEntity.getRole(), userEntity.getBirthDay())).toList();

                return new ResponseEntity<>(userDataResponsesList, HttpStatus.OK);
            }catch (IllegalArgumentException e) {
                return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    public ResponseEntity<String> blockUsers(UsersListRequest userBlockRequest, HttpServletRequest request){
        try {
            Optional<UserEntity> presentUser = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if (presentUser.isEmpty() || presentUser.get().getRole() != Role.ADMIN) {
                return new ResponseEntity<>("Lack of permissions", HttpStatus.UNAUTHORIZED);
            }

            if (userBlockRequest == null || userBlockRequest.getEmails().isEmpty()) {
                return new ResponseEntity<>("No users to block", HttpStatus.BAD_REQUEST);
            }

            List<UserEntity> candidateToBlock = userDAO.findAll().stream()
                    .filter(userEntity -> userBlockRequest.getEmails().contains(userEntity.getEmail()))
                    .filter(UserEntity::getActive)
                    .toList();

            if (candidateToBlock.isEmpty()) {
                return new ResponseEntity<>("No users found to block.", HttpStatus.NO_CONTENT);
            }

            candidateToBlock.forEach(userEntity -> {
                userEntity.setActive(false);
                userDAO.save(userEntity);
            });

            return new ResponseEntity<>("Users blocked successfully", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> unblockUsers(UsersListRequest usersUnblockRequest, HttpServletRequest request){
        try {
            Optional<UserEntity> presentUser = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if (presentUser.isEmpty() || presentUser.get().getRole() != Role.ADMIN) {
                return new ResponseEntity<>("Lack of permissions", HttpStatus.UNAUTHORIZED);
            }

            if (usersUnblockRequest == null || usersUnblockRequest.getEmails().isEmpty()) {
                return new ResponseEntity<>("No users to block", HttpStatus.BAD_REQUEST);
            }

            List<UserEntity> candidateToUnblock = userDAO.findAll().stream()
                    .filter(userEntity -> usersUnblockRequest.getEmails().contains(userEntity.getEmail()))
                    .filter(userEntity -> !userEntity.getActive())
                    .toList();

            if (candidateToUnblock.isEmpty()) {
                return new ResponseEntity<>("No users found to block.", HttpStatus.NO_CONTENT);
            }

            candidateToUnblock.forEach(userEntity -> {
                userEntity.setActive(true);
                userDAO.save(userEntity);
            });

            return new ResponseEntity<>("Users unblocked successfully", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteUsers(UsersListRequest usersDeleteRequest , HttpServletRequest request){
        try {
            Optional<UserEntity> presentUser = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if (presentUser.isEmpty() || presentUser.get().getRole() != Role.ADMIN) {
                return new ResponseEntity<>("Lack of permissions", HttpStatus.UNAUTHORIZED);
            }

            if (usersDeleteRequest == null || usersDeleteRequest.getEmails().isEmpty()) {
                return new ResponseEntity<>("No users provided for deletion", HttpStatus.BAD_REQUEST);
            }

            Optional<List<UserEntity>> optionalUsersToDelete = Optional.ofNullable(userDAO.findAllByEmailIn(usersDeleteRequest.getEmails()));

            if (optionalUsersToDelete.isEmpty() || optionalUsersToDelete.get().isEmpty()) {
                return new ResponseEntity<>("No matching users found for deletion.", HttpStatus.NOT_FOUND);
            }

            userDAO.deleteAll(optionalUsersToDelete.get());

            return new ResponseEntity<>("Users deleted successfully", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> modifyUserData(UserData userDataRequest, HttpServletRequest request){
        try{
            Optional<UserEntity> presentUser = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if (presentUser.isEmpty() || presentUser.get().getRole() != Role.ADMIN) {
                return new ResponseEntity<>("Lack of permissions", HttpStatus.UNAUTHORIZED);
            }

            if (userDataRequest == null) {
                return new ResponseEntity<>("No users provided for modify", HttpStatus.BAD_REQUEST);
            }

            Optional<UserEntity> userToModify = userDAO.findUserById(userDataRequest.getId());

            if (userToModify.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            UserEntity user = userToModify.get();
            if (userDataRequest.getName() != null ) user.setUsername(userDataRequest.getName());
            if (userDataRequest.getSurname() != null) user.setSurname(userDataRequest.getSurname());
            if (userDataRequest.getBirthDay() != null) user.setBirthDay(userDataRequest.getBirthDay());
            if (userDataRequest.getRole() != null) user.setRole(userDataRequest.getRole());
            if (userDataRequest.getIsActive() != null) user.setActive(userDataRequest.getIsActive());

            userDAO.save(user);

            return new ResponseEntity<>("User data updated successfully", HttpStatus.OK);

        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> addCategory(CategoryRequest categoryRequest, HttpServletRequest request){
        try{
            Optional<UserEntity> presentUser = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if (presentUser.isEmpty() || presentUser.get().getRole() != Role.ADMIN) {
                return new ResponseEntity<>("Lack of permissions", HttpStatus.UNAUTHORIZED);
            }

            if(categoryRequest == null){
                return new ResponseEntity<>("Lack category name", HttpStatus.BAD_REQUEST);
            }

            if(categoriesDAO.exists(categoryRequest.getName())){
                return new ResponseEntity<>("Category already exists", HttpStatus.CONFLICT);
            }

            Categories newCategories = new Categories(categoryRequest.getName());

            categoriesDAO.save(newCategories);

            return new ResponseEntity<>("Categories added successfully",HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
