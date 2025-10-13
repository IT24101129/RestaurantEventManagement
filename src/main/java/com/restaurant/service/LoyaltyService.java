package com.restaurant.service;

import com.restaurant.model.User;
import com.restaurant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoyaltyService {

    @Autowired
    private UserRepository userRepository;

    public void awardPointsForReservation(User user, Integer numberOfGuests) {
        if (user == null) return;
        
        int pointsToAward = calculatePointsForReservation(numberOfGuests);
        int currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        user.setLoyaltyPoints(currentPoints + pointsToAward);
        user.setLastPointsUpdate(LocalDateTime.now());
        
        userRepository.save(user);
        
        System.out.println("Awarded " + pointsToAward + " loyalty points to user: " + user.getName());
    }

    public void awardPointsForOrder(User user, Double orderAmount) {
        if (user == null) return;
        
        int pointsToAward = calculatePointsForOrder(orderAmount);
        int currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        user.setLoyaltyPoints(currentPoints + pointsToAward);
        user.setLastPointsUpdate(LocalDateTime.now());
        
        userRepository.save(user);
        
        System.out.println("Awarded " + pointsToAward + " loyalty points for order: $" + orderAmount);
    }

    public boolean redeemPoints(User user, int pointsToRedeem) {
        if (user == null || user.getLoyaltyPoints() == null) return false;
        
        if (user.getLoyaltyPoints() >= pointsToRedeem) {
            user.setLoyaltyPoints(user.getLoyaltyPoints() - pointsToRedeem);
            user.setLastPointsUpdate(LocalDateTime.now());
            userRepository.save(user);
            
            System.out.println("Redeemed " + pointsToRedeem + " loyalty points for user: " + user.getName());
            return true;
        }
        
        return false;
    }

    public String getLoyaltyTier(User user) {
        if (user == null || user.getLoyaltyPoints() == null) return "Bronze";
        
        int points = user.getLoyaltyPoints();
        if (points >= 1000) return "Diamond";
        if (points >= 500) return "Gold";
        if (points >= 200) return "Silver";
        return "Bronze";
    }

    public double getDiscountPercentage(User user) {
        String tier = getLoyaltyTier(user);
        switch (tier) {
            case "Diamond": return 0.15; // 15% discount
            case "Gold": return 0.10;    // 10% discount
            case "Silver": return 0.05;  // 5% discount
            default: return 0.0;         // No discount
        }
    }

    public boolean canRedeemFreeMeal(User user) {
        return user != null && user.getLoyaltyPoints() != null && user.getLoyaltyPoints() >= 500;
    }

    public String getLoyaltyBenefits(User user) {
        String tier = getLoyaltyTier(user);
        StringBuilder benefits = new StringBuilder();
        
        switch (tier) {
            case "Diamond":
                benefits.append("• 15% discount on all orders\n");
                benefits.append("• Free appetizer with every visit\n");
                benefits.append("• Priority table assignment\n");
                benefits.append("• Free dessert on birthdays\n");
                break;
            case "Gold":
                benefits.append("• 10% discount on all orders\n");
                benefits.append("• Free appetizer monthly\n");
                benefits.append("• Priority reservation confirmation\n");
                break;
            case "Silver":
                benefits.append("• 5% discount on all orders\n");
                benefits.append("• Free drink with orders over $50\n");
                break;
            default:
                benefits.append("• Earn points with every reservation and order\n");
                benefits.append("• Points never expire\n");
                benefits.append("• Exclusive member offers\n");
        }
        
        return benefits.toString();
    }

    private int calculatePointsForReservation(Integer numberOfGuests) {
        // Base points: 10 per guest, bonus for larger parties
        int basePoints = numberOfGuests * 10;
        if (numberOfGuests >= 8) basePoints += 50; // Group bonus
        if (numberOfGuests >= 15) basePoints += 100; // Large group bonus
        return basePoints;
    }

    private int calculatePointsForOrder(Double orderAmount) {
        // 1 point per dollar spent, rounded down
        return (int) Math.floor(orderAmount);
    }
}
