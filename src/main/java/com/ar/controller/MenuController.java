package com.ar.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.ar.entity.Menu;
import com.ar.service.MenuService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {

    @Autowired
    private MenuService menuService;

    // 👉 ADD MENU PAGE
    @GetMapping("/add")
    public String addMenuPage(Model model) {
        model.addAttribute("menu", new Menu());
        return "addMenu";
    }

    // 👉 SAVE MENU (IMAGE + DATA)
    @PostMapping("/addMenu")
    public String saveMenu(@ModelAttribute Menu menu,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           HttpSession session) throws IOException {

        // 🔹 Session se restaurant name lo
        String restaurantName = (String) session.getAttribute("restaurantName");

        if (restaurantName == null) {
            return "redirect:/login";
        }

        menu.setRestaurantsName(restaurantName);

        // 🔥 IMAGE UPLOAD FIX
        if (!imageFile.isEmpty()) {

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

            // 👉 Project root uploads folder
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            Path uploadPath = Paths.get(uploadDir);

            // folder create agar nahi hai
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // file save
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageFile.getBytes());

            // DB me path save
            menu.setImage("/uploads/" + fileName);
        }

        menuService.saveMenu(menu);

        return "redirect:/list";
    }

    // 👉 MENU LIST (Thymeleaf)
    @GetMapping("/list")
    public String menuList(Model model, HttpSession session) {

        String restaurantName = (String) session.getAttribute("restaurantName");

        model.addAttribute(
                "menuList",
                menuService.getMenuByRestaurant(restaurantName)
        );

        return "menuList";
    }

    // 👉 FRONT PAGE
    @GetMapping("/")
    public String menuPage() {
        return "foodlist";
    }

    // 👉 API PAGE
    @GetMapping("/Buttonmenu")
    public String menuApiPage() {
        return "NormalApi";
    }
    
    @GetMapping("/MenuButton")
    public String MenuButton() {
        return "MenuButton.html";
    }
    
    @GetMapping("/AboutPage")
    public String AboutPage() {
        return "AboutPage.html";
    }
    
    @GetMapping("/ContactPage")
    public String ContactPage() {
        return "ContactPage.html";
    }
    
    @GetMapping("/editMenu/{id}")
    public String editPage(@PathVariable int id, Model model) {
    	Menu menu =menuService.getById(id);
        model.addAttribute("menu", menu);
        return "EditMenuPage";
    }

    @PostMapping("/updateMenu")
    public String updateMenu(@ModelAttribute Menu menu, HttpSession session) {

        Menu existing = menuService.getById(menu.getId());

        // 🔥 restaurant name fix
        String restaurantName = (String) session.getAttribute("restaurantName");
        menu.setRestaurantsName(restaurantName);

        // 🔥 image fix
        if (menu.getImage() == null || menu.getImage().isEmpty()) {
            menu.setImage(existing.getImage());
        }

        menuService.Update(menu);

        return "redirect:/list";
    }


    @GetMapping("/deleteMenu/{id}")
    public String deleteMenu(@PathVariable int id) {
        menuService.delete(id);
        return "redirect:/list";
    }
    
   
    
}
