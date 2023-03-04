package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user=new User();

        Country country=countryRepository3.findByName(countryName);

        user.setPassword(password);
        user.setUsername(username);
        user.setOriginalIp(country.getCode()+"."+user.getId());
        user.setMaskedIp(null);
        user.setConnected(false);


        user.setCountry(country);

        userRepository3.save(user);

        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user=userRepository3.findById(userId).get();

        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();

        List<User> userList=serviceProvider.getUserList();
        userList.add(user);
        serviceProvider.setUserList(userList);

        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        serviceProviderList.add(serviceProvider);
        user.setServiceProviderList(serviceProviderList);

        serviceProviderRepository3.save(serviceProvider);

        return user;
    }
}
