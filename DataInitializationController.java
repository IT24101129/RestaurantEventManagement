package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.Menu;
import com.resturant.restaurantapp.model.MenuItem;
import com.resturant.restaurantapp.model.MenuPackageItem;
import com.resturant.restaurantapp.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/init")
public class DataInitializationController {
    
    @Autowired
    private MenuService menuService;
    
    @GetMapping("/create-sample-data")
    @ResponseBody
    public String createSampleData() {
        try {
            // Create sample menu items
            MenuItem item1 = new MenuItem();
            item1.setItemName("Chicken Biriyani");
            item1.setItemCategory(MenuItem.ItemCategory.MAIN_COURSE);
            item1.setDescription("Fragrant basmati rice with spiced chicken");
            item1.setPrice(new BigDecimal("1200.00"));
            item1.setIsVegetarian(false);
            item1.setIsSpicy(true);
            item1.setIsAvailable(true);
            menuService.createMenuItem(item1);
            
            MenuItem item2 = new MenuItem();
            item2.setItemName("Vegetable Curry");
            item2.setItemCategory(MenuItem.ItemCategory.CURRY);
            item2.setDescription("Mixed vegetables in coconut curry");
            item2.setPrice(new BigDecimal("800.00"));
            item2.setIsVegetarian(true);
            item2.setIsSpicy(false);
            item2.setIsAvailable(true);
            menuService.createMenuItem(item2);
            
            MenuItem item3 = new MenuItem();
            item3.setItemName("Rice");
            item3.setItemCategory(MenuItem.ItemCategory.RICE);
            item3.setDescription("Steamed basmati rice");
            item3.setPrice(new BigDecimal("200.00"));
            item3.setIsVegetarian(true);
            item3.setIsSpicy(false);
            item3.setIsAvailable(true);
            menuService.createMenuItem(item3);
            
            MenuItem item4 = new MenuItem();
            item4.setItemName("Ice Cream");
            item4.setItemCategory(MenuItem.ItemCategory.DESSERT);
            item4.setDescription("Vanilla ice cream");
            item4.setPrice(new BigDecimal("300.00"));
            item4.setIsVegetarian(true);
            item4.setIsSpicy(false);
            item4.setIsAvailable(true);
            menuService.createMenuItem(item4);
            
            MenuItem item5 = new MenuItem();
            item5.setItemName("Chicken Curry");
            item5.setItemCategory(MenuItem.ItemCategory.CURRY);
            item5.setDescription("Spicy chicken curry with coconut milk");
            item5.setPrice(new BigDecimal("1000.00"));
            item5.setIsVegetarian(false);
            item5.setIsSpicy(true);
            item5.setIsAvailable(true);
            menuService.createMenuItem(item5);
            
            MenuItem item6 = new MenuItem();
            item6.setItemName("Fish Curry");
            item6.setItemCategory(MenuItem.ItemCategory.SEAFOOD);
            item6.setDescription("Traditional Sri Lankan fish curry");
            item6.setPrice(new BigDecimal("1100.00"));
            item6.setIsVegetarian(false);
            item6.setIsSpicy(true);
            item6.setIsAvailable(true);
            menuService.createMenuItem(item6);
            
            // Create sample menus
            Menu basicMenu = new Menu();
            basicMenu.setMenuName("Basic Package");
            basicMenu.setMenuPackage(Menu.MenuPackage.BASIC);
            basicMenu.setDescription("Basic food package - includes rice, curry, and dessert");
            basicMenu.setBasePricePerGuest(new BigDecimal("1500.00"));
            basicMenu.setIsActive(true);
            Menu savedBasicMenu = menuService.createMenu(basicMenu);
            
            Menu standardMenu = new Menu();
            standardMenu.setMenuName("Standard Package");
            standardMenu.setMenuPackage(Menu.MenuPackage.STANDARD);
            standardMenu.setDescription("Standard food package - includes rice, curry, meat dish, and dessert");
            standardMenu.setBasePricePerGuest(new BigDecimal("2000.00"));
            standardMenu.setIsActive(true);
            Menu savedStandardMenu = menuService.createMenu(standardMenu);
            
            Menu premiumMenu = new Menu();
            premiumMenu.setMenuName("Premium Package");
            premiumMenu.setMenuPackage(Menu.MenuPackage.PREMIUM);
            premiumMenu.setDescription("Premium food package - includes biriyani, curry, seafood, and dessert");
            premiumMenu.setBasePricePerGuest(new BigDecimal("2500.00"));
            premiumMenu.setIsActive(true);
            Menu savedPremiumMenu = menuService.createMenu(premiumMenu);
            
            // Add items to Basic Menu
            menuService.addItemToMenu(savedBasicMenu, item3, false); // Rice - required
            menuService.addItemToMenu(savedBasicMenu, item2, false); // Vegetable Curry - required
            menuService.addItemToMenu(savedBasicMenu, item4, false); // Ice Cream - required
            
            // Add items to Standard Menu
            menuService.addItemToMenu(savedStandardMenu, item3, false); // Rice - required
            menuService.addItemToMenu(savedStandardMenu, item2, false); // Vegetable Curry - required
            menuService.addItemToMenu(savedStandardMenu, item5, false); // Chicken Curry - required
            menuService.addItemToMenu(savedStandardMenu, item4, false); // Ice Cream - required
            
            // Add items to Premium Menu
            menuService.addItemToMenu(savedPremiumMenu, item1, false); // Chicken Biriyani - required
            menuService.addItemToMenu(savedPremiumMenu, item5, false); // Chicken Curry - required
            menuService.addItemToMenu(savedPremiumMenu, item6, false); // Fish Curry - required
            menuService.addItemToMenu(savedPremiumMenu, item4, false); // Ice Cream - required
            
            return "SUCCESS: Sample data created successfully!<br><br>" +
                   "Created:<br>" +
                   "• 6 Menu Items (Chicken Biriyani, Vegetable Curry, Rice, Ice Cream, Chicken Curry, Fish Curry)<br>" +
                   "• 3 Menu Packages (Basic, Standard, Premium)<br>" +
                   "• Menu-Item associations<br><br>" +
                   "You can now access the menu selection page!<br>" +
                   "<a href='/admin/events'>Go to Events</a> | " +
                   "<a href='/admin/menu'>Go to Menus</a>";
                   
        } catch (Exception e) {
            return "ERROR: Failed to create sample data: " + e.getMessage();
        }
    }
}
