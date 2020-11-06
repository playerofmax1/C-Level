package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.relation.RelRoleFunctionDAO;
import com.clevel.kudu.api.dao.working.CustomerDAO;
import com.clevel.kudu.api.dao.working.UserDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.model.db.relation.RelRoleFunction;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.api.model.db.working.Customer;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.rest.mapper.CustomerMapper;
import com.clevel.kudu.api.rest.mapper.UserMapper;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.working.CustomerDTO;
import com.clevel.kudu.dto.working.UserDTO;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class CustomerManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private CustomerDAO customerDAO;
    @Inject
    private RelRoleFunctionDAO relRoleFunctionDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private CustomerMapper customerMapper;
    @Inject
    private UserMapper userMapper;

    @Inject
    public CustomerManager() {
    }

    public CustomerDTO createNewCustomer(long userId, CustomerDTO customerDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewCustomer. (userId: {}, customerDTO: {})", userId, customerDTO);

        User user = userDAO.findById(userId);

        Customer newCustomer = customerMapper.toEntity(customerDTO);
        log.debug("newCustomer: {}", newCustomer);

        Date now = DateTimeUtil.now();
        newCustomer.setCreateDate(now);
        newCustomer.setCreateBy(user);
        newCustomer.setModifyDate(now);
        newCustomer.setModifyBy(user);
        newCustomer.setStatus(RecordStatus.ACTIVE);

        newCustomer = customerDAO.persist(newCustomer);

        return customerMapper.toDTO(newCustomer);
    }

    public CustomerDTO getCustomerInfo(long userId, long customerId) throws RecordNotFoundException {
        log.debug("getCustomerInfo. (userId: {}, customerId: {})", userId, customerId);

        // validate user
        userDAO.findById(userId);

        Customer customer = customerDAO.findById(customerId);

        return customerMapper.toDTO(customer);
    }

    public void updateCustomerInfo(long userId, CustomerDTO customerDTO) throws RecordNotFoundException {
        log.debug("updateCustomerInfo. (userId: {}, customerDTO: {})", userId, customerDTO);

        // validate user
        User user = userDAO.findById(userId);

        Customer customer = customerDAO.findById(customerDTO.getId());
        Customer after = customerMapper.updateFromDTO(customerDTO, customer);

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        customerDAO.merge(after);
    }

    public List<CustomerDTO> getCustomerList(long userId) throws RecordNotFoundException {
        log.debug("getCustomerList. (userId: {})", userId);

        // validate user
        User user = userDAO.findById(userId);

        return customerMapper.toDTO(customerDAO.findAll().stream());
    }
    public List<UserDTO> getApproverList() throws RecordNotFoundException {
        log.debug("getApproverList");

        //get roleid form rel_role_fucntion
        List<RelRoleFunction> relRoleFunctionList = relRoleFunctionDAO.findByFunction(Function.F0005);

        //
        if (relRoleFunctionList == null || relRoleFunctionList.size() == 0) {
            throw new RecordNotFoundException("NULL.");
        }

        List<Role> roleList = new ArrayList<>();

        for (RelRoleFunction relRoleFunction : relRoleFunctionList) {
            //วนลูป ดึงค่าRole กับ ชื่อ ออกมา
            roleList.add(relRoleFunction.getRole());
            log.debug("found F0005 on role({})", relRoleFunction.getRole().getName());
        }

        //เอา ค่าของ ฟังก์ชั่น FindByRoleList(roleList) มาเก็บ ไว้ใน ApproverList
        List<User> approverList = userDAO.findByRoleList(roleList);



        return userMapper.toDTO(approverList.stream());
    }


    public void deleteCustomer(long userId, CustomerDTO customerDTO) throws RecordNotFoundException {
        log.debug("deleteCustomer. (userId: {}, customerDTO: {})",userId,customerDTO);

        User user = userDAO.findById(userId);
        Customer customer = customerDAO.findById(customerDTO.getId());

        customer.setStatus(RecordStatus.INACTIVE);
        customer.setModifyDate(DateTimeUtil.now());
        customer.setModifyBy(user);

        customerDAO.persist(customer);
    }

}
