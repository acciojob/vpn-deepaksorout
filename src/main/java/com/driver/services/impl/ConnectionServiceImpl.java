package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user=userRepository2.findById(userId).get();
        ServiceProvider serviceProvider1=null;
        Country country1=null;
        boolean isPossible=false;
        if(user.getConnected()==true){
            throw new Exception("Already connected");
        }
        else if(user.getOriginalCountry().getCountryName().equals(countryName)){
            return user;
        }
        else{
            if(user.getServiceProviderList().size()==0){
                throw new Exception("Unable to connect");
            }
            else{

                int a=Integer.MAX_VALUE;
                for(ServiceProvider serviceProvider:user.getServiceProviderList()){
                    for(Country country: serviceProvider.getCountryList()){
                        if(country.getCountryName().equals(countryName) && a>serviceProvider.getId()){
                            a=serviceProvider.getId();
                            isPossible=true;
                            serviceProvider1=serviceProvider;
                            country1=country;
                            break;
                        }
                    }
                }
            }
            if(isPossible==false){
                throw new Exception("Unable to connect");
            }
            else{
                Connection connection=new Connection();
                connection.setServiceProvider(serviceProvider1);
                connection.setUser(user);


                List<Connection> connectionList=user.getConnectionList();
                connectionList.add(connection);
                user.setConnectionList(connectionList);

                user.setConnected(true);
                user.setMaskedIp(country1.getCode()+"."+serviceProvider1.getId()+"."+userId);

                userRepository2.save(user);

                List<Connection> connectionList1=serviceProvider1.getConnectionList();
                connectionList1.add(connection);
                serviceProvider1.setConnectionList(connectionList1);

                serviceProviderRepository2.save(serviceProvider1);
            }
        }
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();

        if(user.getConnected()==false){
            throw new Exception("Already disconnected");
        }
        else{
            user.setMaskedIp(null);
            user.setConnected(false);
            userRepository2.save(user);
        }
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender=userRepository2.findById(senderId).get();
        User receiver=userRepository2.findById(receiverId).get();

        if(receiver.getConnected()==true){
            String countryCode=receiver.getOriginalCountry().getCode();

            if(countryCode.equals(sender.getOriginalCountry().getCode())){
                return sender;
            }
            else{
                String countryName="";

                if (countryCode.equalsIgnoreCase(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (countryCode.equalsIgnoreCase(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (countryCode.equalsIgnoreCase(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (countryCode.equalsIgnoreCase(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (countryCode.equalsIgnoreCase(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();

                User user=connect(senderId,countryName);
                if(!user.getConnected()){
                    throw new Exception("Cannot establish communication");
                }
                else{
                    return user;
                }
            }
        }
        else{
            if(receiver.getOriginalCountry().equals(sender.getOriginalCountry())){
                return sender;
            }
            String countryName=receiver.getOriginalCountry().getCountryName().toString();
            User user=connect(senderId,countryName);
            if(!user.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            else{
                return user;
            }
        }
    }
}
