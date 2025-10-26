package com.restaurant.controller;

import com.restaurant.dto.FeedbackRequest;
import com.restaurant.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackPageController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackPageController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String showFeedbackForm(Model model) {
        if (!model.containsAttribute("feedbackRequest")) {
            model.addAttribute("feedbackRequest", new FeedbackRequest());
        }
        return "feedback/new";
    }

    @PostMapping("/submit")
    public String submitFeedback(@Valid @ModelAttribute("feedbackRequest") FeedbackRequest feedbackRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("feedbackRequest", feedbackRequest);
            return "feedback/new";
        }

        try {
            feedbackService.submitFeedback(feedbackRequest);
            return "redirect:/feedback/thank-you";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to submit feedback. Please try again.");
            return "feedback/new";
        }
    }

    @GetMapping("/thank-you")
    public String thankYouPage() {
        return "feedback/thank-you";
    }
}
