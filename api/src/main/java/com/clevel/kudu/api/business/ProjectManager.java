package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.relation.RelRoleFunctionDAO;
import com.clevel.kudu.api.dao.working.*;
import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.external.email.template.AssignedTaskEmail;
import com.clevel.kudu.api.external.email.template.ExtendMandaysEmail;
import com.clevel.kudu.api.external.email.template.MDRequestEmail;
import com.clevel.kudu.api.external.email.template.RejectedMDRequestEmail;
import com.clevel.kudu.api.model.db.relation.RelRoleFunction;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.api.model.db.working.MandaysRequest;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.api.rest.mapper.MandaysRequestMapper;
import com.clevel.kudu.api.rest.mapper.ProjectMapper;
import com.clevel.kudu.api.rest.mapper.ProjectTaskExtMapper;
import com.clevel.kudu.api.rest.mapper.ProjectTaskMapper;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.api.util.MDUtil;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.*;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProjectManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private UserDAO userDAO;
    @Inject
    private TaskDAO taskDAO;
    @Inject
    private ProjectDAO projectDAO;
    @Inject
    private ProjectTaskDAO projectTaskDAO;
    @Inject
    private ProjectTaskExtDAO projectTaskExtDAO;
    @Inject
    private MandaysRequestDAO mandaysRequestDAO;
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private ProjectTaskMapper projectTaskMapper;
    @Inject
    private ProjectTaskExtMapper projectTaskExtMapper;
    @Inject
    private MandaysRequestMapper mandaysRequestMapper;
    @Inject
    private CustomerDAO customerDAO;
    @Inject
    private TimeSheetDAO timeSheetDAO;
    @Inject
    private RelRoleFunctionDAO relRoleFunctionDAO;
    @Inject
    private AssignedTaskEmail assignedTaskEmail;
    @Inject
    private ExtendMandaysEmail extendMandaysEmail;
    @Inject
    private RejectedMDRequestEmail rejectedMDRequestEmail;
    @Inject
    private MDRequestEmail mdRequestEmail;

    @Inject
    public ProjectManager() {
    }

    public ProjectDTO createNewProject(long userId, ProjectDTO projectDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewProject. (userId: {}, projectDTO: {})", userId, projectDTO);

        User user = userDAO.findById(userId);

        // validate pre-condition
        // validate code exist and active
        if (projectDAO.isCodeExist(projectDTO.getCode())) {
            log.debug("Code is already in use.");
            throw new ValidationException(APIResponse.CODE_ALREADY_IN_USE);
        }

        Project newProject = projectMapper.toEntity(projectDTO);
        newProject.setCustomer(customerDAO.findById(projectDTO.getCustomer().getId()));
        log.debug("newProject: {}", newProject);

        long billableMDMinute = projectDTO.getBillableMDDuration().toMinutes();
        BigDecimal billableMandays = DateTimeUtil.getManDays(billableMDMinute);
        newProject.setBillableMDMinute(billableMDMinute);
        newProject.setBillableMD(billableMandays);

        Date now = DateTimeUtil.now();
        newProject.setCreateDate(now);
        newProject.setCreateBy(user);
        newProject.setModifyDate(now);
        newProject.setModifyBy(user);
        newProject.setStatus(RecordStatus.ACTIVE);

        newProject = projectDAO.persist(newProject);

        return projectMapper.toDTO(newProject);
    }

    public ProjectDTO getProjectInfo(long userId, long projectId) throws RecordNotFoundException {
        log.debug("getProjectInfo. (userId: {}, projectId: {})", userId, projectId);

        // validate user
        userDAO.findById(userId);

        Project project = projectDAO.findById(projectId);

        return projectMapper.toDTO(project);
    }

    public void updateProjectInfo(long userId, ProjectDTO projectDTO) throws RecordNotFoundException {
        log.debug("updateProjectInfo. (userId: {}, projectDTO: {})", userId, projectDTO);

        long billableMDMinute = projectDTO.getBillableMDDuration().toMinutes();
        BigDecimal billableMandays = DateTimeUtil.getManDays(billableMDMinute);
        projectDTO.setBillableMDMinute(billableMDMinute);
        projectDTO.setBillableMD(billableMandays);

        // validate user
        User user = userDAO.findById(userId);

        Project project = projectDAO.findById(projectDTO.getId());
        Project after = projectMapper.updateFromDTO(projectDTO, project);
        project.setCustomer(customerDAO.findById(projectDTO.getCustomer().getId()));

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        projectDAO.merge(after);
    }

    public List<ProjectDTO> getProjectList(long userId) throws RecordNotFoundException {
        log.debug("getProjectList. (userId: {})", userId);

        // validate user
        User user = userDAO.findById(userId);

        return projectMapper.toDTO(projectDAO.findAll().stream());
    }

    public void closeProject(long userId, ProjectDTO projectDTO) throws RecordNotFoundException {
        log.debug("closeProject. (userId: {}, projectDTO: {})", userId, projectDTO);

        User user = userDAO.findById(userId);
        Project project = projectDAO.findById(projectDTO.getId());

        project.setStatus(RecordStatus.CLOSE);
        project.setModifyDate(DateTimeUtil.now());
        project.setModifyBy(user);

        projectDAO.persist(project);
    }

    public void deleteProject(long userId, ProjectDTO projectDTO) throws RecordNotFoundException {
        log.debug("deleteProject. (userId: {}, projectDTO: {})", userId, projectDTO);

        User user = userDAO.findById(userId);
        Project project = projectDAO.findById(projectDTO.getId());

        project.setStatus(RecordStatus.INACTIVE);
        project.setModifyDate(DateTimeUtil.now());
        project.setModifyBy(user);

        projectDAO.persist(project);
    }

    public List<ProjectDTO> searchProject(long userId, SearchRequest searchRequest) throws RecordNotFoundException {
        log.debug("searchProject. (userId: {}, searchRequest: {})", userId, searchRequest);

        // validate user
        userDAO.findById(userId);

        return projectMapper.toDTO(projectDAO.searchProject(searchRequest.getCode(), searchRequest.getName(), searchRequest.getStatus()).stream());
    }

    // Project task
    public List<ProjectTaskDTO> getProjectTaskList(long userId, long projectId) throws RecordNotFoundException {
        log.debug("getProjectTaskList. (userId: {}, projectId: {})", userId, projectId);

        // validate user
        userDAO.findById(userId);
        Project project = projectDAO.findById(projectId);

        return projectTaskMapper.toDTO(projectTaskDAO.findByProject(project).stream());
    }

    public List<ProjectTaskExtDTO> getProjectTaskExtList(long userId, long projectId) throws RecordNotFoundException {
        log.debug("getProjectTaskExtList. (userId: {}, projectId: {})", userId, projectId);

        // validate user
        userDAO.findById(userId);

        return projectTaskExtMapper.toDTO(projectTaskExtDAO.findByProjectTaskId(projectId).stream());
    }

    public List<ProjectTaskDTO> getProjectTaskListForUser(long userId, long projectId) throws RecordNotFoundException {
        log.debug("getProjectTaskListForUser. (userId: {}, projectId: {})", userId, projectId);

        // validate user
        User user = userDAO.findById(userId);

        return projectTaskMapper.toDTO(projectTaskDAO.findByProjectIdAndUser(projectId, user).stream());
    }

    @Transactional(rollbackOn = {Exception.class})
    public ProjectTaskDTO createNewProjectTask(long userId, ProjectTaskDTO projectTaskDTO, MandaysRequest mandaysRequest) throws RecordNotFoundException, EmailException {
        log.debug("createNewProjectTask. (userId: {}, projectTaskDTO: {})", userId, projectTaskDTO);

        User user = userDAO.findById(userId);

        ProjectTask newProjectTask = projectTaskMapper.toEntity(projectTaskDTO);
        newProjectTask.setPlanMDMinute(newProjectTask.getPlanMDDuration().toMinutes());
        newProjectTask.setPlanMD(DateTimeUtil.getManDays(newProjectTask.getPlanMDMinute()));

        newProjectTask.setExtendMD(BigDecimal.ZERO);
        newProjectTask.setExtendMDDuration(Duration.ZERO);
        newProjectTask.setExtendMDMinute(0L);

        newProjectTask.setActualMD(BigDecimal.ZERO);
        newProjectTask.setActualMDDuration(Duration.ZERO);
        newProjectTask.setActualMDMinute(0L);

        newProjectTask.setPercentAMD(BigDecimal.ZERO);

        newProjectTask.setProject(projectDAO.findById(projectTaskDTO.getProject().getId()));
        newProjectTask.setTask(taskDAO.findById(projectTaskDTO.getTask().getId()));
        newProjectTask.setUser(userDAO.findById(projectTaskDTO.getUser().getId()));
        log.debug("newProjectTask: {}", newProjectTask);

        Date now = DateTimeUtil.now();
        newProjectTask.setCreateDate(now);
        newProjectTask.setCreateBy(user);
        newProjectTask.setModifyDate(now);
        newProjectTask.setModifyBy(user);
        newProjectTask.setStatus(RecordStatus.ACTIVE);

        newProjectTask = projectTaskDAO.persist(newProjectTask);

        assignedTaskEmail.sendMail(newProjectTask, mandaysRequest);

        return projectTaskMapper.toDTO(newProjectTask);
    }

    @Transactional(rollbackOn = {Exception.class})
    public ProjectTaskExtDTO createExtendProjectTask(long userId, ProjectTaskDTO projectTaskDTO, ProjectTaskExtDTO projectTaskExtDTO, MandaysRequest mandaysRequest) throws RecordNotFoundException, EmailException {
        log.debug("createExtendProjectTask. (userId: {}, projectTaskDTO: {}, projectTaskExtDTO: {})", userId, projectTaskDTO, projectTaskExtDTO);
        User user = userDAO.findById(userId);

        ProjectTask parentProjectTask = projectTaskDAO.findById(projectTaskDTO.getId());

        ProjectTaskExt ext = projectTaskExtMapper.toEntity(projectTaskExtDTO);
        log.debug("ext: {}", ext);
        ext.setExtendMDMinute(projectTaskExtDTO.getExtendMDDuration().toMinutes());
        ext.setExtendMD(DateTimeUtil.getManDays(ext.getExtendMDMinute()));

        Date now = DateTimeUtil.now();
        ext.setCreateDate(now);
        ext.setCreateBy(user);
        ext.setModifyDate(now);
        ext.setModifyBy(user);
        ext.setStatus(RecordStatus.ACTIVE);

        ext.setParent(parentProjectTask);

        ext = projectTaskExtDAO.persist(ext);

        // update ext for parent
        parentProjectTask.setExtendMDDuration(parentProjectTask.getExtendMDDuration().plus(ext.getExtendMDDuration()));
        parentProjectTask.setExtendMDMinute(parentProjectTask.getExtendMDDuration().toMinutes());
        parentProjectTask.setExtendMD(DateTimeUtil.getManDays(parentProjectTask.getExtendMDMinute()));
        projectTaskDAO.persist(parentProjectTask);

        extendMandaysEmail.sendMail(parentProjectTask, ext, mandaysRequest);

        return projectTaskExtMapper.toDTO(ext);
    }

    public ProjectTaskDTO getProjectTaskInfo(long userId, long projectTaskId) throws RecordNotFoundException {
        log.debug("getProjectTaskInfo. (userId: {}, projectTaskId: {})", userId, projectTaskId);

        // validate user
        userDAO.findById(userId);

        ProjectTask projectTask = projectTaskDAO.findById(projectTaskId);

        return projectTaskMapper.toDTO(projectTask);
    }

    public void updateProjectTaskInfo(long userId, ProjectTaskDTO projectTaskDTO) throws RecordNotFoundException {
        log.debug("updateProjectTaskInfo. (userId: {}, projectTaskDTO: {})", userId, projectTaskDTO);

        // validate user
        User user = userDAO.findById(userId);

        User workingUser = userDAO.findById(projectTaskDTO.getUser().getId());
        ProjectTask projectTask = projectTaskDAO.findById(projectTaskDTO.getId());
        ProjectTask after = projectTaskMapper.updateFromDTO(projectTaskDTO, projectTask);

        after.setUser(workingUser);

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        projectTaskDAO.merge(after);
    }

    public CostDTO getProjectCost(long userId, long projectId) throws RecordNotFoundException {
        log.debug("getProjectCost. (userId: {}, projectId: {})", userId, projectId);

        // validate user
        userDAO.findById(userId);

        Project project = projectDAO.findById(projectId);

        CostDTO costDTO = new CostDTO();

        // plan cost
        BigDecimal planCost = BigDecimal.ZERO;
        List<ProjectTask> projectTasks = projectTaskDAO.findByProject(project);
        for (ProjectTask pt : projectTasks) {
            planCost = planCost.add(DateTimeUtil.getManDays(pt.getPlanMDMinute()).multiply(pt.getUser().getRate().getCost()).setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP));
        }
        costDTO.setPlanCost(planCost);

        // extend cost
        BigDecimal extendCost = BigDecimal.ZERO;
        List<ProjectTaskExt> projectTaskExtList = projectTaskExtDAO.findByProject(project);
        for (ProjectTaskExt pte : projectTaskExtList) {
            extendCost = extendCost.add(DateTimeUtil.getManDays(pte.getExtendMDMinute()).multiply(pte.getParent().getUser().getRate().getCost()).setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP));
        }
        costDTO.setExtendCost(extendCost);

        // total cost
        costDTO.setTotalCost(planCost.add(extendCost));

        // actual cost
        BigDecimal actualCost = BigDecimal.ZERO;
        List<TimeSheet> timeSheets = timeSheetDAO.findByProject(project);
        for (TimeSheet ts : timeSheets) {
            actualCost = actualCost.add(DateTimeUtil.getManDays(ts.getChargeMinute()).multiply(ts.getUser().getRate().getCost()).setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP));
        }
        costDTO.setActualCost(actualCost);

        return costDTO;
    }

    public void deleteProjectTask(long userId, ProjectTaskDTO projectTaskDTO) throws RecordNotFoundException, ValidationException {
        log.debug("deleteProjectTask. (userId: {}, projectTaskDTO: {})", userId, projectTaskDTO);

        User user = userDAO.findById(userId);
        ProjectTask projectTask = projectTaskDAO.findById(projectTaskDTO.getId());

        // validation project task actual man day still be zero.
        if (projectTask.getActualMDMinute() != 0) {
            log.debug("project task has already started, actual MD(min): {}", projectTask.getActualMDMinute());
            throw new ValidationException(APIResponse.FAILED, "project task has already started!");
        }

        projectTask.setStatus(RecordStatus.INACTIVE);
        projectTask.setModifyDate(DateTimeUtil.now());
        projectTask.setModifyBy(user);

        projectTaskDAO.persist(projectTask);
    }

    public void lockProjectTask(long userId, ProjectTaskDTO projectTaskDTO) throws RecordNotFoundException {
        log.debug("lockProjectTask. (userId: {}, projectTaskDTO: {})", userId, projectTaskDTO);

        User user = userDAO.findById(userId);
        ProjectTask projectTask = projectTaskDAO.findById(projectTaskDTO.getId());

        projectTask.setStatus(RecordStatus.LOCK);
        projectTask.setModifyDate(DateTimeUtil.now());
        projectTask.setModifyBy(user);

        projectTaskDAO.persist(projectTask);
    }

    public void unlockProjectTask(long userId, ProjectTaskDTO projectTaskDTO) throws RecordNotFoundException {
        log.debug("unlockProjectTask. (userId: {}, projectTaskDTO: {})", userId, projectTaskDTO);

        User user = userDAO.findById(userId);
        ProjectTask projectTask = projectTaskDAO.findById(projectTaskDTO.getId());

        projectTask.setStatus(RecordStatus.ACTIVE);
        projectTask.setModifyDate(DateTimeUtil.now());
        projectTask.setModifyBy(user);

        projectTaskDAO.persist(projectTask);
    }

    // special method for re calculation %AMD
    public void reCalculationPercentAMD() {
        log.debug("reCalculationPercentAMD. (start)");
        List<ProjectTask> projectTaskList = projectTaskDAO.findAll();

        for (ProjectTask pt : projectTaskList) {
            pt.setPercentAMD(MDUtil.getPercentAMD(pt.getPlanMDMinute(), pt.getActualMDMinute()));
        }

        projectTaskDAO.persist(projectTaskList);
        log.debug("reCalculationPercentAMD. (finish)");
    }

    public List<MandaysRequestDTO> getMandaysRequestList(long userId, RequestStatus status, Date startDate, Date endDate) throws RecordNotFoundException {
        log.debug("getMandaysRequestList(userId: {}, status: {})", userId, status);

        return mandaysRequestMapper.toDTO(mandaysRequestDAO.findByStatus(userId, status, startDate, endDate).stream());
    }

    @Transactional(rollbackOn = {Exception.class})
    public MandaysRequestDTO createMandaysRequest(long userId, MandaysRequestDTO mandaysRequestDTO) throws RecordNotFoundException, EmailException {
        log.debug("createMandaysRequest(userId: {}, mandaysRequestDTO: {})", userId, mandaysRequestDTO);
        User user = userDAO.findById(userId);

        MandaysRequest newMandaysRequest = mandaysRequestMapper.toEntity(mandaysRequestDTO);

        if (!normalizeMandaysRequest(mandaysRequestDTO, newMandaysRequest)) {
            return null;
        }

        newMandaysRequest.setStatus(RequestStatus.REQUESTED);

        newMandaysRequest.setCreateDate(DateTimeUtil.now());
        newMandaysRequest.setCreateBy(user);
        newMandaysRequest.setModifyDate(DateTimeUtil.now());
        newMandaysRequest.setModifyBy(user);

        newMandaysRequest = mandaysRequestDAO.persist(newMandaysRequest);

        List<RelRoleFunction> relRoleFunctionList = relRoleFunctionDAO.findByFunction(Function.F0005);
        if (relRoleFunctionList == null || relRoleFunctionList.size() == 0) {
            throw new EmailException("Need a role with function 'Approve mandays request', please contact admin.");
        }

        List<Role> roleList = new ArrayList<>();
        Role role;
        for (RelRoleFunction relRoleFunction : relRoleFunctionList) {
            role = relRoleFunction.getRole();
            roleList.add(role);
            log.debug("found F0005 on role({})", role.getName());
        }

        List<User> approverList = userDAO.findByRoleList(roleList);
        for (User approver : approverList) {
            log.debug("found F0005 approver([{}]{}:{})", approver.getRole().getName(), approver.getName(), approver.getEmail());
        }
        if (approverList.size() == 0) {
            throw new EmailException("Need a man to approve mandays request, please contact admin.");
        }

        mdRequestEmail.sendMail(mandaysRequestDTO.getType(), newMandaysRequest, approverList);

        return mandaysRequestMapper.toDTO(newMandaysRequest);
    }

    /**
     * Approve, reject or something depend on the mandaysRequestDTO.status.
     *
     * @return return null when failed, otherwise return the saved mandaysRequestDTO.
     */
    public MandaysRequestDTO acceptMandaysRequest(long userId, MandaysRequestDTO mandaysRequestDTO, StringBuffer message) throws RecordNotFoundException, EmailException {
        log.debug("acceptMandaysRequest(userId: {}, mandaysRequestDTO: {})", userId, mandaysRequestDTO);
        User user = userDAO.findById(userId);

        MandaysRequestDTO result;
        switch (mandaysRequestDTO.getStatus()) {
            case APPROVED:
                result = approveMandaysRequest(user, mandaysRequestDTO);
                if (result == null) {
                    message.append("Incompatible data, please check Type and ProjectTask!");
                } else {
                    message.append("Approved.");
                }
                return result;

            case REJECTED:
                result = rejectMandaysRequest(user, mandaysRequestDTO);
                if (result == null) {
                    message.append("Incompatible data, please check Type and ProjectTask!");
                } else {
                    message.append("Rejected.");
                }
                return result;

            default:
                //case CANCELLED:
                //case REQUESTED:
                message.append("Can't accept status(").append(mandaysRequestDTO.getStatus()).append(") now support only for APPROVED and REJECTED.");
                return null;
        }
    }

    private MandaysRequestDTO approveMandaysRequest(User user, MandaysRequestDTO mandaysRequestDTO) throws RecordNotFoundException, EmailException {
        log.debug("approveMandaysRequest().");
        long mandaysRequestDTOId = mandaysRequestDTO.getId();
        MandaysRequest newMandaysRequest = mandaysRequestDAO.findById(mandaysRequestDTOId);
        if (newMandaysRequest == null) {
            log.warn("approveMandaysRequest mandaysRequestID({}) not found!", mandaysRequestDTOId);
            return null;
        }

        newMandaysRequest = mandaysRequestMapper.updateFromDTO(mandaysRequestDTO, newMandaysRequest);
        if (!normalizeMandaysRequest(mandaysRequestDTO, newMandaysRequest)) {
            return null;
        }

        newMandaysRequest.setStatus(RequestStatus.APPROVED);
        newMandaysRequest.setModifyDate(DateTimeUtil.now());
        newMandaysRequest.setModifyBy(user);

        newMandaysRequest = mandaysRequestDAO.persist(newMandaysRequest);

        if (MandaysRequestType.NEW.equals(mandaysRequestDTO.getType())) {
            /*call same function with the create new project task to use the same business logic and same email template*/
            ProjectTaskDTO projectTaskDTO = new ProjectTaskDTO();
            projectTaskDTO.setProject(mandaysRequestDTO.getProject());
            projectTaskDTO.setTask(mandaysRequestDTO.getTask());
            projectTaskDTO.setUser(mandaysRequestDTO.getUser());
            projectTaskDTO.setPlanMDDuration(mandaysRequestDTO.getExtendMDDuration());
            projectTaskDTO.setDescription(mandaysRequestDTO.getDescription());
            projectTaskDTO.setAmdCalculation(mandaysRequestDTO.isAmdCalculation());

            createNewProjectTask(user.getId(), projectTaskDTO, newMandaysRequest);

        } else /*EXTEND*/ {
            /*call same function with the extend mandays of project task to use the same business logic and same email template*/
            ProjectTaskDTO projectTaskDTO = new ProjectTaskDTO();
            projectTaskDTO.setId(mandaysRequestDTO.getProjectTask().getId());

            ProjectTaskExtDTO projectTaskExtDTO = new ProjectTaskExtDTO();
            projectTaskExtDTO.setExtendMDDuration(mandaysRequestDTO.getExtendMDDuration());
            projectTaskExtDTO.setDescription(mandaysRequestDTO.getDescription());

            createExtendProjectTask(user.getId(), projectTaskDTO, projectTaskExtDTO, newMandaysRequest);
        }

        return mandaysRequestMapper.toDTO(newMandaysRequest);
    }

    @Transactional(rollbackOn = {Exception.class})
    private MandaysRequestDTO rejectMandaysRequest(User user, MandaysRequestDTO mandaysRequestDTO) throws RecordNotFoundException, EmailException {
        log.debug("rejectMandaysRequest().");
        long mandaysRequestDTOId = mandaysRequestDTO.getId();
        MandaysRequest newMandaysRequest = mandaysRequestDAO.findById(mandaysRequestDTOId);
        if (newMandaysRequest == null) {
            log.warn("rejectMandaysRequest mandaysRequestID({}) not found!", mandaysRequestDTOId);
            return null;
        }

        newMandaysRequest = mandaysRequestMapper.updateFromDTO(mandaysRequestDTO, newMandaysRequest);
        if (!normalizeMandaysRequest(mandaysRequestDTO, newMandaysRequest)) {
            return null;
        }

        newMandaysRequest.setStatus(RequestStatus.REJECTED);
        newMandaysRequest.setModifyDate(DateTimeUtil.now());
        newMandaysRequest.setModifyBy(user);

        newMandaysRequest = mandaysRequestDAO.persist(newMandaysRequest);

        rejectedMDRequestEmail.sendMail(mandaysRequestDTO.getType(), newMandaysRequest);

        return mandaysRequestMapper.toDTO(newMandaysRequest);
    }

    private boolean normalizeMandaysRequest(MandaysRequestDTO mandaysRequestDTO, MandaysRequest newMandaysRequest) throws RecordNotFoundException {
        if (MandaysRequestType.NEW.equals(mandaysRequestDTO.getType())) {
            ProjectDTO projectDTO = mandaysRequestDTO.getProject();
            TaskDTO taskDTO = mandaysRequestDTO.getTask();
            if (projectDTO == null || taskDTO == null) {
                log.warn("normalizeMandaysRequest: incompatible NEW type and project + task.");
                return false;
            }

            newMandaysRequest.setProjectTask(null);
            newMandaysRequest.setProject(projectDAO.findById(projectDTO.getId()));
            newMandaysRequest.setTask(taskDAO.findById(taskDTO.getId()));

        } else /*EXTEND*/ {
            ProjectTaskDTO projectTaskDTO = mandaysRequestDTO.getProjectTask();
            if (projectTaskDTO == null) {
                log.warn("normalizeMandaysRequest: incompatible EXTEND type and project task.");
                return false;
            }

            newMandaysRequest.setProject(null);
            newMandaysRequest.setTask(null);
            newMandaysRequest.setProjectTask(projectTaskDAO.findById(projectTaskDTO.getId()));
        }

        newMandaysRequest.setUser(userDAO.findById(mandaysRequestDTO.getUser().getId()));
        return true;
    }

}
