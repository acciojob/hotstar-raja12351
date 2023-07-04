package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.Transformer.SubscriptionTransformer;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = SubscriptionTransformer.convertDtoToEntity(subscriptionEntryDto);
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        int noOfScreen = subscription.getNoOfScreensSubscribed();

        int priceOdSubscription = 0;

        if(subscriptionType.equals(SubscriptionType.BASIC)){
            priceOdSubscription = 500 + (200 * noOfScreen);
        }else if(subscriptionType.equals(SubscriptionType.PRO)){
            priceOdSubscription = 800 + (250 * noOfScreen);
        }else{
            priceOdSubscription = 1000 + + (350 * noOfScreen);
        }
        subscription.setTotalAmountPaid(priceOdSubscription);
        Date date = new Date();
        subscription.setStartSubscriptionDate(date);

        user.setSubscription(subscription);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        Subscription subscription = user.getSubscription();
        int amountPaid = subscription.getTotalAmountPaid();
        int noOfScreens = subscription.getNoOfScreensSubscribed();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        int newAmount = 0;
        if(subscriptionType.equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            newAmount = 800 + (250 * noOfScreens);
            subscription.setTotalAmountPaid(newAmount);
        }
        int diff = newAmount - amountPaid;
        subscription = subscriptionRepository.save(subscription);

        user.setSubscription(subscription);
        userRepository.save(user);

        return diff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        return null;
    }

}
