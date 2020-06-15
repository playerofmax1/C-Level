package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.relation.RelRoleFunctionDAO;
import com.clevel.kudu.api.dao.relation.RelRoleScreenDAO;
import com.clevel.kudu.api.dao.security.RoleDAO;
import com.clevel.kudu.api.dao.working.*;
import com.clevel.kudu.api.exception.*;
import com.clevel.kudu.api.external.email.template.PasswordResetEmail;
import com.clevel.kudu.model.SystemConfig;
import com.clevel.kudu.api.model.db.master.Rate;
import com.clevel.kudu.api.model.db.relation.RelRoleFunction;
import com.clevel.kudu.api.model.db.relation.RelRoleScreen;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.api.model.db.working.PerformanceYear;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.model.db.working.UserPerformance;
import com.clevel.kudu.api.model.db.working.UserTimeSheet;
import com.clevel.kudu.api.rest.mapper.RoleMapper;
import com.clevel.kudu.api.rest.mapper.UserMapper;
import com.clevel.kudu.api.rest.mapper.UserPerformanceMapper;
import com.clevel.kudu.api.rest.mapper.UserTSMapper;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.api.util.EncryptUtil;
import com.clevel.kudu.dto.security.AuthenticationResult;
import com.clevel.kudu.dto.security.RoleDTO;
import com.clevel.kudu.dto.security.RoleFunctionRequest;
import com.clevel.kudu.dto.security.RoleScreenRequest;
import com.clevel.kudu.dto.working.UserDTO;
import com.clevel.kudu.dto.working.UserPerformanceDTO;
import com.clevel.kudu.dto.working.UserTimeSheetDTO;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.model.Screen;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.LookupUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Stateless
public class SecurityManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private UserDAO userDAO;
    @Inject
    private UserPerformanceDAO userPerformanceDAO;
    @Inject
    private PerformanceYearDAO performanceYearDAO;
    @Inject
    private RelRoleScreenDAO relRoleScreenDAO;
    @Inject
    private RelRoleFunctionDAO relRoleFunctionDAO;
    @Inject
    private RateDAO rateDAO;
    @Inject
    private RoleDAO roleDAO;
    @Inject
    private UserTimeSheetDAO userTimeSheetDAO;
    @Inject
    private UserMapper userMapper;
    @Inject
    private RoleMapper roleMapper;
    @Inject
    private UserTSMapper userTSMapper;
    @Inject
    private UserPerformanceMapper userPerformanceMapper;

    @Inject
    PasswordResetEmail passwordResetEmail;

    @Inject
    public SecurityManager() {
    }

    public AuthenticationResult authenticate(String userName, String password) throws CredentialException, InActiveStateException, ForceChangePwdException {
        log.debug("authenticate. (userName: {}, password: [HIDDEN])", userName);

        User user = userDAO.findByLoginName(userName.trim().toLowerCase());
        if (user == null) {
            log.debug("user not found! (user: {})", userName);
            throw new CredentialException("user not found! (login: " + userName + ")");
        }

        // active status validation
        if (user.getStatus() == RecordStatus.INACTIVE) {
            log.debug("account is disabled!");
            throw new InActiveStateException("account is disabled!");
        }

        // password validation
        checkPassword(user, password);

        // =============== pass authentication process ===========

        AuthenticationResult result = new AuthenticationResult();
        result.setUserId(user.getId());
        result.setUserName(user.getLoginName());
        result.setName(user.getName());
        result.setLastName(user.getLastName());
        result.setTsStartDate(user.getTsStartDate());

        List<Screen> screens = new ArrayList<>();
        List<Function> functions = new ArrayList<>();
        if (user.getRole() != null) {

            // role screen
            List<RelRoleScreen> roleScreens = relRoleScreenDAO.getListByRole(user.getRole());
            for (RelRoleScreen s : roleScreens) {
                screens.add(s.getScreen());
            }
            result.setScreenList(screens);

            // role function
            List<RelRoleFunction> roleFunctions = relRoleFunctionDAO.getListByRole(user.getRole());
            for (RelRoleFunction f : roleFunctions) {
                functions.add(f.getFunction());
            }
            result.setFunctionList(functions);
        } else {
            result.setScreenList(screens);
            result.setFunctionList(functions);
        }

        result.setRole("ROLE_USER");

        // update last login
        user.setLastLoginDate(DateTimeUtil.now());
        userDAO.persist(user);

        // force change password status
        if (user.getStatus() == RecordStatus.FORCE_CHANGE_PWD) {
            log.debug("force change password required for loginName: {})", user.getLoginName());
            throw new ForceChangePwdException("force change password required!", result);
        }

        return result;
    }

    public UserDTO changePwd(long userId, String oldPwd, String newPwd) throws RecordNotFoundException, CredentialException {
        log.debug("changePwd. (userId: {})", userId);

        User user = userDAO.findById(userId);

        // password validation
        checkPassword(user, oldPwd);

        user.setPassword(EncryptUtil.getPasswordHash(newPwd, EncryptUtil.getSalt()));
        user.setStatus(RecordStatus.ACTIVE);
        userDAO.persist(user);

        log.debug("change password success.");
        return userMapper.toDTO(user);
    }

    public UserDTO resetPwd(long userId) throws RecordNotFoundException, IOException, EmailException {
        log.debug("changePwd. (userId: {})", userId);

        User user = userDAO.findById(userId);

        String newPwd = RandomStringUtils.random(8, true, true);

        user.setPassword(EncryptUtil.getPasswordHash(newPwd, EncryptUtil.getSalt()));
        user.setStatus(RecordStatus.ACTIVE);
        userDAO.persist(user);

        // notification
        passwordResetEmail.sendMail(user, newPwd);

        log.debug("reset password success.");
        return userMapper.toDTO(user);
    }

    private void checkPassword(User user, String password) throws CredentialException {
        String[] tmp = user.getPassword().split(":");
        byte[] salt = Base64.getDecoder().decode(tmp[1]);
        String hash = EncryptUtil.getPasswordHash(password, salt);
        if (!hash.equals(user.getPassword())) {
            log.debug("password is incorrect!");
            throw new CredentialException("password is incorrect");
        }
    }

    public RoleDTO createNewRole(long userId, RoleDTO roleDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewRole. (userId: {}, roleDTO: {})", userId, roleDTO);

        User user = userDAO.findById(userId);
        Date now = DateTimeUtil.now();

        // using role running code no need to validate
//        // validate role code
//        if (roleDAO.isRoleCodeExist(company, roleDTO.getCode())) {
//            log.debug("role code is already in use.");
//            throw new ValidationException(APIResponse.CODE_ALREADY_IN_USE);
//        }

        Role role = roleMapper.toEntity(roleDTO);
        // force using auto running code
//        role.setCode(businessManager.getRunningNumber(CompanyReferenceType.ROLE_RUNNING, now));
        log.debug("role: {}", role);

        role.setCreateDate(now);
        role.setCreateBy(user);
        role.setModifyDate(now);
        role.setModifyBy(user);
        role.setStatus(RecordStatus.ACTIVE);
        role = roleDAO.persist(role);

        return roleMapper.toDTO(role);
    }

    public RoleDTO getRoleInfo(long userId, long roleId) throws RecordNotFoundException {
        log.debug("getRoleInfo. (userId: {}, roleId: {})", userId, roleId);

        // validate user
        userDAO.findById(userId);

        return roleMapper.toDTO(roleDAO.findById(roleId));
    }

    public void updateRoleInfo(long userId, RoleDTO roleDTO) throws RecordNotFoundException {
        log.debug("updateRoleInfo. (userId: {}, roleDTO: {})", userId, roleDTO);

        // validate user
        User user = userDAO.findById(userId);

        Role role = roleDAO.findById(roleDTO.getId());

        Role after = roleMapper.updateFromDTO(roleDTO, role);

        Date now = DateTimeUtil.now();
        after.setModifyDate(now);
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        roleDAO.persist(after);
    }

    public List<RoleDTO> getRoleList() throws RecordNotFoundException {
        log.debug("getRoleList.");

        List<Role> roleList = roleDAO.findAll();

        return roleMapper.toDTO(roleList.stream());
    }

    public void deleteRole(long userId, RoleDTO roleDTO) throws RecordNotFoundException, ValidationException {
        log.debug("deleteRole. (userId: {},roleDTO: {})", userId, roleDTO);

        // validate user
        User user = userDAO.findById(userId);

        Role deleteRole = roleDAO.findById(roleDTO.getId());

        //validate no user attach to this role
        if (userDAO.getUserByRoleCount(deleteRole) > 0) {
            log.debug("role currently in use. (role: {})", deleteRole);
            throw new ValidationException(APIResponse.ROLE_CURRENTLY_IN_USE);
        }

        deleteRole.setStatus(RecordStatus.INACTIVE);
        deleteRole.setModifyDate(DateTimeUtil.now());
        deleteRole.setModifyBy(user);

        roleDAO.persist(deleteRole);
    }

    public RoleScreenRequest getRelRoleScreen(long userId, RoleDTO roleDTO) throws RecordNotFoundException {
        log.debug("getRelRoleScreen. (userId: {}, roleDTO: {})", userId, roleDTO);

        User user = userDAO.findById(userId);

        Role role = roleDAO.findById(roleDTO.getId());

        List<RelRoleScreen> relRoleScreenList = relRoleScreenDAO.getListByRole(role);

        RoleScreenRequest result = new RoleScreenRequest();
        result.setRole(roleDTO);

        ArrayList<Screen> screens = new ArrayList<>();
        for (RelRoleScreen relRoleScreen : relRoleScreenList) {
            screens.add(relRoleScreen.getScreen());
        }
        result.setScreenList(screens);

        return result;
    }

    public void updateRoleScreen(long userId, RoleDTO roleDTO, List<Screen> screenList) throws RecordNotFoundException {
        log.debug("updateRoleScreen. (userId: {}, roleDTO: {}, screenList: {})", userId, roleDTO, screenList);

        User user = userDAO.findById(userId);

        // remove previous setting
        Role role = roleDAO.findById(roleDTO.getId());

        relRoleScreenDAO.deleteRole(role);

        // setup new relation set
        RelRoleScreen relRoleScreen;
        List<RelRoleScreen> relRoleScreens = new ArrayList<>();
        for (Screen screen : screenList) {
            relRoleScreen = new RelRoleScreen();
            relRoleScreen.setScreen(screen);
            relRoleScreen.setRole(role);
            relRoleScreens.add(relRoleScreen);
        }

        relRoleScreenDAO.persist(relRoleScreens);
        // update role modify date
        role.setModifyBy(user);
        role.setModifyDate(DateTimeUtil.now());
        roleDAO.persist(role);

        log.debug("role's screen updated. (roleId: {}, screenList: {})", roleDTO.getId(), screenList);
    }

    public RoleFunctionRequest getRelRoleFunction(long userId, RoleDTO roleDTO) throws RecordNotFoundException {
        log.debug("getRelRoleFunction. (userId: {}, roleDTO: {})", userId, roleDTO);

        User user = userDAO.findById(userId);

        Role role = roleDAO.findById(roleDTO.getId());

        List<RelRoleFunction> relRoleFunctionList = relRoleFunctionDAO.getListByRole(role);

        RoleFunctionRequest result = new RoleFunctionRequest();
        result.setRole(roleDTO);

        ArrayList<Function> functions = new ArrayList<>();
        for (RelRoleFunction relRoleFunction : relRoleFunctionList) {
            functions.add(relRoleFunction.getFunction());
        }
        result.setFunctionList(functions);

        return result;
    }

    public void updateRoleFunction(long userId, RoleDTO roleDTO, List<Function> functionList) throws RecordNotFoundException {
        log.debug("updateRoleFunction. (userId: {}, roleDTO: {}, functionList: {})", userId, roleDTO, functionList);

        User user = userDAO.findById(userId);

        // remove previous setting
        Role role = roleDAO.findById(roleDTO.getId());

        relRoleFunctionDAO.deleteRole(role);

        // setup new relation set
        RelRoleFunction relRoleFunction;
        List<RelRoleFunction> relRoleFunctions = new ArrayList<>();
        for (Function function : functionList) {
            relRoleFunction = new RelRoleFunction();
            relRoleFunction.setFunction(function);
            relRoleFunction.setRole(role);
            relRoleFunctions.add(relRoleFunction);
        }

        relRoleFunctionDAO.persist(relRoleFunctions);
        // update role modify date
        role.setModifyBy(user);
        role.setModifyDate(DateTimeUtil.now());
        roleDAO.persist(role);

        log.debug("role's function updated. (roleId: {}, screenList: {})", roleDTO.getId(), functionList);
    }

    public UserDTO createNewUser(long userId, UserDTO userDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewUser. (userId: {}, userDTO: {})", userId, userDTO);

        User user = userDAO.findById(userId);

        // validate existing username under company
        User existingUser = userDAO.findByLoginName(userDTO.getLoginName());
        if (existingUser != null) {
            log.debug("login name is already in used.");
            throw new ValidationException(APIResponse.LOGIN_NAME_ALREADY_IN_USE);
        }

        User newUser = userMapper.toEntity(userDTO);
        log.debug("newUser: {}", newUser);

        // password validation and encryption
        if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
            throw new ValidationException(APIResponse.INVALID_INPUT_PARAMETER);
        } else {
            newUser.setPassword(EncryptUtil.getPasswordHash(newUser.getPassword()));
        }

        if (userDTO.getRate() != null) {
            Rate rate = rateDAO.findById(userDTO.getRate().getId());
            newUser.setRate(rate);
        }

        if (userDTO.getRole() != null) {
            Role role = roleDAO.findById(userDTO.getRole().getId());
            newUser.setRole(role);
        }

        Date now = DateTimeUtil.now();
        newUser.setCreateDate(now);
        newUser.setCreateBy(user);
        newUser.setModifyDate(now);
        newUser.setModifyBy(user);
        newUser.setStatus(RecordStatus.ACTIVE);

        newUser = userDAO.persist(newUser);

        return userMapper.toDTO(newUser);
    }

    public UserDTO getUserInfoById(long userId, long id) throws RecordNotFoundException {
        log.debug("getUserInfoById. (userId: {}, id: {})", userId, id);

        // validate user
        userDAO.findById(userId);
        User user = userDAO.findById(id);

        UserDTO userDTO = userMapper.toDTO(user);

        List<UserPerformance> userPerformanceList = userPerformanceDAO.findByUserId(id);
        if (userPerformanceList == null) {
            /*For users created before 2020.05.27*/
            userPerformanceList = createUserPerformance(userId, id);
        }
        userDTO.setUserPerformanceList(userPerformanceMapper.toDTO(userPerformanceList.stream()));
        log.debug("userPerformanceList = {}", userDTO.getUserPerformanceList());

        return userDTO;
    }

    public List<UserPerformance> createUserPerformance(long userId, long timeSheetUserId) throws RecordNotFoundException {
        log.debug("createUserPerformance(userId:{},timeSheetUserId:{})", userId, timeSheetUserId);
        List<UserPerformance> userPerformanceList = new ArrayList<>();

        User user = userDAO.findById(userId);
        Date now = DateTimeUtil.now();
        BigDecimal defaultTargetUtilization = new BigDecimal(app.getConfig(SystemConfig.DEFAULT_TARGET_UTILIZATION));

        List<PerformanceYear> performanceYearList = performanceYearDAO.findAll();
        UserPerformance userPerformance;
        for (PerformanceYear performanceYear : performanceYearList) {
            userPerformance = new UserPerformance();
            userPerformance.setUserId(timeSheetUserId);
            userPerformance.setPerformanceYear(performanceYear);
            userPerformance.setTargetUtilization(defaultTargetUtilization);

            userPerformance.setCreateBy(user);
            userPerformance.setCreateDate(now);
            userPerformance.setModifyBy(user);
            userPerformance.setModifyDate(now);

            userPerformanceList.add(userPerformance);
        }

        userPerformanceDAO.persist(userPerformanceList);

        return userPerformanceList;
    }

    public void updateUserInfo(long userId, UserDTO userDTO) throws RecordNotFoundException {
        log.debug("updateUserInfo. (userId: {}, userDTO: {})", userId, userDTO);

        // validate user
        userDAO.findById(userId);

        User user = userDAO.findById(userDTO.getId());
        User after = userMapper.updateFromDTO(userDTO, user);

        if (userDTO.getRate() != null) {
            Rate rate = rateDAO.findById(userDTO.getRate().getId());
            after.setRate(rate);
        } else {
            after.setRate(null);
        }

        if (userDTO.getRole() != null) {
            Role role = roleDAO.findById(userDTO.getRole().getId());
            after.setRole(role);
        } else {
            after.setRole(null);
        }

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        userDAO.persist(after);

        updateUserPerformance(user, userDTO.getId(), userDTO.getUserPerformanceList());
    }

    private void updateUserPerformance(User user, long timeSheetUserId, List<UserPerformanceDTO> userPerformanceDTOList) {
        log.debug("updateUserPerformance.userPerformanceDTOList = {}", userPerformanceDTOList);
        if (userPerformanceDTOList == null || userPerformanceDTOList.size() == 0) {
            return;
        }

        Date now = DateTimeUtil.now();
        List<UserPerformance> userPerformanceList = userPerformanceDAO.findByUserId(timeSheetUserId);
        UserPerformanceDTO userPerformanceDTO;
        for (UserPerformance userPerformance : userPerformanceList) {
            userPerformanceDTO = LookupUtil.getObjById(userPerformanceDTOList,userPerformance.getId());
            userPerformance.setTargetUtilization(userPerformanceDTO.getTargetUtilization());

            userPerformance.setModifyBy(user);
            userPerformance.setModifyDate(now);
        }

        userPerformanceDAO.persist(userPerformanceList);
    }

    /**
     * Get list of user marked as readable by the User with the {id}.
     * @param userId current user (logged in user)
     * @param id userId of the User own the result
     */
    public List<UserTimeSheetDTO> getUserViewTS(long userId, long id) throws RecordNotFoundException {
        log.debug("getUserViewTS. (userId: {}, id: {})", userId, id);

        // validate user
        userDAO.findById(userId);

        User user = userDAO.findById(id);

        return userTSMapper.toDTO(userTimeSheetDAO.findByUser(user).stream());
    }

    public void updateUserViewTS(long userId, long ownerUserId, List<UserTimeSheetDTO> userTimeSheetDTOList) throws RecordNotFoundException {
        log.debug("updateUserViewTS. (userId: {}, ownerUserId: {}, userTimeSheetDTOList: {})", userId, ownerUserId, userTimeSheetDTOList);
        // validate user
        userDAO.findById(userId);

        User user = userDAO.findById(ownerUserId);
        userTimeSheetDAO.deleteUserTimeSheet(user);

        if (userTimeSheetDTOList.isEmpty()) {
            log.debug("user TS list is empty!");
            return;
        }

        List<UserTimeSheet> userTimeSheetList = new ArrayList<>();
        UserTimeSheet userTimeSheet;

        for (UserTimeSheetDTO u : userTimeSheetDTOList) {
            userTimeSheet = new UserTimeSheet();
            userTimeSheet.setUser(user);
            userTimeSheet.setTimeSheetUser(userDAO.findById(u.getTimeSheetUser().getId()));

            userTimeSheetList.add(userTimeSheet);
        }
        log.debug("userTimeSheetList: {}", userTimeSheetList);

        userTimeSheetDAO.persist(userTimeSheetList);
    }

    public List<UserDTO> getUserList(long userId) throws RecordNotFoundException {
        log.debug("getUserList. (userId: {})", userId);

        // validate user
        User user = userDAO.findById(userId);

        return userMapper.toDTO(userDAO.findAll().stream());
    }

    public void deleteUser(long userId, UserDTO userDTO) throws RecordNotFoundException {
        log.debug("deleteUser. (userId: {}, userDTO: {})", userId, userDTO);

        User user = userDAO.findById(userId);
        User deleteUser = userDAO.findById(userDTO.getId());

        deleteUser.setStatus(RecordStatus.INACTIVE);
        deleteUser.setModifyDate(DateTimeUtil.now());
        deleteUser.setModifyBy(user);

        userDAO.persist(deleteUser);
    }

}
